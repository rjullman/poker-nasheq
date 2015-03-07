import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class InfoSet implements Iterable<GameNode> {

    private final String label;
    private final List<GameNode> nodes;

    public InfoSet(String label) {
        this.label = label;
        this.nodes = Lists.newArrayList();
    }

    public String getLabel() {
        return label;
    }

    public int size() {
        return nodes.size();
    }

    public List<GameNode> getNodes() {
        return nodes;
    }

    public boolean add(GameNode n) {
        return nodes.add(n);
    }

    public GameNode get(int i) {
        return nodes.get(i);
    }

    public Iterator<GameNode> iterator() {
        return nodes.iterator();
    }

    public String toString() {
        return "   " + label;
    }

}
