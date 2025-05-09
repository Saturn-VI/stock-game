public class Investment {
    private Stock stock;
    private int shares;

    public Investment(Stock stock, int shares) {
        this.stock = stock;
        this.shares = shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getShares() {
        return shares;
    }

    public Stock getStock() {
        return stock;
    }
}
