/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cs203_2.group2.kachingko.dashboard.ui;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel(int userId) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("📊 Monthly Spending Overview", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Main area with 3 sections (grid style)
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        centerPanel.setBackground(Color.WHITE);

        // Placeholder buttons/cards 
        JButton budgetButton = new JButton("🧾 Budget Planner");
        JButton trendlineButton = new JButton("📈 Spending Trendline");
        JButton creditButton = new JButton("💳 Credit Utilization");

        centerPanel.add(budgetButton);
        centerPanel.add(trendlineButton);
        centerPanel.add(creditButton);

        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.LIGHT_GRAY);
        menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        menuPanel.add(new JButton("🏠 Home"));
        menuPanel.add(new JButton("🔁 Refresh"));
        menuPanel.add(new JButton("🚪 Logout"));

        add(menuPanel, BorderLayout.SOUTH);
    }
}

