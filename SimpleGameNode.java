import java.util.ArrayList;
import java.util.List;

public class SimpleGameNode implements GameNode {
    public static final int PLAYER_NATURE = -1;

    private final List<GameNode> children;
    private final int player;
    private final boolean publicVis;
    private GameNode parent;
    private int cindex;
    private Double payoff;
    private Double freq;
    private InfoSet iset;

    public SimpleGameNode() {
        this.children = new ArrayList<GameNode>();
        this.player = PLAYER_NATURE;
        this.publicVis = true;
    }

    public SimpleGameNode(int player, boolean publicVis) {
        this.children = new ArrayList<GameNode>();
        this.player = player;
        this.publicVis = publicVis;
    }

    public GameNode getParent() {
        return parent;
    }

    public void setParent(GameNode parent) {
        this.parent = parent;
    }

    public void addChild(GameNode n) {
        children.add(n);
        n.setChildIndex(children.size() - 1);
        n.setParent(this);
    }

    public int getChildIndex() {
        return cindex;
    }

    public void setChildIndex(int cindex) {
        this.cindex = cindex;
    }

    public List<GameNode> getChildren() {
        return children;
    }

    public Double getPayoff() {
        return payoff;
    }

    public Double setPayoff(Double payoff) {
        this.payoff = payoff;
        return this.payoff;
    }

    public Double getFreq() {
        return freq;
    }

    public Double setFreq(Double freq) {
        this.freq = freq;
        return this.freq;
    }

    public InfoSet getInfoSet() {
        return iset;
    }

    public void setInfoSet(InfoSet iset) {
        this.iset = iset;
    }

    public int getPlayer() {
        return player;
    }

    public boolean isPublic() {
        return publicVis;
    }

    public String getChildString(int cindex) {
        return this.toString();
    }

}


