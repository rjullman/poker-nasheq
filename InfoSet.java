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
        return label;
    }

    @Override  
    public boolean equals(Object obj) {  
        if (obj == null) {  
            return false;  
        }  
        if (getClass() != obj.getClass()) {  
            return false;  
        }  
        final InfoSet other = (InfoSet) obj;  
        return com.google.common.base.Objects.equal(this.label, other.label);
    }

    @Override  
    public int hashCode() {  
        return com.google.common.base.Objects.hashCode(this.label);  
    } 

}
