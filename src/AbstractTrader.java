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
}
