import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class GameTree {

    private PokerGame game;
    private GameNode root;
    private Map<String, InfoSet> infosets;

    public GameTree(PokerGame game, GameNode root) {
        this.game = game;
        this.root = root;
        this.infosets = new HashMap<String, InfoSet>();
    }

    public PokerGame getGame() {
        return game;
    }

    public GameNode getRoot() {
        return root;
    }

    public void reset() {
        reset(root);
    }

    private void reset(GameNode n) {
        n.setPayoff(null);
        for (GameNode c : n.getChildren()) {
            reset(c);
        }
    }

    public Map<String, InfoSet> getInfoSets() {
        return infosets;
    }

    public void addToInfoSet(ActionNode n) {
        getInfoSet(n).add(n);
    }

    public InfoSet getInfoSet(ActionNode n) {
        int player = n.getPlayer();
        String key = makeKey(n, player);
        if (!infosets.containsKey(key)) {
            infosets.put(key, new InfoSet(key, player));
        }
        return infosets.get(key);
    }

    public static String makeKey(GameNode n, int player) {
        GameNode p = n.getParent();
        int cindex = n.getChildIndex();
        if (p == null) {
            return "";
        }

        String str = "";
        if (p instanceof DealNode) {
            DealNode dn = (DealNode) p;
            if (!dn.isHoleCards() || dn.getPlayer() == player || player < 0) {
                str = dealNodeToString(dn, cindex);
            }
        } else if (p instanceof ActionNode) {
            ActionNode an = (ActionNode) p;
            str = actionNodeToString(an, cindex);
        }
        return makeKey(n.getParent(), player) + str;
    }

    private static String dealNodeToString(DealNode dn, int cindex) {
        List<Card> deal = dn.getDeal(cindex);
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < deal.size(); i++) {
            sb.append(deal.get(i));
        }
        sb.append(']');
        return sb.toString();
    }

    private static String actionNodeToString(ActionNode an, int cindex) {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(an.getAction(cindex)).append(']');
        return sb.toString();
    }

}
