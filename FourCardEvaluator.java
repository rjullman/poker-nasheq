import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class FourCardEvaluator implements Evaluator {
    private final static int CARDS_PER_HAND = 4;

    private static class Hand implements Comparable<Hand> {
        public final int player;
        public final List<Card> cards;
        public final int value;
        public Hand(int player, List<Card> cards, int value) {
            this.player = player;
            this.cards = cards;
            this.value = value;
        }

        public int compareTo(Hand h) {
            return value - h.value;
        }
    }

    private enum HandType {
        JUNK(0), PAIR(3), TWO_PAIR(4), THREE_OF_A_KINDS(5), FOUR_OF_A_KIND(6);

        private final int ranking;

        private HandType(int ranking) {
            this.ranking = ranking;
        }

        public int getRankValue() {
            return ranking;
        }
    }

    private enum CardCount {
        ONE_CARD(1), TWO_CARDS(2), THREE_CARDS(3), FOUR_CARDS(4);

        private final int count;

        private CardCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public static CardCount ofCount(int count) {
            for (CardCount c : CardCount.values()) {
                if (c.getCount() == count) {
                    return c;
                }
            }
            throw new RuntimeException("no CardCount of given count");
        }
    }

    public double[] payoffs(List<List<Card>> holeCards, List<Card> sharedCards, double[] bets, boolean[] folds) {
        int numPlayers = holeCards.size();
        int numFolds = Arrays.sum(folds);

        Set<Integer> winners = Sets.newHashSet();
        if (numFolds + 1 < numPlayers) {
            List<Hand> hands = buildHands(holeCards, sharedCards, folds);
            Collections.sort(hands, Collections.reverseOrder());
            for (Hand hand : hands) {
                if (hand.value == hands.get(0).value) {
                    winners.add(hand.player);
                } else {
                    break;
                }
            }
        } else {
            for (int i = 0; i < numPlayers; i++) {
                if (!folds[i]) {
                    winners.add(i);
                    break;
                }
            }
        }

        double pot = Arrays.sum(bets);
        double[] payoffs = new double[holeCards.size()];
        for (int p = 0; p < numPlayers; p++) {
            payoffs[p] = -bets[p];
            if (winners.contains(p)) {
                payoffs[p] += (1.0 / winners.size()) * pot;
            }
        }
        return payoffs;
    }

    private static List<Hand> buildHands(List<List<Card>> holeCards, List<Card> sharedCards, boolean[] folds) {
        List<Hand> hands = Lists.newArrayList();
        for (int player = 0; player < holeCards.size(); player++) {
            if (folds[player]) { 
                continue;
            }
            List<Card> cards = Lists.newArrayList(holeCards.get(player));
            for (Card c : sharedCards) {
                cards.add(c);
            }
            List<List<Card>> playerHands = buildPossibleHands(cards, CARDS_PER_HAND);
            for (List<Card> hand : playerHands) {
                hands.add(new Hand(player, hand, evaluate(hand)));
            }
        }
        return hands;
    }

    private static List<List<Card>> buildPossibleHands(List<Card> cards, int size) {
        if (size == 0) {
            return Collections.singletonList(new ArrayList<Card>());
        }

        Preconditions.checkArgument(cards.size() != 0, "not enough cards to make hand of desired size");
        List<List<Card>> hands = Lists.newArrayList();
        for (int i = cards.size() - 1; i >= 0; i--) {
            Card c = cards.get(i);
            cards.remove(c);
            for (List<Card> hand : buildPossibleHands(cards, size - 1)) {
                hand.add(c);
                hands.add(hand);
            }
            cards.add(c);
        }

        return hands;
    }

    private static int evaluate(List<Card> hand) {
        Map<CardCount, List<Integer>> vtr = buildCountMap(hand);
        List<Integer> kickers = vtr.keySet().contains(CardCount.ONE_CARD) 
            ? vtr.get(CardCount.ONE_CARD) : new ArrayList<Integer>();
        if (vtr.keySet().contains(CardCount.FOUR_CARDS)) {
            return value(HandType.FOUR_OF_A_KIND, vtr.get(CardCount.FOUR_CARDS).get(0), 0, 0, 0);
        } else if (vtr.keySet().contains(CardCount.THREE_CARDS)) {
            return value(HandType.THREE_OF_A_KINDS, vtr.get(CardCount.THREE_CARDS).get(0), kickers.get(0), 0, 0);
        } else if (vtr.keySet().contains(CardCount.TWO_CARDS)) {
            List<Integer> pairs = vtr.get(CardCount.TWO_CARDS);
            if (pairs.size() == 2) {
                return value(HandType.TWO_PAIR, pairs.get(0), pairs.get(1), 0, 0);
            } else {
                return value(HandType.PAIR, pairs.get(0), kickers.get(0), kickers.get(1), 0);
            }
        } else {
            return value(HandType.JUNK, kickers.get(0), kickers.get(1), kickers.get(2), kickers.get(3));
        }
    }

    public static int value(HandType e, int d, int c, int b, int a) {
        return 10000 * e.getRankValue() + 1000 * d + 100 * c + 10 * b + a;
    }

    private static Map<CardCount, List<Integer>> buildCountMap(List<Card> hand) {
        Map<CardCount, List<Integer>> countMap = Maps.newHashMap();
        Collections.sort(hand, Collections.reverseOrder());
        int rank = hand.get(0).rank();
        int count = 1;
        for (int i = 1; i < hand.size(); i++) {
            Card c = hand.get(i);
            if (c.rank() == rank) {
                count++;
            } else {
                addCountToMap(countMap, count, rank);
                rank = c.rank();
                count = 1;
            }
        }
        addCountToMap(countMap, count, rank);
        return countMap;
    }

    private static void addCountToMap(Map<CardCount, List<Integer>> countMap, int count, int rank) {
        CardCount cc = CardCount.ofCount(count);
        if (!countMap.keySet().contains(cc)) {
            countMap.put(cc, new ArrayList<Integer>());
        }
        countMap.get(cc).add(rank);
    }

}
