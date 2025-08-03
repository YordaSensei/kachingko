/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cs203_2.group2.kachingko.monthlySpendingOverview;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import cs203_2.group2.kachingko.DBConnection;
import cs203_2.group2.kachingko.auth.Session;
import cs203_2.group2.kachingko.dashboard.DashboardFrame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;   

/**
 *
 * @author joy
 */
public class MonthlySpendingOverview extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MonthlySpendingOverview.class.getName());

    /**
     * Creates new form MonthlySpendingOverview
     */

    public MonthlySpendingOverview() {
        initComponents();
        setTitle("Monthly Spending Overview");
        setSize(460, 680);
        setResizable(false);
        setLocationRelativeTo(null);

        // Load data from DB
        loadMonthlySpendingData();
        
    }
    
    private void loadMonthlySpendingData() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Clear previous data

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Total Spent This Month
            double totalSpent = 0;
            String totalSpentQuery =
                    "SELECT SUM(amount) AS total FROM transactions " +
                    "WHERE user_id = ? AND MONTH(date) = MONTH(CURDATE()) AND YEAR(date) = YEAR(CURDATE())";
            try (PreparedStatement stmt = conn.prepareStatement(totalSpentQuery)) {
                stmt.setInt(1, Session.currentUserId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    totalSpent = rs.getDouble("total");
                    totalSpentMonth.setText(String.format("₱%.2f", totalSpent));
                } else {
                    totalSpentMonth.setText("₱0.00");
                }
            }

            // Insights Queries
            String topMerchantQuery =
                    "SELECT merchant, SUM(amount) AS total FROM transactions " +
                    "WHERE user_id = ? GROUP BY merchant ORDER BY total DESC LIMIT 1";
            String topCategoryQuery =
                    "SELECT category, SUM(amount) AS total FROM transactions " +
                    "WHERE user_id = ? GROUP BY category ORDER BY total DESC LIMIT 1";
            String highSpendingDayQuery =
                    "SELECT DATE(date) AS day, SUM(amount) AS total FROM transactions " +
                    "WHERE user_id = ? GROUP BY day ORDER BY total DESC LIMIT 1";
            String lowSpendingDayQuery =
                    "SELECT DATE(date) AS day, SUM(amount) AS total FROM transactions " +
                    "WHERE user_id = ? GROUP BY day ORDER BY total ASC LIMIT 1";

            String topMerchant = getSingleValue(conn, topMerchantQuery, "merchant");
            String topCategory = getSingleValue(conn, topCategoryQuery, "category");
            String highSpendingDay = getSingleValue(conn, highSpendingDayQuery, "day");
            String lowSpendingDay = getSingleValue(conn, lowSpendingDayQuery, "day");

            // Recurring Expenses and Anomaly Detection
            String recurringExpenses = getRecurringExpense(conn);
            String anomalyDetected = getAnomalyStatus(conn, totalSpent);

            // Add row with all values
            model.addRow(new Object[]{"Top Merchant", topMerchant != null ? topMerchant : "N/A"});
            model.addRow(new Object[]{"Top Category", topCategory != null ? topCategory : "N/A"});
            model.addRow(new Object[]{"High Spending Day", highSpendingDay != null ? highSpendingDay : "N/A"});
            model.addRow(new Object[]{"Low Spending Day", lowSpendingDay != null ? lowSpendingDay : "N/A"});
            model.addRow(new Object[]{"Recurring Expenses", recurringExpenses});
            model.addRow(new Object[]{"Anomaly Detector", anomalyDetected});


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String getSingleValue(Connection conn, String query, String columnLabel) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Session.currentUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(columnLabel);
            }
        }
        return null;
    }

    private String getRecurringExpense(Connection conn) throws SQLException {
        String query =
                "SELECT merchant FROM transactions " +
                "WHERE user_id = ? AND MONTH(date) = MONTH(CURDATE()) AND YEAR(date) = YEAR(CURDATE()) " +
                "GROUP BY merchant HAVING COUNT(*) >= 2 ORDER BY COUNT(*) DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Session.currentUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("merchant");
            }
        }
        return "None";
    }

    private String getAnomalyStatus(Connection conn, double currentTotal) throws SQLException {
        double avgSpent = 0;
        int monthCount = 0;
        String query =
                "SELECT AVG(monthly_total) AS avg_spent, COUNT(*) AS months FROM (" +
                " SELECT SUM(amount) AS monthly_total FROM transactions " +
                " WHERE user_id = ? GROUP BY YEAR(date), MONTH(date) " +
                " ORDER BY YEAR(date) DESC, MONTH(date) DESC LIMIT 6" +
                ") AS subquery";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Session.currentUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                avgSpent = rs.getDouble("avg_spent");
                monthCount = rs.getInt("months");
            }
        }

        if (monthCount < 2) {
            return "Not enough data";
        }

        if (avgSpent > 0 && currentTotal > avgSpent * 1.5) {
            return "Yes (↑ higher than usual)";
        } else {
            return "No";
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

        totalSpent = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        totalSpentMonth = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        backToDashboard = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        totalSpent.setBackground(new java.awt.Color(255, 255, 255));
        totalSpent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 108, 56)));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(55, 74, 34));
        jLabel1.setText("Total spent this month:");

        totalSpentMonth.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        totalSpentMonth.setForeground(new java.awt.Color(96, 108, 56));
        totalSpentMonth.setText("₱0.00");

        javax.swing.GroupLayout totalSpentLayout = new javax.swing.GroupLayout(totalSpent);
        totalSpent.setLayout(totalSpentLayout);
        totalSpentLayout.setHorizontalGroup(
            totalSpentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalSpentLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel1)
                .addGap(32, 32, 32)
                .addComponent(totalSpentMonth)
                .addContainerGap(108, Short.MAX_VALUE))
        );
        totalSpentLayout.setVerticalGroup(
            totalSpentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalSpentLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(totalSpentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(totalSpentMonth))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        getContentPane().add(totalSpent);
        totalSpent.setBounds(30, 130, 390, 60);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 108, 56)));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Insight","Value"}
        ));
        jTable1.setGridColor(new java.awt.Color(96, 108, 56));
        jTable1.setSelectionBackground(new java.awt.Color(255, 255, 255));
        jTable1.setSelectionForeground(new java.awt.Color(96, 108, 56));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 210, 390, 350);

        backToDashboard.setBackground(new java.awt.Color(96, 108, 56));
        backToDashboard.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        backToDashboard.setForeground(new java.awt.Color(255, 255, 255));
        backToDashboard.setText("BACK TO DASHBOARD");
        backToDashboard.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        backToDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backToDashboardActionPerformed(evt);
            }
        });
        getContentPane().add(backToDashboard);
        backToDashboard.setBounds(259, 583, 160, 30);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/monthlyBG.png"))); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(0, 0, 450, 660);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backToDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backToDashboardActionPerformed
        DashboardFrame dashboardWindow = new DashboardFrame();
        dashboardWindow.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_backToDashboardActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */

        java.awt.EventQueue.invokeLater(() -> new MonthlySpendingOverview().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backToDashboard;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel totalSpent;
    private javax.swing.JLabel totalSpentMonth;
    // End of variables declaration//GEN-END:variables
}