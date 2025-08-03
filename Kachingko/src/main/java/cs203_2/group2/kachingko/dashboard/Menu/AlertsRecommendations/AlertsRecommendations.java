/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cs203_2.group2.kachingko.dashboard.Menu.AlertsRecommendations;
import cs203_2.group2.kachingko.DBConnection;
import cs203_2.group2.kachingko.auth.Session;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author ADMIN
 */
public class AlertsRecommendations extends javax.swing.JFrame {
private java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
private javax.swing.DefaultListModel<String> alertsModel;
private javax.swing.DefaultListModel<String> recommendationsModel;
private javax.swing.DefaultListModel<String> warningsModel;


    /**
     * Creates new form AlertsRecommendations
     */
    public AlertsRecommendations() {
        initComponents();
        setTitle("Alerts & Recommendations");
        setSize(460, 680);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setupListModels();
        loadAlerts();
        loadRecommendations();
    }
    
    private void setupListModels() {
    alertsModel = new javax.swing.DefaultListModel<>();
    recommendationsModel = new javax.swing.DefaultListModel<>();
    warningsModel = new javax.swing.DefaultListModel<>();
    
    alertsList.setModel(alertsModel);
    recommendationsList.setModel(recommendationsModel);
    customWarningsList.setModel(warningsModel);
    
    loadCategories();
    loadCustomWarnings();
}

   
private void loadAlerts() {
    alertsModel.clear();
    
    if (cs203_2.group2.kachingko.auth.Session.currentUserId == -1) return;
    
    try (java.sql.Connection conn = cs203_2.group2.kachingko.DBConnection.getConnection()) {
        java.util.List<String> alerts = new java.util.ArrayList<>();
        
        checkHighSpendingAlerts(conn, alerts);
        checkBudgetAlerts(conn, alerts);
        checkUnusualSpendingAlerts(conn, alerts);
        
        if (alerts.isEmpty()) {
            alertsModel.addElement("No current alerts. Your spending looks good!");
        } else {
            for (String alert : alerts) {
                alertsModel.addElement(alert);
            }
        }
        
    } catch (java.sql.SQLException ex) {
        alertsModel.addElement("Error loading alerts: " + ex.getMessage());
    }
}
    
private void loadRecommendations() {
    recommendationsModel.clear();
    
    if (cs203_2.group2.kachingko.auth.Session.currentUserId == -1) return;
    
    try (java.sql.Connection conn = cs203_2.group2.kachingko.DBConnection.getConnection()) {
        java.util.List<String> recommendations = new java.util.ArrayList<>();
        
        generateSpendingRecommendations(conn, recommendations);
        generateSavingsRecommendations(conn, recommendations);
        generateBudgetRecommendations(conn, recommendations);
        
        if (recommendations.isEmpty()) {
            recommendationsModel.addElement("No specific recommendations at this time.");
        } else {
            for (String recommendation : recommendations) {
                recommendationsModel.addElement(recommendation);
            }
        }
        
    } catch (java.sql.SQLException ex) {
        recommendationsModel.addElement("Error loading recommendations: " + ex.getMessage());
    }
}


private void loadCustomWarnings() {
    warningsModel.clear();
    
    if (cs203_2.group2.kachingko.auth.Session.currentUserId == -1) return;
    
    try (java.sql.Connection conn = cs203_2.group2.kachingko.DBConnection.getConnection()) {
        String query = "SELECT category, warning_amount FROM custom_warnings WHERE user_id = ? ORDER BY category";
        java.sql.PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, cs203_2.group2.kachingko.auth.Session.currentUserId);
        java.sql.ResultSet rs = stmt.executeQuery();
        
        boolean hasWarnings = false;
        while (rs.next()) {
            String category = rs.getString("category");
            double amount = rs.getDouble("warning_amount");
            warningsModel.addElement(category + ": ₱" + df.format(amount));
            hasWarnings = true;
        }
        
        if (!hasWarnings) {
            warningsModel.addElement("No custom warnings set.");
        }
        
    } catch (java.sql.SQLException ex) {
        warningsModel.addElement("Error loading custom warnings: " + ex.getMessage());
    }
}

