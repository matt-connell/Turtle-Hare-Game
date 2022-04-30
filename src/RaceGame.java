import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RaceGame extends JFrame {
    JPanel topPanel;
    JButton submit;
    JLabel label;
    JTextField text;


    public RaceGame(){
        super("Turtle vs Hare Game");
        GamePanel gp = new GamePanel();
        setLayout(new BorderLayout());
        add(gp);


        //this will be the top panel that is displayed to get user input
        topPanel = new JPanel();
        label = new JLabel("Turtle Speed");
        topPanel.add(label);
        text = new JTextField(10);
        topPanel.add(text);
        submit = new JButton("Confirm");
        topPanel.add(submit);

        //add to north part of frame
        add(topPanel, BorderLayout.NORTH);


        //when user clicks confirm
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //converts the textfield into an int value
                int speed = Integer.parseInt(text.getText());

                if (label.getText() == "Turtle Speed") {
                    //set value in GamePanel object
                    gp.turtleDelay = speed;
                    gp.turtleThreadObj.setDelay(speed);
                    label.setText("Hare Speed");
                    text.setText("");
                } else if (label.getText() == "Hare Speed"){
                    //set value in GamePanel object
                    gp.hareStartDelay = speed;
                    gp.hareThreadObj.setStartDelay(speed);
                    label.setText("Hare Sleep Time");
                    text.setText("");

                } else if (label.getText() == "Hare Sleep Time"){
                    label.setText("Winner");
                    //set value in GamePanel
                    gp.hareStopDelay = speed;
                    gp.hareThreadObj.setStopDelay(speed);

                    //start the threads
                    gp.hareThread.start();
                    gp.turtleThread.start();
                    gp.paintingThread.start();
                    topPanel.remove(text);
                    topPanel.remove(submit);

                    remove(topPanel);
                }

            }
        });



    }
}