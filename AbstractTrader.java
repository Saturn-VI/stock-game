public abstract class AbstractTrader {

    private final String name;
    private final double initialMoney;
    private final int traderId;

    public AbstractTrader(int traderId, String name) {
        initialMoney = 10000.0;
        this.traderId = traderId;
        this.name = name;
    }

    public AbstractTrader(int traderId, double money, String name) {
        this.initialMoney = money;
        this.traderId = traderId;
        this.name = name;
    }

    public double getInitialMoney() {
        return initialMoney;
    }

    public int getTraderId() {
        return traderId;
    }

    public String getName() {
        return name;
    }
}
