import java.util.ArrayList;

public abstract class AbstractTrader {

    private double money;
    private final int traderId;

    public AbstractTrader(int traderId) {
        money = 0.0;
        this.traderId = traderId;
    }

    public AbstractTrader(int traderId, double money) {
        this.money = money;
        this.traderId = traderId;
    }
}
