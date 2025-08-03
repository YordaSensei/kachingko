/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cs203_2.group2.kachingko.dashboard.Menu.ProfileSettings;
import cs203_2.group2.kachingko.DBConnection;
import cs203_2.group2.kachingko.auth.Session;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class ProfileSettings extends javax.swing.JFrame {

    /**
     * Creates new form AlertsRecommendations
     */
    public ProfileSettings() {
        initComponents();
        setSize(460, 680);
        setResizable(false);
        setLocationRelativeTo(null);
        loadUserData();
        setupComboBoxes();
    }
    private void loadUserData() {
    if (Session.currentUserId == -1) return;
    
    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT username, email FROM users WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, Session.currentUserId);
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            usernameField.setText(rs.getString("username"));
            emailField.setText(rs.getString("email"));
            usernameDisplayLabel.setText(rs.getString("username"));
        }
        
        // Load user settings/preferences if they exist
        loadUserSettings();
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error loading user data: " + ex.getMessage(),
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void loadUserSettings() {
    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT * FROM user_settings WHERE user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, Session.currentUserId);
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            // Set currency combo
            String currency = rs.getString("currency");
            if (currency != null) {
                currencyCombo.setSelectedItem(currency);
            }
            
            // Set notification preferences
            emailNotificationsCheck.setSelectedItem(rs.getBoolean("email_notifications") ? "Yes" : "No");
            pushNotificationsCheck.setSelectedItem(rs.getBoolean("push_notifications") ? "Yes" : "No");
            smsNotificationsCheck.setSelectedItem(rs.getBoolean("sms_notifications") ? "Yes" : "No");
            marketingCheck.setSelectedItem(rs.getBoolean("marketing_emails") ? "Yes" : "No");
        }
    } catch (SQLException ex) {
        // Settings table might not exist or no settings saved yet
        System.out.println("No user settings found or error loading: " + ex.getMessage());
    }
}

private void setupComboBoxes() {
    // Setup notification combo boxes
    String[] yesNo = {"No", "Yes"};
    
    emailNotificationsCheck.setModel(new DefaultComboBoxModel<>(yesNo));
    emailNotificationsCheck.setSelectedItem("Yes");
    
    pushNotificationsCheck.setModel(new DefaultComboBoxModel<>(yesNo));
    pushNotificationsCheck.setSelectedItem("Yes");
    
    smsNotificationsCheck.setModel(new DefaultComboBoxModel<>(yesNo));
    smsNotificationsCheck.setSelectedItem("No");
    
    marketingCheck.setModel(new DefaultComboBoxModel<>(yesNo));
    marketingCheck.setSelectedItem("No");
}

