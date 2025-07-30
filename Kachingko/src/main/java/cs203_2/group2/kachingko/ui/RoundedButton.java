/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cs203_2.group2.kachingko.ui;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JButton component with rounded corners and a flat, modern look.
 * This class overrides the paintComponent method to draw a button with rounded corners.
 *
 * Author: Jeff M.
 */
public class RoundedButton extends JButton {

    // These control how rounded the corners are
    private int arcWidth = 20;
    private int arcHeight = 20;

    /**
     * Constructor to create a RoundedButton with custom text.
     * Applies styling to make the button look flat and modern.
     *
     * @param text The text label of the button
     */
    public RoundedButton(String text) {
        super(text);
        setFocusPainted(false);            // Removes the default focus rectangle
        setContentAreaFilled(false);       // Prevents default button background painting
        setBorderPainted(false);           // Removes default button border
        setForeground(Color.WHITE);        // Text color
        setBackground(new Color(66, 133, 244)); // Background color (Google blue)
    }

    /**
     * Paints the rounded background and text of the button.
     * This method is automatically called when the button is rendered.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable anti-aliasing for smoother edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the button with a rounded rectangle background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);

        // Draw the button text and foreground elements
        super.paintComponent(g);

        g2.dispose();
    }
}

