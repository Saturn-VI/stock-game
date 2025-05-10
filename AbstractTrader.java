import java.util.ArrayList;

public abstract class AbstractTrader {

    private double money;
    private ArrayList<Investment> portfolio = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public AbstractTrader() {
        money = 0.0;
    }

    public AbstractTrader(double money) {
        this.money = money;
    }

    public AbstractTrader(double money, ArrayList<Stock> stocksList) {
        this.money = money;
        for (Stock stock : stocksList) {
            portfolio.add(new Investment(stock, 0));
        }
    }

    private Investment getInvestmentForStock(Stock stock) {
        for (Investment investment : portfolio) {
            if (investment.getStock().equals(stock)) {
                return investment;
            }
        }
        // don't have an investment for that ticker in the portfolio yet, make one
        Investment investment = new Investment(stock, 0);
        portfolio.add(investment);
        return investment;
    }

    /**
     * Buys a certain amount of shares.
     * If sharesToBuy > amount of shares that can be bought, it will throw an exception.
     * Adds the transaction into transactions (the transaction method)
     * @param stock the stock for which to buy shares
     * @param sharesToBuy the amount of shares to buy
     */
    public void buyShares(Stock stock, int sharesToBuy) {
        Investment investment = getInvestmentForStock(stock);
        try {
            Transaction tr = investment.buyShares(sharesToBuy, money);
            money += tr.price(); 
            // it's  a += because the value of tr.price() will be negative when buying.
            transactions.add(tr);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * Sells a certain amount of shares.
     * If sharesToSell > the amount that is invested, it will throw an exception.
     * Adds the transaction into transactions (the transaction method)
     * @param stock the stock for which to sell shares
     * @param sharesToSell how many shares should be sold
     */
    public void sellShares(Stock stock, int sharesToSell) {
        Investment investment = getInvestmentForStock(stock);
        try {
            Transaction tr = investment.sellShares(sharesToSell);
            money += tr.price();
            transactions.add(tr);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}
