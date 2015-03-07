import java.util.List;
import java.util.ArrayList;

public class DealNode extends SimpleGameNode {
    public static final int SHARED_CARDS = -1;

    private final int forPlayer;
    private final List<List<Card>> deals;
    private final List<Double> freqs;

    public DealNode() {
        this(SHARED_CARDS);
    }

    public DealNode(int forPlayer) {
        this(forPlayer, forPlayer == SHARED_CARDS);
    }

    private DealNode(int forPlayer, boolean publicVis) {
        super(SimpleGameNode.PLAYER_NATURE, publicVis);
        this.forPlayer = forPlayer;
        this.deals = new ArrayList<List<Card>>();
        this.freqs = new ArrayList<Double>();
    }

    public boolean isHoleCards() {
        return forPlayer != SHARED_CARDS;
    }

    public int getForPlayer() {
        return forPlayer;
    }

    public void addChild(GameNode n, List<Card> deal, double freq) {
        super.addChild(n);
        deals.add(deal);
        freqs.add(freq);
    }

    public void addChild(GameNode n) {
        throw new UnsupportedOperationException("use overloaded addChild()");
    }

    public List<Card> getDeal(int i) {
        return deals.get(i);
    }

    public double getFreq(int i) {
        return freqs.get(i);
    }

    public String getChildString(int cindex) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Card c : getDeal(cindex)) {
            sb.append(c);
        }
        sb.append(']');
        return sb.toString();
    }

}


