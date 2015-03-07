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

    public Double getFreq();
    public Double setFreq(Double freq);

    public InfoSet getInfoSet();
    public void setInfoSet(InfoSet iset);

    public int getPlayer();

    public boolean isPublic();

    public String getChildString(int cindex);

}
