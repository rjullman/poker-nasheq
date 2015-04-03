import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class Distribution<K> {

    Map<K, Double> dist;

    public Distribution(Set<K> opts) {
        dist = new HashMap<K, Double>();
        for (K opt : opts) {
            dist.put(opt, 1.0/opts.size());
            /*
             *dist.put(opt, 0.0);
             */
        }
        /*
         *dist.put(opts.iterator().next(), 1.0);
         */
    }

    public double get(K opt) {
        return dist.get(opt);
    }

    public Set<K> getOptions() {
        return dist.keySet();
    }

    public void unilaterally(K opt) {
        for (K key : dist.keySet()) {
            dist.put(key, 0.0);
        }
        dist.put(opt, 1.0);
    }

    public void average(Distribution<K> d, double w) {
        for (K opt : d.getOptions()) {
            dist.put(opt, (1 - w) * dist.get(opt) + w * d.get(opt));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("0.000");
        Iterator<K> iter = dist.keySet().iterator();
        while (iter.hasNext()) {
            K opt = iter.next();
            sb.append(opt).append('[').append(df.format(dist.get(opt))).append(']');
            if (iter.hasNext()) {
                sb.append('/');
            }
        }
        return sb.toString();
    }

}
