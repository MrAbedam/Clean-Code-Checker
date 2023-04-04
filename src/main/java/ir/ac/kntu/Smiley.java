package ir.ac.kntu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Smiley extends JPanel {

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.YELLOW);
        g.fillOval(50, 50, 200, 200);

        g.setColor(Color.BLACK);
        g.fillOval(90, 90, 30, 30);
        g.fillOval(180, 90, 30, 30);

        g.setColor(Color.RED);
        g.fillOval(135, 135, 30, 30);

        g.setColor(Color.BLACK);
        g.drawArc(90, 130, 120, 80, 200, 140);


        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Your code doesn't stink", 50, 300);
    }
}
