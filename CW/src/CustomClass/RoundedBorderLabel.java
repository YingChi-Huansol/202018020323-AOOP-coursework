package CustomClass;
import javax.swing.*;
import java.awt.*;

// Create a custom class for JLabel with rounded borders
public class RoundedBorderLabel extends JLabel {
    private final int radius;

    public RoundedBorderLabel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    // Override the paintComponent method to draw rounded corners
    @Override
    protected void paintComponent(Graphics g) {
        Dimension arcs = new Dimension(radius, radius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded rectangles
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
        graphics.setColor(getForeground());
        // Center text
        super.paintComponent(g);
    }

    // Override the paintBorder method to draw a border
    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.BLACK);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(50, 50);
    }

}