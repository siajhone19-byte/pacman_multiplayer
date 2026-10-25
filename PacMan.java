
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U';
        int velocityX = 0, velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            startX = x;
            startY = y;
        }

        void updateDirection(char direction) {
            char previousDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            x += velocityX;
            y += velocityY;

            for (Block wall : walls) {
                if (collision(this, wall)) {
                    x -= velocityX;
                    y -= velocityY;
                    this.direction = previousDirection;
                    updateVelocity();
                    break;
                }
            }
        }

        void updateVelocity() {
            if (direction == 'U') {
                velocityX = 0;
                velocityY = -tileSize / 4;
            } else if (direction == 'D') {
                velocityX = 0;
                velocityY = tileSize / 4;
            } else if (direction == 'L') {
                velocityX = -tileSize / 4;
                velocityY = 0;
            } else if (direction == 'R') {
                velocityX = tileSize / 4;
                velocityY = 0;
            }
        }

        void reset() {
            x = startX;
            y = startY;
            velocityX = 0;
            velocityY = 0;
        }
    }

    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;
    private final int boardHeight = rowCount * tileSize;

    private Image powerFoodImage, wallImage;
    private Image blueGhostImage, orangeGhostImage;
    private Image pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage;
    private Image pacmanLeftImage, pacmanRightImage;
    private Image pacmanUpClosedImage, pacmanDownClosedImage;
    private Image pacmanLeftClosedImage, pacmanRightClosedImage;

    private final String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "X      XbpoX      X",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X      P    X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    private HashSet<Block> walls;
    private HashSet<Block> foods;
    private LinkedHashSet<Block> ghosts;
    private Block player1, player2;

    public Timer gameLoop;

    private final char[] directions = {'U', 'D', 'L', 'R'};
    private final Random random = new Random();

    private int score1 = 0, score2 = 0;
    private int lives1 = 3, lives2 = 3;

    private boolean gameOver = false;
    private boolean gameOverShown = false;
    private boolean paused = false;

    private JButton pauseButton;
    private JButton resumeButton;
    private JButton restartButton;
    private JButton exitButton;
    private JPanel pauseMenuPanel;

    private boolean player1Eating = false;
    private boolean player2Eating = false;

    private final int playerCount;
    private final boolean networked;
    private final boolean networkHost;
    private final PacManServer networkServer;
    private final PacManClient networkClient;

    public PacMan(int players) {
        this(players, false, true, null, null);
    }

    public PacMan(int players, boolean networked, boolean networkHost,
                  PacManServer server, PacManClient client) {
        playerCount = players;
        this.networked = networked;
        this.networkHost = networkHost;
        networkServer = server;
        networkClient = client;

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(new Color(5, 10, 36));
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);

        pauseButton = new JButton("|| PAUSE");
        styleButton(pauseButton, new Color(30, 100, 220));
        pauseButton.setBounds(boardWidth - 100, 42, 90, 32);
        pauseButton.addActionListener(e -> {
            if (paused) {
                resumeGame();
            } else {
                showPauseMenu();
            }
        });
        add(pauseButton);

        pauseMenuPanel = new JPanel();
        pauseMenuPanel.setLayout(new GridLayout(4, 1, 10, 10));
        pauseMenuPanel.setBackground(new Color(5, 10, 36));
        pauseMenuPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.YELLOW, 3),
                        BorderFactory.createEmptyBorder(18, 22, 18, 22)));
        pauseMenuPanel.setBounds(
                (boardWidth - 250) / 2,
                (boardHeight - 260) / 2,
                250, 260);

        JLabel pauseTitle = new JLabel("GAME PAUSED", SwingConstants.CENTER);
        pauseTitle.setForeground(Color.YELLOW);
        pauseTitle.setFont(new Font("Arial", Font.BOLD, 21));
        pauseMenuPanel.add(pauseTitle);

        resumeButton = new JButton("RESUME");
        restartButton = new JButton("RESTART");
        exitButton = new JButton("EXIT TO MENU");

        styleButton(resumeButton, new Color(22, 150, 75));
        styleButton(restartButton, new Color(30, 100, 220));
        styleButton(exitButton, new Color(200, 45, 55));

        pauseMenuPanel.add(resumeButton);
        pauseMenuPanel.add(restartButton);
        pauseMenuPanel.add(exitButton);
        pauseMenuPanel.setVisible(false);
        add(pauseMenuPanel);

        resumeButton.addActionListener(e -> resumeGame());

        restartButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Restart the game? Your score and lives will reset.",
                    "Restart Pac-Man",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            }
        });

        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Return to the main menu?",
                    "Exit to Menu",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                returnToMainMenu();
            }
        });

        loadImages();
        loadMap();

        for (Block ghost : ghosts) {
            ghost.updateDirection(
                    directions[random.nextInt(directions.length)]);
        }

        gameLoop = new Timer(50, this);

        if (networked && !networkHost && networkClient != null) {
            networkClient.setMessageHandler(message -> {
                if (message.startsWith("STATE|")) {
                    final String state = message.substring(6);
                    SwingUtilities.invokeLater(() -> applyNetworkState(state));
                }
            });
        }
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showPauseMenu() {
        if (gameOver || paused) {
            return;
        }

        paused = true;
        gameLoop.stop();
        pauseMenuPanel.setVisible(true);
        pauseButton.setText("> RESUME");
        pauseButton.setBackground(new Color(22, 150, 75));
        repaint();
    }

    private void resumeGame() {
        if (gameOver) {
            return;
        }

        paused = false;
        pauseMenuPanel.setVisible(false);
        pauseButton.setText("|| PAUSE");
        pauseButton.setBackground(new Color(30, 100, 220));
        gameLoop.start();
        requestFocusInWindow();
        repaint();
    }

    private void restartGame() {
        gameLoop.stop();

        score1 = 0;
        score2 = 0;
        lives1 = 3;
        lives2 = 3;
        gameOver = false;
        gameOverShown = false;
        paused = false;
        player1Eating = false;
        player2Eating = false;

        loadMap();
        resetPositions();

        pauseMenuPanel.setVisible(false);
        pauseButton.setText("|| PAUSE");
        pauseButton.setBackground(new Color(30, 100, 220));

        repaint();
        requestFocusInWindow();
        gameLoop.start();
    }

    private void returnToMainMenu() {
        gameLoop.stop();

        Window currentWindow = SwingUtilities.getWindowAncestor(this);

        // Open the menu before disposing the game window.
        PacManMenu menu = new PacManMenu();
        menu.setVisible(true);

        if (currentWindow instanceof JFrame) {
            JFrame frame = (JFrame) currentWindow;
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.dispose();
        }

        menu.requestFocus();
    }

    public boolean isPaused() {
        return paused;
    }

    private void loadImages() {
        powerFoodImage = loadImage("powerFood.png");
        wallImage = loadImage("wall.png");
        blueGhostImage = loadImage("blueGhost.png");
        orangeGhostImage = loadImage("orangeGhost.png");
        pinkGhostImage = loadImage("pinkGhost.png");
        redGhostImage = loadImage("redGhost.png");

        pacmanUpImage = loadImage("3Dpacman_up.png");
        pacmanDownImage = loadImage("3Dpacman_down.png");
        pacmanLeftImage = loadImage("3Dpacman_left.png");
        pacmanRightImage = loadImage("3Dpacman_right.png");

        pacmanUpClosedImage = loadImage("3Dpacman_up_closed.png");
        pacmanDownClosedImage = loadImage("3Dpacman_down_closed.png");
        pacmanLeftClosedImage = loadImage("3Dpacman_left_closed.png");
        pacmanRightClosedImage = loadImage("3Dpacman_right_closed.png");
    }

    private Image loadImage(String fileName) {
        java.net.URL url = getClass().getResource("/" + fileName);
        if (url == null) {
            System.out.println("WARNING: Image not found: " + fileName);
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new LinkedHashSet<>();
        player1 = null;
        player2 = null;

        for (int r = 0; r < rowCount; r++) {
            String row = tileMap[r];

            for (int c = 0; c < columnCount; c++) {
                char tile = row.charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                if (tile == 'X') {
                    walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                } else if (tile == 'b') {
                    ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                } else if (tile == 'o') {
                    ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                } else if (tile == 'p') {
                    ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                } else if (tile == 'r') {
                    ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                } else if (tile == 'P') {
                    player1 = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                } else if (tile == ' ') {
                    foods.add(new Block(powerFoodImage, x + 14, y + 14, 4, 4));
                }
            }
        }

        if (playerCount == 2 && lives2 > 0) {
            int x = tileSize * 17;
            int y = tileSize * 15;
            player2 = new Block(pacmanRightImage, x, y, tileSize, tileSize);

            if (isWall(player2)) {
                player2.x = tileSize * 16;
                player2.y = tileSize * 15;
            }
        }

        if (lives1 <= 0) {
            player1 = null;
        }
    }

    private boolean isWall(Block player) {
        for (Block wall : walls) {
            if (collision(player, wall)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Block wall : walls) {
            if (wall.image != null) {
                g.drawImage(wall.image, wall.x, wall.y,
                        wall.width, wall.height, null);
            }
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        for (Block ghost : ghosts) {
            if (ghost.image != null) {
                g.drawImage(ghost.image, ghost.x, ghost.y,
                        ghost.width, ghost.height, null);
            }
        }

        drawPlayer(g, player1);
        if (playerCount == 2) {
            drawPlayer(g, player2);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 15));

        if (playerCount == 1) {
            g.drawString("P1 Lives: " + lives1 + "   Score: " + score1, 10, 20);
        } else {
            g.drawString("P1 Lives: " + lives1 + "  Score: " + score1, 10, 18);
            g.drawString("P2 Lives: " + lives2 + "  Score: " + score2, 10, 36);
            g.drawString("P1: ARROWS   P2: WASD", 10, boardHeight - 10);
        }

        if (paused) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 0, 175));
            g2.fillRect(0, 0, boardWidth, boardHeight);
            g2.dispose();
        }
    }

    private void drawPlayer(Graphics g, Block player) {
        if (player == null) {
            return;
        }

        Image image = player.image;

        if (player == player1 && player1Eating) {
            if (player.direction == 'U') image = pacmanUpClosedImage;
            else if (player.direction == 'D') image = pacmanDownClosedImage;
            else if (player.direction == 'L') image = pacmanLeftClosedImage;
            else if (player.direction == 'R') image = pacmanRightClosedImage;
        }

        if (player == player2 && player2Eating) {
            if (player.direction == 'U') image = pacmanUpClosedImage;
            else if (player.direction == 'D') image = pacmanDownClosedImage;
            else if (player.direction == 'L') image = pacmanLeftClosedImage;
            else if (player.direction == 'R') image = pacmanRightClosedImage;
        }

        if (image != null) {
            g.drawImage(image, player.x, player.y,
                    player.width, player.height, null);
        }
    }

    public void move() {
        if (gameOver || paused) {
            return;
        }

        movePlayer(player1);
        if (playerCount == 2) {
            movePlayer(player2);
        }

        for (Block ghost : ghosts) {
            if (player1 != null && collision(ghost, player1)) {
                loseLife(1);
                if (gameOver) return;
                break;
            }

            if (playerCount == 2 && player2 != null && collision(ghost, player2)) {
                loseLife(2);
                if (gameOver) return;
                break;
            }

            if (ghost.y == tileSize * 9
                    && ghost.direction != 'U'
                    && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for (Block wall : walls) {
                if (collision(ghost, wall)
                        || ghost.x <= 0
                        || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(
                            directions[random.nextInt(directions.length)]);
                    break;
                }
            }
        }

        Block foodEaten = null;

        for (Block food : foods) {
            if (player1 != null && collision(player1, food)) {
                foodEaten = food;
                score1 += 10;
                player1Eating = true;
                new Timer(150, e -> {
                    player1Eating = false;
                    ((Timer) e.getSource()).stop();
                    repaint();
                }).start();
                break;
            }

            if (playerCount == 2 && player2 != null && collision(player2, food)) {
                foodEaten = food;
                score2 += 10;
                player2Eating = true;
                new Timer(150, e -> {
                    player2Eating = false;
                    ((Timer) e.getSource()).stop();
                    repaint();
                }).start();
                break;
            }
        }

        if (foodEaten != null) {
            foods.remove(foodEaten);
        }

        if (foods.isEmpty()) {
            loadMap();
            for (Block ghost : ghosts) {
                ghost.updateDirection(
                        directions[random.nextInt(directions.length)]);
            }
            resetPositions();
        }
    }

    private void movePlayer(Block player) {
        if (player == null) return;

        player.x += player.velocityX;
        player.y += player.velocityY;

        for (Block wall : walls) {
            if (collision(player, wall)) {
                player.x -= player.velocityX;
                player.y -= player.velocityY;
                break;
            }
        }
    }

    private void loseLife(int player) {
        if (player == 1) {
            lives1--;
            if (lives1 <= 0) player1 = null;
        } else if (player == 2) {
            lives2--;
            if (lives2 <= 0) player2 = null;
        }

        if (playerCount == 1) {
            if (lives1 <= 0) gameOver = true;
            else resetPositions();
        } else {
            if (lives1 <= 0 && lives2 <= 0) gameOver = true;
            else resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        if (a == null || b == null) return false;

        return a.x < b.x + b.width
                && a.x + a.width > b.x
                && a.y < b.y + b.height
                && a.y + a.height > b.y;
    }

    public void resetPositions() {
        if (player1 != null && lives1 > 0) {
            player1.x = player1.startX;
            player1.y = player1.startY;
            player1.velocityX = 0;
            player1.velocityY = 0;
        }

        if (player2 != null && lives2 > 0) {
            player2.x = tileSize * 17;
            player2.y = tileSize * 15;

            if (isWall(player2)) {
                player2.x = tileSize * 16;
                player2.y = tileSize * 15;
            }

            player2.velocityX = 0;
            player2.velocityY = 0;
        }

        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(
                    directions[random.nextInt(directions.length)]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            gameLoop.stop();

            if (!gameOverShown) {
                gameOverShown = true;

                SwingUtilities.invokeLater(() -> {
                    JFrame currentFrame = (JFrame)
                            SwingUtilities.getWindowAncestor(this);

                    if (currentFrame != null) {
                        currentFrame.dispose();
                    }

                    GameOverFrame gameOverFrame =
                            new GameOverFrame(score1 + score2, playerCount);
                    gameOverFrame.setVisible(true);
                });
            }
            return;
        }

        if (paused) {
            repaint();
            return;
        }

        if (!networked || networkHost) {
            move();

            if (networked && networkHost && networkServer != null) {
                networkServer.broadcastState(createNetworkState());
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver || paused) {
            return;
        }

        int key = e.getKeyCode();

        if (!networked || networkHost) {
            if (key == KeyEvent.VK_UP) setDirection(player1, 'U');
            else if (key == KeyEvent.VK_DOWN) setDirection(player1, 'D');
            else if (key == KeyEvent.VK_LEFT) setDirection(player1, 'L');
            else if (key == KeyEvent.VK_RIGHT) setDirection(player1, 'R');
        }

        if (networked && !networkHost) {
            char direction = 0;

            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) direction = 'U';
            else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) direction = 'D';
            else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) direction = 'L';
            else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) direction = 'R';

            if (direction != 0 && networkClient != null) {
                networkClient.send("INPUT|" + direction);
            }
        }

        if (!networked && playerCount == 2 && player2 != null) {
            if (key == KeyEvent.VK_W) setDirection(player2, 'U');
            else if (key == KeyEvent.VK_S) setDirection(player2, 'D');
            else if (key == KeyEvent.VK_A) setDirection(player2, 'L');
            else if (key == KeyEvent.VK_D) setDirection(player2, 'R');
        }
    }

    private void setDirection(Block player, char direction) {
        if (player == null || paused || gameOver) return;

        player.updateDirection(direction);

        if (direction == 'U') player.image = pacmanUpImage;
        else if (direction == 'D') player.image = pacmanDownImage;
        else if (direction == 'L') player.image = pacmanLeftImage;
        else if (direction == 'R') player.image = pacmanRightImage;
    }

    public void acceptRemoteDirection(char direction) {
        if (networked && networkHost && !paused && !gameOver) {
            setDirection(player2, direction);
        }
    }

    private String createNetworkState() {
        StringBuilder b = new StringBuilder();
        appendPlayer(b, player1);
        b.append('|');
        appendPlayer(b, player2);

        b.append('|')
                .append(score1).append(',')
                .append(score2).append(',')
                .append(lives1).append(',')
                .append(lives2);

        b.append('|');
        boolean first = true;

        for (Block food : foods) {
            if (!first) b.append(';');
            first = false;
            b.append(food.x).append(',').append(food.y);
        }

        b.append('|');
        first = true;

        for (Block ghost : ghosts) {
            if (!first) b.append(';');
            first = false;
            b.append(ghost.x).append(',')
                    .append(ghost.y).append(',')
                    .append(ghost.direction);
        }

        return b.toString();
    }

    private void appendPlayer(StringBuilder b, Block p) {
        if (p == null) {
            b.append("-1,-1,U");
            return;
        }

        b.append(p.x).append(',')
                .append(p.y).append(',')
                .append(p.direction);
    }

    private void applyNetworkState(String state) {
        try {
            String[] sections = state.split("\\|", -1);
            applyPlayer(player1, sections[0]);
            applyPlayer(player2, sections[1]);

            String[] stats = sections[2].split(",");
            score1 = Integer.parseInt(stats[0]);
            score2 = Integer.parseInt(stats[1]);
            lives1 = Integer.parseInt(stats[2]);
            lives2 = Integer.parseInt(stats[3]);

            if (player1 != null && lives1 <= 0) player1 = null;
            if (player2 != null && lives2 <= 0) player2 = null;

            foods.clear();

            if (!sections[3].isEmpty()) {
                for (String item : sections[3].split(";")) {
                    String[] xy = item.split(",");
                    foods.add(new Block(
                            powerFoodImage,
                            Integer.parseInt(xy[0]),
                            Integer.parseInt(xy[1]),
                            4, 4));
                }
            }

            String[] ghostStates = sections[4].isEmpty()
                    ? new String[0] : sections[4].split(";");

            int i = 0;
            for (Block ghost : ghosts) {
                if (i >= ghostStates.length) break;

                String[] values = ghostStates[i++].split(",");
                ghost.x = Integer.parseInt(values[0]);
                ghost.y = Integer.parseInt(values[1]);
                ghost.direction = values[2].charAt(0);
                ghost.updateVelocity();
            }

            repaint();
        } catch (Exception ex) {
            System.out.println("Invalid multiplayer update: " + ex.getMessage());
        }
    }

    private void applyPlayer(Block player, String data) {
        if (player == null) return;

        String[] values = data.split(",");
        int x = Integer.parseInt(values[0]);
        int y = Integer.parseInt(values[1]);
        char d = values[2].charAt(0);

        if (x >= 0 && y >= 0) {
            player.x = x;
            player.y = y;
        }

        player.direction = d;
        player.updateVelocity();

        if (d == 'U') player.image = pacmanUpImage;
        else if (d == 'D') player.image = pacmanDownImage;
        else if (d == 'L') player.image = pacmanLeftImage;
        else if (d == 'R') player.image = pacmanRightImage;
    }
}
