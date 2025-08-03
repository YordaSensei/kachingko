package cs203_2.group2.kachingko.ui;


import cs203_2.group2.kachingko.DBConnection;
import cs203_2.group2.kachingko.auth.Session;
import cs203_2.group2.kachingko.dashboard.DashboardFrame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class uploadCSV extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(uploadCSV.class.getName());

    public uploadCSV() {
        initComponents();
        setSize(460, 680);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        uploadbtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        uploadbtn.setBackground(new java.awt.Color(62, 83, 39));
        uploadbtn.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        uploadbtn.setForeground(new java.awt.Color(255, 255, 255));
        uploadbtn.setText("UPLOAD CSV");
        uploadbtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        uploadbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadbtnActionPerformed(evt);
            }
        });
        getContentPane().add(uploadbtn);
        uploadbtn.setBounds(130, 340, 200, 40);

        jLabel4.setFont(new java.awt.Font("DM Sans", 1, 20)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(62, 83, 39));
        jLabel4.setText("to get started");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(160, 390, 140, 27);

        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\Administrator\\Desktop\\Programming\\kachingko\\Kachingko\\src\\main\\resources\\images\\upload.png")); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, -30, 460, 710);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void uploadbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadbtnActionPerformed
       JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV or TXT Files", "csv", "txt"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                int count = 0;
                
                JOptionPane optionPane = new JOptionPane("Uploading transactions...", JOptionPane.INFORMATION_MESSAGE);
                final javax.swing.JDialog dialog = optionPane.createDialog(this, "Please Wait");
                dialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);

                new Thread(() -> {
                    dialog.setVisible(true);
                }).start();

                br.readLine(); 

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");

                    if (values.length < 7) continue;

                    try {
                        String txnId = values[0].trim();
                        String date = values[1].trim();
                        String merchant = values[2].trim();
                        String category = values[3].trim();
                        double amount = Double.parseDouble(values[4].trim());
                        String location = values[5].trim();
                        String cardType = values[6].trim();

                        insertTransaction(txnId, date, merchant, category, amount, location, cardType);
                        count++;

                    } catch (NumberFormatException nfe) {
                        logger.warning("Skipping row due to invalid amount: " + line);
                    } catch (Exception ex) {
                        logger.warning("Skipping row due to error: " + ex.getMessage());
                    }

                }

                dialog.setVisible(false);
                dialog.dispose();
                
                JOptionPane.showMessageDialog(this, "Uploaded " + count + " transactions!");

                java.awt.EventQueue.invokeLater(() -> {
                    new DashboardFrame().setVisible(true);
                    this.dispose();
                });
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_uploadbtnActionPerformed

private void insertTransaction(String txnId, String date, String merchant,
                               String category, double amount, String location, String cardType) {
    String sql = "INSERT INTO transactions (user_id, transaction_id, date, merchant, category, amount, location, card_type) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        java.util.Date parsedDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(date);
        java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

        stmt.setInt(1, Session.currentUserId);
        stmt.setString(2, txnId);
        stmt.setDate(3, sqlDate);
        stmt.setString(4, merchant);
        stmt.setString(5, category);
        stmt.setDouble(6, amount);
        stmt.setString(7, location);
        stmt.setString(8, cardType);

        stmt.executeUpdate();

    } catch (Exception e) {
        logger.warning("DB Insert Error for txnId " + txnId + ": " + e.getMessage());
    }
}
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new uploadCSV().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton uploadbtn;
    // End of variables declaration//GEN-END:variables
}
