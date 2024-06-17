import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.nio.channels.Pipe;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener,KeyListener{
    int boardWidth=360;
    int boardHeight=640;
    // Changing Background Image of our game
    Image backgroundImg;    //Creating variable for images;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird Coordinates
    int birdX=boardWidth/8;
    int birdY=boardHeight/2;
    int birdWidth=44;
    int birdHeight=30;

    class Bird{
        int x=birdX;
        int y=birdY;
        int width=birdWidth;
        int height=birdHeight;
        Image img;

        Bird(Image img){
            this.img=img;

        }
    }

    //Pipes
    int pipex=boardWidth;
    int pipey=0;
    int pipeWidth=64;
    int pipeHeight=512;

    class pipe{
        int x=pipex;
        int y=pipey;
        int width=pipeWidth;
        int height=pipeHeight;
        Image img;
        boolean passed=false;

        pipe(Image img){
            this.img=img;
        }
    }
    Bird bird;
    int velocityX=-4;
    int velocityY=0;
    int gravity=1;

    ArrayList<pipe> pipes;
    Random random=new Random();
    Timer gameLoop;
    Timer placePipesTimer;

    //Game over conditions
    boolean gameOver=false;

    //Score Tracker
    double score=0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load Images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        bird=new Bird(birdImg);

        pipes=new ArrayList<pipe>();

        gameLoop= new Timer(1000/60,this);
        gameLoop.start();
        //Place piper timer
        placePipesTimer=new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

    }
    public void placePipes(){
        int randomPipeY=(int)(pipey-pipeHeight/4-Math.random()*(pipeHeight/2));
        int openingSpace=boardHeight/4;
        pipe topPipe=new pipe(topPipeImg);
        topPipe.y=randomPipeY;
        pipes.add(topPipe);

        pipe bottomPipe= new pipe(bottomPipeImg);
        bottomPipe.y=topPipe.y +pipeHeight+openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.drawImage(backgroundImg,0,0,boardWidth,boardHeight,null);
        g.drawImage(bird.img,bird.x,bird.y,bird.width,bird.height,null);
        for (int i=0;i<pipes.size();i++){
            pipe Pipe=pipes.get(i);
            g.drawImage(Pipe.img,Pipe.x,Pipe.y,Pipe.width,Pipe.height,null);
        }
        //Score
        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.PLAIN,32));

        if(gameOver){
            g.drawString("Game Over: "+String.valueOf((int)score),10,35);
        }else{
            g.drawString("Score: "+String.valueOf((int)score),10,35);
        }
    }
    public void move(){
        velocityY+=gravity;
        bird.y+=velocityY;
        bird.y=Math.max(bird.y,0);
        // pipes
        for (int i=0;i<pipes.size();i++){
            pipe Pipe=pipes.get(i);
            Pipe.x +=velocityX;
            if(!Pipe.passed && bird.x>Pipe.x +Pipe.width){
                Pipe.passed=true;
                score+=0.5;
            }

            if(collision(bird,Pipe)){
                gameOver=true;
            }
        }
        if(bird.y>boardHeight){
            gameOver=true;
        }
    }

    public boolean collision(Bird a, pipe b){
        return a.x< b.x +b.width &&
                a.x+a.width>b.x &&
                a.y<b.y +b.height &&
                a.y +a.height>b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        move();
        repaint();
        if(gameOver==true){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }
    @Override
    public void keyPressed(KeyEvent e){
        if (e.getKeyCode()==KeyEvent.VK_SPACE){
            velocityY=-9;
        }
    }@Override
    public void keyTyped(KeyEvent e){
    }@Override
    public void keyReleased(KeyEvent e) {
    }

}
