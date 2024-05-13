package CustomClass;

import javax.swing.*;
import java.awt.*;

// Create a custom text box class that inherits from JTextField
public class SpacedJTextField extends JTextField {
    private final float letterSpacing;

    // Constructor to specify word spacing
    public SpacedJTextField(float letterSpacing) {
        this.letterSpacing = letterSpacing;
        // Set the background color to white
        this.setBackground(Color.WHITE);
        // Set to opaque to show the background color
        this.setOpaque(true);
    }

    // Override the paintComponent method to customize text painting
    @Override
    protected void paintComponent(Graphics g) {
        // Manual background drawing
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(getFont());

        FontMetrics fm = g2d.getFontMetrics();
        String text = getText();
        int x = (int)letterSpacing/2;

        g2d.setColor(getForeground()); // Use the component's foreground color

        // Draws each character according to letterSpacing
        for (char c : text.toCharArray()) {
            String s = String.valueOf(c);
            g2d.drawString(s, x, fm.getAscent());
            x += fm.stringWidth(s) + letterSpacing;
        }

        g2d.dispose();
    }
}
