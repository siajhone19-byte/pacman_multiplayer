import javax.swing.*;
import java.awt.*;

public class PacManMenu extends JFrame {

        public PacManMenu() {
                setTitle("PAC-MAN");
                setSize(600, 600);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
                setResizable(false);
                showMainMenu();
        }

        private void showMainMenu() {
                getContentPane().removeAll();

                JPanel panel = new JPanel() {
                        Image background = new ImageIcon("PacManBackground.jpg").getImage();

                        @Override
                        protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                        }
                };

                panel.setLayout(new GridLayout(4, 1, 20, 20));
                panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

                JLabel title = new JLabel("PAC-MAN", SwingConstants.CENTER);
                title.setForeground(Color.YELLOW);
                title.setFont(new Font("Arial", Font.BOLD, 45));

                JButton onePlayer = new JButton("1 PLAYER");
                JButton twoPlayers = new JButton("2 PLAYERS");
                JButton multiplayer = new JButton("MULTIPLAYER");

                styleButton(onePlayer);
                styleButton(twoPlayers);
                styleButton(multiplayer);

                panel.add(title);
                panel.add(onePlayer);
                panel.add(twoPlayers);
                panel.add(multiplayer);

                add(panel);

                onePlayer.addActionListener(e -> startGame(1));
                twoPlayers.addActionListener(e -> startGame(2));
                multiplayer.addActionListener(e -> showMultiplayerMenu());

                revalidate();
                repaint();
        }

        private void showMultiplayerMenu() {
                getContentPane().removeAll();

                JPanel panel = new JPanel() {
                        Image background = new ImageIcon("PacManBackground.jpg").getImage();

                        @Override
                        protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                        }
                };

                panel.setLayout(new GridLayout(4, 1, 20, 20));
                panel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

                JLabel title = new JLabel("MULTIPLAYER", SwingConstants.CENTER);
                title.setForeground(Color.YELLOW);
                title.setFont(new Font("Arial", Font.BOLD, 35));

                JButton host = new JButton("HOST GAME");
                JButton join = new JButton("JOIN GAME");
                JButton back = new JButton("BACK");

                styleButton(host);
                styleButton(join);
                styleButton(back);

                panel.add(title);
                panel.add(host);
                panel.add(join);
                panel.add(back);

                add(panel);

                host.addActionListener(e -> hostGame());
                join.addActionListener(e -> joinGame());
                back.addActionListener(e -> showMainMenu());

                revalidate();
                repaint();
        }

        private void styleButton(JButton button) {
                button.setFont(new Font("Arial", Font.BOLD, 22));
                button.setForeground(Color.YELLOW);
                button.setBackground(new Color(0, 0, 0, 180));
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 255), 3));
                button.setFocusPainted(false);
                button.setContentAreaFilled(true);
                button.setOpaque(true);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));

                button.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseEntered(java.awt.event.MouseEvent e) {
                                button.setForeground(Color.WHITE);
                                button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                        }

                        @Override
                        public void mouseExited(java.awt.event.MouseEvent e) {
                                button.setForeground(Color.YELLOW);
                                button.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 255), 3));
                        }
                });
        }

        private void startGame(int players) {
                dispose();

                JFrame gameFrame = new JFrame();
                PacMan pacman = new PacMan(players);

                if (players == 1) {
                        gameFrame.setTitle("Pac-Man - 1 Player");
                } else {
                        gameFrame.setTitle("Pac-Man - 2 Players");
                }

                gameFrame.setContentPane(pacman);
                gameFrame.pack();
                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameFrame.setLocationRelativeTo(null);
                gameFrame.setResizable(false);
                gameFrame.setVisible(true);

                SwingUtilities.invokeLater(() -> {
                        pacman.requestFocusInWindow();
                        pacman.gameLoop.start();
                });
        }

        private void hostGame() {
                try {
                        String ip = java.net.InetAddress.getLocalHost().getHostAddress();

                        JOptionPane.showMessageDialog(
                                        this,
                                        "HOST GAME\n\nYour IP Address:\n" + ip +
                                                        "\n\nGive this IP to Player 2.");

                        dispose();

                        new Thread(() -> {
                                PacManServer server = new PacManServer();
                                server.startServer();
                        }).start();

                } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Could not start server:\n" + e.getMessage());
                }
        }

        private void joinGame() {
                String ip = JOptionPane.showInputDialog(
                                this,
                                "Enter Host IP Address:",
                                "JOIN GAME",
                                JOptionPane.PLAIN_MESSAGE);

                if (ip == null || ip.trim().isEmpty()) {
                        return;
                }

                PacManClient client = new PacManClient();

                boolean connected = client.connect(ip.trim());

                if (connected) {
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Connected to Host!\nStarting game...");

                        startGame(2);
                } else {
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Could not connect to host.\n\n" +
                                                        "Check the IP address and make sure both PCs are on the same Wi-Fi.");
                }
        }

        public static void main(String[] args) {
                SwingUtilities.invokeLater(() -> {
                        new PacManMenu().setVisible(true);
                });
        }
}