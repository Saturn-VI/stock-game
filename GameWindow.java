import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.AbstractTableModel;

public class GameWindow {

    private JFrame gameFrame;
    private JPanel stockDisplayPanel, portfolioPanel;
    private JTabbedPane tabbedPane;

    public GameWindow() {
        setupGameFrame();
    }

    private void setupGameFrame() {
        gameFrame = new JFrame();
        gameFrame.setLayout(new BorderLayout());

        stockDisplayPanel = new StockDisplayPanel();
        portfolioPanel = new PortfolioPanel();

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("View stock", stockDisplayPanel);
        tabbedPane.addTab("Portfolio", portfolioPanel);

        gameFrame.add(tabbedPane);

        gameFrame.setSize(600, 600);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.pack();
        gameFrame.setMinimumSize(gameFrame.getSize());
        gameFrame.setVisible(true);
    }

    class PortfolioPanel extends JPanel {

        private JLabel moneyAmountLabel;
        private JScrollPane tableScrollPane;
        private JTable stockInfoTable;

        public PortfolioPanel() {
            super();
            this.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(10, 10, 10, 10);

            moneyAmountLabel = new JLabel("", SwingConstants.CENTER);
            TitledBorder moneyBorder;
            moneyBorder = BorderFactory.createTitledBorder("Money");
            moneyBorder.setTitleJustification(TitledBorder.CENTER);
            moneyAmountLabel.setBorder(moneyBorder);
            moneyAmountLabel.setPreferredSize(new Dimension(150, 50));

            tableScrollPane = new JScrollPane();
            tableScrollPane.setPreferredSize(new Dimension(400, 100));
            stockInfoTable = new JTable();
            stockInfoTable = new JTable(new CustomTableModel());
            stockInfoTable.setFillsViewportHeight(true);
            tableScrollPane.add(stockInfoTable);

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
                "$" + Market.getTraderMoneyAmount(Main.playerTraderId)
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
                for (String stockName : Market.getListOfStocksForTrader(
                    Main.playerTraderId
                )) {
                    Stock stock = Market.getStockByName(stockName);
                    long howManyPlayerOwns = Market.getSharesOwnedInStock(
                        Main.playerTraderId,
                        stock.getName()
                    );
                    double currentProfit = Market.getCurrentTraderProfitOnStock(
                        Main.playerTraderId,
                        stockName
                    );
                    tableData.add(
                        new TableStockInfo(
                            stock.getName(),
                            stock.getPrice(),
                            howManyPlayerOwns,
                            howManyPlayerOwns * stock.getPrice(),
                            currentProfit
                        )
                    );
                }
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
                switch (col) {
                    case 0:
                        // Name
                        break;
                    case 1:
                        // Price
                        break;
                    case 2:
                        // Shares Owned
                        break;
                    case 3:
                        // Holding Value
                        break;
                    case 4:
                        // Current Profit
                        break;
                }
                return null;
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
    }
}
