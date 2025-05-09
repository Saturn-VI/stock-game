public class Stock {

	private String symbol;
	private String name;
	private double price;
	private final long totalShares; // total shares that exist for this stock - this is not changed, just used to give more weight to certain stock movements

	public Stock(String symbol, String name, double price, long totalShares) {
	    this.symbol = symbol;
		this.name = name;
		this.price = price;
		this.totalShares = totalShares;
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

	public String toString() {
	    return String.format("%s $%.2f", symbol, price);
	}
}
