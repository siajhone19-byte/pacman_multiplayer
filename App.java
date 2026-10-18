
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            PacMan pacmanGame = new PacMan(2);

            JFrame frame = new JFrame("Pac-Man Multiplayer");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set the game panel as the frame's content
            frame.setContentPane(pacmanGame);

            // Automatically fit the frame to the game board
            frame.pack();

            // Prevent the user from resizing the game
            frame.setResizable(false);

            // Center the game window
            frame.setLocationRelativeTo(null);

            // Show the game
            frame.setVisible(true);

            // Start the game loop
            pacmanGame.requestFocusInWindow();
            pacmanGame.gameLoop.start();
        });
    }
}
