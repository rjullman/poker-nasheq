import java.lang.StringBuilder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.EnumSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Strategy {

    private final HashMap<InfoSet, Distribution<Double>> strategy;

    public Strategy(GameTree gt) {
        this.strategy = new HashMap<InfoSet, Distribution<Double>>();
        init(gt, gt.getRoot());
    }

    private void init(GameTree gt, GameNode n) {
        if (n.getChildren().size() == 0) {
            return;
        }

        if (n instanceof ActionNode) {
            ActionNode an = (ActionNode) n;
            Set<Double> bset = Sets.newHashSet(an.getBets());
            strategy.put(an.getInfoSet(), new Distribution<Double>(bset));
        }

        for (GameNode c : n.getChildren()) {
            init(gt, c);
        }
    }

    public Distribution<Double> getDist(InfoSet iset) {
        return strategy.get(iset);
    }

    public void setDist(InfoSet iset, Distribution<Double> dist) {
        strategy.put(iset, dist);
    }

    public double getProb(InfoSet iset, Double d) {
        try {
            return strategy.get(iset).get(d);
        } catch (Exception e) {
            System.out.println(this);
            System.out.println(d + " " + iset);
            System.out.println(strategy.get(iset));
            throw new RuntimeException(e);
        }
    }

    public void setAction(InfoSet iset, Double d) {
        strategy.get(iset).unilaterally(d);
    }

    public void average(Strategy s, double w) {
        assert strategy.keySet().size() == s.strategy.keySet().size();
        for (InfoSet is : strategy.keySet()) {
            strategy.get(is).average(s.strategy.get(is), w);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        List<InfoSet> isets = Lists.newArrayList(strategy.keySet());
        Collections.sort(isets, new Comparator<InfoSet>() {
            public int compare(InfoSet s1, InfoSet s2) {
                return s1.toString().compareTo(s2.toString());
            }
        });
        for (InfoSet iset : isets) {
            formatter.format("%-60s %s\n", iset, strategy.get(iset));
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

}
