/**
 * Transaction is an immutable data class that records(put not intended lmao) the
 * stuff being exchanged during a transaction. Shares is the number of shares
 * bought/sold, while price is the total price paid/received.
 * If shares is negative, it is sold. If shares is positive, it is a bought.
 * If price is negative, it is bought. If price is positive, it is sold.
 */
record Transaction(int shares, double price) {}

public class Investment {

    private Stock stock;
    private int shares;

    public Investment(Stock stock, int shares) {
        this.stock = stock;
        this.shares = shares;
    }

    /**
     * Sells a certain amount of shares.
     * If sharesToSell > the amount of shares in the investment, it will raise an exception.
     * @param sharesToSell the number of shares to sell
     * @return how much money was gained
     */
    public Transaction sellShares(int sharesToSell) {
        if (sharesToSell > shares) throw new IllegalArgumentException(
            "Cannot sell more shares than owned"
        );
        shares -= sharesToSell;
        return new Transaction(
            -1 * sharesToSell,
            stock.getPrice() * sharesToSell
        );
    }

    /**
     * Buys a certain amount of shares.
     * if sharesToBuy is more than can be bought with moneyAvailable, the maximum number possible will be bought.
     * @param sharesToBuy the number of shares to buy
     * @param moneyAvailable how much money can be spent on this purchase
     * @return 2-element array: first element is how many stocks were bought, second element is how much it cost
     */
    public Transaction buyShares(int sharesToBuy, double moneyAvailable) {
        if (sharesToBuy < 0) throw new IllegalArgumentException(
            "Cannot buy a negative number of shares."
        );

        int sharesThatCanBeBought = (int) (moneyAvailable / stock.getPrice());
        if (sharesToBuy > sharesThatCanBeBought) {
            throw new IllegalArgumentException(
                "Cannot buy more shares than can be bought with available money."
            );
        }
        return new Transaction(
            sharesToBuy,
            -1 * stock.getPrice() * sharesToBuy
        );
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
