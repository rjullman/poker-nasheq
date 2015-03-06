import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class InfoSet implements Iterable<GameNode> {

    private final String name;
    private final int player;
    private final Set<GameNode> nodes;

    public InfoSet(String name, int player) {
        this.name = name;
        this.player = player;
        this.nodes = new HashSet<GameNode>();
    }

    public String getName() {
        return name;
    }

    public int getPlayer() {
        return player;
    }

    public int size() {
        return nodes.size();
    }

    public Set<GameNode> getNodes() {
        return nodes;
    }

    public boolean add(GameNode n) {
        return nodes.add(n);
    }

    public Iterator<GameNode> iterator() {
        return nodes.iterator();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("p").append(player).append(' ').append(name);
        return sb.toString();
    }

}
