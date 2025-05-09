public class Investment {

    private Stock stock;
    private int shares;

    public Investment(Stock stock, int shares) {
        this.stock = stock;
        this.shares = shares;
    }

    /**
     * Sells a certain amount of shares.
     * If sharesToSell > the amount of shares in the investment, all shares will be sold.
     * @param sharesToSell the number of shares to sell
     * @return how much money was gained
     */
    public double sellShares(int sharesToSell) {
        if (sharesToSell > shares) return -1.0;
        shares -= sharesToSell;
        return stock.getPrice() * sharesToSell;
    }

    /**
     * Buys a certain amount of shares.
     * if sharesToBuy is more than can be bought with moneyAvailable, the maximum number possible will be bought.
     * @param sharesToBuy the number of shares to buy
     * @param moneyAvailable how much money can be spent on this purchase
     * @return 2-element array: first element is how many stocks were bought, second element is how much it cost
     */
    public double[] buyShares(int sharesToBuy, double moneyAvailable) {
        if (sharesToBuy <= 0) {
            return new double[] { 0.0, 0.0 };
        }
        int sharesThatCanBeBought = (int) (moneyAvailable / stock.getPrice());
        if (sharesThatCanBeBought == 0) {
            return new double[] { 0.0, 0.0 };
        }
        int sharesBought = Math.min(sharesToBuy, sharesThatCanBeBought);
        return new double[] { sharesBought, stock.getPrice() * sharesBought };
    }

    public double getTotalValue() {
        return shares * stock.getPrice();
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
