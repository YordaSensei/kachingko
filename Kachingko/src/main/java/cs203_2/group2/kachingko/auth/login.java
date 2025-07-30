package cs203_2.group2.kachingko.auth;
import cs203_2.group2.kachingko.dashboard.DashboardFrame;
import cs203_2.group2.kachingko.DBConnection;
import cs203_2.group2.kachingko.ui.uploadCSV;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
public class login extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(login.class.getName());

    public login() {
        initComponents();
        setSize(460, 680);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        loginbtn = new javax.swing.JButton();
        noAccbtn = new javax.swing.JButton();
        passwordField = new javax.swing.JPasswordField();
        usernameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("DM Sans", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(62, 83, 39));
        jLabel1.setText("Password");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(80, 400, 86, 24);

        jLabel3.setFont(new java.awt.Font("DM Sans", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(62, 83, 39));
        jLabel3.setText("Username");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(80, 290, 89, 24);

        loginbtn.setBackground(new java.awt.Color(62, 83, 39));
        loginbtn.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        loginbtn.setForeground(new java.awt.Color(255, 255, 255));
        loginbtn.setText("LOG IN");
        loginbtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loginbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginbtnActionPerformed(evt);
            }
        });
        getContentPane().add(loginbtn);
        loginbtn.setBounds(250, 510, 110, 40);

        noAccbtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        noAccbtn.setForeground(new java.awt.Color(62, 83, 39));
        noAccbtn.setText("Don't have an account?");
        noAccbtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        noAccbtn.setBorderPainted(false);
        noAccbtn.setContentAreaFilled(false);
        noAccbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        noAccbtn.setFocusPainted(false);
        noAccbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noAccbtnActionPerformed(evt);
            }
        });
        getContentPane().add(noAccbtn);
        noAccbtn.setBounds(70, 520, 170, 19);

        passwordField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(62, 83, 39), 1, true));
        getContentPane().add(passwordField);
        passwordField.setBounds(80, 430, 280, 50);

        usernameField.setFont(new java.awt.Font("DM Sans", 0, 14)); // NOI18N
        usernameField.setForeground(new java.awt.Color(62, 83, 39));
        usernameField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        usernameField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(62, 83, 39), 1, true));
        usernameField.setCaretColor(new java.awt.Color(62, 83, 39));
        usernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameFieldActionPerformed(evt);
            }
        });
        getContentPane().add(usernameField);
        usernameField.setBounds(80, 320, 280, 50);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loginbg.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(0, -10, 450, 670);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void usernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernameFieldActionPerformed

    private void noAccbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noAccbtnActionPerformed
        signup signup = new signup();
        signup.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_noAccbtnActionPerformed

    private void loginbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginbtnActionPerformed
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                Session.currentUserId = userId;

                String checkQuery = "SELECT COUNT(*) FROM transactions WHERE user_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setInt(1, userId);

                ResultSet checkRs = checkStmt.executeQuery();
                checkRs.next();
                int count = checkRs.getInt(1);

                JOptionPane.showMessageDialog(this, "Log in successful!");

                if (count == 0) {
                    new uploadCSV().setVisible(true);
                } else {
                     new DashboardFrame().setVisible(true);
                }

                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        } catch (SQLException ex) {
            System.getLogger(login.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_loginbtnActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton loginbtn;
    private javax.swing.JButton noAccbtn;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
