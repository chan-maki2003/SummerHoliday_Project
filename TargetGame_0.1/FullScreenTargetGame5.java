import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

public class FullScreenTargetGame5 extends JFrame {
    private static final int TARGET_SIZE = 50;
    private static final int GAME_DURATION_SEC = 30;

    private JLabel scoreLabel;
    private JLabel timerLabel;
    private int score = 0;
    private int remainingTime = GAME_DURATION_SEC;
    private boolean isFullScreen = true;
    private Timer gameTimer;

    public FullScreenTargetGame5() {
        setTitle("フルスクリーン的をクリックして消すゲーム");
        setUndecorated(true); // Remove window decorations (title bar, borders, etc.)
        setResizable(false); // Disable window resizing
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Start in full-screen mode
        setAlwaysOnTop(true); // Keep the window always on top

        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTarget(g);
            }
        };
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setFullScreen(!isFullScreen);
                }
            }
        });
        gamePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                gamePanelMousePressed(evt);
            }
        });
        add(gamePanel);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(scoreLabel, BorderLayout.NORTH);

        timerLabel = new JLabel("Time: " + GAME_DURATION_SEC);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(timerLabel, BorderLayout.SOUTH);

        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingTime--;
                timerLabel.setText("Time: " + remainingTime);
                if (remainingTime == 0) {
                    ((Timer) e.getSource()).stop();
                    showScoreBoard();
                }
            }
        });
        gameTimer.setInitialDelay(0);
    }

    private void setFullScreen(boolean fullscreen) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        if (fullscreen && device.isFullScreenSupported()) {
            dispose();
            setUndecorated(true);
            device.setFullScreenWindow(this);
            isFullScreen = true;
            setVisible(true);
            startGame();
        } else {
            device.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setResizable(false); // Disable window resizing
            setSize(800, 600);
            setLocationRelativeTo(null);
            isFullScreen = false;
            setVisible(true);
            startGame();
        }
    }

    private void startGame() {
        score = 0;
        remainingTime = GAME_DURATION_SEC;
        scoreLabel.setText("Score: 0");
        timerLabel.setText("Time: " + GAME_DURATION_SEC);
        gameTimer.restart();
        resetTargetPosition();
        repaint();
    }

    private void drawTarget(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(targetX, targetY, TARGET_SIZE, TARGET_SIZE);
    }

    private void gamePanelMousePressed(java.awt.event.MouseEvent evt) {
        int mouseX = evt.getX();
        int mouseY = evt.getY();
        if (isHit(mouseX, mouseY)) {
            score++;
            scoreLabel.setText("Score: " + score);
            resetTargetPosition();
            repaint();
        }
    }

    private boolean isHit(int mouseX, int mouseY) {
        return mouseX >= targetX && mouseX <= targetX + TARGET_SIZE
                && mouseY >= targetY && mouseY <= targetY + TARGET_SIZE;
    }

    private int targetX, targetY;

    private void resetTargetPosition() {
        Random random = new Random();
        int maxX = getWidth() - TARGET_SIZE;
        int maxY = getHeight() - TARGET_SIZE;
        targetX = random.nextInt(maxX + 1);
        targetY = random.nextInt(maxY + 1);
    }

    private void showScoreBoard() {
        JOptionPane.showMessageDialog(this, "ゲーム終了\nスコア: " + score, "結果", JOptionPane.INFORMATION_MESSAGE);
        int choice = JOptionPane.showConfirmDialog(this, "もう一度遊びますか？", "リスタート", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            setFullScreen(false);
        }
    }

    private void resetGame() {
        startGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FullScreenTargetGame5 game = new FullScreenTargetGame5();
                game.setVisible(true);
                JOptionPane.showMessageDialog(game, "フルスクリーン的をクリックして消すゲーム\nスペースキーでゲームスタート", "ゲーム説明", JOptionPane.INFORMATION_MESSAGE);
                game.startGame();
            }
        });
    }
}