import java.util.ArrayList;
import java.util.List;

public class SimpleGameNode implements GameNode {

    private final List<GameNode> children;
    private GameNode parent;
    private int cindex;
    private Double payoff;
    private InfoSet iset;

    public SimpleGameNode() {
        this.children = new ArrayList<GameNode>();
        this.parent = null;
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

    public InfoSet getInfoSet() {
        return iset;
    }

    public void setInfoSet(InfoSet iset) {
        this.iset = iset;
    }

    public String getChildString(int cindex) {
        return this.toString();
    }

}


