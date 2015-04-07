import java.util.Set;

public class PokerGames {
    
    private PokerGames() {
        // uninstantiable untility class
    }

    // Standard SMH (Simple Mercer Holdem) and MMH (Multiround Mercer Holdem) Games
    public static PokerGame newSMHGame(int numPlayers) {
        return initSMHGameBuilder(numPlayers, new FourCardEvaluator()).build();
    }

    public static PokerGame newMMHGame(int numPlayers) {
        return initMMHGameBuilder(numPlayers, new FourCardEvaluator()).build();
    }

    // Games with weak/strong collusion
    public static PokerGame newSMHCollGame(int numPlayers, Set<Integer> team, boolean strongColl) {
        Evaluator eval = new CollusionEvaluator(new FourCardEvaluator(), team, true);
        PokerGame.Builder builder = initSMHGameBuilder(numPlayers, eval);
        if (strongColl) {
            builder.addStronglyColludingPlayers(team);
        }
        return builder.build();
    }

    public static PokerGame newMMHCollGame(int numPlayers, Set<Integer> team, boolean strongColl) {
        Evaluator eval = new CollusionEvaluator(new FourCardEvaluator(), team, true);
        PokerGame.Builder builder = initMMHGameBuilder(numPlayers, eval);
        if (strongColl) {
            builder.addStronglyColludingPlayers(team);
        }
        return builder.build();
    }

    // Base constructors
    private static PokerGame.Builder initSMHGameBuilder(int numPlayers, Evaluator eval) {
        Deck deck = new Deck(5,4);
        PokerGame.Builder builder = PokerGame.newBuilder(deck, eval, numPlayers);
        Round r1 = Round.newBuilder()
                        .noSharedCards()
                        .setNumHoleCards(2)
                        .setMaxBetsPerPlayer(1)
                        .setAnte(1.0)
                        .addBetOption(4.0)
                        .canCheck(false)
                        .build();
        Round r2 = Round.newBuilder()
                        .noBetting()
                        .noHoleCards()
                        .setNumSharedCards(2)
                        .build();
        return builder.addRound(r1).addRound(r2);
    }

    private static PokerGame.Builder initMMHGameBuilder(int numPlayers, Evaluator eval) {
        Deck deck = new Deck(5,4);
        PokerGame.Builder builder = PokerGame.newBuilder(deck, eval, numPlayers);
        Round r1 = Round.newBuilder()
                        .noSharedCards()
                        .setNumHoleCards(2)
                        .setMaxBetsPerPlayer(2)
                        .setAnte(1.0)
                        .addBetOption(4.0)
                        .canCheck(true)
                        .build();
        Round r2 = Round.newBuilder()
                        .noHoleCards()
                        .setNumSharedCards(2)
                        .setMaxBetsPerPlayer(2)
                        .noAnte()
                        .addBetOption(4.0)
                        .canCheck(true)
                        .build();
        return builder.addRound(r1).addRound(r2);
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
