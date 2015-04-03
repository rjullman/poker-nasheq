import java.util.List;
import java.util.ArrayList;

public class ActionNode extends SimpleGameNode {

    private final List<PlayerAction> actions;
    private final List<Double> bets;

    public ActionNode(int player) {
        super(player, true);
        this.actions = new ArrayList<PlayerAction>();
        this.bets = new ArrayList<Double>();
    }

    public void addChild(GameNode n, PlayerAction action, Double bet) {
        super.addChild(n);
        actions.add(action);
        bets.add(bet);
    }

    public void addChild(GameNode n) {
        throw new UnsupportedOperationException("use overloaded addChild()");
    }

    public void addFoldChild(GameNode n) {
        addChild(n, PlayerAction.FOLD, null);
    }

    public PlayerAction getAction(int i) {
        return actions.get(i);
    }

    public List<Double> getBets() {
        return bets;
    }

    public Double getBet(int i) {
        return bets.get(i);
    }

    public String getChildString(int cindex) {
        StringBuilder sb = new StringBuilder();
        PlayerAction a = getAction(cindex);
        sb.append('[').append(getPlayer()).append(a);
        if (a == PlayerAction.BET) {
            sb.append('(').append(getBet(cindex)).append(')');
        }
        sb.append(']');
        return sb.toString();
    }

}


