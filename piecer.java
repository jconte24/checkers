import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JFrame;


public class piecer extends JFrame
{
    public piecer ()
    {
        setTitle("piecer");
        setSize(960,960);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


        public void paint(Graphics g)
        {
    g.setColor(Color.RED);
    g.fillOval(480,480,200,200);
    
}
    
public static void main(String[] args)
{
    piecer t = new piecer();
    t.paint(null);
}
}