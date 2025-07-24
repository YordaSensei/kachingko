package cs203_2.group2.kachingko.ui;

import cs203_2.group2.kachingko.auth.login;
import cs203_2.group2.kachingko.auth.signup;

public class Landing extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Landing.class.getName());

    public Landing() {
        initComponents();
        setSize(460, 680);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        signupbtn = new javax.swing.JButton();
        loginbtn2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        signupbtn.setBackground(new java.awt.Color(62, 83, 39));
        signupbtn.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        signupbtn.setForeground(new java.awt.Color(255, 255, 255));
        signupbtn.setText("SIGN UP");
        signupbtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        signupbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signupbtnActionPerformed(evt);
            }
        });
        getContentPane().add(signupbtn);
        signupbtn.setBounds(100, 550, 240, 40);

        loginbtn2.setBackground(new java.awt.Color(62, 83, 39));
        loginbtn2.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        loginbtn2.setForeground(new java.awt.Color(255, 255, 255));
        loginbtn2.setText("LOG IN");
        loginbtn2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loginbtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginbtn2ActionPerformed(evt);
            }
        });
        getContentPane().add(loginbtn2);
        loginbtn2.setBounds(100, 500, 240, 40);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/landingbg.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, -20, 450, 690);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void signupbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupbtnActionPerformed
        signup signup = new signup();
        signup.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_signupbtnActionPerformed

    private void loginbtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginbtn2ActionPerformed
        login login = new login();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_loginbtn2ActionPerformed

    public static void main(String args[]) {
  
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton loginbtn2;
    private javax.swing.JButton signupbtn;
    // End of variables declaration//GEN-END:variables
}
