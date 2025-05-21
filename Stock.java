import java.util.*;

public class Stock {

    private String symbol;
    private String name;
    private ArrayList<Double> prices; // updates once per day
    private final long totalShares; // total shares that exist for this stock - this is not changed, just used to give more weight to certain stock movements

    public Stock(String symbol, double price, long totalShares, String name) {
        this.symbol = symbol;
        this.prices = new ArrayList<Double>();
        this.prices.add(price);
        this.totalShares = totalShares;
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    private void addPriceToHistory(double newPrice) {
        this.prices.add(newPrice);
    }

    public void setPrice(double newPrice) {
        addPriceToHistory(newPrice);
    }

    public double getPriceByDay(int day) throws DayDoesNotExistException {
        if (day >= prices.size()) {
            throw new DayDoesNotExistException("");
        }
        return prices.get(day);
    }

    public double getPrice() {
        return prices.get(prices.size() - 1);
    }

    public long getTotalShares() {
        return totalShares;
    }

    public double getMarketCap() {
        return getPrice() * totalShares;
    }

    public double getPriceChangeFromYesterday() {
        if (prices.size() < 2) {
            return 0;
        }
        return prices.get(prices.size() - 1) - prices.get(prices.size() - 2);
    }

    public double getPriceChangePercentFromYesterday() {
        if (prices.size() < 2) {
            return 0;
        }
        double prevPrice = prices.get(prices.size() - 2);
        if (prevPrice == 0.0) return 0.0;
        return (getPriceChangeFromYesterday() / prevPrice) * 100;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Stock) {
            Stock s = (Stock) obj;
            return (
                this.getSymbol().equals(s.getSymbol()) &&
                this.getName().equals(s.getName()) &&
                this.getPrice() == s.getPrice() &&
                this.getTotalShares() == s.getTotalShares()
            );
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getName(), getSymbol());
    }
}
