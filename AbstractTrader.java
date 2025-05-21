public abstract class AbstractTrader implements Comparable<AbstractTrader> {

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

    public abstract void simulateTraderDay();

    public double getInitialMoney() {
        return initialMoney;
    }

    public int getTraderId() {
        return traderId;
    }

    public String getName() {
        return name;
    }

    public int compareTo(AbstractTrader other) {
        return (int) (Market.getTraderMoneyAmount(getTraderId()) - Market.getTraderMoneyAmount(other.getTraderId()));
    }

    public String toString() {
        return String.format("%s: $%.2f (traderID: %d)", name, Market.getTraderMoneyAmount(traderId), traderId);
    }
}
