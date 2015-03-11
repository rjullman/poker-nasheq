import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

public class Strategies {

    private Strategies() {
        // uninstantiable utility class
    }

    public static Strategy nes(PokerGame game, int limit) {
        GameTree gt = game.buildGameTree();
        Strategy s = new Strategy(gt);
        for (int i = 1; i <= limit; i++) {
            /*
             *System.out.println("Iter " + i);
             *System.out.println(s);
             *System.out.println();
             */
            s.average(response(gt, s), (1.0/(i+1)));
        }
        return s;
    }

    private static Strategy response(GameTree gt, Strategy s) {
        List<Strategy> strats = new ArrayList<Strategy>();
        Strategy response = new Strategy(gt);
        strats.add(s);
        for (int p = 0; p < gt.getGame().getNumPlayers(); p++) {
            gt.reset();
            strats.add(p, response);
            payoff(gt, gt.getRoot(), strats, p);
            strats.remove(p);
        }
        return response;
    }

    private static double payoff(GameTree gt, GameNode n, List<Strategy> strats, int rplayer) {
        if (n.getPayoff() != null) {
            return n.getPayoff();
        }

        List<GameNode> children = n.getChildren();
        if (children.size() == 0) {
            return n.setPayoff(terminalPayoff(gt, n, strats, rplayer));
        }

        InfoSet iset = n.getInfoSet();
        double[][] cpayoffs = new double[iset.size()][children.size()];
        double[] tpayoffs = new double[children.size()];
        for (int i = 0; i < iset.size(); i++) {
            GameNode in = iset.get(i);
            List<GameNode> nchildren = in.getChildren();
            for (int cindex = 0; cindex < nchildren.size(); cindex++) {
                GameNode child = nchildren.get(cindex);
                double pf = payoff(gt, child, strats, rplayer);
                cpayoffs[i][cindex] += pf;
                tpayoffs[cindex] += pf;
            }
        }

        int player = n.getPlayer();

        // compute best action for a responding player
        int aindex = -1;
        if (player == rplayer) {
            Preconditions.checkArgument(n instanceof ActionNode, "responding player must have actions");
            ActionNode an = (ActionNode) n;
            aindex = Arrays.maxarg(tpayoffs);
            strats.get(player).setAction(iset, an.getAction(aindex));
        }

        // set payoffs for all nodes in the information set
        for (int i = 0; i < iset.size(); i++) {
            GameNode in = iset.get(i);
            double payoff = (player == rplayer) 
                ? cpayoffs[i][aindex] : Arrays.sum(cpayoffs[i]); 
            in.setPayoff(payoff);
        }

        return n.getPayoff();
    }

    public static double terminalPayoff(GameTree gt, GameNode n, List<Strategy> strats, int player) {
        PokerGame g = gt.getGame();
        double[] bets = new double[g.getNumPlayers()];
        boolean[] fold = new boolean[g.getNumPlayers()];
        double pot = 0;
        List<Card> sharedcards = new ArrayList<Card>();
        List<List<Card>> holecards = new ArrayList<List<Card>>();
        for (int i = 0; i < g.getNumPlayers(); i++) {
            holecards.add(new ArrayList<Card>());
            bets[i] = 1;
            pot += 1;
        }

        GameNode p = n;
        while (p.getParent() != null) {
            int cindex = p.getChildIndex();
            p = p.getParent();

            if (p instanceof DealNode) {
                DealNode dn = (DealNode) p;
                int play = dn.getForPlayer();
                if (dn.isHoleCards()) {
                    holecards.get(play).addAll(dn.getDeal(cindex));
                } else {
                    sharedcards.addAll(dn.getDeal(cindex));
                }
            } else if (p instanceof ActionNode) {
                ActionNode an = (ActionNode) p;
                fold[an.getPlayer()] = (an.getAction(cindex) == PlayerAction.FOLD);
                bets[an.getPlayer()] += an.getBet(cindex);
                pot += an.getBet(cindex);
            }
        }

        double winnings = 0;
        double contr = bets[player];
        boolean fp = fold[player];
        boolean fo = fold[1 - player];
        assert (fp == false || fo == false);
        if (fp) {
            winnings = -contr;
        } else if (fo) {
            winnings = pot - contr;
        } else {
            double take = g.getEvaluator().result(holecards, sharedcards).getShareOfPotForPlayer(player);
            winnings = pot * take - contr;
        }

        return freq(n, strats, player) * winnings;
    }

    private static double freq(GameNode n, List<Strategy> strats, int rplayer) {
        if (n.getFreq() != null) {
            return n.getFreq();
        }

        GameNode p = n.getParent();
        int cindex = n.getChildIndex();
        if (p == null) {
            return 1.0;
        }

        double f = 1.0;
        if (p instanceof DealNode) {
            DealNode dn = (DealNode) p;
            f = dn.getFreq(cindex);
        } else if (p instanceof ActionNode) {
            ActionNode an = (ActionNode) p;
            if (an.getPlayer() != rplayer) {
                InfoSet iset = an.getInfoSet();
                f = strats.get(an.getPlayer()).getProb(iset, an.getAction(cindex));
            }
        }
        return n.setFreq(freq(p, strats, rplayer) * f);
    }

}
