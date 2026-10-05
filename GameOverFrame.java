import javax.swing.*;
import java.awt.*;

public class GameOverFrame extends JFrame {

        public GameOverFrame(
                        int score,
                        int players) {

                setTitle("PAC-MAN - Game Over");
                setSize(400, 320);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
                setResizable(false);

                JPanel panel = new JPanel();
                panel.setBackground(new Color(5, 10, 36));
                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                panel.setBorder(
                                BorderFactory.createEmptyBorder(
                                                30,
                                                50,
                                                30,
                                                50));

                JLabel title = new JLabel("GAME OVER");

                title.setForeground(Color.RED);
                title.setFont(
                                new Font(
                                                "Arial",
                                                Font.BOLD,
                                                40));

                title.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JLabel scoreLabel = new JLabel(
                                "Score: " + score);

                scoreLabel.setForeground(Color.WHITE);
                scoreLabel.setFont(
                                new Font(
                                                "Arial",
                                                Font.BOLD,
                                                22));

                scoreLabel.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JButton tryAgain = new JButton("TRY AGAIN");

                tryAgain.setFont(
                                new Font(
                                                "Arial",
                                                Font.BOLD,
                                                20));

                tryAgain.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JButton exit = new JButton("EXIT");

                exit.setFont(
                                new Font(
                                                "Arial",
                                                Font.BOLD,
                                                20));

                exit.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                tryAgain.addActionListener(e -> {
                        dispose();

                        SwingUtilities.invokeLater(() -> {
                                new PacManMenu().setVisible(true);
                        });
                });

                exit.addActionListener(e -> {
                        System.exit(0);
                });

                panel.add(title);

                panel.add(
                                Box.createVerticalStrut(15));

                panel.add(scoreLabel);

                panel.add(
                                Box.createVerticalStrut(30));

                panel.add(tryAgain);

                panel.add(
                                Box.createVerticalStrut(15));

                panel.add(exit);

                add(panel);
        }
}