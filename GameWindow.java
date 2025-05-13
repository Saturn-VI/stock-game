import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class GameWindow {

    // private final int gridWidth = 12;
    // private final int gridHeight = 6;

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

        gameFrame.setSize(400, 400);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);
    }
}
