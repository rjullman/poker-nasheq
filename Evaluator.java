import java.util.List;

public interface Evaluator {
    public GameResult result(List<List<Card>> holeCards, List<Card> sharedCards, boolean[] folds);
}
