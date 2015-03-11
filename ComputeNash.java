import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComputeNash {

    public static void main (String[] args) {
        PokerGame game = PokerGames.newMHGame();
        Strategy nash = Strategies.nes(game, 10000);
        System.out.println(nash);
    }
    
}