private void importCSVFile(File csvFile) {
    if (Session.currentUserId == -1) {
        JOptionPane.showMessageDialog(this, "No user is logged in. Cannot import CSV.");
        return;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
         Connection conn = DBConnection.getConnection()) {

        // 🧹 Delete existing transactions for the current user
        PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM transactions WHERE user_id = ?");
        deleteStmt.setInt(1, Session.currentUserId);
        deleteStmt.executeUpdate();

        String line;
        int lineCount = 0;
        int importedCount = 0;

        // Show loading dialog
        JOptionPane optionPane = new JOptionPane("Importing transactions...", JOptionPane.INFORMATION_MESSAGE);
        final javax.swing.JDialog dialog = optionPane.createDialog(this, "Please Wait");
        dialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
        new Thread(() -> dialog.setVisible(true)).start();

        br.readLine(); // Skip header line

        String insertQuery = """
            INSERT INTO transactions (user_id, transaction_id, date, merchant, category, amount, location, card_type)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        PreparedStatement stmt = conn.prepareStatement(insertQuery);

        while ((line = br.readLine()) != null) {
            lineCount++;
            String[] parts = line.split(",");

            if (parts.length < 7) continue;

            try {
                String txnId = parts[0].trim();
                String rawDate = parts[1].trim();
                String merchant = parts[2].trim();
                String category = parts[3].trim();
                double amount = Double.parseDouble(parts[4].trim());
                String location = parts[5].trim();
                String cardType = parts[6].trim();

                java.util.Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(rawDate);
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
                importedCount++;

            } catch (Exception e) {
                System.out.println("Line " + lineCount + " error: " + e.getMessage());
            }
        }

        dialog.setVisible(false);
        dialog.dispose();

        JOptionPane.showMessageDialog(this,
            "Import complete!\nProcessed: " + lineCount + "\nImported: " + importedCount,
            "Done", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Failed to import CSV: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}


private void exportToCSV(File file) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
        // Write header
        writer.println("Date,Description,Amount,Category,Card Type");
        
        // Export transactions
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Session.currentUserId);
            
            ResultSet rs = stmt.executeQuery();
            int exportCount = 0;
            
            while (rs.next()) {
                writer.printf("%s,%s,%.2f,%s,%s%n",
                    rs.getString("date"),
                    "\"" + rs.getString("description") + "\"",
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getString("card_type") != null ? rs.getString("card_type") : ""
                );
                exportCount++;
            }
            
            JOptionPane.showMessageDialog(this,
                "Data exported successfully!\n" +
                "File: " + file.getName() + "\n" +
                "Records exported: " + exportCount,
                "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                
        }
    } catch (IOException | SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error exporting data: " + ex.getMessage(),
            "Export Error", JOptionPane.ERROR_MESSAGE);
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        profileInfoHeader = new javax.swing.JLabel();
        usernameDisplayLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        emailLabel = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        currPassLabel = new javax.swing.JLabel();
        newPassLabel = new javax.swing.JLabel();
        currentPasswordField = new javax.swing.JPasswordField();
        newPasswordField = new javax.swing.JPasswordField();
        saveAccDetailsBtn = new javax.swing.JToggleButton();
        settingsHeader = new javax.swing.JLabel();
        currencyLabel = new javax.swing.JLabel();
        currencyCombo = new javax.swing.JComboBox<>();
        emailNotifsLabel = new javax.swing.JLabel();
        emailNotificationsCheck = new javax.swing.JComboBox<>();
        pushNotifsLabel = new javax.swing.JLabel();
        pushNotificationsCheck = new javax.swing.JComboBox<>();
        smsNotifsLabel = new javax.swing.JLabel();
        smsNotificationsCheck = new javax.swing.JComboBox<>();
        marketingCheck = new javax.swing.JComboBox<>();
        profileSettingsHeader12 = new javax.swing.JLabel();
        menuBtn = new javax.swing.JButton();
        updatecsvBtn = new javax.swing.JButton();
        updatecsvLabel = new javax.swing.JLabel();
        exportDataLabel = new javax.swing.JLabel();
        exportDataBtn = new javax.swing.JButton();
        saveSettingsBtn = new javax.swing.JToggleButton();
        profileSettingsHeader = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(460, 680));
        setMinimumSize(new java.awt.Dimension(460, 680));
        setResizable(false);
        setSize(new java.awt.Dimension(460, 680));
        getContentPane().setLayout(null);

        profileInfoHeader.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        profileInfoHeader.setForeground(new java.awt.Color(96, 108, 56));
        profileInfoHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        profileInfoHeader.setText("PROFILE INFORMATION");
        profileInfoHeader.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 108, 56)));
        getContentPane().add(profileInfoHeader);
        profileInfoHeader.setBounds(30, 120, 400, 30);

        usernameDisplayLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        usernameDisplayLabel.setText(" ");
        getContentPane().add(usernameDisplayLabel);
        usernameDisplayLabel.setBounds(117, 67, 23, 26);

        usernameLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        usernameLabel.setForeground(new java.awt.Color(96, 108, 56));
        usernameLabel.setText("Username:");
        getContentPane().add(usernameLabel);
        usernameLabel.setBounds(30, 160, 77, 19);

        usernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameFieldActionPerformed(evt);
            }
        });
        getContentPane().add(usernameField);
        usernameField.setBounds(230, 160, 200, 22);

        emailLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        emailLabel.setForeground(new java.awt.Color(96, 108, 56));
        emailLabel.setText("Email:");
        getContentPane().add(emailLabel);
        emailLabel.setBounds(30, 190, 60, 20);

        emailField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailFieldActionPerformed(evt);
            }
        });
        getContentPane().add(emailField);
        emailField.setBounds(230, 190, 200, 22);

        currPassLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        currPassLabel.setForeground(new java.awt.Color(96, 108, 56));
        currPassLabel.setText("Current Password:");
        getContentPane().add(currPassLabel);
        currPassLabel.setBounds(30, 220, 135, 20);

        newPassLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        newPassLabel.setForeground(new java.awt.Color(96, 108, 56));
        newPassLabel.setText("New Password:");
        getContentPane().add(newPassLabel);
        newPassLabel.setBounds(30, 250, 111, 19);

        currentPasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentPasswordFieldActionPerformed(evt);
            }
        });
        getContentPane().add(currentPasswordField);
        currentPasswordField.setBounds(230, 220, 200, 22);

        newPasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPasswordFieldActionPerformed(evt);
            }
        });
        getContentPane().add(newPasswordField);
        newPasswordField.setBounds(230, 250, 200, 22);

        saveAccDetailsBtn.setBackground(new java.awt.Color(96, 108, 56));
        saveAccDetailsBtn.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        saveAccDetailsBtn.setForeground(new java.awt.Color(255, 255, 255));
        saveAccDetailsBtn.setText("SAVE DETAILS");
        saveAccDetailsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAccDetailsBtnActionPerformed(evt);
            }
        });
        getContentPane().add(saveAccDetailsBtn);
        saveAccDetailsBtn.setBounds(30, 280, 150, 24);

        settingsHeader.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        settingsHeader.setForeground(new java.awt.Color(96, 108, 56));
        settingsHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        settingsHeader.setText("SETTINGS");
        settingsHeader.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(96, 108, 56)));
        getContentPane().add(settingsHeader);
        settingsHeader.setBounds(30, 320, 400, 30);

        currencyLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        currencyLabel.setForeground(new java.awt.Color(96, 108, 56));
        currencyLabel.setText("Current Currency:");
        getContentPane().add(currencyLabel);
        currencyLabel.setBounds(30, 360, 130, 20);

        currencyCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        currencyCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currencyComboActionPerformed(evt);
            }
        });
        getContentPane().add(currencyCombo);
        currencyCombo.setBounds(260, 360, 165, 22);

        emailNotifsLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        emailNotifsLabel.setForeground(new java.awt.Color(96, 108, 56));
        emailNotifsLabel.setText("Email Notifications:");
        getContentPane().add(emailNotifsLabel);
        emailNotifsLabel.setBounds(30, 390, 143, 20);

        emailNotificationsCheck.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        emailNotificationsCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailNotificationsCheckActionPerformed(evt);
            }
        });
        getContentPane().add(emailNotificationsCheck);
        emailNotificationsCheck.setBounds(260, 390, 165, 22);

        pushNotifsLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        pushNotifsLabel.setForeground(new java.awt.Color(96, 108, 56));
        pushNotifsLabel.setText("Push Notifications:");
        getContentPane().add(pushNotifsLabel);
        pushNotifsLabel.setBounds(30, 420, 140, 20);

        pushNotificationsCheck.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        pushNotificationsCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pushNotificationsCheckActionPerformed(evt);
            }
        });
        getContentPane().add(pushNotificationsCheck);
        pushNotificationsCheck.setBounds(260, 420, 165, 22);

        smsNotifsLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        smsNotifsLabel.setForeground(new java.awt.Color(96, 108, 56));
        smsNotifsLabel.setText("SMS Notifications");
        getContentPane().add(smsNotifsLabel);
        smsNotifsLabel.setBounds(30, 450, 131, 20);

        smsNotificationsCheck.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        smsNotificationsCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smsNotificationsCheckActionPerformed(evt);
            }
        });
        getContentPane().add(smsNotificationsCheck);
        smsNotificationsCheck.setBounds(260, 450, 165, 22);

        marketingCheck.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        marketingCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marketingCheckActionPerformed(evt);
            }
        });
        getContentPane().add(marketingCheck);
        marketingCheck.setBounds(260, 480, 165, 22);

        profileSettingsHeader12.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        profileSettingsHeader12.setForeground(new java.awt.Color(96, 108, 56));
        profileSettingsHeader12.setText("Marketing Emails:");
        getContentPane().add(profileSettingsHeader12);
        profileSettingsHeader12.setBounds(30, 480, 131, 20);

        menuBtn.setBackground(new java.awt.Color(96, 108, 56));
        menuBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        menuBtn.setForeground(new java.awt.Color(255, 255, 255));
        menuBtn.setText("BACK");
        menuBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        menuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnActionPerformed(evt);
            }
        });
        getContentPane().add(menuBtn);
        menuBtn.setBounds(350, 70, 80, 30);

        updatecsvBtn.setBackground(new java.awt.Color(96, 108, 56));
        updatecsvBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        updatecsvBtn.setForeground(new java.awt.Color(255, 255, 255));
        updatecsvBtn.setText("UPDATE");
        updatecsvBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        updatecsvBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatecsvBtnActionPerformed(evt);
            }
        });
        getContentPane().add(updatecsvBtn);
        updatecsvBtn.setBounds(260, 520, 165, 23);

        updatecsvLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        updatecsvLabel.setForeground(new java.awt.Color(96, 108, 56));
        updatecsvLabel.setText("Update CSV:");
        getContentPane().add(updatecsvLabel);
        updatecsvLabel.setBounds(30, 520, 89, 20);

        exportDataLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        exportDataLabel.setForeground(new java.awt.Color(96, 108, 56));
        exportDataLabel.setText("Export Data:");
        getContentPane().add(exportDataLabel);
        exportDataLabel.setBounds(30, 550, 93, 19);

        exportDataBtn.setBackground(new java.awt.Color(96, 108, 56));
        exportDataBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        exportDataBtn.setForeground(new java.awt.Color(255, 255, 255));
        exportDataBtn.setText("EXPORT");
        exportDataBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        exportDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportDataBtnActionPerformed(evt);
            }
        });
        getContentPane().add(exportDataBtn);
        exportDataBtn.setBounds(260, 550, 165, 23);

        saveSettingsBtn.setBackground(new java.awt.Color(96, 108, 56));
        saveSettingsBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        saveSettingsBtn.setForeground(new java.awt.Color(255, 255, 255));
        saveSettingsBtn.setText("SAVE SETTINGS");
        saveSettingsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSettingsBtnActionPerformed(evt);
            }
        });
        getContentPane().add(saveSettingsBtn);
        saveSettingsBtn.setBounds(30, 590, 150, 30);

        profileSettingsHeader.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        profileSettingsHeader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/profileBG.png"))); // NOI18N
        getContentPane().add(profileSettingsHeader);
        profileSettingsHeader.setBounds(0, -30, 460, 720);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveAccDetailsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAccDetailsBtnActionPerformed
       String username = usernameField.getText().trim();
    String email = emailField.getText().trim();
    String currentPassword = new String(currentPasswordField.getPassword());
    String newPassword = new String(newPasswordField.getPassword());
    
    // Validation
    if (username.isEmpty() || email.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Username and email cannot be empty.", "Validation Error", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    if (!email.contains("@")) {
        JOptionPane.showMessageDialog(this,
            "Please enter a valid email address.", "Validation Error", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try (Connection conn = DBConnection.getConnection()) {
        // If password change is requested
        if (!currentPassword.isEmpty() || !newPassword.isEmpty()) {
            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Both current and new password must be filled to change password.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this,
                    "New password must be at least 6 characters long.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Verify current password
            String verifyQuery = "SELECT password FROM users WHERE id = ?";
            PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery);
            verifyStmt.setInt(1, Session.currentUserId);
            ResultSet rs = verifyStmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (!currentPassword.equals(storedPassword)) {
                    JOptionPane.showMessageDialog(this,
                        "Current password is incorrect.", "Authentication Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Update with new password
            String updateQuery = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, username);
            updateStmt.setString(2, email);
            updateStmt.setString(3, newPassword);
            updateStmt.setInt(4, Session.currentUserId);
            updateStmt.executeUpdate();
            
            // Clear password fields
            currentPasswordField.setText("");
            newPasswordField.setText("");
            
        } else {
            // Update only username and email
            String updateQuery = "UPDATE users SET username = ?, email = ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, username);
            updateStmt.setString(2, email);
            updateStmt.setInt(3, Session.currentUserId);
            updateStmt.executeUpdate();
        }
        
        // Update display
        usernameDisplayLabel.setText(username);
        
        JOptionPane.showMessageDialog(this,
            "Account details saved successfully!", "Success", 
            JOptionPane.INFORMATION_MESSAGE);
            
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error saving account details: " + ex.getMessage(),
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_saveAccDetailsBtnActionPerformed

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        try {
        cs203_2.group2.kachingko.dashboard.Menu.Menu alertsWindow = 
        new cs203_2.group2.kachingko.dashboard.Menu.Menu();
        alertsWindow.setVisible(true);
        this.dispose();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error opening Menu: " + e.getMessage(),
            "Navigation Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_menuBtnActionPerformed

    private void updatecsvBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatecsvBtnActionPerformed
        JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select CSV File to Import");
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv", "txt"));
    
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "This will import transactions from:\n" + selectedFile.getName() + 
            "\n\nProceed with import?", "Confirm Import", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            importCSVFile(selectedFile);
        }
    }
    }//GEN-LAST:event_updatecsvBtnActionPerformed

    private void exportDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportDataBtnActionPerformed
       JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Data Export");
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
    fileChooser.setSelectedFile(new File("my_financial_data.csv"));
    
    int result = fileChooser.showSaveDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File saveFile = fileChooser.getSelectedFile();
        if (!saveFile.getName().endsWith(".csv")) {
            saveFile = new File(saveFile.getAbsolutePath() + ".csv");
        }
        exportToCSV(saveFile);
    }
    }//GEN-LAST:event_exportDataBtnActionPerformed

    private void saveSettingsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSettingsBtnActionPerformed
       try (Connection conn = DBConnection.getConnection()) {
        // Create settings table if it doesn't exist
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS user_settings (
                user_id INT PRIMARY KEY,
                currency VARCHAR(50),
                email_notifications BOOLEAN DEFAULT TRUE,
                push_notifications BOOLEAN DEFAULT TRUE,
                sms_notifications BOOLEAN DEFAULT FALSE,
                marketing_emails BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )""";
        conn.createStatement().execute(createTableQuery);
        
        // Save or update settings
        String upsertQuery = """
            INSERT INTO user_settings (user_id, currency, email_notifications, 
            push_notifications, sms_notifications, marketing_emails) 
            VALUES (?, ?, ?, ?, ?, ?) 
            ON DUPLICATE KEY UPDATE 
            currency = VALUES(currency),
            email_notifications = VALUES(email_notifications),
            push_notifications = VALUES(push_notifications),
            sms_notifications = VALUES(sms_notifications),
            marketing_emails = VALUES(marketing_emails)""";
            
        PreparedStatement stmt = conn.prepareStatement(upsertQuery);
        stmt.setInt(1, Session.currentUserId);
        stmt.setString(2, (String) currencyCombo.getSelectedItem());
        stmt.setBoolean(3, "Yes".equals(emailNotificationsCheck.getSelectedItem()));
        stmt.setBoolean(4, "Yes".equals(pushNotificationsCheck.getSelectedItem()));
        stmt.setBoolean(5, "Yes".equals(smsNotificationsCheck.getSelectedItem()));
        stmt.setBoolean(6, "Yes".equals(marketingCheck.getSelectedItem()));
        
        stmt.executeUpdate();
        
        JOptionPane.showMessageDialog(this,
            "Settings saved successfully!", "Success", 
            JOptionPane.INFORMATION_MESSAGE);
            
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error saving settings: " + ex.getMessage(),
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    }//GEN-LAST:event_saveSettingsBtnActionPerformed

    private void marketingCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marketingCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_marketingCheckActionPerformed

    private void smsNotificationsCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smsNotificationsCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_smsNotificationsCheckActionPerformed

    private void pushNotificationsCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pushNotificationsCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pushNotificationsCheckActionPerformed

    private void emailNotificationsCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailNotificationsCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailNotificationsCheckActionPerformed

    private void currencyComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currencyComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_currencyComboActionPerformed

    private void newPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newPasswordFieldActionPerformed

    private void currentPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentPasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_currentPasswordFieldActionPerformed

    private void emailFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailFieldActionPerformed

    private void usernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernameFieldActionPerformed

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
            java.util.logging.Logger.getLogger(ProfileSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProfileSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProfileSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProfileSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProfileSettings().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel currPassLabel;
    private javax.swing.JComboBox<String> currencyCombo;
    private javax.swing.JLabel currencyLabel;
    private javax.swing.JPasswordField currentPasswordField;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JComboBox<String> emailNotificationsCheck;
    private javax.swing.JLabel emailNotifsLabel;
    private javax.swing.JButton exportDataBtn;
    private javax.swing.JLabel exportDataLabel;
    private javax.swing.JComboBox<String> marketingCheck;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel newPassLabel;
    private javax.swing.JPasswordField newPasswordField;
    private javax.swing.JLabel profileInfoHeader;
    private javax.swing.JLabel profileSettingsHeader;
    private javax.swing.JLabel profileSettingsHeader12;
    private javax.swing.JComboBox<String> pushNotificationsCheck;
    private javax.swing.JLabel pushNotifsLabel;
    private javax.swing.JToggleButton saveAccDetailsBtn;
    private javax.swing.JToggleButton saveSettingsBtn;
    private javax.swing.JLabel settingsHeader;
    private javax.swing.JComboBox<String> smsNotificationsCheck;
    private javax.swing.JLabel smsNotifsLabel;
    private javax.swing.JButton updatecsvBtn;
    private javax.swing.JLabel updatecsvLabel;
    private javax.swing.JLabel usernameDisplayLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
