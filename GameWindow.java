import java.awt.*;
import javax.swing.*;

public class GameWindow {

    public void initializeWindow() {
        SwingUtilities.invokeLater(() -> new GameFrame());
    }

    public static class GameFrame extends JFrame {

        private JPanel stockDisplayPanel, topStockPanel, portfolioPanel;
    }
}
