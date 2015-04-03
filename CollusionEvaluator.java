import java.util.List;
import java.util.Set;

public class CollusionEvaluator implements Evaluator {

    private final Evaluator eval;
    private final Set<Integer> team;
    private final boolean zeroSum;

    public CollusionEvaluator(Evaluator eval, Set<Integer> team) {
        this(eval, team, true);
    }

    public CollusionEvaluator(Evaluator eval, Set<Integer> team, boolean zeroSum) {
        this.eval = eval;
        this.team = team;
        this.zeroSum = zeroSum;
    }

    public double[] payoffs(List<List<Card>> holeCards, List<Card> sharedCards, double[] bets, boolean[] folds) {
        double[] payoffs = eval.payoffs(holeCards, sharedCards, bets, folds); 
        double totalWinnings = 0;
        for (int player : team) {
            totalWinnings += payoffs[player];
        }

        int players = folds.length;
        for (int p = 0; p < players; p++) {
            if (team.contains(p)) {
                payoffs[p] = zeroSum ? totalWinnings / team.size() : totalWinnings;
            }
        }
        return payoffs; 
    }
 
}
