import java.io.PrintWriter;

import java.lang.StringBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ComputeNash {

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("usage: javac ComputeNash [1st coll player] [2nd coll player] [epsilon cutoff] [output prefix]");
            System.exit(1);
        }

/*
 *        double epsilon = Double.parseDouble(args[2]);
 *        PokerGame game = PokerGames.newMMHGame(2);
 *        GameTree gt = game.buildGameTree();
 *        Strategy s = Strategies.nes(gt, epsilon);
 *
 *        String pathPrefix = args[3];
 *        writeToFile(pathPrefix + ".nash", s.toString());
 *
 *        StringBuffer summary = new StringBuffer();
 *        summary.append(epsilon).append(" max regret\n")
 *               .append(getExpectedPayoffsStr(gt, s));
 *        writeToFile(pathPrefix + "-summary.payoffs", summary.toString());
 */

        int p1 = Integer.parseInt(args[0]);
        int p2 = Integer.parseInt(args[1]);
        int p3 = 3 - p1 - p2;

        double epsilon = Double.parseDouble(args[2]);
        Set<Integer> team = Sets.newHashSet(p1,p2);
        PokerGame gcol = PokerGames.newMMHCollGame(3, team, true);
        GameTree gtcol = gcol.buildGameTree();
        Strategy scol = Strategies.nes(gtcol, epsilon);

        PokerGame gnocol = PokerGames.newMMHGame(3);
        GameTree gtnocol = gnocol.buildGameTree();
        Strategy snocol = Strategies.nes(gtnocol, epsilon);

        Strategy smixedcol = buildStrategy(gtcol, scol, snocol, p3);
        Strategy smixednocol = buildStrategy(gtnocol, snocol, scol, p3);

        String pathPrefix = args[3];
        writeToFile(pathPrefix + "-no-collusion.nash", snocol.toString());
        writeToFile(pathPrefix + "-collusion.nash", scol.toString());
        writeToFile(pathPrefix + "-mixed-no-collusion.nash", smixednocol.toString());
        writeToFile(pathPrefix + "-mixed-collusion.nash", smixedcol.toString());

        StringBuffer summary = new StringBuffer();
        summary.append(epsilon).append(" max regret\n")
               .append('p').append(p1).append(" and p").append(p2).append(" versus p").append(p3).append('\n')
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
