import javax.swing.*;
import java.awt.*;

public class PlayerSelectionFrame extends JFrame {

    public PlayerSelectionFrame() {

        setTitle("PAC-MAN");

        setSize(400, 350);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setResizable(false);

        JPanel panel = new JPanel();

        panel.setBackground(Color.BLACK);

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS));

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        40,
                        50,
                        40,
                        50));

        JLabel title = new JLabel("PAC-MAN");

        title.setForeground(Color.YELLOW);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        45));

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        JLabel select = new JLabel("SELECT PLAYERS");

        select.setForeground(Color.WHITE);

        select.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20));

        select.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        JButton onePlayer = new JButton("1 PLAYER");

        onePlayer.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20));

        onePlayer.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        JButton twoPlayers = new JButton("2 PLAYERS");

        twoPlayers.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20));

        twoPlayers.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        onePlayer.addActionListener(e -> {
            startGame(1);
        });

        twoPlayers.addActionListener(e -> {
            startGame(2);
        });

        panel.add(title);

        panel.add(
                Box.createVerticalStrut(20));

        panel.add(select);

        panel.add(
                Box.createVerticalStrut(25));

        panel.add(onePlayer);

        panel.add(
                Box.createVerticalStrut(15));

        panel.add(twoPlayers);

        add(panel);
    }

    private void startGame(int players) {

        dispose();

        JFrame gameFrame = new JFrame();

        PacMan pacman = new PacMan(players);

        if (players == 1) {
            gameFrame.setTitle(
                    "PAC-MAN - 1 Player");
        } else {
            gameFrame.setTitle(
                    "PAC-MAN - 2 Players");
        }

        gameFrame.setContentPane(
                pacman);

        gameFrame.pack();

        gameFrame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        gameFrame.setLocationRelativeTo(null);

        gameFrame.setResizable(false);

        gameFrame.setVisible(true);

        pacman.requestFocusInWindow();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            PlayerSelectionFrame frame = new PlayerSelectionFrame();

            frame.setVisible(true);
        });
    }
}