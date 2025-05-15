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


    /**
     * Buys a certain amount of shares.
     * If sharesToBuy > amount of shares that can be bought, it will throw an exception.
     * Adds the transaction into transactions (the transaction method)
     * @param stock the stock for which to buy shares
     * @param sharesToBuy the amount of shares to buy
     */

    /**
     * Sells a certain amount of shares.
     * If sharesToSell > the amount that is invested, it will throw an exception.
     * Adds the transaction into transactions (the transaction method)
     * @param stock the stock for which to sell shares
     * @param sharesToSell how many shares should be sold
     */

    public double initialMoney() { return initialMoney; }
}
