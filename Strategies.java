import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

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
            return n.setPayoff(terminalpayoff(gt, n, strats, rplayer));
        }

        double[] cpayoffs = new double[children.size()];
        for (int i = 0; i < children.size(); i++) {
            cpayoffs[i] = payoff(gt, children.get(i), strats, rplayer);
        }

        double payoff = 0;
        for (int i = 0; i < cpayoffs.length; i++) {
            if (n instanceof DealNode) {
                DealNode dn = (DealNode) n;
                payoff += cpayoffs[i];
            } else if (n instanceof ActionNode) {
                ActionNode an = (ActionNode) n;
                int player = an.getPlayer();
                InfoSet iset = gt.getInfoSet(an);
                Strategy s = strats.get(player);
                if (player != rplayer) {
                    payoff += cpayoffs[i];
                } else if (player == rplayer) {
                    double[] tpayoffs = new double[cpayoffs.length];
                    Set<GameNode> nodes = getIndistNodes(gt, n, rplayer);
                    for (GameNode in : nodes) {
                        List<GameNode> cs = in.getChildren();
                        for (int j = 0; j < cs.size(); j++) {
                            double pf = payoff(gt, cs.get(j), strats, rplayer);
                            tpayoffs[j] += pf;
                        }
                    }
                    int aindex = Arrays.maxarg(tpayoffs);
                    s.setAction(iset, an.getAction(aindex));
                    payoff = cpayoffs[aindex];
                    break;
                }
            }
        }

        /*
         *for (GameNode in : getIndistNodes(gt, n, rplayer)) {
         *    in.setPayoff(payoff);
         *}
         */

        return n.setPayoff(payoff);
    }

    public static Set<GameNode> getIndistNodes(GameTree gt, GameNode n, int rplayer) {
        Set<GameNode> nodes;
        if (n instanceof ActionNode) {
            ActionNode an = (ActionNode) n;
            if (an.getPlayer() == rplayer) {
                nodes = gt.getInfoSet((ActionNode) n).getNodes();
                return nodes;
            }
        } 
        nodes = new HashSet<GameNode>();
        nodes.add(n);
        return nodes;
    }

    public static double terminalpayoff(GameTree gt, GameNode n, List<Strategy> strats, int player) {
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
                int play = dn.getPlayer();
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
            double take = PokerUtils.winner(holecards.get(player), holecards.get(1 - player), sharedcards);
            winnings = pot * take - contr;
        }

        double freq = freqWeight(gt, n, strats, player);
        return freq * winnings;
    }

    private static double freqWeight(GameTree gt, GameNode n, List<Strategy> strats, int rplayer) {
        GameNode p = n.getParent();
        int cindex = n.getChildIndex();
        if (p == null) {
            return 1.0;
        }

        double freq = 1.0;
        if (p instanceof DealNode) {
            DealNode dn = (DealNode) p;
            freq = dn.getFreq(cindex);
        } else if (p instanceof ActionNode) {
            ActionNode an = (ActionNode) p;
            if (an.getPlayer() != rplayer) {
                InfoSet iset = gt.getInfoSet(an);
                freq = strats.get(an.getPlayer()).getProb(iset, an.getAction(cindex));
            }
        }
        return freqWeight(gt, p, strats, rplayer) * freq;
    }

}
