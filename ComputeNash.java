import java.io.PrintWriter;

import java.lang.StringBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

public class ComputeNash {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("usage: javac ComputeNash [output prefix]");
            System.exit(1);
        }

        double epsilon = 0.1;
        Set<Integer> team = Sets.newHashSet(1,2);
        PokerGame gcol = PokerGames.newMHZeroSumCollusionGame(3, false, team);
        GameTree gtcol = gcol.buildGameTree();
        Strategy scol = Strategies.nes(gtcol, epsilon);

        PokerGame gnocol = PokerGames.newMHGame(3, false);
        GameTree gtnocol = gnocol.buildGameTree();
        Strategy snocol = Strategies.nes(gtnocol, epsilon);

        Strategy smixedcol = buildStrategy(gtcol, scol, snocol, 0);
        Strategy smixednocol = buildStrategy(gtcol, snocol, scol, 0);

        String pathPrefix = args[0];
        writeToFile(pathPrefix + "-no-collusion.nash", snocol.toString());
        writeToFile(pathPrefix + "-collusion.nash", scol.toString());
        writeToFile(pathPrefix + "-mixed-no-collusion.nash", smixednocol.toString());
        writeToFile(pathPrefix + "-mixed-collusion.nash", smixedcol.toString());

        StringBuffer summary = new StringBuffer();
        summary.append(epsilon).append(" max regret\n")
               .append("no collusion\n")
               .append(getExpectedPayoffsStr(gtnocol, snocol))
               .append("collusion\n")
               .append(getExpectedPayoffsStr(gtcol, scol))
               .append("mixed (no collusion)\n")
               .append(getExpectedPayoffsStr(gtnocol, smixednocol))
               .append("mixed (collusion)\n")
               .append(getExpectedPayoffsStr(gtcol, smixedcol));
        writeToFile(pathPrefix + "-summary.payoffs", summary.toString());
    }

    public static void writeToFile(String path, String str) throws Exception {
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println(str);
        writer.close();
    }

    public static Strategy buildStrategy(GameTree gt, Strategy s1, Strategy s2, int player) {
        Strategy s = new Strategy(gt);
        buildStrategyInternal(gt.getRoot(), s, s1, s2, player);
        return s;
    }

    public static void buildStrategyInternal(GameNode node, Strategy s, Strategy s1, Strategy s2, int player) {
        for (GameNode c : node.getChildren()) {
            if (c instanceof ActionNode) {
                InfoSet iset = c.getInfoSet();
                if (c.getPlayer() != player) {
                    s.setDist(iset, s1.getDist(iset));
                } else {
                    s.setDist(iset, s2.getDist(iset));
                }
            }
            buildStrategyInternal(c, s, s1, s2, player);
        }
    }

    public static String getExpectedPayoffsStr(GameTree gt, Strategy s) {
        StringBuffer sb = new StringBuffer();
        double[] epayoffs = Strategies.expectedPayoffs(gt, s);
        for (int p = 0; p < gt.getGame().getNumPlayers(); p++) {
            sb.append("expected payoff for player ").append(p).append(" is ").append(epayoffs[p]).append('\n');
        }
        return sb.toString();
    }
    
}
