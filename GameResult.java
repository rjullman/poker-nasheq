import java.util.Set;

public class GameResult {
    private Set<Integer> winners;

    public GameResult(Set<Integer> winners) {
        this.winners = winners;
    }

    public double getShareOfPotForPlayer(int player) {
        if (winners.contains(player)) {
            return 1.0 / winners.size();
        }
        return 0.0;
    }

}
