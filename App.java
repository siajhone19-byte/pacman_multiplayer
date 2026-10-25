
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PacManMenu menu = new PacManMenu();
            menu.setVisible(true);
        });
    }
}
