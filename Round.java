import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

public class Round {
    private final int numSharedCards;
    private final int numHoleCards;
    private final int maxBetsPerPlayer;
    private final double ante;
    private final double[] bets;

    public static class Builder {
        private Integer numSharedCards;
        private Integer numHoleCards;
        private Integer maxBetsPerPlayer;
        private Double ante;
        private Boolean canCheck;
        private List<Double> bets;

        private Builder() {
            bets = Lists.newArrayList();
        }

        public Builder setNumSharedCards(int numSharedCards) {
            Preconditions.checkArgument(numSharedCards >= 0, "num shared cards cannot be negative");
            this.numSharedCards = numSharedCards;
            return this;
        }

        public Builder setNumHoleCards(int numHoleCards) {
            Preconditions.checkArgument(numHoleCards >= 0, "num hole cards cannot be negative");
            this.numHoleCards = numHoleCards;
            return this;
        }

        public Builder setMaxBetsPerPlayer(int maxBetsPerPlayer) {
            Preconditions.checkArgument(numHoleCards >= 0, "max bets per player cannot be negative");
            this.maxBetsPerPlayer = maxBetsPerPlayer;
            return this;
        }

        public Builder setAnte(double ante) {
            Preconditions.checkArgument(ante >= 0, "ante cannot be negative");
            this.ante = ante;
            return this;
        }

        public Builder addBetOption(double bet) {
            Preconditions.checkArgument(bet >= 0, "bets cannot be negative");
            Preconditions.checkArgument(bet != 0, "cannot add a 0 value bet (use setCanCheck() instead)");
            bets.add(bet);
            return this;
        }

        public Builder noAnte() {
            ante = 0.0;
            return this;
        }

        public Builder noBetting() {
            canCheck = false;
            maxBetsPerPlayer = 0;
            ante = 0.0;
            bets.clear();
            return this;
        }

        public Builder noDeal() {
            numSharedCards = 0;
            numHoleCards = 0;
            return this;
        }

        public Builder noSharedCards() {
            numSharedCards = 0;
            return this;
        }

        public Builder noHoleCards() {
            numHoleCards = 0;
            return this;
        }

        public Builder canCheck(boolean canCheck) {
            this.canCheck = canCheck;
            return this;
        }

        public Round build() {
            Preconditions.checkNotNull(numSharedCards);
            Preconditions.checkNotNull(numHoleCards);
            Preconditions.checkNotNull(maxBetsPerPlayer);
            Preconditions.checkNotNull(ante);
            Preconditions.checkNotNull(canCheck);
            if (canCheck) {
                bets.add(0.0);
            }
            return new Round(numSharedCards, numHoleCards, maxBetsPerPlayer, ante, Doubles.toArray(bets));
        }
    }

    public Round(int numSharedCards, int numHoleCards, int maxBetsPerPlayer, double ante, double[] bets) {
        this.numSharedCards = numSharedCards;
        this.numHoleCards = numHoleCards;
        this.maxBetsPerPlayer = maxBetsPerPlayer;
        this.ante = ante;
        this.bets = bets;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getNumSharedCards() {
        return numSharedCards;
    }

    public int getNumHoleCards() {
        return numHoleCards;
    }

    public int getMaxBetsPerPlayer() {
        return maxBetsPerPlayer;
    }

    public double getAnte() {
        return ante;
    }

    public double[] getBets() {
        return bets;
    }

}
