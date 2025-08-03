/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cs203_2.group2.kachingko.budget_planner;

import cs203_2.group2.kachingko.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class manageExpenses extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(manageExpenses.class.getName());
    private int selectedExpenseId = -1;
    
    public manageExpenses() {
        initComponents();
        setLocationRelativeTo(null);
        
        categoryTable.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Category ID", "Category", "Budget", "Total", "Budget Left"}
        ));
        
        expenseTable.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Expense ID", "Expense", "Amount", "Date"}
        ));
        
        expenseTable.getSelectionModel().addListSelectionListener(event -> {
            int row = expenseTable.getSelectedRow();
            if (row >= 0) {
                selectedExpenseId = (int) expenseTable.getValueAt(row, 0); // Get ID
                txtName.setText(expenseTable.getValueAt(row, 1).toString());
                txtAmount.setText(expenseTable.getValueAt(row, 2).toString());
            }
        });
        
        loadCategories();
        
        categoryTable.getColumnModel().getColumn(0).setMinWidth(0);
        categoryTable.getColumnModel().getColumn(0).setMaxWidth(0);
        categoryTable.getColumnModel().getColumn(0).setWidth(0);
        
        expenseTable.getColumnModel().getColumn(0).setMinWidth(0);
        expenseTable.getColumnModel().getColumn(0).setMaxWidth(0);
        expenseTable.getColumnModel().getColumn(0).setWidth(0);
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
                JOptionPane.showMessageDialog(this, "No categories found.");
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

        String sql = "SELECT * FROM expenses WHERE category_id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No expenses found for user ID: " + categoryId);
            }
            
            while (rs.next()) {
                int expenseId = rs.getInt("expense_id");
                String name = rs.getString("name");
                double amount = rs.getDouble("amount");
                Date date = rs.getDate("date");
                
                model.addRow(new Object[]{expenseId, name, amount, date});
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

        jScrollPane2 = new javax.swing.JScrollPane();
        categoryTable = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        expenseTable = new javax.swing.JTable();
        txtName = new javax.swing.JTextField();
        createBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        backButton = new javax.swing.JButton();
        updateBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        categoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Category", "Budget", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        categoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                categoryTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(categoryTable);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(6, 12, 440, 135);

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
        jScrollPane1.setViewportView(expenseTable);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(6, 153, 440, 290);

        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });
        getContentPane().add(txtName);
        txtName.setBounds(6, 481, 211, 37);

        createBtn.setText("Create");
        createBtn.setToolTipText("");
        createBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBtnActionPerformed(evt);
            }
        });
        getContentPane().add(createBtn);
        createBtn.setBounds(6, 536, 211, 50);

        deleteBtn.setText("Delete");
        deleteBtn.setToolTipText("");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });
        getContentPane().add(deleteBtn);
        deleteBtn.setBounds(6, 592, 211, 50);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Expense Name:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(6, 449, 92, 20);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Amount:");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(235, 449, 53, 20);

        txtAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAmountActionPerformed(evt);
            }
        });
        getContentPane().add(txtAmount);
        txtAmount.setBounds(235, 481, 211, 37);

        backButton.setText("Back");
        backButton.setToolTipText("");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        getContentPane().add(backButton);
        backButton.setBounds(235, 592, 211, 50);

        updateBtn.setText("Update");
        updateBtn.setToolTipText("");
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });
        getContentPane().add(updateBtn);
        updateBtn.setBounds(235, 536, 211, 50);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

    private void txtAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAmountActionPerformed

    private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBtnActionPerformed
        String name = txtName.getText();
        String amountText = txtAmount.getText();

        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category first.");
            return;
        }
        
        if (name.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for amount.");
            return;
        }
        
        int categoryId = (int) categoryTable.getValueAt(selectedRow, 0);
        
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/kachingko", "root", "");

            String sql = "INSERT INTO expenses (id, category_id, name, amount, date) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, 2); // Replace with dynamic user ID if available
            pstmt.setInt(2, categoryId);
            pstmt.setString(3, name);
            pstmt.setDouble(4, amount);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Expense created successfully!");
                
                txtName.setText("");
                txtAmount.setText("");
                
                loadExpenses(categoryId);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_createBtnActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        menu menuWindow = new menu();
        menuWindow.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_backButtonActionPerformed

    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        String name = txtName.getText();
        String amountText = txtAmount.getText();

        if (selectedExpenseId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to update.");
            return;
        }

        if (name.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/kachingko", "root", "");
            String sql = "UPDATE expenses SET name = ?, amount = ?, date = NOW() WHERE expense_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, amount);
            pstmt.setInt(3, selectedExpenseId);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Expense updated successfully!");
                
                txtName.setText("");
                txtAmount.setText("");
                selectedExpenseId = -1;
            
                int selectedCategoryRow = categoryTable.getSelectedRow();
                if (selectedCategoryRow != -1) {
                    int categoryId = (int) categoryTable.getValueAt(selectedCategoryRow, 0);
                    loadExpenses(categoryId); // Refresh only current category
                }
            }
            conn.close();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for amount.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_updateBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        if (selectedExpenseId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/kachingko", "root", "");
            String sql = "DELETE FROM expenses WHERE expense_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, selectedExpenseId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Category deleted successfully!");
                
                txtName.setText("");
                txtAmount.setText("");
                selectedExpenseId = -1;
            
                int selectedCategoryRow = categoryTable.getSelectedRow();
                if (selectedCategoryRow != -1) {
                    int categoryId = (int) categoryTable.getValueAt(selectedCategoryRow, 0);
                    loadExpenses(categoryId); // Reload expenses for the selected category
                }
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void categoryTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categoryTableMouseClicked
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow != -1) {
            int categoryId = (int) categoryTable.getValueAt(selectedRow, 0);
            loadExpenses(categoryId);
        }
    }//GEN-LAST:event_categoryTableMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new manageExpenses().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JTable categoryTable;
    private javax.swing.JButton createBtn;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JTable expenseTable;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtName;
    private javax.swing.JButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
