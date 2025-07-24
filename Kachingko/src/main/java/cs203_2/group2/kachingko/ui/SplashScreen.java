package cs203_2.group2.kachingko.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class SplashScreen extends JFrame {

    private Image backgroundImage;
    private int width;
    private int height;

    public SplashScreen() {
        URL imageUrl = getClass().getResource("/images/splash.png");

        if (imageUrl == null) {
            JOptionPane.showMessageDialog(null,
                "Splash image not found!\nMake sure it's in src/main/resources/images.",
                "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        ImageIcon icon = new ImageIcon(imageUrl);
        backgroundImage = icon.getImage();

        width = icon.getIconWidth();
        height = icon.getIconHeight();

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        content.setPreferredSize(new Dimension(width, height));
        content.setLayout(null);

        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setBounds(20, height - 50, 200, 30);
        content.add(loadingLabel);

        setContentPane(content);
        setUndecorated(true);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        new Thread(() -> {
             try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                new Landing().setVisible(true);
                dispose(); 
            });
        }).start();

    }

   public static void main(String[] args) {
    SwingUtilities.invokeLater(SplashScreen::new);
   }

}
