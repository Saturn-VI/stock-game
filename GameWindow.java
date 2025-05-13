import java.awt.*;
import java.beans.Customizer;
import javax.swing.*;
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
        gameFrame.setVisible(true);
    }

    class PortfolioPanel extends JPanel {

        public PortfolioPanel() {
            super();
        }

        class CustomTableModel extends AbstractTableModel {

            private static final String[] columnNames = new String[] {
                "Name",
                "Price",
                "Shares Owned",
                "Holding Value",
            };

            public String getColumnName(int col) {
                return columnNames[col];
            }

            public int getRowCount() {
                // TODO: Get the number of stocks that a trader has shares in
                return 0;
            }

            public int getColumnCount() {
                return columnNames.length;
            }

            public Object getValueAt(int row, int col) {
                // TODO
                return null;
            }

            public boolean isCellEditable(int row, int col) {
                return false;
            }

            public void setValueAt(Object value, int row, int col) {
                // does nothing
            }
        }
    }

    class StockDisplayPanel extends JPanel {

        public StockDisplayPanel() {
            super();
        }
    }
}