private void loadCategories() {
    warningCategoryCombo.removeAllItems();
    warningCategoryCombo.addItem("All Categories");
    
    try (java.sql.Connection conn = cs203_2.group2.kachingko.DBConnection.getConnection()) {
        String query = "SELECT DISTINCT category FROM transactions WHERE user_id = ? ORDER BY category";
        java.sql.PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, cs203_2.group2.kachingko.auth.Session.currentUserId);
        java.sql.ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            String category = rs.getString("category");
            if (category != null && !category.trim().isEmpty()) {
                warningCategoryCombo.addItem(category);
            }
        }
    } catch (java.sql.SQLException ex) {
        System.err.println("Error loading categories: " + ex.getMessage());
    }
}

private void checkBudgetAlerts(java.sql.Connection conn, java.util.List<String> alerts) throws java.sql.SQLException {
    String query = "SELECT c.name, c.budget, COALESCE(SUM(ABS(t.amount)), 0) as spent " +
                  "FROM categories c " +
                  "LEFT JOIN transactions t ON c.name = t.category AND t.user_id = c.id " +
                  "AND MONTH(t.date) = MONTH(CURDATE()) AND t.amount < 0 " +
                  "WHERE c.id = ? AND c.budget > 0 " +
                  "GROUP BY c.category_id, c.name, c.budget " +
                  "HAVING spent > c.budget * 0.8";
    
    java.sql.PreparedStatement stmt = conn.prepareStatement(query);
    stmt.setInt(1, cs203_2.group2.kachingko.auth.Session.currentUserId);
    java.sql.ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
        String category = rs.getString("name");
        double budget = rs.getDouble("budget");
        double spent = rs.getDouble("spent");
        double percentage = (spent / budget) * 100;
        
        if (percentage >= 100) {
            alerts.add("Budget exceeded for " + category + ": ₱" + df.format(spent) + " / ₱" + df.format(budget));
        } else {
            alerts.add("Approaching budget limit for " + category + ": " + 
                      String.format("%.1f", percentage) + "% used");
        }
    }
}

private void checkHighSpendingAlerts(java.sql.Connection conn, java.util.List<String> alerts) throws java.sql.SQLException {
    String query = "SELECT category, SUM(ABS(amount)) as total " +
                  "FROM transactions " +
                  "WHERE user_id = ? AND amount < 0 AND MONTH(date) = MONTH(CURDATE()) " +
                  "GROUP BY category " +
                  "HAVING total > 10000 " +
                  "ORDER BY total DESC";
    
    java.sql.PreparedStatement stmt = conn.prepareStatement(query);
    stmt.setInt(1, cs203_2.group2.kachingko.auth.Session.currentUserId);
    java.sql.ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
        String category = rs.getString("category");
        double amount = rs.getDouble("total");
        alerts.add("High spending in " + category + ": ₱" + df.format(amount) + " this month");
    }
}

private void checkUnusualSpendingAlerts(java.sql.Connection conn, java.util.List<String> alerts) throws java.sql.SQLException {
    // Check for unusually high single transactions
    String query = "SELECT merchant, amount, date FROM transactions " +
                  "WHERE user_id = ? AND amount < -5000 AND DATE(date) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                  "ORDER BY amount ASC LIMIT 5";
    
    java.sql.PreparedStatement stmt = conn.prepareStatement(query);
    stmt.setInt(1, cs203_2.group2.kachingko.auth.Session.currentUserId);
    java.sql.ResultSet rs = stmt.executeQuery();
    
    while (rs.next()) {
        String merchant = rs.getString("merchant");
        double amount = Math.abs(rs.getDouble("amount"));
        alerts.add("Large transaction: ₱" + df.format(amount) + " at " + merchant);
    }
}

