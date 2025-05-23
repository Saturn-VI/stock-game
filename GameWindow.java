import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class GameWindow {

    private JFrame gameFrame;
    private HomePanel homePanel;
    private StockListPanel stockListPanel;
    private JScrollPane homeScrollPane;
    private StockDisplayPanel stockDisplayPanel;
    private PortfolioPanel portfolioPanel;
    private JTabbedPane tabbedPane;

    private static GameWindow singletonInstance;

    public GameWindow() {
        setupGameFrame();
    }

    private void setupGameFrame() {
        gameFrame = new JFrame("TradeMaster 2000");
        gameFrame.setLayout(new BorderLayout());

        homePanel = new HomePanel();
        stockListPanel = new StockListPanel();
        homeScrollPane = new JScrollPane(homePanel);
        stockDisplayPanel = new StockDisplayPanel();
        portfolioPanel = new PortfolioPanel();

        tabbedPane = new JTabbedPane();
        String[] tabNames = { "Home", "Stock List", "View stock", "Portfolio" };
        JComponent[] tabPanels = {
            homeScrollPane,
            stockListPanel,
            stockDisplayPanel,
            portfolioPanel,
        }; // Home will be added as scrollpane
        Font tabFont = FontFactory.getFont("Bold", 18);

        for (int i = 0; i < tabPanels.length; i++) {
            tabbedPane.addTab("", tabPanels[i]);
            JLabel tabLabel = new JLabel("", SwingConstants.CENTER);
            tabbedPane.setTabComponentAt(i, tabLabel);
            tabLabel.setText(tabNames[i]);
            tabLabel.setFont(tabFont);
            tabLabel.setPreferredSize(new Dimension(200, 30));
        }

        gameFrame.add(tabbedPane);

        gameFrame.setSize(1200, 1200);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.pack();
        gameFrame.setMinimumSize(
            new Dimension(
                (int) (gameFrame.getSize().getWidth() * 1.1),
                (int) (gameFrame.getSize().getHeight() * 1.1)
            )
        );

        updateData();

        ImageIcon icon = new ImageIcon("./assets/trademaster2ksmall.png");
        Image image = icon.getImage();
        gameFrame.setIconImage(image);

        gameFrame.setVisible(true);
    }

    public void goToStockDisplayPanel(String stock) {
        tabbedPane.setSelectedIndex(2);
        stockDisplayPanel.setViewStock(stock);
    }

    public void updateData() {
        if (homePanel != null && homePanel instanceof HomePanel) {
            homePanel.updateHomePanelData();
        }
        if (stockListPanel != null) {
            stockListPanel.updateStockListData();
        }
        if (stockDisplayPanel != null) {
            stockDisplayPanel.updateStockDisplayData();
        }
        if (portfolioPanel != null) {
            portfolioPanel.updatePortfolioInfo();
        }
    }

    public void triggerStockEvent(
        boolean isGood,
        String stockName,
        String eventMessage
    ) {
        if (
            Market.getListOfStocksForTrader(Main.playerTraderId).contains(
                stockName
            )
        ) {
            // the player owns this stock
            String message = String.format(
                "A stock you own (%s) has been affected by a major event:%n%s",
                stockName,
                String.format(
                    eventMessage,
                    Market.getStockByTicker(stockName).getName()
                )
            );
            JOptionPane.showMessageDialog(
                gameFrame,
                message,
                "TM2K Alert",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public static GameWindow getInstance() {
        if (singletonInstance == null) singletonInstance = new GameWindow();
        return singletonInstance;
    }

    class BaseFontCellRenderer extends DefaultTableCellRenderer {

        private final Font cellFont = FontFactory.getFont("Medium", 20);

        public BaseFontCellRenderer(boolean alignToRight) {
            super();
            if (alignToRight) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            setVerticalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
        ) {
            Component c = super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column
            );
            if (cellFont != null) {
                c.setFont(cellFont);
            }
            return c;
        }
    }

    class ChangeAndPercentageTableCellRenderer extends BaseFontCellRenderer {

        private boolean colorize;

        public ChangeAndPercentageTableCellRenderer(boolean colorize) {
            super(true);
            this.colorize = colorize;
        }

        @Override
        public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
        ) {
            Component c = super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column
            );

            // try to parse a double from it
            String[] splitString = value.toString().split(" ");
            Double change = 0.0;
            try {
                change = Double.parseDouble(splitString[0]);
            } catch (NullPointerException | NumberFormatException e) {
                // If it can't be parsed, just return the default
                c.setForeground(UIManager.getColor("Label.foreground"));
                return c;
            }
            if (colorize) {
                if (change > 0) {
                    c.setForeground(Color.GREEN.darker());
                } else if (change < 0) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(UIManager.getColor("Label.foreground"));
                }
            }
            return c;
        }
    }

    class CurrencyTableCellRenderer extends BaseFontCellRenderer {

        private boolean colorize;
        private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

        public CurrencyTableCellRenderer(boolean colorize) {
            super(true);
            this.colorize = colorize;
        }

        @Override
        public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
        ) {
            Component c = super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column
            );

            if (value instanceof Number) {
                setText(currencyFormat.format(value));
            } else {
                setText("$" + value.toString());
            }

            if (colorize && value instanceof Number) {
                double doubleValue = ((Number) value).doubleValue();
                if (doubleValue > 0) {
                    c.setForeground(Color.GREEN.darker());
                } else if (doubleValue < 0) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(UIManager.getColor("Label.foreground"));
                }
            } else {
                c.setForeground(UIManager.getColor("Label.foreground"));
            }

            return c;
        }
    }

    class HomePanel extends JPanel {

        private JLabel banner;
        private JLabel welcomeLabel;
        private JList<AbstractTrader> traderList;
        private JLabel currentDayLabel;
        private JButton simulateMarketDayButton;

        public HomePanel() {
            super();
            this.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(10, 10, 10, 10);

            ImageIcon icon = new ImageIcon("./assets/trademaster2k.png");
            Image image = icon
                .getImage()
                .getScaledInstance(333, 100, Image.SCALE_SMOOTH);
            banner = new JLabel(new ImageIcon(image));

            welcomeLabel = new JLabel(
                "Welcome to TradeMaster 2000!",
                SwingConstants.CENTER
            );
            welcomeLabel.setFont(FontFactory.getFont("Bold", 40));

            traderList = new JList<AbstractTrader>();
            traderList.setSelectionModel(
                new DefaultListSelectionModel() {
                    @Override
                    public void setSelectionInterval(int index0, int index1) {
                        // Disable selection
                    }
                }
            );
            traderList.setFocusable(false);
            traderList.setOpaque(false);
            traderList.setBackground(new Color(0, 0, 0, 0));
            traderList.setCellRenderer(new PlayerHighlighterListCellRenderer());
            TitledBorder traderListBorder;
            traderListBorder = BorderFactory.createTitledBorder("Traders");
            traderListBorder.setTitleJustification(TitledBorder.CENTER);
            traderListBorder.setTitleFont(FontFactory.getFont("Bold", 24));
            traderList.setFont(FontFactory.getFont("SemiBold", 18));
            traderList.setBorder(traderListBorder);

            currentDayLabel = new JLabel("", SwingConstants.CENTER);
            currentDayLabel.setFont(FontFactory.getFont("Bold", 24));

            simulateMarketDayButton = new JButton("Next Day");
            simulateMarketDayButton.setFont(FontFactory.getFont("Bold", 24));
            simulateMarketDayButton.addActionListener(e -> {
                Market.simulateMarketDay();
            });

            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            this.add(banner, constraints);

            constraints.gridy++;
            this.add(welcomeLabel, constraints);

            constraints.gridy++;
            constraints.gridx = 1;
            this.add(traderList, constraints);

            constraints.gridy++;
            constraints.gridx = 1;
            this.add(currentDayLabel, constraints);

            constraints.gridy++;
            constraints.gridx = 1;
            constraints.fill = GridBagConstraints.NONE;
            this.add(simulateMarketDayButton, constraints);
        }

        public void updateHomePanelData() {
            currentDayLabel.setText(
                String.format("Day %d", Market.getCurrentDay())
            );
            ArrayList<AbstractTrader> traders = Market.getListOfTraders();
            Collections.sort(traders);
            Collections.reverse(traders);
            traderList.setListData(traders.toArray(new AbstractTrader[0]));
        }

        class PlayerHighlighterListCellRenderer
            extends DefaultListCellRenderer {

            public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
                );
                if (value instanceof AbstractTrader) {
                    AbstractTrader trader = (AbstractTrader) value;
                    if (trader.getTraderId() == Main.playerTraderId) {
                        setBackground(Color.YELLOW);
                    } else {
                        setBackground(new Color(0, 0, 0, 0));
                    }
                }
                return this;
            }
        }
    }

    class StockListPanel extends JPanel {

        private JScrollPane stockListScrollPane;
        private JTable stockTable;

        public StockListPanel() {
            super();
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            stockTable = new JTable(new StockListPanelTableModel());
            JTableHeader tableHeader = stockTable.getTableHeader();
            tableHeader.setFont(FontFactory.getFont("SemiBold", 18));
            stockTable.setRowHeight(24);
            stockTable.setFillsViewportHeight(true);

            // Set custom renderers for columns
            // column 0: Name (regular, left align)
            stockTable
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(new BaseFontCellRenderer(false));
            // column 1: Price (no color)
            stockTable
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(new CurrencyTableCellRenderer(false));
            // column 2: Change vs yesterday (color)
            stockTable
                .getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                    new ChangeAndPercentageTableCellRenderer(true)
                );
            // column 3: Shares owned (no color, right align)
            stockTable
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(new BaseFontCellRenderer(true));

            stockTable.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        JTable target = (JTable) e.getSource();
                        int row = target.rowAtPoint(e.getPoint());
                        int column = target.columnAtPoint(e.getPoint());

                        if (row == -1 || column == -1) {
                            return;
                        }

                        String stockSymbol =
                            ((StockListPanelTableModel) stockTable.getModel()).getTickerForRow(
                                    row
                                );

                        GameWindow.getInstance().goToStockDisplayPanel(stockSymbol);
                    }
                }
            );

            stockListScrollPane = new JScrollPane(stockTable);

            this.add(stockListScrollPane);
        }

        public void updateStockListData() {
            ((StockListPanelTableModel) stockTable.getModel()).tableDataChanged();
        }

        class StockListPanelTableModel extends AbstractTableModel {

            ArrayList<StockListTableInfo> tableData;

            public StockListPanelTableModel() {
                tableData = new ArrayList<StockListTableInfo>();
            }

            private static final String[] columnNames = new String[] {
                "Stock",
                "Price",
                "Change vs Yesterday",
                "Shares Owned",
            };

            public String getTickerForRow(int row) {
                return tableData.get(row).stockSymbol();
            }

            public void tableDataChanged() {
                tableData.clear();
                try {
                    for (String stockName : Market.getStockNames()) {
                        Stock stock = Market.getStockByTicker(stockName);
                        tableData.add(
                            new StockListTableInfo(
                                stock.getName(),
                                stock.getSymbol(),
                                stock.getPrice(),
                                stock.getPriceChangeFromYesterday(),
                                stock.getPriceChangePercentFromYesterday(),
                                Market.getSharesOwnedInStock(
                                    Main.playerTraderId,
                                    stock.getSymbol()
                                )
                            )
                        );
                    }
                } catch (StockDoesNotExistException e) {}
                fireTableDataChanged();
            }

            public String getColumnName(int col) {
                return columnNames[col];
            }

            public int getRowCount() {
                return tableData.size();
            }

            public int getColumnCount() {
                return columnNames.length;
            }

            public Object getValueAt(int row, int col) {
                // Keep in mind that the list of stocks is alphabetical
                switch (col) {
                    case 0:
                        // Name
                        return Market.getStockByTicker(
                            tableData.get(row).stockSymbol()
                        ).toString();
                    case 1:
                        // Price
                        return tableData.get(row).stockPrice();
                    case 2:
                        // Change vs Yesterday
                        return String.format(
                            "%.2f (%.2f%%)",
                            tableData.get(row).changeFromYesterday(),
                            tableData.get(row).changePercent()
                        );
                    case 3:
                        // Shares Owned
                        return tableData.get(row).sharesInStock();
                }
                // Should never happen
                return null;
            }

            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return String.class;
                    case 1:
                        return Double.class;
                    case 2:
                        return Long.class;
                    case 3:
                        return Double.class;
                    case 4:
                        return Double.class;
                }
                return Object.class;
            }
        }
    }

    class StockDisplayPanel extends JPanel {

        private String currentStockSymbol = null;
        private JLabel nameLabel = new JLabel("Stock Name");
        private JPanel buttonContainer = new JPanel();
        private JButton buyButton = new JButton("Buy");
        private JButton sellButton = new JButton("Sell");
        private JButton buyAllButton = new JButton("Buy All");
        private JButton sellAllButton = new JButton("Sell All");
        private JLabel priceLabel = new JLabel("Price: ");
        private JLabel priceChangeLabel = new JLabel("Change: ");
        private JLabel marketCapLabel = new JLabel("Market Cap: ");
        private JLabel playerSharesLabel = new JLabel("Your Shares: ");
        private JLabel playerValueLabel = new JLabel("Your Holding Value: ");
        private JLabel playerProfitLabel = new JLabel("Your Profit: ");
        private JLabel volumeLabel = new JLabel("Volume (today): ");
        private JTable priceHistoryTable;
        private JTable transactionTable;
        private ArrayList<JLabel> jlabels = new ArrayList<JLabel>(
            Arrays.asList(
                priceLabel,
                priceChangeLabel,
                marketCapLabel,
                playerSharesLabel,
                playerValueLabel,
                playerProfitLabel,
                volumeLabel
            )
        );
        private JScrollPane priceHistoryScrollPane;
        private JScrollPane transactionScrollPane;

        public StockDisplayPanel() {
            super();
            this.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(10, 10, 10, 10);
            constraints.gridwidth = 2;
            constraints.anchor = GridBagConstraints.WEST;

            nameLabel.setFont(FontFactory.getFont("Bold", 32));

            buyButton.addActionListener(e -> {
                String dialogReturn = JOptionPane.showInputDialog(
                    gameFrame,
                    "How many shares do you want to buy?",
                    "TM2K Alert",
                    JOptionPane.QUESTION_MESSAGE
                );
                if (dialogReturn == null) {
                    return;
                }
                int sharesToBuy;
                try {
                    sharesToBuy = Integer.parseInt(dialogReturn);
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(
                        gameFrame,
                        "Please enter a valid integer.",
                        "TM2K Alert",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                try {
                    Market.buyShares(
                        Main.playerTraderId,
                        sharesToBuy,
                        currentStockSymbol
                    );
                } catch (NotEnoughMoneyException exception) {
                    JOptionPane.showMessageDialog(
                        gameFrame,
                        String.format("You do not have enough money to buy %d shares in this stock.", sharesToBuy),
                        "TM2K Alert",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            sellButton.addActionListener(e -> {
                String dialogReturn = JOptionPane.showInputDialog(
                    gameFrame,
                    "How many shares do you want to sell?",
                    "TM2K Alert",
                    JOptionPane.QUESTION_MESSAGE
                );
                if (dialogReturn == null) {
                    return;
                }
                int sharesToSell;
                try {
                    sharesToSell = Integer.parseInt(dialogReturn);
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(
                        gameFrame,
                        "Please enter a valid integer.",
                        "TM2K Alert",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                try {
                    Market.sellShares(
                        Main.playerTraderId,
                        sharesToSell,
                        currentStockSymbol
                    );
                } catch (NotEnoughSharesException exception) {
                    JOptionPane.showMessageDialog(
                        gameFrame,
                        String.format("You do not have %d shares in this stock.", sharesToSell),
                        "TM2K Alert",
                        JOptionPane.ERROR_MESSAGE
                    );
                } catch (StockDoesNotExistException exception) {
                    JOptionPane.showMessageDialog(
                        gameFrame,
                        "This stock does not exist. Something has gone *very* wrong.",
                        "TM2K Alert",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            buyAllButton.addActionListener(e -> {
                try {
                    Market.buyAllShares(
                        Main.playerTraderId,
                        currentStockSymbol
                    );
                } catch (NotEnoughMoneyException exception) {
                    JOptionPane.showMessageDialog(
                        gameFrame,
                        "You do not have enough money to buy this stock.",
                        "TM2K Alert",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            sellAllButton.addActionListener(e -> {
                try {
                    Market.sellAllShares(
                        Main.playerTraderId,
                        currentStockSymbol
                    );
                } catch (NotEnoughSharesException exception) {
                    JOptionPane.showMessageDialog(
                        gameFrame,
                        "You do not have any shares in this stock.",
                        "TM2K Alert",
                        JOptionPane.ERROR_MESSAGE
                    );
                } catch (StockDoesNotExistException exception) {
                    JOptionPane.showMessageDialog(
                        gameFrame,
                        "This stock does not exist. Something has gone *very* wrong.",
                        "TM2K Alert",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            buttonContainer.setLayout(new FlowLayout());
            buttonContainer.setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT
            );
            buyButton.setFont(FontFactory.getFont("Medium", 20));
            sellButton.setFont(FontFactory.getFont("Medium", 20));
            buyAllButton.setFont(FontFactory.getFont("Medium", 20));
            sellAllButton.setFont(FontFactory.getFont("Medium", 20));
            buttonContainer.add(buyButton);
            buttonContainer.add(sellButton);
            buttonContainer.add(buyAllButton);
            buttonContainer.add(sellAllButton);

            priceLabel.setFont(FontFactory.getFont("Bold", 28));

            priceChangeLabel.setFont(FontFactory.getFont("Medium", 20));

            marketCapLabel.setFont(FontFactory.getFont("Medium", 20));

            playerSharesLabel.setFont(FontFactory.getFont("Medium", 20));

            playerValueLabel.setFont(FontFactory.getFont("Medium", 20));

            playerProfitLabel.setFont(FontFactory.getFont("Medium", 20));

            volumeLabel.setFont(FontFactory.getFont("Medium", 20));

            ArrayList<JComponent> components = new ArrayList<JComponent>(
                Arrays.asList(
                    nameLabel,
                    buttonContainer,
                    priceLabel,
                    priceChangeLabel,
                    marketCapLabel,
                    playerSharesLabel,
                    playerValueLabel,
                    playerProfitLabel,
                    volumeLabel
                )
            );

            constraints.gridx = 0;
            for (int i = 0; i < components.size(); i++) {
                constraints.gridy++;
                this.add(components.get(i), constraints);
            }

            constraints.gridy++;
            priceHistoryTable = new JTable(
                new Object[0][0],
                new String[] { "Day", "Price" }
            );
            priceHistoryTable.setFont(FontFactory.getFont("Medium", 16));
            priceHistoryTable.setRowHeight(20);
            priceHistoryTable
                .getTableHeader()
                .setFont(FontFactory.getFont("SemiBold", 18));
            priceHistoryScrollPane = new JScrollPane(priceHistoryTable);
            TitledBorder priceHistoryBorder;
            priceHistoryBorder = BorderFactory.createTitledBorder(
                "Price History (last 10 days)"
            );
            priceHistoryBorder.setTitleFont(
                FontFactory.getFont("SemiBold", 18)
            );
            priceHistoryBorder.setTitleJustification(TitledBorder.CENTER);
            priceHistoryScrollPane.setBorder(priceHistoryBorder);
            priceHistoryScrollPane.setPreferredSize(new Dimension(300, 220));
            this.add(priceHistoryScrollPane, constraints);

            constraints.gridy++;
            transactionTable = new JTable(
                new Object[0][0],
                new String[] { "Day", "Trader", "Buy/Sell", "Shares", "Price" }
            );
            transactionTable.setFont(FontFactory.getFont("Medium", 16));
            transactionTable
                .getTableHeader()
                .setFont(FontFactory.getFont("SemiBold", 18));
            transactionTable.setRowHeight(20);
            transactionScrollPane = new JScrollPane(transactionTable);
            TitledBorder transactionTableBorder;
            transactionTableBorder = BorderFactory.createTitledBorder(
                "Recent Transactions"
            );
            transactionTableBorder.setTitleFont(
                FontFactory.getFont("SemiBold", 18)
            );
            transactionTableBorder.setTitleJustification(TitledBorder.CENTER);
            transactionScrollPane.setBorder(transactionTableBorder);
            transactionScrollPane.setPreferredSize(new Dimension(600, 220));
            this.add(transactionScrollPane, constraints);
        }

        public void setViewStock(String stockSymbol) {
            this.currentStockSymbol = stockSymbol;
            updateStockDisplayData();
        }

        private void noStock(String reason) {
            for (JLabel label : jlabels) {
                label.setText("");
            }
            buyButton.setVisible(false);
            sellButton.setVisible(false);
            buyAllButton.setVisible(false);
            sellAllButton.setVisible(false);
            nameLabel.setText(reason);
            priceHistoryTable.setModel(
                new NonEditableTableModel(
                    new Object[0][0],
                    new String[] { "Day", "Price" }
                )
            );
            transactionTable.setModel(
                new NonEditableTableModel(
                    new Object[0][0],
                    new String[] {
                        "Day",
                        "Trader",
                        "Buy/Sell",
                        "Shares",
                        "Price",
                    }
                )
            );
        }

        public void updateStockDisplayData() {
            if (currentStockSymbol == null) {
                noStock("No stock selected");
                return;
            }
            try {
                Stock stock = Market.getStockByTicker(currentStockSymbol);
                if (stock == null) {
                    noStock("Stock not found");
                    return;
                }
                nameLabel.setText(
                    String.format("%s (%s)", stock.getName(), stock.getSymbol())
                );
                buyButton.setVisible(true);
                sellButton.setVisible(true);
                buyAllButton.setVisible(true);
                sellAllButton.setVisible(true);
                double price = stock.getPrice();
                priceLabel.setText(String.format("Price: $%.2f", price));

                double change = stock.getPriceChangeFromYesterday();

                priceChangeLabel.setText(
                    String.format(
                        "Change from yesterday: %+.2f (%+.2f%%)",
                        change,
                        stock.getPriceChangePercentFromYesterday()
                    )
                );
                if (change > 0) {
                    priceChangeLabel.setForeground(Color.GREEN.darker());
                } else if (change < 0) {
                    priceChangeLabel.setForeground(Color.RED);
                } else {
                    priceChangeLabel.setForeground(
                        UIManager.getColor("Label.foreground")
                    );
                }
                NumberFormat shortFormatter =
                    NumberFormat.getCompactNumberInstance(
                        Locale.US,
                        NumberFormat.Style.SHORT
                    );
                NumberFormat longFormatter =
                    NumberFormat.getCompactNumberInstance(
                        Locale.US,
                        NumberFormat.Style.SHORT
                    );
                shortFormatter.setMinimumFractionDigits(2);
                longFormatter.setMinimumFractionDigits(2);
                marketCapLabel.setText(
                    String.format(
                        "Market Cap: $%s (~%s total shares)",
                        shortFormatter.format(stock.getMarketCap()),
                        longFormatter.format(stock.getTotalShares())
                    )
                );

                // Player info
                long sharesOwned = 0;
                double holdingValue = 0;
                double profit = 0;
                try {
                    sharesOwned = Market.getSharesOwnedInStock(
                        Main.playerTraderId,
                        stock.getSymbol()
                    );
                    holdingValue = sharesOwned * price;
                    profit = Market.getCurrentTraderProfitOnStock(
                        Main.playerTraderId,
                        stock.getSymbol()
                    );
                } catch (StockDoesNotExistException e) {
                    e.printStackTrace();
                }
                playerSharesLabel.setText("Your Shares: " + sharesOwned);
                playerValueLabel.setText(
                    String.format("Your Holding Value: $%.2f", holdingValue)
                );
                playerProfitLabel.setText(
                    String.format("Your Overall Profit: $%.2f", profit)
                );

                // Calculate volume for today
                int volume = 0;
                ArrayList<Transaction> transactions = Market.copyTransactions();
                transactions = Market.filterByStock(
                    stock.getSymbol(),
                    transactions
                );
                for (Transaction t : Market.filterByDays(0, transactions)) { // gets transactions made today
                    volume += t.shares();
                }
                volumeLabel.setText(
                    String.format("Volume (today): %d shares", volume)
                );

                // Price history (last 10 days)
                int days = (int) Market.getCurrentDay() + 1;
                int historyLen = Math.min(days, 10);
                Object[][] priceHistoryData = new Object[historyLen][2];
                for (int i = 0; i < historyLen; i++) {
                    int dayIdx = days - historyLen + i;
                    double dayPrice = price;
                    try {
                        dayPrice = stock.getPriceByDay(dayIdx);
                    } catch (Exception e) {}
                    priceHistoryData[i][0] = "Day " + dayIdx;
                    priceHistoryData[i][1] = String.format("$%.2f", dayPrice);
                }
                priceHistoryTable.setModel(
                    new NonEditableTableModel(
                        priceHistoryData,
                        new String[] { "Day", "Price" }
                    )
                );

                // Recent transactions (last 10 for this stock)
                ArrayList<Transaction> stockTransactions = new ArrayList<>();
                ArrayList<Transaction> allTransactions =
                    Market.copyTransactions();
                for (Transaction t : allTransactions) {
                    if (
                        t.stock() != null &&
                        t.stock().getSymbol().equals(stock.getSymbol())
                    ) {
                        stockTransactions.add(t);
                    }
                }

                // Sort by day, most recent first
                stockTransactions.sort((a, b) ->
                    Long.compare(b.dayTransactionMade(), a.dayTransactionMade())
                );

                // Take only the first 10
                if (stockTransactions.size() > 10) {
                    stockTransactions = new ArrayList<>(
                        stockTransactions.subList(0, 10)
                    );
                }

                Object[][] txData = new Object[stockTransactions.size()][5];
                for (int i = 0; i < stockTransactions.size(); i++) {
                    Transaction t = stockTransactions.get(i);
                    txData[i][0] = "Day " + t.dayTransactionMade();
                    AbstractTrader trader = Market.getTraderById(t.traderId());
                    txData[i][1] = trader != null
                        ? trader.getName()
                        : ("Trader " + t.traderId());
                    txData[i][2] = t.selling() ? "Sell" : "Buy";
                    txData[i][3] = t.shares();
                    txData[i][4] = String.format("$%.2f", t.price());
                }
                transactionTable.setModel(
                    new NonEditableTableModel(
                        txData,
                        new String[] {
                            "Day",
                            "Trader",
                            "Buy/Sell",
                            "Shares",
                            "Price",
                        }
                    )
                );
            } catch (Exception e) {
                e.printStackTrace();
                noStock("Error loading stock data: " + e.getMessage());
            }
        }

        class NonEditableTableModel extends DefaultTableModel {

            public NonEditableTableModel(
                Object[][] data,
                String[] columnNames
            ) {
                super(data, columnNames);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        }
    }

    class PortfolioPanel extends JPanel {

        private JLabel moneyAmountLabel;
        private JScrollPane tableScrollPane;
        private JTable stockInfoTable;

        public PortfolioPanel() {
            super();
            this.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(20, 20, 20, 20);

            moneyAmountLabel = new JLabel("", SwingConstants.CENTER);
            TitledBorder moneyBorder;
            moneyBorder = BorderFactory.createTitledBorder("Money");
            moneyBorder.setTitleJustification(TitledBorder.CENTER);
            moneyBorder.setTitleFont(FontFactory.getFont("Bold", 24));
            moneyAmountLabel.setBorder(moneyBorder);
            moneyAmountLabel.setFont(FontFactory.getFont("ExtraBold", 60));
            moneyAmountLabel.setPreferredSize(new Dimension(450, 150));

            tableScrollPane = new JScrollPane();
            tableScrollPane.setPreferredSize(new Dimension(800, 200));
            stockInfoTable = new JTable(new CustomTableModel());

            // Set custom renderers for columns
            // column 0: Name (regular, left align)
            stockInfoTable
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(new BaseFontCellRenderer(false));
            // column 1: Price (no color)
            stockInfoTable
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(new CurrencyTableCellRenderer(false));
            // column 2: Shares Owned (no color, right align)
            stockInfoTable
                .getColumnModel()
                .getColumn(2)
                .setCellRenderer(new BaseFontCellRenderer(true));
            // column 3: Holding Value (no color)
            stockInfoTable
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(new CurrencyTableCellRenderer(false));
            // column 4: Current Profit (coloring)
            stockInfoTable
                .getColumnModel()
                .getColumn(4)
                .setCellRenderer(new CurrencyTableCellRenderer(true));

            JTableHeader tableHeader = stockInfoTable.getTableHeader();
            tableHeader.setFont(FontFactory.getFont("SemiBold", 18));

            stockInfoTable.setPreferredScrollableViewportSize(
                new Dimension(800, 20)
            );
            stockInfoTable.setRowHeight(24);
            stockInfoTable.setFillsViewportHeight(true);
            stockInfoTable.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        JTable target = (JTable) e.getSource();
                        int row = target.rowAtPoint(e.getPoint());
                        int column = target.columnAtPoint(e.getPoint());

                        if (row == -1 || column == -1) {
                            return;
                        }

                        String stockSymbol =
                            ((CustomTableModel) stockInfoTable.getModel()).getTickerForRow(
                                    row
                                );
                        GameWindow.getInstance().goToStockDisplayPanel(stockSymbol);
                    }
                }
            );

            tableScrollPane = new JScrollPane(stockInfoTable);
            tableScrollPane.setPreferredSize(new Dimension(900, 250));

            constraints.gridx = 2;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            this.add(moneyAmountLabel, constraints);

            constraints.gridx = 0;
            constraints.gridy++;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridwidth = 3;
            constraints.gridheight = 2;
            this.add(tableScrollPane, constraints);
        }

        public void updatePortfolioInfo() {
            moneyAmountLabel.setText(
                String.format(
                    "$%.2f",
                    Market.getTraderMoneyAmount(Main.playerTraderId)
                )
            );
            CustomTableModel tableModel =
                ((CustomTableModel) stockInfoTable.getModel());
            tableModel.tableDataChanged();
        }

        class CustomTableModel extends AbstractTableModel {

            ArrayList<TableStockInfo> tableData;

            public CustomTableModel() {
                tableData = new ArrayList<TableStockInfo>();
            }

            private static final String[] columnNames = new String[] {
                "Name",
                "Price",
                "Shares Owned",
                "Holding Value",
                "Current Profit",
            };

            public String getTickerForRow(int row) {
                return tableData.get(row).stockSymbol();
            }

            public void tableDataChanged() {
                tableData.clear();
                ArrayList<String> ownedStocks = Market.getListOfStocksForTrader(
                    Main.playerTraderId
                );
                try {
                    for (String stockName : ownedStocks) {
                        Stock stock = Market.getStockByTicker(stockName);
                        long howManyPlayerOwns = Market.getSharesOwnedInStock(
                            Main.playerTraderId,
                            stock.getSymbol()
                        );
                        double currentProfit =
                            Market.getCurrentTraderProfitOnStock(
                                Main.playerTraderId,
                                stockName
                            );
                        tableData.add(
                            new TableStockInfo(
                                stock.getName(),
                                stock.getSymbol(),
                                stock.getPrice(),
                                howManyPlayerOwns,
                                howManyPlayerOwns * stock.getPrice(),
                                currentProfit
                            )
                        );
                    }
                } catch (StockDoesNotExistException e) {}
                fireTableDataChanged();
            }

            public String getColumnName(int col) {
                return columnNames[col];
            }

            public int getRowCount() {
                return tableData.size();
            }

            public int getColumnCount() {
                return columnNames.length;
            }

            public Object getValueAt(int row, int col) {
                // Keep in mind that the list of stocks is alphabetical
                switch (col) {
                    case 0:
                        // Name
                        return String.format(
                            "%s (%s)",
                            tableData.get(row).stockName(),
                            tableData.get(row).stockSymbol()
                        );
                    case 1:
                        // Price
                        return tableData.get(row).stockPrice();
                    case 2:
                        // Shares Owned
                        return tableData.get(row).sharesOwned();
                    case 3:
                        // Holding Value
                        return tableData.get(row).holdingValue();
                    case 4:
                        // Current Profit
                        return tableData.get(row).currentProfit();
                }
                // Should never happen
                return null;
            }

            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return String.class;
                    case 1:
                        return Double.class;
                    case 2:
                        return Long.class;
                    case 3:
                        return Double.class;
                    case 4:
                        return Double.class;
                }
                return Object.class;
            }
        }
    }
}
