import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //Image
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int heigh = birdHeight;
        Image img = birdImg;

        Bird(Image img){
            this.img = img;
        }

    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg =new ImageIcon(getClass().getResource("image/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("image/flappybird.png")).getImage();
        topPipeImg =new ImageIcon(getClass().getResource("image/toppipe.png")).getImage();
        bottomPipeImg =new ImageIcon(getClass().getResource("image/bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer = new Timer(1500,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();
        //game timer
        gameLoop = new Timer(1000/60,this);
        gameLoop.start();
    }

    public void placePipes(){
        //phạm vi giữa pipeY - pipeHeight / 4 và pipeY - pipeHeight / 4 - pipeHeight / 2.
        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = randomPipeY + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }


    public void draw(Graphics g) {

        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.heigh, null);

        //pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if(gameOver){
            String text = "Non vậy tar";
            FontMetrics metrics = g.getFontMetrics();

            // Tính toán vị trí căn giữa
            int x = (boardWidth - metrics.stringWidth(text)) / 2; // căn giữa theo chiều ngang
            int y = (boardHeight - metrics.getHeight()) / 2 + metrics.getAscent(); // căn giữa theo chiều dọc

            // Vẽ văn bản
            g.drawString(text, x, y);
        }
        else {
            g.drawString(String.valueOf((int) score),10,35);
        }
    }

    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score+=0.5;
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if(bird.y > boardHeight){
            gameOver = true;
        }

    }

    public boolean collision(Bird bird, Pipe pipe){
        return bird.x< pipe.x + pipe.width &&
                bird.x + bird.width > pipe.x &&
                bird.y < pipe.y + pipe.height&&
                bird.y + bird.heigh > pipe.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();

        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;
            if(gameOver){
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                placePipesTimer.start();
                gameLoop.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

   @Override
    public void keyReleased(KeyEvent e) {

    }
}
