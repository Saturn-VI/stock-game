public class Stock {

	private String symbol;
	private String name;
	private double price;
	private final long totalShares; // total shares that exist for this stock - this is not changed, just used to give more weight to certain stock movements

	public Stock(String symbol, double price, long totalShares, String name) {
	    this.symbol = symbol;
		this.price = price;
		this.totalShares = totalShares;
		this.name = name;
	}

	public String getSymbol() {
	    return symbol;
	}

	public String getName() {
	    return name;
	}

	public double getPrice() {
	    return price;
	}

	public long getTotalShares() {
	    return totalShares;
	}

	public double getMarketCap() {
		return price * totalShares;
	}

    @Override
	public boolean equals(Object obj) {
	    if (obj instanceof Stock) {
			Stock s = (Stock) obj;
			return (this.getSymbol().equals(s.getSymbol()) &&
			        this.getName().equals(s.getName()) &&
			        this.getPrice() == s.getPrice() &&
					this.getTotalShares() == s.getTotalShares());
		}
		return false;
	}

    @Override
	public String toString() {
	    return String.format("%s $%.2f", symbol, price);
	}
}
