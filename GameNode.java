import java.util.Set;
import java.util.List;

public interface GameNode {

    public List<GameNode> getChildren();
    public GameNode getParent();
    public void setParent(GameNode parent);

    public int getChildIndex();
    public void setChildIndex(int cindex);

    public Double getPayoff();
    public Double setPayoff(Double payoff);

    public InfoSet getInfoSet();
    public void setInfoSet(InfoSet iset);

    public String getChildString(int cindex);

}
