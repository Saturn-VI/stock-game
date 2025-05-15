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

    public double getMoney() { return money; }
    
    public void setMoney(double m) {
        this.money = m;
    }

    public void changeMoney(double m) {
        this.money += m;
    }
}
