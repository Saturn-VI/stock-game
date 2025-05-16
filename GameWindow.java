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
    private StockDisplayPanel stockDisplayPanel;
    private PortfolioPanel portfolioPanel;
    private JTabbedPane tabbedPane;

    public GameWindow() {
        setupGameFrame();
    }

    private void setupGameFrame() {
        gameFrame = new JFrame("TradeMaster 2000");
        gameFrame.setLayout(new BorderLayout());

        homePanel = new HomePanel();
        stockDisplayPanel = new StockDisplayPanel();
        portfolioPanel = new PortfolioPanel();

        tabbedPane = new JTabbedPane();
        String[] tabNames = { "Home", "View stock", "Portfolio" };
        JPanel[] tabPanels = { homePanel, stockDisplayPanel, portfolioPanel };
        Font tabFont = FontFactory.getFont("Bold", 18);

        for (int i = 0; i < 3; i++) {
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
        gameFrame.setVisible(true);
    }

    public void goToStockDisplayPanel(String stock) {
        tabbedPane.setSelectedIndex(1);
        stockDisplayPanel.setViewStock(stock);
    }

    public void updatePortfolioInfo() {
        portfolioPanel.updatePortfolioInfo();
    }

    class BaseFontCellRenderer extends DefaultTableCellRenderer {

        private final Font cellFont = FontFactory.getFont("Medium", 14);

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

        public HomePanel() {
            super();
            this.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(10, 10, 10, 10);
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
            moneyBorder.setTitleFont(FontFactory.getFont("Bold", 18));
            moneyAmountLabel.setBorder(moneyBorder);
            moneyAmountLabel.setFont(FontFactory.getFont("ExtraBold", 48));
            moneyAmountLabel.setPreferredSize(new Dimension(300, 100));

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
            Font headerFont = FontFactory.getFont("SemiBold", 16);
            if (headerFont != null) {
                tableHeader.setFont(headerFont);
            }

            stockInfoTable.setPreferredScrollableViewportSize(
                new Dimension(800, 20)
            );
            stockInfoTable.setFillsViewportHeight(true);
            stockInfoTable.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            JTable target = (JTable) e.getSource();
                            int row = target.rowAtPoint(e.getPoint());
                            int column = target.columnAtPoint(e.getPoint());

                            if (row != -1 && column != -1) {
                                System.out.println(
                                    "Clicked on cell: (" +
                                    row +
                                    ", " +
                                    column +
                                    ")"
                                );
                                System.out.println(
                                    "Cell value: " +
                                    stockInfoTable.getValueAt(row, column)
                                );
                            }
                        }
                    }
                }
            );

            tableScrollPane = new JScrollPane(stockInfoTable);
            tableScrollPane.setPreferredSize(new Dimension(800, 200));

            constraints.gridx = 2;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            this.add(moneyAmountLabel, constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.gridwidth = 3;
            constraints.gridheight = 2;
            this.add(tableScrollPane, constraints);

            updatePortfolioInfo();
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

            public void tableDataChanged() {
                tableData.clear();
                ArrayList<String> ownedStocks = Market.getListOfStocksForTrader(
                    Main.playerTraderId
                );
                try {
                    for (String stockName : ownedStocks) {
                        Stock stock = Market.getStockByName(stockName);
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
                System.out.println(tableData);
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

    class StockDisplayPanel extends JPanel {

        public StockDisplayPanel() {
            super();
            this.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(10, 10, 10, 10);
        }

        public void setViewStock(String stockName) {
            // TODO
            // Takes a stock symbol (String) and sets the data to show this stock

        }
    }
}
