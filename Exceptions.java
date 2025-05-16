class NotEnoughMoneyException extends Exception {

    public NotEnoughMoneyException(String m) {
        super(m);
    }
}

class NotEnoughSharesException extends Exception {

    public NotEnoughSharesException(String m) {
        super(m);
    }
}

class TraderDoesNotExistException extends Exception {

    public TraderDoesNotExistException(String m) {
        super(m);
    }
}

class StockDoesNotExistException extends Exception {

    public StockDoesNotExistException(String m) {
        super(m);
    }
}
