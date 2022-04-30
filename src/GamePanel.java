import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class GamePanel extends JPanel{
    public ImageIcon backgroundImg, turtle, hare;
    public Timer turtleTimer, hareTimer;

    int turtleX = 120, turtleY = 270;
    int hareX = 70, hareY = 280;

    int currentX, currentY;
    int point2X = 0, point2Y = 0, point1X = 0, point1Y = 0;

    Line2D sleep1LineVert, sleep2LineVert;
    Line2D sleep1LineHor, sleep2LineHor;

    boolean firstSleep = true;

    public int hareStopDelay, hareStartDelay;
    public int turtleDelay;
    int clickCount = 0;

    public Thread turtleThread, hareThread, paintingThread;
    public TurtleThread turtleThreadObj;
    public HareThread hareThreadObj;
    public PaintingThread paintingThreadObj;

    public GamePanel(){
        //set images for everything
        backgroundImg = new ImageIcon(getClass().getResource("backgroundimg.png"));
        turtle = new ImageIcon(getClass().getResource("turtleUp.png"));
        hare = new ImageIcon(getClass().getResource("hareUp.png"));

        //these lines are for determining sleep positions
        sleep1LineHor = new Line2D.Double(0,0,0,0);
        sleep1LineVert = new Line2D.Double(0,0,0,0);
        sleep2LineHor = new Line2D.Double(0,0,0,0);
        sleep2LineVert = new Line2D.Double(0,0,0,0);

        System.out.println("current turtle delay: " + turtleDelay);

        //create Periodic Painting Thread
        paintingThreadObj = new PaintingThread();
        paintingThread = new Thread(paintingThreadObj);

        //create Turtle Thread
        turtleThreadObj = new TurtleThread(turtleDelay);
        turtleThread = new Thread(turtleThreadObj);

        //create Hare Thread
        hareThreadObj = new HareThread();
        hareThread = new Thread(hareThreadObj);

        repaint();
        addMouseListener(new MouseHandler());
        setLayout(new BorderLayout());

    }

    public int isInSleepBounds(){
        double distanceH1 = Line2D.ptSegDist(sleep1LineHor.getX1(), sleep1LineHor.getY1(), sleep1LineHor.getX2(), sleep1LineHor.getY2(), hareX, hareY);
        double distanceV1 = Line2D.ptSegDist(sleep1LineVert.getX1(), sleep1LineVert.getY1(), sleep1LineVert.getX2(), sleep1LineVert.getY2(), hareX, hareY);

        double distanceH2 = Line2D.ptSegDist(sleep2LineHor.getX1(), sleep2LineHor.getY1(), sleep2LineHor.getX2(), sleep2LineHor.getY2(), hareX, hareY);
        double distanceV2 = Line2D.ptSegDist(sleep2LineVert.getX1(), sleep2LineVert.getY1(), sleep2LineVert.getX2(), sleep2LineVert.getY2(), hareX, hareY);

        //checks how close current hare location is to sleep point,
        //if within 20 of either lines will alert sleep and return 1 or 2
        if (distanceH1 < 40|| distanceV1 < 40) {
            sleep1LineVert = new Line2D.Double(0,0,0,0);
            sleep1LineHor = new Line2D.Double(0,0,0,0);
            return 1; //on line 1
        } else if (distanceH2 < 40|| distanceV2 < 40){
            sleep2LineVert = new Line2D.Double(0,0,0,0);
            sleep2LineHor = new Line2D.Double(0,0,0,0);
            return 2; //on line 2
        }
        return 0; //not on the line
    }


    public void paintComponent(Graphics g){
        super.paintComponents(g);
        Graphics2D g2 = (Graphics2D)g;

        //paints all the images
        backgroundImg.paintIcon(this, g, 0, 0);
        turtle.paintIcon(this, g, turtleX, turtleY);
        hare.paintIcon(this, g, hareX, hareY);

        //draws all the lines
        g2.draw(sleep2LineVert);
        g2.draw(sleep2LineHor);
        g2.draw(sleep1LineVert);
        g2.draw(sleep1LineHor);

    }

    private class MouseHandler implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            currentX = e.getX();
            currentY = e.getY();

            //if clickCount is 1
            if (clickCount == 1){
                point2X = e.getX();
                point2Y = e.getY();
                //draws line at current mouse location
                sleep2LineVert = new Line2D.Double(currentX, currentY+20, currentX, currentY - 20);
                sleep2LineHor = new Line2D.Double(currentX-20, currentY, currentX+20, currentY);
                repaint();
            }

            if (clickCount == 0){
                point1X = e.getX();
                point1Y = e.getY();

                sleep1LineVert = new Line2D.Double(currentX, currentY+20, currentX, currentY - 20);
                sleep1LineHor = new Line2D.Double(currentX-20, currentY, currentX+20, currentY);
                repaint();
            }

            clickCount++;
        }

        //if mouse is pressed
        @Override
        public void mousePressed(MouseEvent e) {

        }

        //once mouse is released
        @Override
        public void mouseReleased(MouseEvent e) {


        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public class TurtleThread extends Thread{
        public int delay;
        public TurtleThread(int x){
            System.out.println("the delay is " + x);
            this.delay = x;
        }


        public void setDelay(int x){
            delay = x;
            System.out.println("the delay is " + x);
        }

        public void run(){
            try{
                while(true){
                    Thread.sleep(delay);
                    if (turtleY >= 140 && turtleX == 120) {
                        //move turtle up by 20 until it reaches 140
                        turtleY -= 20;
                    } else if (turtleY <= 140 && turtleX <= 320){
                        //move turtle left until 320 and swap image
                        turtleX += 20;
                        turtle = new ImageIcon(getClass().getResource("turtle.png"));
                    } else {
                        //move turtle down until 290
                        turtleY += 20;
                        turtle = new ImageIcon(getClass().getResource("turtleDown.png"));

                        //once reaches end stop turtle timer
                        if (turtleY == 290){
                            break;
                        }

                    }

                    //insert turtle image if turtle wins
                    if (turtleY >= 290 && hareY < 290) {
                        backgroundImg = new ImageIcon(getClass().getResource("turtlewins.png"));
                        repaint();
                        break;
                    }

                    repaint();
                }


            }catch (Exception e){
                System.out.println("error");
            }
        }



    }

    public class HareThread extends Thread{
        public int stopDelay;
        public int startDelay;
        public HareThread(){
            startDelay = 0;
            stopDelay = 0;
        }

        public void setStopDelay(int x){
            stopDelay = x;
            System.out.println("stop delay: " + x);
        }

        public void setStartDelay(int x){
            startDelay = x;
            System.out.println("start delay: " + x);
        }


        public void run(){
            try{
                while(true) {
                    Thread.sleep(startDelay);

                    if (hareY >= 110 && hareX == 70) {
                        //move hare up 20 until reaches 110
                        hareY -= 20;
                    } else if (hareY <= 110 && hareX <= 370) {
                        //move hare to the right and change image
                        hareX += 20;
                        hare = new ImageIcon(getClass().getResource("rabit.png"));
                    } else {
                        //move hare down and change image
                        hareY += 20;
                        hare = new ImageIcon(getClass().getResource("hareDown.png"));

                        //checks if hare has finished

                    }

                    //checks if hare wins
                    if (hareY >= 290 && turtleY < 290) {
                        backgroundImg = new ImageIcon(getClass().getResource("harewins.png"));
                        //turtleTimer.stop();
                        repaint();
                        break;

                    } else if (turtleY >= 290 && hareY < 290){
                        backgroundImg = new ImageIcon(getClass().getResource("turtlewins.png"));
                        repaint();
                        break;
                    }

                    //checks if
                    int location = isInSleepBounds();
                    if (location > 0) {
                        Thread.sleep(stopDelay);
                        System.out.println("on sleep line");
                        //clears which sleep line it is on
                        if (location == 1) {
                            sleep1LineVert = new Line2D.Double(0, 0, 0, 0);
                            sleep1LineHor = new Line2D.Double(0, 0, 0, 0);
                        } else if (location == 2) {
                            sleep2LineVert = new Line2D.Double(0, 0, 0, 0);
                            sleep2LineHor = new Line2D.Double(0, 0, 0, 0);
                        }
                    }
                    repaint();
                }


            }catch (Exception e){
                System.out.println("error");
            }
        }



    }

    public class PaintingThread extends Thread{
        public PaintingThread(){}

        public void run(){
            try{
                while(true){
                    Thread.sleep(500);
                    repaint();
                }
            }catch (Exception e){

            }
        }


    }


}
