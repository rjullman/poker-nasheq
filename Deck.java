import java.util.ArrayList;
import java.util.List;
import java.lang.IllegalStateException;

public class Deck {

    private final int ranks;
    private final int suits;

    private final Card[] cards;

    private int[] rankCounts;
    private int remaining;

    protected Deck(int ranks, int suits) {
        this.ranks = ranks;
        this.suits = suits;
        this.remaining = ranks * suits;
        this.rankCounts = new int[ranks];
        this.cards = new Card[ranks * suits];
        for (int r = 0; r < ranks; r++) {
            rankCounts[r] = suits;
            for (int s = 0; s < suits; s++) {
                cards[r * suits + s] = new Card(r,s);
            }
        }
    }

    public int ranks() {
        return ranks;
    }

    public int suits() {
        return suits;
    }

    public int size() {
        return ranks() * suits();
    }

    public Card[] cards() {
        return cards;
    }

    public Card card(int r, int s) {
        return cards[r * suits + s];
    }

    public int remaining() {
        return remaining;
    }

    public int count(Card c) {
        return rankCounts[c.rank()];
    }

    public boolean canDraw(Card c) {
        return rankCounts[c.rank()] > 0;
    }

    public void draw(Card c) {
        if (!canDraw(c)) {
            throw new RuntimeException("No more card (" + c + ") in the deck.");
        }
        remaining--;
        rankCounts[c.rank()]--;
    }

    public void replace(Card c) {
        if (rankCounts[c.rank()] > suits) {
            throw new RuntimeException("Too many cards (" + c + ") in the deck.");
        } 
        remaining++;
        rankCounts[c.rank()]++;
    }

}
