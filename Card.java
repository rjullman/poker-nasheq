public class Card implements Comparable<Card> {
    private static final String CARD_NAMES = "23456789TJQKA";

    private final int rank;
    private final int suit;

    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int rank() {
        return rank;
    }

    public int suit() {
        return suit;
    }

    public String toString() {
        return Character.toString(CARD_NAMES.charAt(rank));
        /*
         *return "(" + rank + "," + suit + ")";
         */
    }

    @Override
    public int compareTo(Card other){
        if (rank == other.rank()) {
            return suit - other.suit();
        }
        return rank - other.rank();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Card other = (Card) obj;
        return com.google.common.base.Objects.equal(this.rank, other.rank)
            && com.google.common.base.Objects.equal(this.suit, other.suit);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.rank, this.suit);
    }

}
