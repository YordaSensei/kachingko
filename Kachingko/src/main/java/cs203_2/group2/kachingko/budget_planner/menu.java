package cs203_2.group2.kachingko.budget_planner;

import cs203_2.group2.kachingko.dashboard.DashboardFrame;
import cs203_2.group2.kachingko.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class menu extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(menu.class.getName());

    public menu() {
        initComponents();
        setSize(460, 680);
        setResizable(false);
        setLocationRelativeTo(null);
        
        categoryTable.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Category ID", "Category", "Budget", "Total", "Budget Left"}
        ));

        expenseTable.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Expense", "Amount", "Date"}
        ));

        loadCategories();
        
        categoryTable.getColumnModel().getColumn(0).setMinWidth(0);
        categoryTable.getColumnModel().getColumn(0).setMaxWidth(0);
        categoryTable.getColumnModel().getColumn(0).setWidth(0);
    }
    
    private void loadCategories() {
        DefaultTableModel model = (DefaultTableModel) categoryTable.getModel();
        model.setRowCount(0);

        String sql = "SELECT * FROM categories WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int userId = 2;
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No categories found for user ID: " + userId);
            }
            
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                String name = rs.getString("name");
                double budget = rs.getDouble("budget");
                double totalAmount = getTotalExpenses(categoryId);
                double currentBudget = getBudgetLeft(categoryId);
                
                model.addRow(new Object[]{categoryId, name, budget, totalAmount, currentBudget});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }
    
    private void loadExpenses(int categoryId) {
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        model.setRowCount(0);
        
        String sql = "SELECT name, amount, date FROM expenses WHERE category_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                double amount = rs.getDouble("amount");
                String date = rs.getString("date");

                model.addRow(new Object[]{name, amount, date});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage());
        }
    }
    
    private double getTotalExpenses(int categoryId) {
        double total = 0.0;
        String sql = "SELECT SUM(amount) AS total FROM expenses WHERE category_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error calculating total: " + e.getMessage());
        }

        return total;
    }
    
    private double getBudgetLeft(int categoryId) {
        double budget = 0.0;
        double totalExpenses = getTotalExpenses(categoryId);

        String sql = "SELECT budget FROM categories WHERE category_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                budget = rs.getDouble("budget");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving budget: " + e.getMessage());
        }

        return budget - totalExpenses;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        categoryTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        expenseTable = new javax.swing.JTable();
        catBtn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        goalTable = new javax.swing.JTable();
        expensesBtn = new javax.swing.JButton();
        goalsBtn = new javax.swing.JButton();
        notifBtn = new javax.swing.JButton();
        backBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        categoryTable.setFont(new java.awt.Font("DM Sans", 0, 12)); // NOI18N
        categoryTable.setForeground(new java.awt.Color(40, 54, 24));
        categoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Category", "Budget", "Total", "Budget Left"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        categoryTable.setGridColor(new java.awt.Color(40, 54, 24));
        categoryTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        categoryTable.setSelectionForeground(new java.awt.Color(40, 54, 24));
        categoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                categoryTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(categoryTable);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(10, 120, 426, 130);

        expenseTable.setFont(new java.awt.Font("DM Sans", 0, 12)); // NOI18N
        expenseTable.setForeground(new java.awt.Color(40, 54, 24));
        expenseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Expense", "Amount", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        expenseTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        expenseTable.setSelectionForeground(new java.awt.Color(40, 54, 24));
        jScrollPane2.setViewportView(expenseTable);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(10, 260, 426, 140);

        catBtn.setBackground(new java.awt.Color(40, 54, 24));
        catBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        catBtn.setForeground(new java.awt.Color(255, 255, 255));
        catBtn.setText("MANAGE CATEGORIES");
        catBtn.setToolTipText("");
        catBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                catBtnActionPerformed(evt);
            }
        });
        getContentPane().add(catBtn);
        catBtn.setBounds(10, 560, 207, 30);

        goalTable.setFont(new java.awt.Font("DM Sans", 0, 12)); // NOI18N
        goalTable.setForeground(new java.awt.Color(40, 54, 24));
        goalTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Goal", "Current Amount", "Target Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        goalTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        goalTable.setSelectionForeground(new java.awt.Color(40, 54, 24));
        jScrollPane3.setViewportView(goalTable);

        getContentPane().add(jScrollPane3);
        jScrollPane3.setBounds(10, 410, 426, 130);

        expensesBtn.setBackground(new java.awt.Color(40, 54, 24));
        expensesBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        expensesBtn.setForeground(new java.awt.Color(255, 255, 255));
        expensesBtn.setText("MANAGE EXPENSES");
        expensesBtn.setToolTipText("");
        expensesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expensesBtnActionPerformed(evt);
            }
        });
        getContentPane().add(expensesBtn);
        expensesBtn.setBounds(230, 560, 207, 30);

        goalsBtn.setBackground(new java.awt.Color(40, 54, 24));
        goalsBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        goalsBtn.setForeground(new java.awt.Color(255, 255, 255));
        goalsBtn.setText("LOG GOALS");
        goalsBtn.setToolTipText("");
        goalsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goalsBtnActionPerformed(evt);
            }
        });
        getContentPane().add(goalsBtn);
        goalsBtn.setBounds(10, 600, 207, 30);

        notifBtn.setBackground(new java.awt.Color(40, 54, 24));
        notifBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        notifBtn.setForeground(new java.awt.Color(255, 255, 255));
        notifBtn.setText("NOTIFICATIONS");
        notifBtn.setToolTipText("");
        notifBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notifBtnActionPerformed(evt);
            }
        });
        getContentPane().add(notifBtn);
        notifBtn.setBounds(230, 600, 207, 30);

        backBtn.setBackground(new java.awt.Color(40, 54, 24));
        backBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        backBtn.setForeground(new java.awt.Color(255, 255, 255));
        backBtn.setText("BACK TO DASHBOARD");
        backBtn.setToolTipText("");
        backBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });
        getContentPane().add(backBtn);
        backBtn.setBounds(279, 80, 150, 23);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/budgetplanner.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 450, 660);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed
        DashboardFrame dashboardWindow = new DashboardFrame();
        dashboardWindow.setVisible(true);

        this.setVisible(false);
    }//GEN-LAST:event_backBtnActionPerformed

    private void expensesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expensesBtnActionPerformed
        manageExpenses expensesWindow = new manageExpenses();
        expensesWindow.setVisible(true);

        this.setVisible(false);
    }//GEN-LAST:event_expensesBtnActionPerformed

    private void catBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_catBtnActionPerformed
        manageCat categoryWindow = new manageCat();
        categoryWindow.setVisible(true);

        this.setVisible(false);
    }//GEN-LAST:event_catBtnActionPerformed

    private void categoryTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categoryTableMouseClicked
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow != -1) {
            int categoryId = (int) categoryTable.getValueAt(selectedRow, 0); // Assumes category_id is in column 0
            loadExpenses(categoryId);
        }
    }//GEN-LAST:event_categoryTableMouseClicked


    private void notifBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notifBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_notifBtnActionPerformed

    private void goalsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goalsBtnActionPerformed
        logGoals logWindow = new logGoals();
        logWindow.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_goalsBtnActionPerformed


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
        java.awt.EventQueue.invokeLater(() -> new menu().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JButton catBtn;
    private javax.swing.JTable categoryTable;
    private javax.swing.JTable expenseTable;
    private javax.swing.JButton expensesBtn;
    private javax.swing.JTable goalTable;
    private javax.swing.JButton goalsBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton notifBtn;
    // End of variables declaration//GEN-END:variables
}
