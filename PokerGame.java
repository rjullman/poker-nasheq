import java.lang.StringBuilder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PokerGame {
    private final Deck deck;
    private final Evaluator evaluator;
    private final int numPlayers;
    private final Set<Integer> collPlayers;
    private final Round[] rounds;

    public static class Builder {
        private final Deck deck;
        private final Evaluator evaluator;
        private int numPlayers;
        Set<Integer> collPlayers;
        List<Round> rounds;

        private Builder(Deck deck, Evaluator evaluator, int numPlayers) {
            Preconditions.checkNotNull(deck);
            Preconditions.checkNotNull(evaluator);
            Preconditions.checkArgument(numPlayers > 0, "the game must have player");
            this.deck = deck;
            this.evaluator = evaluator;
            this.numPlayers = numPlayers;
            this.collPlayers = Sets.newHashSet();
            this.rounds = Lists.newArrayList();
        }

        public Builder addRound(Round r) {
            Preconditions.checkNotNull(r);
            rounds.add(r);
            return this;
        }

        public Builder addStronglyColludingPlayers(Set<Integer> collPlayers) {
            this.collPlayers = Sets.newHashSet(collPlayers);
            return this;
        }

        public PokerGame build() {
            Preconditions.checkArgument(rounds.size() != 0, "the game must have at least 1 round");
            return new PokerGame(deck, evaluator, numPlayers, collPlayers, Iterables.toArray(rounds, Round.class));
        }
    }

    public static Builder newBuilder(Deck d, Evaluator e, int numPlayers) {
        return new Builder(d, e, numPlayers);
    } 
    
    public PokerGame(Deck deck, Evaluator evaluator, int numPlayers, Set<Integer> collPlayers, Round[] rounds) {
        this.deck = deck;
        this.evaluator = evaluator;
        this.numPlayers = numPlayers;
        this.collPlayers = collPlayers;
        this.rounds = rounds;
    }
    
    public Evaluator getEvaluator() {
        return evaluator;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    private class GameState {
        private final Evaluator eval;
        private final int numPlayers;

        public double[] bets;
        public boolean[] folds;
        public List<List<Card>> holeCards;
        public List<Card> sharedCards;

        public GameState(Evaluator eval, int numPlayers) {
            this.eval = eval;
            this.numPlayers = numPlayers;
            bets = new double[numPlayers];
            folds = new boolean[numPlayers];
            holeCards = Lists.newArrayList();
            for (int i = 0; i < numPlayers; i++) {
                holeCards.add(new ArrayList<Card>());
            }
            sharedCards = Lists.newArrayList();
        }

        public PayoffNode buildPayoffNode() {
            return new PayoffNode(eval.payoffs(holeCards, sharedCards, bets, folds));
        }

        public double getMaxPlayerContrib() {
            return Arrays.max(bets);
        }

        public int prevPlayer(int curPlayer) {
            for (int i = 0; i < numPlayers; i--) {
                int player = (curPlayer - i - 1 + numPlayers) % numPlayers;
                if (!folds[player]) {
                    return player;
                }
            }
            throw new IllegalStateException();
        }

        public int nextPlayer(int curPlayer) {
            for (int i = 0; i < numPlayers; i++) {
                int player = (i + curPlayer + 1) % numPlayers;
                if (!folds[player]) {
                    return player;
                }
            }
            throw new IllegalStateException();
        }

        public void addCards(int player, List<Card> deal) {
            if (player == DealNode.SHARED_CARDS) {
                sharedCards.addAll(deal);
            } else {
                holeCards.get(player).addAll(deal);
            }
        }

        public void removeCards(int player, List<Card> deal) {
            if (player == DealNode.SHARED_CARDS) {
                sharedCards.removeAll(deal);
            } else {
                holeCards.get(player).removeAll(deal);
            }
        }
    }

    public GameTree buildGameTree() { 
        c = 0;
        GameNode root = buildGameTreeRound(new GameState(getEvaluator(), getNumPlayers()), 0);
        Collection<InfoSet> isets = buildInfoSets(root, new HashMap<String, InfoSet>());
        GameTree gt = new GameTree(this, root, isets);
        System.out.println(c);
        return gt;
    }

    private Collection<InfoSet> buildInfoSets(GameNode n, Map<String, InfoSet> isetsByLabel) {
        int player = n.getPlayer();

        // add node to infoset
        String label = getLabel(n, player);
        InfoSet iset = new InfoSet(n.toString() + label);
        if (n instanceof ActionNode) {
            iset = isetsByLabel.containsKey(label) ? isetsByLabel.get(label) : new InfoSet(label);
            isetsByLabel.put(label, iset);
        }
        iset.add(n);
        n.setInfoSet(iset);

        // recursively add children to their infosets
        List<GameNode> children = n.getChildren();
        for (int cindex = 0; cindex < children.size(); cindex++) {
            buildInfoSets(children.get(cindex), isetsByLabel);
        }

        return isetsByLabel.values();
    }

    private String getLabel(GameNode n, int player) {
        return buildLabel(n, player, new StringBuilder()).toString();
    }

    private StringBuilder buildLabel(GameNode n, int player, StringBuilder sb) {
        GameNode p = n.getParent();
        if (p == null) {
            return sb;
        }

        buildLabel(p, player, sb);
        int cindex = n.getChildIndex();
        String cstring = p.getChildString(cindex);
        if (p.isPublic() || player == SimpleGameNode.PLAYER_NATURE) {
            sb.append(cstring);
        } else if (p instanceof DealNode) {
            DealNode dn = (DealNode) p;
            int forPlayer = dn.getForPlayer();
            if (forPlayer == player || sameTeam(forPlayer, player)) { 
                sb.append(cstring);
            }
        }

        return sb;
    }

    private boolean sameTeam(int p1, int p2) {
        return collPlayers.contains(p1) && collPlayers.contains(p2);
    }

    private static int c = 0;

    private GameNode buildGameTreeRound(GameState state, int rindex) {
        c++;
        if (rindex >= rounds.length) {
            return state.buildPayoffNode();
        } else {
            Round r = rounds[rindex];
            for (int i = 0; i < getNumPlayers(); i++) {
                if (!state.folds[i]) {
                    state.bets[i] += r.getAnte();
                }
            }
            GameNode node = buildDealNodes(state, rindex);
            for (int i = 0; i < getNumPlayers(); i++) {
                if (!state.folds[i]) {
                    state.bets[i] -= r.getAnte();
                }
            }
            return node;
        }
    }

    private GameNode buildDealNodes(GameState state, int rindex) {
        return buildHoleDealNode(state, rindex, 0);
    }

    private GameNode buildDealNode(GameState state, int rindex, int player) {
        c++;
        Round r = rounds[rindex];
        boolean holeCards = (player != DealNode.SHARED_CARDS);
        int numCards = holeCards ? r.getNumHoleCards() : r.getNumSharedCards();

        if (player >= numPlayers || numCards == 0) {
            return holeCards ? buildSharedDealNode(state, rindex) : buildActionNodes(state, rindex);
        }

        DealNode dn = new DealNode(player);
        Map<List<Card>, Double> dealsFreqMap = makeAllDeals(deck, numCards);
        for (List<Card> deal : dealsFreqMap.keySet()) {
            state.addCards(player, deal);
            for (Card c : deal) {
                deck.draw(c);
            }
            GameNode child = holeCards ? buildHoleDealNode(state, rindex, player + 1) : buildActionNodes(state, rindex);
            dn.addChild(child, deal, dealsFreqMap.get(deal));
            for (Card c : deal) {
                deck.replace(c);
            }
            state.removeCards(player, deal);
        }
        return dn;
    }

    private GameNode buildHoleDealNode(GameState state, int rindex, int player) {
        return buildDealNode(state, rindex, player);
    }

    private GameNode buildSharedDealNode(GameState state, int rindex) {
        return buildDealNode(state, rindex, DealNode.SHARED_CARDS);
    }

    private GameNode buildActionNodes(GameState state, int rindex) {
        return buildActionNode(state, rindex, 0, 0);
    }

    private GameNode buildActionNode(GameState state, int rindex, int player, int round) {
        c++;
        if (Arrays.sum(state.folds) + 1 == numPlayers) {
            return state.buildPayoffNode();
        }

        Round r = rounds[rindex];
        if (round >= r.getMaxBetsPerPlayer()) {
            return buildGameTreeRound(state, rindex + 1);
        }

        ActionNode an = new ActionNode(player);
        int nextPlayer = state.nextPlayer(player);
        int prevPlayer = state.prevPlayer(player);
        int nextRound = (nextPlayer > player) ? round : round + 1;
        boolean noRaises = (round + 1 == r.getMaxBetsPerPlayer()) && (prevPlayer < player);
        double maxContrib = state.getMaxPlayerContrib();
        for (double bet : r.getBets()) {
            if (bet + state.bets[player] >= maxContrib) {
                if (!noRaises || (bet + state.bets[player]) == maxContrib) {
                    state.bets[player] += bet;
                    GameNode betChild = buildActionNode(state, rindex, nextPlayer, nextRound);
                    an.addChild(betChild, PlayerAction.BET, bet);
                    state.bets[player] -= bet;
                }
            }
        }
        state.folds[player] = true;
        GameNode foldChild = buildActionNode(state, rindex, nextPlayer, nextRound);
        an.addFoldChild(foldChild);
        state.folds[player] = false;
        return an;
    }

    private static Map<List<Card>, Double> makeAllDeals(Deck deck, int size) {
        if (size == 0) {
            Map<List<Card>, Double> emptyDeal = Maps.newHashMap();
            emptyDeal.put(new ArrayList<Card>(), 1.0);
            return emptyDeal;
        }

        Map<List<Card>, Double> deals = Maps.newHashMap();
        Map<List<Card>, Double> smallerDeals = makeAllDeals(deck, size - 1);
        for (List<Card> sdeal : smallerDeals.keySet()) {
            double sfreq = smallerDeals.get(sdeal);
            for (Card c : sdeal) {
                deck.draw(c);
            }

            int lastRank = sdeal.isEmpty() ? -1 : sdeal.get(sdeal.size() - 1).rank();
            for (int r = 0; r < deck.ranks(); r++) {
                // prevent permutations of existing hands
                if (r < lastRank) {
                    continue;
                }

                Card c = deck.card(r, 0);
                if (!deck.canDraw(c)) {
                    continue;
                }
                c = deck.card(r, deck.suits() - deck.count(c));

                List<Card> deal = Lists.newArrayList(sdeal);
                deal.add(c);
                int count = countMaxRankCards(deal);
                double freq = sfreq * (size / (deck.remaining() * 1.0)) * ((deck.count(c) * 1.0) / count);
                deals.put(deal, freq);
            }

            for (Card c : sdeal) {
                deck.replace(c);
            }
        }
        return deals;
    }

    /** Returns the number of max rank cards in deal given that deal is sorted
     * in ascending order by rank.
     */
    private static int countMaxRankCards(List<Card> deal) {
        int rank = deal.get(deal.size() - 1).rank();
        int count = 1;
        for (int i = deal.size() - 2; i >= 0; i--) {
            if (deal.get(i).rank() == rank) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

}
