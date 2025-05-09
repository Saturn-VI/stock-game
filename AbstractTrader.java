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

    public AbstractTrader(double money, Stock[] stocks) {
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

    // TODO
    public void buyShares(Stock stock, int sharesToBuy) {

    }

    // TODO
    public void sellShares(Stock stock, int sharesToSell) {

    }
}
