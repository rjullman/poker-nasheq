import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

public class ComputeNash {

    public static void main(String[] args) {
        double epsilon = 0.1;
        Set<Integer> team = Sets.newHashSet(1,2);
        PokerGame game = PokerGames.newMHZeroSumCollusionGame(3, false, team);
        /*
         *PokerGame game = PokerGames.newMHGame(3, false);
         */

        GameTree gt = game.buildGameTree();
        Strategy nash = Strategies.nes(gt, epsilon);

        PokerGame g2 = PokerGames.newMHGame(3, false);
        GameTree gt2 = g2.buildGameTree();
        Strategy nash2 = Strategies.nes(gt2, epsilon);
        Strategy s = buildStrategy(gt, nash, nash2, 0);

        System.out.println(epsilon + " max regret");
        /*
         *System.out.println(nash);
         */
        System.out.println("Collusion");
        System.out.println(nash);
        System.out.println("No Collusion");
        System.out.println(nash2);
        System.out.println("Mixed");
        System.out.println(s);
        System.out.println("Collusion");
        printExpectedPayoffs(gt, nash);
        System.out.println("No Collusion");
        printExpectedPayoffs(gt2, nash2);
        System.out.println("Mixed");
        printExpectedPayoffs(gt, s);

        /*
         *printExpectedPayoffs(PokerGames.newMHZeroSumCollusionGame(3, false, team).buildGameTree(), nash);
         */
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

    public static void printExpectedPayoffs(GameTree gt, Strategy s) {
        double[] epayoffs = Strategies.expectedPayoffs(gt, s);
        for (int p = 0; p < gt.getGame().getNumPlayers(); p++) {
            System.out.println("expected payoff for player " + p + " is " + epayoffs[p]);
        }
    }
    
}
