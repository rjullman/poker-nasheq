import java.lang.StringBuilder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PokerGame {
    private final Deck deck;
    private final int numPlayers;
    private final Round[] rounds;

    public static class Builder {
        private final Deck deck;
        private int numPlayers;
        List<Round> rounds;

        private Builder(Deck deck, int numPlayers) {
            Preconditions.checkNotNull(deck);
            Preconditions.checkArgument(numPlayers > 0, "the game must have player");
            this.deck = deck;
            this.numPlayers = numPlayers;
            this.rounds = Lists.newArrayList();
        }

        public Builder addRound(Round r) {
            Preconditions.checkNotNull(r);
            rounds.add(r);
            return this;
        }

        public PokerGame build() {
            Preconditions.checkArgument(rounds.size() != 0, "the game must have at least 1 round");
            return new PokerGame(deck, numPlayers, Iterables.toArray(rounds, Round.class));
        }
    }

    public static Builder newBuilder(Deck d, int numPlayers) {
        return new Builder(d, numPlayers);
    }

    public PokerGame(Deck deck, int numPlayers, Round[] rounds) {
        this.deck = deck;
        this.numPlayers = numPlayers;
        this.rounds = rounds;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public GameTree buildGameTree() { 
        SimpleGameNode root = new SimpleGameNode();
        GameTree gt = new GameTree(this, root);
        root.addChild(buildGameTreeRound(0));
        buildInfoSets(gt, gt.getRoot());
        return gt;
    }

    private void buildInfoSets(GameTree gt, GameNode n) {
        if (n instanceof ActionNode) {
            ActionNode an = (ActionNode) n;
            gt.addToInfoSet(an);
        }
        for (GameNode c : n.getChildren()) {
            buildInfoSets(gt, c);
        }
    }

    private GameNode buildGameTreeRound(int rindex) {
        if (rindex >= rounds.length) {
            return new SimpleGameNode();
        }
        return buildDealNodes(rindex);
    }

    private GameNode buildDealNodes(int rindex) {
        return buildHoleDealNode(rindex, 0);
    }

    private GameNode buildDealNode(int rindex, int player) {
        Round r = rounds[rindex];
        boolean holeCards = (player != DealNode.SHARED_CARDS);
        int numCards = holeCards ? r.getNumHoleCards() : r.getNumSharedCards();

        if (player >= numPlayers || numCards == 0) {
            return holeCards ? buildSharedDealNode(rindex) : buildActionNodes(rindex);
        }

        DealNode dn = new DealNode(player);
        Map<List<Card>, Double> dealsFreqMap = makeAllDeals(deck, numCards);
        for (List<Card> deal : dealsFreqMap.keySet()) {
            for (Card c : deal) {
                deck.draw(c);
            }
            GameNode child = holeCards ? buildHoleDealNode(rindex, player + 1) : buildActionNodes(rindex);
            dn.addChild(child, deal, dealsFreqMap.get(deal));
            for (Card c : deal) {
                deck.replace(c);
            }
        }
        return dn;
    }

    private GameNode buildHoleDealNode(int rindex, int player) {
        return buildDealNode(rindex, player);
    }

    private GameNode buildSharedDealNode(int rindex) {
        return buildDealNode(rindex, DealNode.SHARED_CARDS);
    }

    private GameNode buildActionNodes(int rindex) {
        return buildActionNode(rindex, 0, 0, 0, 0);
    }

    private GameNode buildActionNode(int rindex, int player, int round, double maxBet, int numFolds) {
        if (numFolds + 1 == numPlayers) {
            return new SimpleGameNode();
        }

        Round r = rounds[rindex];
        if (round >= r.getMaxBetsPerPlayer()) {
            return buildGameTreeRound(rindex + 1);
        }

        ActionNode an = new ActionNode(player);
        int nextPlayer = (player + 1 < numPlayers) ? player + 1 : 0;
        int nextRound = (nextPlayer != 0) ? round : round + 1;
        for (double bet : r.getBets()) {
            if (bet >= maxBet) {
                GameNode betChild = buildActionNode(rindex, nextPlayer, nextRound, bet, numFolds);
                an.addChild(betChild, PlayerAction.BET, bet);
            }
        }
        GameNode foldChild = buildActionNode(rindex, nextPlayer, nextRound, maxBet, numFolds + 1);
        an.addFoldChild(foldChild);
        return an;
    }

    private static Map<List<Card>, Double> makeAllDeals(Deck deck, int size) {
        if (size == 0) {
            Map<List<Card>, Double> emptyDeal = Maps.newHashMap();
            emptyDeal.put(Lists.newArrayList(), 1.0);
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

                List<Card> deal = Lists.newArrayList(sdeal);
                deal.add(c);
                int count = countMaxRankCards(deal);
                double freq = sfreq * (size / (deck.remaining() * 1.0)) * ((deck.count(c) * 1.0) / count);
                /*
                 *double freq = sfreq * (size / (deck.size() - size + 1.0)) * ((deck.suits() - count + 1.0) / count);
                 */
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
