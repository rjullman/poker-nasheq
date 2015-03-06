import java.lang.IllegalStateException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public final class PokerUtils {

    public static double winner(List<Card> h1, List<Card> h2, List<Card> shared) {
        List<Card> c1 = new ArrayList<Card>(shared);
        c1.addAll(h1);
        List<Card> c2 = new ArrayList<Card>(shared);
        c2.addAll(h2);
        int r1 = evaluate(c1);
        int r2 = evaluate(c2);

        if (r1 > r2) {
            return 1;
        } else if (r1 == r2) {
            return 0.5;
        } else {
            return 0;
        }
    }

    public static int evaluate(List<Card> cards) {
        Collections.sort(cards, Collections.reverseOrder());

        int maxCount = 1;
        int maxRank = cards.get(0).rank();
        int count = 1;
        for (int i = 1; i < cards.size(); i++) {
            Card p = cards.get(i-1);
            Card n = cards.get(i);
            if (p.rank() == n.rank()) {
                count++;
                if (count > maxCount) {
                    maxCount = count;
                    maxRank = p.rank();
                }
            } else {
                count = 1;
            }
        }

        switch (maxCount) {
            case 4: return value(6, maxRank, 0, 0, 0);
            case 3: return value(5, maxRank, kicker(cards, 0, maxRank), 0, 0);
            case 2: 
                int k = kicker(cards, 0, maxRank);
                if (k == cards.get(3).rank()) {
                    return value(4, maxRank, k, 0, 0);
                } else {
                    return value(3, maxRank, k, kicker(cards, 1, maxRank), 0);
                }
            case 1: return value(0, cards.get(0).rank(), cards.get(1).rank(),
                            cards.get(2).rank(), cards.get(3).rank());
            default: throw new IllegalStateException("Impossible");
        }
    }

    public static int kicker(List<Card> cards, int n, int ignoreRank) {
        for (int i = 0; i < cards.size(); i++) {
            int r = cards.get(i).rank();
            if (r != ignoreRank) {
                n--;
            }
            if (n < 0) {
                return r;
            }
        }
        
        throw new IllegalStateException("Impossible");
    }

    public static int value(int e, int d, int c, int b, int a) {
        return 10000 * e + 1000 * d + 100 * c + 10 * b + a;
    }

}
