package cs203_2.group2.kachingko;

import cs203_2.group2.kachingko.ui.SplashScreen;
import javax.swing.SwingUtilities;

public class Kachingko {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SplashScreen().setVisible(true); 
        });
    }
}
