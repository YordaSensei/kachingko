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
    try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
        String line;
        int lineCount = 0;
        int importedCount = 0;
        
        // Skip header line
        reader.readLine();
        
        try (Connection conn = DBConnection.getConnection()) {
            String insertQuery = """
                INSERT INTO transactions (user_id, date, description, amount, category, card_type) 
                VALUES (?, ?, ?, ?, ?, ?)""";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            
            while ((line = reader.readLine()) != null) {
                lineCount++;
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        stmt.setInt(1, Session.currentUserId);
                        stmt.setString(2, parts[0].trim()); // date
                        stmt.setString(3, parts[1].trim()); // description
                        stmt.setDouble(4, Double.parseDouble(parts[2].trim())); // amount
                        stmt.setString(5, parts.length > 3 ? parts[3].trim() : "Other"); // category
                        stmt.setString(6, parts.length > 4 ? parts[4].trim() : null); // card_type
                        
                        stmt.executeUpdate();
                        importedCount++;
                    }
                } catch (Exception e) {
                    System.out.println("Error processing line " + lineCount + ": " + e.getMessage());
                }
            }
            
            JOptionPane.showMessageDialog(this,
                "CSV import completed!\n" +
                "Total lines processed: " + lineCount + "\n" +
                "Successfully imported: " + importedCount + " transactions",
                "Import Complete", JOptionPane.INFORMATION_MESSAGE);
                
        }
    } catch (IOException | SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error importing CSV file: " + ex.getMessage(),
            "Import Error", JOptionPane.ERROR_MESSAGE);
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
        profileSettingsHeader = new javax.swing.JLabel();
        usernameDisplayLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        welcomeLabel = new javax.swing.JLabel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(460, 680));
        setMinimumSize(new java.awt.Dimension(460, 680));
        setResizable(false);
        setSize(new java.awt.Dimension(460, 680));

        profileInfoHeader.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        profileInfoHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        profileInfoHeader.setText("Profile Information");

        profileSettingsHeader.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        profileSettingsHeader.setText("Profile &  Settings");

        usernameDisplayLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        usernameDisplayLabel.setText(" ");

        usernameLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        usernameLabel.setText("Username:");

        welcomeLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        welcomeLabel.setText("Welcome");

        usernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameFieldActionPerformed(evt);
            }
        });

        emailLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        emailLabel.setText("Email:");

        emailField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailFieldActionPerformed(evt);
            }
        });

        currPassLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        currPassLabel.setText("Current Password:");

        newPassLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        newPassLabel.setText("New Password:");

        currentPasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentPasswordFieldActionPerformed(evt);
            }
        });

        newPasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPasswordFieldActionPerformed(evt);
            }
        });

        saveAccDetailsBtn.setText("Save Account Details");
        saveAccDetailsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAccDetailsBtnActionPerformed(evt);
            }
        });

        settingsHeader.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        settingsHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        settingsHeader.setText("Settings");

        currencyLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        currencyLabel.setText("Current Currency:");

        currencyCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        currencyCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currencyComboActionPerformed(evt);
            }
        });

        emailNotifsLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        emailNotifsLabel.setText("Email Notifications:");

        emailNotificationsCheck.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        emailNotificationsCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailNotificationsCheckActionPerformed(evt);
            }
        });

        pushNotifsLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        pushNotifsLabel.setText("Push Notifications:");

        pushNotificationsCheck.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        pushNotificationsCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pushNotificationsCheckActionPerformed(evt);
            }
        });

        smsNotifsLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        smsNotifsLabel.setText("SMS Notifications");

        smsNotificationsCheck.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        smsNotificationsCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smsNotificationsCheckActionPerformed(evt);
            }
        });

        marketingCheck.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        marketingCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marketingCheckActionPerformed(evt);
            }
        });

        profileSettingsHeader12.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        profileSettingsHeader12.setText("Marketing Emails:");

        menuBtn.setText("Back to Menu");
        menuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnActionPerformed(evt);
            }
        });

        updatecsvBtn.setText("Update");
        updatecsvBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatecsvBtnActionPerformed(evt);
            }
        });

        updatecsvLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        updatecsvLabel.setText("Update CSV:");

        exportDataLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 16)); // NOI18N
        exportDataLabel.setText("Export Data:");

        exportDataBtn.setText("Export");
        exportDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportDataBtnActionPerformed(evt);
            }
        });

        saveSettingsBtn.setText("Save Account Settings");
        saveSettingsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSettingsBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(profileInfoHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingsHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(saveSettingsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(currencyLabel)
                            .addComponent(emailNotifsLabel)
                            .addComponent(pushNotifsLabel)
                            .addComponent(profileSettingsHeader12)
                            .addComponent(updatecsvLabel)
                            .addComponent(exportDataLabel)
                            .addComponent(smsNotifsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(exportDataBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(updatecsvBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(marketingCheck, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(smsNotificationsCheck, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pushNotificationsCheck, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(emailNotificationsCheck, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(currencyCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(profileSettingsHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(menuBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(saveAccDetailsBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(emailLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGap(18, 18, 18))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(usernameLabel)
                                                .addComponent(currPassLabel))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(newPassLabel)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(welcomeLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(usernameDisplayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(52, 52, 52)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(emailField)
                                    .addComponent(usernameField)
                                    .addComponent(currentPasswordField)
                                    .addComponent(newPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(25, 25, 25))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(menuBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(profileSettingsHeader))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(welcomeLabel)
                            .addComponent(usernameDisplayLabel))
                        .addGap(18, 18, 18)))
                .addComponent(profileInfoHeader)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailLabel)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currPassLabel)
                    .addComponent(currentPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newPassLabel)
                    .addComponent(newPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(saveAccDetailsBtn)
                .addGap(18, 18, 18)
                .addComponent(settingsHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(currencyLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(emailNotifsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pushNotifsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(smsNotifsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(profileSettingsHeader12))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(currencyCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(emailNotificationsCheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pushNotificationsCheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(smsNotificationsCheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(marketingCheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(updatecsvBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(exportDataBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(updatecsvLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(exportDataLabel)))
                .addGap(18, 18, 18)
                .addComponent(saveSettingsBtn)
                .addGap(70, 70, 70))
        );

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
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
    
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
    private javax.swing.JLabel welcomeLabel;
    // End of variables declaration//GEN-END:variables
}
