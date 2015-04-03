import java.util.Set;

public class PokerGames {
    
    private PokerGames() {
        // uninstantiable untility class
    }

    public static PokerGame newMHGame() {
        return newMHGame(2, false);
    }

    public static PokerGame newMHZeroSumCollusionGame(int numPlayers, boolean canCheck, Set<Integer> team) {
        Evaluator eval = new CollusionEvaluator(new FourCardEvaluator(), team, true);
        return newMHGameInternal(numPlayers, canCheck, eval);
    }

    public static PokerGame newMHNonZeroSumCollusionGame(int numPlayers, boolean canCheck, Set<Integer> team) {
        Evaluator eval = new CollusionEvaluator(new FourCardEvaluator(), team, false);
        return newMHGameInternal(numPlayers, canCheck, eval);
    }

    public static PokerGame newMHGame(int numPlayers, boolean canCheck) {
        return newMHGameInternal(numPlayers, canCheck, new FourCardEvaluator());
    }

    private static PokerGame newMHGameInternal(int numPlayers, boolean canCheck, Evaluator eval) {
        Deck deck = new Deck(5,4);
        PokerGame.Builder builder = PokerGame.newBuilder(deck, eval, numPlayers);
        Round r1 = Round.newBuilder()
                        .noSharedCards()
                        .setNumHoleCards(2)
                        .setMaxBetsPerPlayer(1)
                        .setAnte(1.0)
                        .addBetOption(4.0)
                        .canCheck(canCheck)
                        .build();
        /*
         *Round r2 = Round.newBuilder()
         *                .noHoleCards()
         *                .setNumSharedCards(2)
         *                .setMaxBetsPerPlayer(2)
         *                .noAnte()
         *                .addBetOption(4.0)
         *                .canCheck(canCheck)
         *                .build();
         */
        Round r2 = Round.newBuilder()
                        .noBetting()
                        .noHoleCards()
                        .setNumSharedCards(2)
                        .build();
        return builder.addRound(r1).addRound(r2).build();
    }

    public static PokerGame newNoSuitFourCardHoldem() {
        int numPlayers = 2;
        boolean canCheck = false;
        Deck deck = new Deck(13,4);
        PokerGame.Builder builder =
            PokerGame.newBuilder(deck, new FourCardEvaluator(), numPlayers);
        Round r1 = Round.newBuilder()
                        .noSharedCards()
                        .setNumHoleCards(2)
                        .setMaxBetsPerPlayer(1)
                        .setAnte(1.0)
                        .addBetOption(4.0)
                        .canCheck(canCheck)
                        .build();
        Round r2 = Round.newBuilder()
                        .noHoleCards()
                        .setNumSharedCards(1)
                        .noAnte()
                        .setMaxBetsPerPlayer(1)
                        .addBetOption(4.0)
                        .canCheck(canCheck)
                        .build();
        Round r3 = Round.newBuilder()
                        .noHoleCards()
                        .setNumSharedCards(1)
                        .noAnte()
                        .setMaxBetsPerPlayer(1)
                        .addBetOption(4.0)
                        .canCheck(canCheck)
                        .build();
        return builder.addRound(r1).addRound(r2).addRound(r3).build();
    }

}
