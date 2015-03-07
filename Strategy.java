import java.lang.StringBuilder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.EnumSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Strategy {

    private final HashMap<InfoSet, Distribution<PlayerAction>> strategy;

    public Strategy(GameTree gt) {
        this.strategy = new HashMap<InfoSet, Distribution<PlayerAction>>();
        init(gt, gt.getRoot());
    }

    private void init(GameTree gt, GameNode n) {
        if (n.getChildren().size() == 0) {
            return;
        }

        if (n instanceof ActionNode) {
            ActionNode an = (ActionNode) n;
            Set<PlayerAction> aset = Sets.newHashSet(an.getActions());
            strategy.put(an.getInfoSet(), new Distribution<PlayerAction>(aset));
        }

        for (GameNode c : n.getChildren()) {
            init(gt, c);
        }
    }

    public double getProb(InfoSet iset, PlayerAction a) {
        return strategy.get(iset).get(a);
    }

    public void setAction(InfoSet iset, PlayerAction a) {
        strategy.get(iset).unilaterally(a);
    }

    public void average(Strategy s, double w) {
        assert strategy.keySet().size() == s.strategy.keySet().size();
        for (InfoSet is : strategy.keySet()) {
            strategy.get(is).average(s.strategy.get(is), w);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<InfoSet> isets = Lists.newArrayList(strategy.keySet());
        Collections.sort(isets, new Comparator<InfoSet>() {
            public int compare(InfoSet s1, InfoSet s2) {
                return s1.toString().compareTo(s2.toString());
            }
        });
        for (InfoSet iset : isets) {
            sb.append(iset).append(":\t\t").append(strategy.get(iset)).append('\n');
        }
        return sb.toString();
    }

}
