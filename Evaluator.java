import java.util.List;

public interface Evaluator {
    public double[] payoffs(List<List<Card>> holeCards, List<Card> sharedCards, double[] bets, boolean[] folds);
}
