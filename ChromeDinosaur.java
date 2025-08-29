import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 750;
    int boardHeight = 250;

    // Images
    Image dinosaurImg, dinosaurDeadImg, dinosaurJumpImg;
    Image[] cactusImages;

    class Block {
        int x, y, width, height;
        Image img;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    // Dinosaur properties
    Block dinosaur;
    int dinosaurX = 50, dinosaurY = boardHeight - 94; // Height of the dinosaur
    int velocityY = 0, gravity = 1;
    boolean gameOver = false;
    int score = 0;
    ArrayList<Block> cactusArray;

    Timer gameLoop, placeCactusTimer;

    public ChromeDinosaur() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.lightGray);
        setFocusable(true);
        addKeyListener(this);

        loadImages();
        initializeGameObjects();
        startGameTimers();
    }

    private void loadImages() {
        dinosaurImg = loadImage("./img/dino-run.gif");
        dinosaurDeadImg = loadImage("./img/dino-dead.png");
        dinosaurJumpImg = loadImage("./img/dino-jump.png");
        cactusImages = new Image[]{
            loadImage("./img/cactus1.png"),
            loadImage("./img/cactus2.png"),
            loadImage("./img/cactus3.png")
        };
    }

    private Image loadImage(String path) {
        Image img = new ImageIcon(getClass().getResource(path)).getImage();
        if (img == null) {
            System.err.println("Image not found: " + path);
        }
        return img;
    }

    private void initializeGameObjects() {
        dinosaur = new Block(dinosaurX, dinosaurY, 88, 94, dinosaurImg);
        cactusArray = new ArrayList<>();
    }

    private void startGameTimers() {
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        placeCactusTimer = new Timer(1500, e -> placeCactus());
        placeCactusTimer.start();
    }

    void placeCactus() {
        if (gameOver) return;

        int cactusType = (int) (Math.random() * cactusImages.length);
        cactusArray.add(new Block(700, boardHeight - 70, 
            cactusImages[cactusType].getWidth(null),
            70, cactusImages[cactusType]));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(dinosaur.img, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);
        for (Block cactus : cactusArray) {
            g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }
        drawScore(g);
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.black);
        g.setFont(new Font("Courier", Font.PLAIN, 32));
        g.drawString(gameOver ? "Game Over: " + score : String.valueOf(score), 10, 35);
    }

    public void move() {
        // Dinosaur movement
        velocityY += gravity;
        dinosaur.y += velocityY;
        if (dinosaur.y > dinosaurY) {
            dinosaur.y = dinosaurY;
            velocityY = 0;
            dinosaur.img = dinosaurImg;
        }

        // Cactus movement
        cactusArray.removeIf(cactus -> (cactus.x += -12) < -cactus.width);
        for (Block cactus : cactusArray) {
            if (collision(dinosaur, cactus)) {
                gameOver = true;
                dinosaur.img = dinosaurDeadImg;
            }
        }

        // Increment score
        score++;
    }

    boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
               a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placeCactusTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameOver && dinosaur.y == dinosaurY) {
                velocityY = -17; // Jump height
                dinosaur.img = dinosaurJumpImg;
            } else if (gameOver) {
                restartGame();
            }
        }
    }

    private void restartGame() {
        dinosaur.y = dinosaurY;
        dinosaur.img = dinosaurImg;
        velocityY = 0;
        cactusArray.clear();
        score = 0;
        gameOver = false;
        gameLoop.start();
        placeCactusTimer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
