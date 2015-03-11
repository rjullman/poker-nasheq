public class PayoffNode extends SimpleGameNode {

    private final double[] payoffs;

    public PayoffNode(double[] payoffs) {
        this.payoffs = payoffs;
    }

    public double getPayoffForPlayer(int player) {
        return payoffs[player];
    }

}
