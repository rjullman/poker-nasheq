import java.util.List;
import java.util.ArrayList;

public class ActionNode extends SimpleGameNode {

    private final int player;
    private final List<PlayerAction> actions;
    private final List<Double> bets;

    public ActionNode(int player) {
        this.player = player;
        this.actions = new ArrayList<PlayerAction>();
        this.bets = new ArrayList<Double>();
    }

    public int getPlayer() {
        return player;
    }

    public void addChild(GameNode n, PlayerAction action, double bet) {
        super.addChild(n);
        actions.add(action);
        bets.add(bet);
    }

    public void addChild(GameNode n) {
        throw new UnsupportedOperationException("use overloaded addChild()");
    }

    public void addFoldChild(GameNode n) {
        addChild(n, PlayerAction.FOLD, 0);
    }

    public PlayerAction getAction(int i) {
        return actions.get(i);
    }

    public List<PlayerAction> getActions() {
        return actions;
    }

    public double getBet(int i) {
        return bets.get(i);
    }

    public String getChildString(int cindex) {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(getAction(cindex)).append(']');
        return sb.toString();
    }

}


