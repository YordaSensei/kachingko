/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cs203_2.group2.kachingko.budget_planner;

import cs203_2.group2.kachingko.DBConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class manageGoals extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(manageGoals.class.getName());
    private int selectedGoalId = -1;
    
    public manageGoals() {
        initComponents();
        setSize(460, 680);
        setResizable(false);
        setLocationRelativeTo(null);
        
        goalsTable.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Goal ID", "Goal", "Target", "Current"}
        ));
        
        goalsTable.getSelectionModel().addListSelectionListener(event -> {
            int row = goalsTable.getSelectedRow();
            if (row >= 0) {
                selectedGoalId = (int) goalsTable.getValueAt(row, 0); // Get ID
                txtGoal.setText(goalsTable.getValueAt(row, 1).toString());
                txtTarget.setText(goalsTable.getValueAt(row, 2).toString());
            }
        });
        
        loadGoals();
        
        goalsTable.getColumnModel().getColumn(0).setMinWidth(0);
        goalsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        goalsTable.getColumnModel().getColumn(0).setWidth(0);
    }
    
    private void loadGoals() {
        DefaultTableModel model = (DefaultTableModel) goalsTable.getModel();
        model.setRowCount(0);

        String sql = "SELECT * FROM goals WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int userId = 2; // or dynamically passed
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int goalId = rs.getInt("goal_id");
                String name = rs.getString("name");
                double target = rs.getDouble("target_amount");
                double current = rs.getDouble("current_amount");

                // You can leave addedAmount and date blank initially
                model.addRow(new Object[]{goalId, name, target, current});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading goals: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        goalsTable = new javax.swing.JTable();
        txtGoal = new javax.swing.JTextField();
        createBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtTarget = new javax.swing.JTextField();
        backButton = new javax.swing.JButton();
        updateBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(450, 655));
        getContentPane().setLayout(null);

        goalsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Goal", "Target", "Current", "Added", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(goalsTable);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(10, 10, 430, 410);

        txtGoal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGoalActionPerformed(evt);
            }
        });
        getContentPane().add(txtGoal);
        txtGoal.setBounds(10, 470, 270, 37);

        createBtn.setText("CREATE");
        createBtn.setToolTipText("");
        createBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBtnActionPerformed(evt);
            }
        });
        getContentPane().add(createBtn);
        createBtn.setBounds(10, 520, 211, 50);

        deleteBtn.setText("DELETE");
        deleteBtn.setToolTipText("");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });
        getContentPane().add(deleteBtn);
        deleteBtn.setBounds(230, 520, 211, 50);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Target Amount:");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(290, 440, 120, 20);

        txtTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTargetActionPerformed(evt);
            }
        });
        getContentPane().add(txtTarget);
        txtTarget.setBounds(290, 470, 150, 37);

        backButton.setText("BACK");
        backButton.setToolTipText("");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        getContentPane().add(backButton);
        backButton.setBounds(230, 580, 210, 50);

        updateBtn.setText("UPDATE");
        updateBtn.setToolTipText("");
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });
        getContentPane().add(updateBtn);
        updateBtn.setBounds(10, 580, 210, 50);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Goal Description:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(10, 440, 150, 20);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 450, 660);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtGoalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGoalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGoalActionPerformed

    private void txtTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTargetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTargetActionPerformed

    private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBtnActionPerformed
        String name = txtGoal.getText();
        String targetText = txtTarget.getText();

        if (name.isEmpty() || targetText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            double target = Double.parseDouble(targetText);
            double current = 0.0;
            
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/kachingko", "root", "");

            String sql = "INSERT INTO goals (id, name, target_amount, current_amount) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, 2); // Replace with dynamic user ID if available
            pstmt.setString(2, name);
            pstmt.setDouble(3, target);
            pstmt.setDouble(4, current);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Goal created successfully!");
                
                txtGoal.setText("");
                txtTarget.setText("");
                
                loadGoals();
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_createBtnActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        logGoals logWindow = new logGoals();
        logWindow.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_backButtonActionPerformed

    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        String name = txtGoal.getText();
        String targetText = txtTarget.getText();

        if (selectedGoalId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a goal to update.");
            return;
        }

        if (name.isEmpty() || targetText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            double target = Double.parseDouble(targetText);
            
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/kachingko", "root", "");
            String sql = "UPDATE goals SET name = ?, target_amount = ? WHERE goal_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, target);
            pstmt.setInt(3, selectedGoalId);
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Goal updated successfully!");
                
                txtGoal.setText("");
                txtTarget.setText("");
                selectedGoalId = -1;
            
                loadGoals();
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_updateBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        if (selectedGoalId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a goal to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this goal?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/kachingko", "root", "");
            String sql = "DELETE FROM goals WHERE goal_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, selectedGoalId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Goal deleted successfully!");
                
                txtGoal.setText("");
                txtTarget.setText("");
                selectedGoalId = -1;
            
                loadGoals();
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_deleteBtnActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new manageGoals().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JButton createBtn;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JTable goalsTable;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtGoal;
    private javax.swing.JTextField txtTarget;
    private javax.swing.JButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
