public class PokerGames {
    
    private PokerGames() {
        // uninstantiable untility class
    }

    public static PokerGame newMHGame() {
        Deck deck = new Deck(5,4);
        int numPlayers = 2;
        PokerGame.Builder builder =
            PokerGame.newBuilder(deck, new FourCardEvaluator(), numPlayers);
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
        return builder.addRound(r1).addRound(r2).build();
    }

}
