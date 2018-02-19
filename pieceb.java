import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JFrame;


public class pieceb extends JFrame
{
    public pieceb ()
    {
        setTitle("pieceb");
        setSize(960,960);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


        public void paint(Graphics g)
        {
    g.setColor(Color.BLACK);
    g.fillOval(480,480,200,200);
    
}
    
public static void main(String[] args)
{
    pieceb t = new pieceb();
    t.paint(null);
}
}