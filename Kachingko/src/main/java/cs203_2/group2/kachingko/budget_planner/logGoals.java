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

public class logGoals extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(logGoals.class.getName());
    private int selectedGoalId = -1;
    
    public logGoals() {
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
                txtName.setText(goalsTable.getValueAt(row, 1).toString());
            }
        });
        
        loadGoals();
        
        historyTable.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Goal ID", "Target", "Current", "Added", "Date"}
        ));
        
        goalsTable.getColumnModel().getColumn(0).setMinWidth(0);
        goalsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        goalsTable.getColumnModel().getColumn(0).setWidth(0);
        
        historyTable.getColumnModel().getColumn(0).setMinWidth(0);
        historyTable.getColumnModel().getColumn(0).setMaxWidth(0);
        historyTable.getColumnModel().getColumn(0).setWidth(0);
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

                model.addRow(new Object[]{goalId, name, target, current, "", ""});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading goals: " + e.getMessage());
        }
    }
    
    private void loadGoalLogs(int goalId) {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0); // Clear previous logs

        String sql = """
        SELECT g.name, g.target_amount, g.current_amount, gl.added_amount, gl.date_time
        FROM goal_logs gl
        JOIN goals g ON gl.goal_id = g.goal_id
        WHERE gl.goal_id = ?
        ORDER BY gl.date_time DESC
        """;

        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

           stmt.setInt(1, goalId);
           ResultSet rs = stmt.executeQuery();

           while (rs.next()) {
               model.addRow(new Object[]{
                   rs.getString("name"),
                   rs.getDouble("target_amount"),
                   rs.getDouble("current_amount"),
                   rs.getDouble("added_amount"),
                   rs.getTimestamp("date_time")
               });
           }

       } catch (SQLException e) {
           JOptionPane.showMessageDialog(this, "Error loading goal logs: " + e.getMessage());
       }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        goalsTable = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        historyTable = new javax.swing.JTable();
        txtName = new javax.swing.JTextField();
        goalsBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        addBtn = new javax.swing.JButton();
        txtAmount = new javax.swing.JTextField();
        backButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(450, 655));
        getContentPane().setLayout(null);

        goalsTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 108, 56)));
        goalsTable.setFont(new java.awt.Font("DM Sans", 0, 12)); // NOI18N
        goalsTable.setForeground(new java.awt.Color(96, 108, 56));
        goalsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Goal", "Target", "Current"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        goalsTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        goalsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                goalsTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(goalsTable);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(20, 140, 410, 110);

        historyTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 108, 56)));
        historyTable.setFont(new java.awt.Font("DM Sans", 0, 12)); // NOI18N
        historyTable.setForeground(new java.awt.Color(96, 108, 56));
        historyTable.setModel(new javax.swing.table.DefaultTableModel(
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
        historyTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(historyTable);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 260, 410, 190);

        txtName.setFont(new java.awt.Font("DM Sans", 0, 12)); // NOI18N
        txtName.setForeground(new java.awt.Color(96, 108, 56));
        txtName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 108, 56)));
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });
        getContentPane().add(txtName);
        txtName.setBounds(170, 490, 260, 37);

        goalsBtn.setBackground(new java.awt.Color(96, 108, 56));
        goalsBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        goalsBtn.setForeground(new java.awt.Color(255, 255, 255));
        goalsBtn.setText("MANAGE GOALS");
        goalsBtn.setToolTipText("");
        goalsBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        goalsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goalsBtnActionPerformed(evt);
            }
        });
        getContentPane().add(goalsBtn);
        goalsBtn.setBounds(20, 590, 410, 40);

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(96, 108, 56));
        jLabel2.setText("SELECT A GOAL:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 110, 100, 17);

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(96, 108, 56));
        jLabel4.setText("ADD AMOUNT:");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(30, 460, 120, 19);

        addBtn.setBackground(new java.awt.Color(96, 108, 56));
        addBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        addBtn.setForeground(new java.awt.Color(255, 255, 255));
        addBtn.setText("ADD AMOUNT");
        addBtn.setToolTipText("");
        addBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });
        getContentPane().add(addBtn);
        addBtn.setBounds(20, 540, 200, 40);

        txtAmount.setFont(new java.awt.Font("DM Sans", 0, 12)); // NOI18N
        txtAmount.setForeground(new java.awt.Color(96, 108, 56));
        txtAmount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 108, 56)));
        txtAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAmountActionPerformed(evt);
            }
        });
        getContentPane().add(txtAmount);
        txtAmount.setBounds(20, 490, 140, 37);

        backButton.setBackground(new java.awt.Color(96, 108, 56));
        backButton.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        backButton.setForeground(new java.awt.Color(255, 255, 255));
        backButton.setText("BACK");
        backButton.setToolTipText("");
        backButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        getContentPane().add(backButton);
        backButton.setBounds(230, 540, 200, 40);

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(96, 108, 56));
        jLabel3.setText("GOAL DESCRIPTION:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(170, 460, 150, 19);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon("C:\\Users\\Administrator\\Desktop\\Programming\\kachingko\\Kachingko\\src\\main\\resources\\images\\logGoalsBG.png")); // NOI18N
        getContentPane().add(jLabel5);
        jLabel5.setBounds(0, 0, 450, 660);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

    private void txtAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAmountActionPerformed

    private void goalsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goalsBtnActionPerformed
        manageGoals goalsWindow = new manageGoals();
        goalsWindow.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_goalsBtnActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        menu menuWindow = new menu();
        menuWindow.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_backButtonActionPerformed

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        int selectedRow = goalsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a goal to add amount.");
            return;
        }

        try {
            int goalId = (int) goalsTable.getValueAt(selectedRow, 0); // goal_id in column 0
            double addedAmount = Double.parseDouble(txtAmount.getText());

            String updateGoalSQL = "UPDATE goals SET current_amount = current_amount + ? WHERE goal_id = ?";
            try (Connection conn = DBConnection.getConnection();
            PreparedStatement updateStmt = conn.prepareStatement(updateGoalSQL)) {
                updateStmt.setDouble(1, addedAmount);
                updateStmt.setInt(2, goalId);
                updateStmt.executeUpdate();
            }

            String insertLogSQL = "INSERT INTO goal_logs (goal_id, added_amount, date_time) VALUES (?, ?, NOW())";
            try (Connection conn = DBConnection.getConnection();
            PreparedStatement insertStmt = conn.prepareStatement(insertLogSQL)) {
                insertStmt.setInt(1, goalId);
                insertStmt.setDouble(2, addedAmount);
                insertStmt.executeUpdate();
            }

            loadGoals();
            loadGoalLogs(goalId);

            txtAmount.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating goal: " + e.getMessage());
        }
    }//GEN-LAST:event_addBtnActionPerformed

    private void goalsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_goalsTableMouseClicked
        int selectedRow = goalsTable.getSelectedRow();
        if (selectedRow != -1) {
            int goalId = (int) goalsTable.getValueAt(selectedRow, 0); // Assumes goal_id is in column 0
            loadGoalLogs(goalId);
        }
    }//GEN-LAST:event_goalsTableMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new logGoals().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton backButton;
    private javax.swing.JButton goalsBtn;
    private javax.swing.JTable goalsTable;
    private javax.swing.JTable historyTable;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
