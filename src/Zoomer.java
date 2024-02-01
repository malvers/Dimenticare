import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Zoomer extends JButton {

    private static final int yPos = 100;
    private double initZoom = 0.56;
    private double zoomFactor = initZoom;

    Image image;

    public Zoomer() {

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                zoomFactor = (e.getY() + yPos) * 100;
                System.out.println("Mouse y: " + zoomFactor);
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {

                switch (event.getKeyCode()) {
                    case KeyEvent.VK_Q:
                    case KeyEvent.VK_W:
                        if (event.isMetaDown()) {
                            System.exit(1);
                        }
                        break;
                    case KeyEvent.VK_UP:
                        zoomFactor *= 1.1;
                        break;
                    case KeyEvent.VK_DOWN:
                        zoomFactor /= 1.1;
                        break;
                    case KeyEvent.VK_ESCAPE:
                        zoomFactor = initZoom;
                        break;
                    case KeyEvent.VK_SPACE:
                        zoomFactor = 1.0;
                        break;
                }
                System.out.println("zoom: " + zoomFactor);
                repaint();
            }
        });

        image = Toolkit.getDefaultToolkit().getImage("/Users/malvers/IdeaProjects/Dimenticare/squirl.jpg");
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        int iw = (int) (image.getWidth(null) * zoomFactor);
        int ih = (int) (image.getHeight(null) * zoomFactor);
        int xPos = (int) ((getWidth() - iw) / 2.0);
        int yPos = (int) ((getHeight() - ih) / 2.0);
        g2d.drawImage(image, xPos, yPos, iw, ih, null);
    }

    public static void main(String[] args) {

        JFrame f = new JFrame();
        f.setSize(800, 800);
        int xPos = 400;
        f.setLocation(xPos, yPos);

        Zoomer z = new Zoomer();
        f.add(z);
        f.setVisible(true);
        f.revalidate();
        f.repaint();
        z.repaint();
    }
}
