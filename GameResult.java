import java.util.Set;

public class GameResult {
    private double[] shares;

    public GameResult(double[] shares) {
        this.shares = shares;
    }

    public double getShareOfPotForPlayer(int player) {
        return shares[player];
    }

}
