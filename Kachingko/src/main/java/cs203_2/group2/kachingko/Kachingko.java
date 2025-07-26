/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package cs203_2.group2.kachingko;

import javax.swing.*;
import cs203_2.group2.kachingko.dashboard.ui.DashboardPanel;

public class Kachingko {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Kachinko - Credit Spending Analyzer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600); 

            
            DashboardPanel dashboard = new DashboardPanel(1);
            frame.setContentPane(dashboard);

            frame.setLocationRelativeTo(null); 
            frame.setVisible(true);
        });
    }
}

