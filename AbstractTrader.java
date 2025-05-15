import java.util.ArrayList;

public abstract class AbstractTrader {

    private final double initialMoney;
    private final int traderId;

    public AbstractTrader(int traderId) {
        initialMoney = 10000;
        this.traderId = traderId;
    }

    public AbstractTrader(int traderId, double money) {
        this.initialMoney = money;
        this.traderId = traderId;
    }

    public double initialMoney() {
        return initialMoney;
    }
}
