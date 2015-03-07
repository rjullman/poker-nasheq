import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class GameTree {

    private PokerGame game;
    private GameNode root;
    private Collection<InfoSet> isets;

    public GameTree(PokerGame game, GameNode root, Collection<InfoSet> isets) {
        this.game = game;
        this.root = root;
        this.isets = isets;
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
        n.setFreq(null);
        for (GameNode c : n.getChildren()) {
            reset(c);
        }
    }

    public Collection<InfoSet> getInfoSets() {
        return isets;
    }

}