private void generateSpendingRecommendations(java.sql.Connection conn, java.util.List<String> recommendations) throws java.sql.SQLException {
    // Find highest spending category
    String query = "SELECT category, SUM(ABS(amount)) as total " +
                  "FROM transactions " +
                  "WHERE user_id = ? AND amount < 0 AND MONTH(date) = MONTH(CURDATE()) " +
                  "GROUP BY category ORDER BY total DESC LIMIT 1";
    
    java.sql.PreparedStatement stmt = conn.prepareStatement(query);
    stmt.setInt(1, cs203_2.group2.kachingko.auth.Session.currentUserId);
    java.sql.ResultSet rs = stmt.executeQuery();
    
    if (rs.next()) {
        String category = rs.getString("category");
        double amount = rs.getDouble("total");
        recommendations.add("Your highest spending is in " + category + " (₱" + df.format(amount) + 
                          "). Consider setting a budget for this category.");
    }
}

private void generateSavingsRecommendations(java.sql.Connection conn, java.util.List<String> recommendations) throws java.sql.SQLException {
    // Calculate potential savings
    recommendations.add("Try the 50/30/20 rule: 50% needs, 30% wants, 20% savings");
    recommendations.add("Review your subscriptions monthly to avoid unused services");
    recommendations.add("Set up automatic transfers to savings when you receive income");
}

