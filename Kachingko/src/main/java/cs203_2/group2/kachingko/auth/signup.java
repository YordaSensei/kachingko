package cs203_2.group2.kachingko.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import cs203_2.group2.kachingko.DBConnection;


public class signup extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(signup.class.getName());

    public signup() {
        initComponents();
        setSize(460, 680);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        signupbtn = new javax.swing.JButton();
        yesAccbtn = new javax.swing.JButton();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel3.setFont(new java.awt.Font("DM Sans", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(62, 83, 39));
        jLabel3.setText("Email");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(90, 340, 49, 24);

        emailField.setFont(new java.awt.Font("DM Sans", 0, 14)); // NOI18N
        emailField.setForeground(new java.awt.Color(62, 83, 39));
        emailField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        emailField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(62, 83, 39), 1, true));
        emailField.setCaretColor(new java.awt.Color(62, 83, 39));
        emailField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailFieldActionPerformed(evt);
            }
        });
        getContentPane().add(emailField);
        emailField.setBounds(90, 370, 280, 50);

        jLabel2.setFont(new java.awt.Font("DM Sans", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(62, 83, 39));
        jLabel2.setText("Password");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(90, 440, 86, 24);

        jLabel4.setFont(new java.awt.Font("DM Sans", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(62, 83, 39));
        jLabel4.setText("Username");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(90, 240, 90, 24);

        signupbtn.setBackground(new java.awt.Color(62, 83, 39));
        signupbtn.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        signupbtn.setForeground(new java.awt.Color(255, 255, 255));
        signupbtn.setText("SIGN UP");
        signupbtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        signupbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signupbtnActionPerformed(evt);
            }
        });
        getContentPane().add(signupbtn);
        signupbtn.setBounds(260, 550, 110, 40);

        yesAccbtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        yesAccbtn.setForeground(new java.awt.Color(62, 83, 39));
        yesAccbtn.setText("Already have an account?");
        yesAccbtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        yesAccbtn.setBorderPainted(false);
        yesAccbtn.setContentAreaFilled(false);
        yesAccbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        yesAccbtn.setFocusPainted(false);
        yesAccbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yesAccbtnActionPerformed(evt);
            }
        });
        getContentPane().add(yesAccbtn);
        yesAccbtn.setBounds(80, 560, 170, 19);

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
        usernameField.setBounds(90, 270, 280, 50);

        passwordField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(62, 83, 39), 1, true));
        getContentPane().add(passwordField);
        passwordField.setBounds(90, 470, 280, 50);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/signup.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, -20, 450, 680);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void emailFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailFieldActionPerformed

    private void usernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernameFieldActionPerformed

    private void yesAccbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yesAccbtnActionPerformed
       login login = new login();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_yesAccbtnActionPerformed

    private void signupbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupbtnActionPerformed
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (username.isEmpty() || username.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password); 
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Signup successful!");
                login login = new login();
                login.setVisible(true);
                this.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Signup failed: " + ex.getMessage());
            }
    }//GEN-LAST:event_signupbtnActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new signup().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JButton signupbtn;
    private javax.swing.JTextField usernameField;
    private javax.swing.JButton yesAccbtn;
    // End of variables declaration//GEN-END:variables
}
