package CustomClass;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

// Create a custom button class that inherits from JButton
public class RoundedButton extends JButton {
    private Color currentBackgroundColor; // Stores the current background color
    private Color defaultBackgroundColor; // Stores the default background color
    private Color hoverBackgroundColor; // The background color when the mouse is hovering
    private Color pressedBackgroundColor; // The background color when the button is pressed

    public RoundedButton(String label) {
        super(label);
        // Set the button content area not to be filled with the default background color
        setContentAreaFilled(false);
        currentBackgroundColor = defaultBackgroundColor = new Color(220,225,237); // Initialize to the default color
        hoverBackgroundColor = new Color(200, 200, 200); // Initialize to the default hover color
        pressedBackgroundColor = new Color(150, 150, 150); // Initialize to default pressed color

        // Add mouse event listener to change button color
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentBackgroundColor = hoverBackgroundColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentBackgroundColor = defaultBackgroundColor; // Reset to default background color
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                currentBackgroundColor = pressedBackgroundColor;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentBackgroundColor = hoverBackgroundColor;
                repaint();
            }
        });
    }

    // Override the paintComponent method to draw the button contents
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(currentBackgroundColor); // Draw using the stored background color
        g.fillRoundRect(0, 0, getSize().width - 1, getSize().height - 1, 20, 20);
        super.paintComponent(g);
    }

    // Override the paintBorder method to draw the button border
    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
    }

    // Override the contains method to change the click area of the button
    @Override
    public boolean contains(int x, int y) {
        // Determine whether the point clicked is inside the rounded rectangle
        Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20);
        return shape.contains(x, y);
    }

    @Override
    public void setBackground(Color bg) {
        currentBackgroundColor = bg; // Update background color
        defaultBackgroundColor = bg;
        super.setBackground(bg); // Call the setBackground method of the parent class
        repaint(); // Request to redraw the button
    }

    // Set the background color when the mouse is hovering
    public void setHoverBackgroundColor(Color hoverColor) {
        hoverBackgroundColor = hoverColor;
    }

    // Set the background color when the button is pressed
    public void setPressedBackgroundColor(Color pressedColor) {
        pressedBackgroundColor = pressedColor;
    }
}