private void generateBudgetRecommendations(java.sql.Connection conn, java.util.List<String> recommendations) throws java.sql.SQLException {
    // Check if user has budgets set
    String query = "SELECT COUNT(*) as budget_count FROM categories WHERE id = ? AND budget > 0";
    java.sql.PreparedStatement stmt = conn.prepareStatement(query);
    stmt.setInt(1, cs203_2.group2.kachingko.auth.Session.currentUserId);
    java.sql.ResultSet rs = stmt.executeQuery();
    
    if (rs.next() && rs.getInt("budget_count") == 0) {
        recommendations.add("Consider setting monthly budgets for your spending categories");
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        alertsHeader = new javax.swing.JLabel();
        alertsHeaderLabel = new javax.swing.JScrollPane();
        alertsList = new javax.swing.JList<>();
        recommendationsHeaderLabel = new javax.swing.JLabel();
        recommendationsScrollPane = new javax.swing.JScrollPane();
        recommendationsList = new javax.swing.JList<>();
        warningsHeaderLabel = new javax.swing.JLabel();
        menuBtn = new javax.swing.JButton();
        categoryLabel = new javax.swing.JLabel();
        warningCategoryCombo = new javax.swing.JComboBox<>();
        addWarningBtn = new javax.swing.JButton();
        customWarningsScrollPane = new javax.swing.JScrollPane();
        customWarningsList = new javax.swing.JList<>();
        refreshBtn = new javax.swing.JButton();
        warningAmountField = new javax.swing.JTextField();
        alertsRecommHeader = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(460, 680));
        setMinimumSize(new java.awt.Dimension(460, 680));
        setResizable(false);
        setSize(new java.awt.Dimension(460, 680));
        getContentPane().setLayout(null);

        alertsHeader.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        alertsHeader.setForeground(new java.awt.Color(55, 74, 34));
        alertsHeader.setText("CURRENT ALERTS:");
        getContentPane().add(alertsHeader);
        alertsHeader.setBounds(20, 110, 170, 19);

        alertsList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        alertsHeaderLabel.setViewportView(alertsList);

        getContentPane().add(alertsHeaderLabel);
        alertsHeaderLabel.setBounds(20, 150, 410, 85);

        recommendationsHeaderLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        recommendationsHeaderLabel.setForeground(new java.awt.Color(55, 74, 34));
        recommendationsHeaderLabel.setText("SMART TIPS:");
        getContentPane().add(recommendationsHeaderLabel);
        recommendationsHeaderLabel.setBounds(20, 250, 400, 19);

        recommendationsList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        recommendationsScrollPane.setViewportView(recommendationsList);

        getContentPane().add(recommendationsScrollPane);
        recommendationsScrollPane.setBounds(20, 280, 410, 85);

        warningsHeaderLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        warningsHeaderLabel.setForeground(new java.awt.Color(55, 74, 34));
        warningsHeaderLabel.setText("CUSTOM WARNING:");
        getContentPane().add(warningsHeaderLabel);
        warningsHeaderLabel.setBounds(20, 380, 400, 26);

        menuBtn.setBackground(new java.awt.Color(55, 74, 34));
        menuBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        menuBtn.setForeground(new java.awt.Color(255, 255, 255));
        menuBtn.setText("BACK");
        menuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnActionPerformed(evt);
            }
        });
        getContentPane().add(menuBtn);
        menuBtn.setBounds(320, 80, 110, 40);

        categoryLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        categoryLabel.setForeground(new java.awt.Color(55, 74, 34));
        categoryLabel.setText("CATEGORY");
        getContentPane().add(categoryLabel);
        categoryLabel.setBounds(20, 410, 69, 40);

        warningCategoryCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(warningCategoryCombo);
        warningCategoryCombo.setBounds(110, 410, 199, 22);

        addWarningBtn.setBackground(new java.awt.Color(55, 74, 34));
        addWarningBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        addWarningBtn.setForeground(new java.awt.Color(255, 255, 255));
        addWarningBtn.setText("ADD");
        addWarningBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWarningBtnActionPerformed(evt);
            }
        });
        getContentPane().add(addWarningBtn);
        addWarningBtn.setBounds(320, 410, 106, 55);

        customWarningsList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        customWarningsScrollPane.setViewportView(customWarningsList);

        getContentPane().add(customWarningsScrollPane);
        customWarningsScrollPane.setBounds(20, 480, 410, 100);

        refreshBtn.setBackground(new java.awt.Color(55, 74, 34));
        refreshBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        refreshBtn.setForeground(new java.awt.Color(255, 255, 255));
        refreshBtn.setText("REFRESH");
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });
        getContentPane().add(refreshBtn);
        refreshBtn.setBounds(275, 594, 160, 30);

        warningAmountField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warningAmountFieldActionPerformed(evt);
            }
        });
        getContentPane().add(warningAmountField);
        warningAmountField.setBounds(110, 440, 199, 22);

        alertsRecommHeader.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        alertsRecommHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alertsRecommHeader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/alertsBG.png"))); // NOI18N
        getContentPane().add(alertsRecommHeader);
        alertsRecommHeader.setBounds(-10, -10, 470, 680);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addWarningBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWarningBtnActionPerformed
     System.out.println("=== DEBUGGING SESSION ===");
    System.out.println("Session.currentUserId: " + cs203_2.group2.kachingko.auth.Session.currentUserId);
    
    // Check if user is logged in
    if (cs203_2.group2.kachingko.auth.Session.currentUserId == -1) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "You must be logged in to add custom warnings.",
            "Not Logged In", javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String category = (String) warningCategoryCombo.getSelectedItem();
    String amountText = warningAmountField.getText().trim();
    
    if (category == null || amountText.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Please select a category and enter an amount.",
            "Invalid Input", javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try {
        double amount = Double.parseDouble(amountText);
        if (amount <= 0) {
            throw new NumberFormatException();
        }
        
        // Save to database
        try (java.sql.Connection conn = cs203_2.group2.kachingko.DBConnection.getConnection()) {
            // Optional: Verify the user exists in the database
            String userCheckQuery = "SELECT id FROM users WHERE id = ?";
            try (java.sql.PreparedStatement userStmt = conn.prepareStatement(userCheckQuery)) {
                userStmt.setInt(1, cs203_2.group2.kachingko.auth.Session.currentUserId);
                java.sql.ResultSet userRs = userStmt.executeQuery();
                
                if (!userRs.next()) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                        "Current user not found in database. Please log in again.",
                        "User Validation Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Check if warning already exists for this user and category
            String checkQuery = "SELECT warning_id FROM custom_warnings WHERE user_id = ? AND category = ?";
            try (java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, cs203_2.group2.kachingko.auth.Session.currentUserId);
                checkStmt.setString(2, category);
                java.sql.ResultSet checkRs = checkStmt.executeQuery();
                
                if (checkRs.next()) {
                    // Update existing warning
                    String updateQuery = "UPDATE custom_warnings SET warning_amount = ? WHERE user_id = ? AND category = ?";
                    try (java.sql.PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setDouble(1, amount);
                        updateStmt.setInt(2, cs203_2.group2.kachingko.auth.Session.currentUserId);
                        updateStmt.setString(3, category);
                        updateStmt.executeUpdate();
                        
                        javax.swing.JOptionPane.showMessageDialog(this,
                            "Custom warning updated for " + category + " to ₱" + df.format(amount),
                            "Warning Updated", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // Insert new warning
                    String insertQuery = "INSERT INTO custom_warnings (user_id, category, warning_amount) VALUES (?, ?, ?)";
                    try (java.sql.PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, cs203_2.group2.kachingko.auth.Session.currentUserId);
                        insertStmt.setString(2, category);
                        insertStmt.setDouble(3, amount);
                        insertStmt.executeUpdate();
                        
                        javax.swing.JOptionPane.showMessageDialog(this,
                            "Custom warning added for " + category + " at ₱" + df.format(amount),
                            "Warning Added", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            
            warningAmountField.setText("");
            loadCustomWarnings();
            
        } catch (java.sql.SQLException ex) {
            System.err.println("SQL Error: " + ex.getMessage());
            System.err.println("SQL State: " + ex.getSQLState());
            System.err.println("Error Code: " + ex.getErrorCode());
            
            javax.swing.JOptionPane.showMessageDialog(this,
                "Error saving warning: " + ex.getMessage(),
                "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (NumberFormatException ex) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Please enter a valid positive number for the amount.",
            "Invalid Amount", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_addWarningBtnActionPerformed

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        loadAlerts();
        loadRecommendations();
    }//GEN-LAST:event_refreshBtnActionPerformed

    private void warningAmountFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warningAmountFieldActionPerformed
        addWarningBtnActionPerformed(evt);
    }//GEN-LAST:event_warningAmountFieldActionPerformed

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        try {
        cs203_2.group2.kachingko.dashboard.Menu.Menu menuWindow = 
            new cs203_2.group2.kachingko.dashboard.Menu.Menu();
        menuWindow.setVisible(true);
        this.dispose(); // Close current AlertsRecommendations window
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error opening Menu: " + e.getMessage(),
            "Navigation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_menuBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AlertsRecommendations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AlertsRecommendations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AlertsRecommendations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AlertsRecommendations.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AlertsRecommendations().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addWarningBtn;
    private javax.swing.JLabel alertsHeader;
    private javax.swing.JScrollPane alertsHeaderLabel;
    private javax.swing.JList<String> alertsList;
    private javax.swing.JLabel alertsRecommHeader;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JList<String> customWarningsList;
    private javax.swing.JScrollPane customWarningsScrollPane;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel recommendationsHeaderLabel;
    private javax.swing.JList<String> recommendationsList;
    private javax.swing.JScrollPane recommendationsScrollPane;
    private javax.swing.JButton refreshBtn;
    private javax.swing.JTextField warningAmountField;
    private javax.swing.JComboBox<String> warningCategoryCombo;
    private javax.swing.JLabel warningsHeaderLabel;
    // End of variables declaration//GEN-END:variables
}
