public class Card implements Comparable<Card> {
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
        return Integer.toString(rank);
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

}
