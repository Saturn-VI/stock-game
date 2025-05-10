import java.util.ArrayList;

public abstract class AbstractTrader {

    private double money;
    private ArrayList<Investment> portfolio;

    public AbstractTrader() {
        money = 0.0;
        portfolio = new ArrayList<Investment>();
    }

    public AbstractTrader(double money) {
        this.money = money;
        portfolio = new ArrayList<Investment>();
    }

    public AbstractTrader(double money, ArrayList<Stock> stocks) {
        this.money = money;
        portfolio = new ArrayList<Investment>();
        for (Stock stock : stocks) {
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
     * Will spend all available money.
     * @param stock the stock for which to buy shares
     * @param sharesToBuy the amount of shares to buy
     */
    public void buyShares(Stock stock, int sharesToBuy) {
        Investment investment = getInvestmentForStock(stock);
        double[] purchaseReturn = investment.buyShares(sharesToBuy, money);
        money -= purchaseReturn[1];
    }

    /**
     * Sells a certain amount of shares.
     * If sharesToSell > the amount that is invested, it will sell all shares.
     * @param stock the stock for which to sell shares
     * @param sharesToSell how many shares should be sold
     */
    public void sellShares(Stock stock, int sharesToSell) {
        Investment investment = getInvestmentForStock(stock);
        money += investment.sellShares(sharesToSell);
    }
}
