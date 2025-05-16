public abstract class AbstractTrader {

    private final double initialMoney;
    private final int traderId;

    public AbstractTrader(int traderId) {
        initialMoney = 10000.0;
        this.traderId = traderId;
    }

    public AbstractTrader(int traderId, double money) {
        this.initialMoney = money;
        this.traderId = traderId;
    }

    public int getId() {
        return traderId;
    }

    public int getTraderId() {
        return traderId;
    }
}
