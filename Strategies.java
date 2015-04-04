import java.lang.Runtime;

import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class Strategies {

    private Strategies() {
        // uninstantiable utility class
    }

    private static class IterState {
        double expectedPayoffs[];
        double maxRegret;

        public IterState(int numPlayers) {
            expectedPayoffs = new double[numPlayers];
            maxRegret = Double.MAX_VALUE;
        }

        public void setExpectedPayoff(int player, double payoff) {
            expectedPayoffs[player] = payoff;
        }

        public double[] getExpectedPayoffs() {
            return expectedPayoffs;
        }

        public void setMaxRegret(double maxRegret) {
            this.maxRegret = maxRegret;
        }

        public double getMaxRegret() {
            return maxRegret;
        }
    }

    public static double[] expectedPayoffs(GameTree gt, Strategy s) {
        List<Strategy> strats = Lists.newArrayList();
        for (int p = 0; p < gt.getGame().getNumPlayers(); p++) {
            strats.add(s);
        }

        ForkJoinPool exec = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        int numPlayers = gt.getGame().getNumPlayers();
        double[] epayoffs = new double[numPlayers];
        for (int p = 0; p < numPlayers; p++) {
            gt.reset();
            epayoffs[p] = exec.invoke(new PayoffTask(gt, gt.getRoot(), strats, p, false));
        }
        return epayoffs;
    }

    public static Strategy nes(GameTree gt, double epsilon) {
        Strategy s = new Strategy(gt);
        PokerGame game = gt.getGame();
        IterState state = new IterState(game.getNumPlayers());
        ForkJoinPool exec = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        int iter = 0;

        while (state.getMaxRegret() >= epsilon) {
            iter++;
            s.average(response(gt, s, state, exec), (1.0/(iter+1)));
            System.out.println(state.getMaxRegret());
        }
        System.out.println(iter + " iterations");
        return s;
    }

    private static Strategy response(GameTree gt, Strategy s, IterState state, ForkJoinPool exec) {
        List<Strategy> strats = Lists.newArrayList();
        Strategy response = new Strategy(gt);
        for (int p = 1; p < gt.getGame().getNumPlayers(); p++) {
            strats.add(s);
        }

        double maxRegret = 0;
        for (int p = 0; p < gt.getGame().getNumPlayers(); p++) {
            gt.reset();
            strats.add(p, s);
            double prePayoff = exec.invoke(new PayoffTask(gt, gt.getRoot(), strats, p, false));
            strats.remove(p);

            gt.reset();
            strats.add(p, response);
            double postPayoff = exec.invoke(new PayoffTask(gt, gt.getRoot(), strats, p, true));
            strats.remove(p);

            assert postPayoff >= prePayoff;
            maxRegret = Math.max(maxRegret, postPayoff - prePayoff);
            state.setExpectedPayoff(p, prePayoff);
        }

        state.setMaxRegret(maxRegret);
        return response;
    }

    public static class PayoffTask extends RecursiveTask<Double> {
        private final GameTree gt;
        private final GameNode n;
        private final List<Strategy> strats;
        private final int rplayer;
        private final boolean compBestResp;

        public PayoffTask(GameTree gt, GameNode n, List<Strategy> strats, int rplayer, boolean compBestResp) {
            this.gt = gt;
            this.n = n;
            this.strats = strats;
            this.rplayer = rplayer;
            this.compBestResp = compBestResp;
        }

        @Override
        protected Double compute() {
            if (n.getPayoff() != null) {
                return n.getPayoff();
            }

            List<GameNode> children = n.getChildren();
            if (children.size() == 0) {
                return n.setPayoff(terminalPayoff(n, strats, rplayer, compBestResp));
            }

            InfoSet iset = n.getInfoSet();
            PayoffTask[][] tasks = new PayoffTask[iset.size()][children.size()];
            List<PayoffTask> allTasks = Lists.newArrayList();
            double[][] cpayoffs = new double[iset.size()][children.size()];
            double[] tpayoffs = new double[children.size()];
            for (int i = 0; i < iset.size(); i++) {
                GameNode in = iset.get(i);
                List<GameNode> nchildren = in.getChildren();
                for (int cindex = 0; cindex < nchildren.size(); cindex++) {
                    GameNode child = nchildren.get(cindex);
                    PayoffTask pt = new PayoffTask(gt, child, strats, rplayer, compBestResp);
                    tasks[i][cindex] = pt;
                    allTasks.add(pt);
                }
            }

            invokeAll(allTasks);

            for (int i = 0; i < tasks.length; i++) {
                for (int cindex = 0; cindex < tasks[0].length; cindex++) {
                    double pf = tasks[i][cindex].join();
                    cpayoffs[i][cindex] += pf;
                    tpayoffs[cindex] += pf;
                }
            }

            int player = n.getPlayer();

            // compute best action for a responding player
            int aindex = -1;
            if (player == rplayer && compBestResp) {
                Preconditions.checkArgument(n instanceof ActionNode, "responding player must have actions");
                ActionNode an = (ActionNode) n;
                aindex = Arrays.maxarg(tpayoffs);
                strats.get(player).setAction(iset, an.getBet(aindex));
            }

            // set payoffs for all nodes in the information set
            for (int i = 0; i < iset.size(); i++) {
                GameNode in = iset.get(i);
                double payoff = (player == rplayer && compBestResp)  
                    ? cpayoffs[i][aindex] : Arrays.sum(cpayoffs[i]); 
                in.setPayoff(payoff);
            }

            return n.getPayoff();
        }

        public double terminalPayoff(GameNode n, List<Strategy> strats, int player, boolean compBestResp) {
            Preconditions.checkArgument(n instanceof PayoffNode, "terminal node must be a PayoffNode");
            double payoff = ((PayoffNode) n).getPayoffForPlayer(player);
            return freq(n, strats, player, compBestResp) * payoff;
        }

        private double freq(GameNode n, List<Strategy> strats, int rplayer, boolean compBestResp) {
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
                if (an.getPlayer() != rplayer || !compBestResp) {
                    InfoSet iset = an.getInfoSet();
                    f = strats.get(an.getPlayer()).getProb(iset, an.getBet(cindex));
                }
            }
            return n.setFreq(freq(p, strats, rplayer, compBestResp) * f);
        }

    }

}
