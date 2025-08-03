/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cs203_2.group2.kachingko.dashboard.SpendingTrendline;

import cs203_2.group2.kachingko.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Connection;  
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

import cs203_2.group2.kachingko.auth.Session;
import cs203_2.group2.kachingko.dashboard.DashboardFrame;

public class SpendingTrendline extends javax.swing.JFrame {
    
    // Column constants
    private static final int DATE_COLUMN = 0;
    private static final int MERCHANT_COLUMN = 1;
    private static final int LOCATION_COLUMN = 2;
    private static final int CARD_TYPE_COLUMN = 3;
    private static final int CATEGORY_COLUMN = 4;
    private static final int AMOUNT_COLUMN = 5;

    // Instance variables
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public SpendingTrendline() {
        initComponents();
        setTitle("Spending Trendline");
        setSize(460, 680);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize table model and sorter
        tableModel = (DefaultTableModel) trendlineTable.getModel();
        sorter = new TableRowSorter<>(tableModel);
        trendlineTable.setRowSorter(sorter);
        
        // Load Data
        loadTransactionData();

        // Add search listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        // Add sort listener
        sortDropdown.addActionListener(e -> sortTable());
    }

    // Load transactions into table
    private void loadTransactionData() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM transactions WHERE user_id = ?")) {

            stmt.setInt(1, Session.currentUserId); // Use actual session ID
//            stmt.setInt(1, 1); //Sample ID
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("date"),
                    rs.getString("merchant"),
                    rs.getString("location"),
                    rs.getString("card_type"),
                    rs.getString("category"),
                    rs.getDouble("amount")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database Error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Filter rows by search
    private void filterTable() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Use Pattern.quote to handle special characters
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(keyword)));
        }
    }

    // Sorting logic
    private void sortTable() {
        if (tableModel.getRowCount() == 0) {
            showMessage("No data available for sorting");
            return;
        }

        String selected = (String) sortDropdown.getSelectedItem();
        if (selected == null) return;

        switch (selected) {
            case "Most Expensive":
                sortByAmount();
                break;
            case "Most Common Category":
                sortByFrequency(CATEGORY_COLUMN, "category");
                break;
            case "Most Frequent Merchant":
                sortByFrequency(MERCHANT_COLUMN, "merchant");
                break;
            case "Reset":
                sorter.setRowFilter(null);
                sorter.setSortKeys(null);
                showMessage("Sorting and filtering reset");
                break;
            default:
                showMessage("Invalid sort option");
        }
    }

    /**
     * Sort by amount (descending order)
     */
    private void sortByAmount() {
        List<RowSorter.SortKey> sortKeys = Collections.singletonList(
            new RowSorter.SortKey(AMOUNT_COLUMN, SortOrder.DESCENDING)
        );
        sorter.setSortKeys(sortKeys);
        showMessage("Table sorted by amount (highest to lowest)");
    }
    
    /**
     * Sort by frequency of values in specified column
     */
    private void sortByFrequency(int columnIndex, String columnName) {
        // Calculate frequencies
        FrequencyResult result = calculateFrequencies(columnIndex);
        
        if (result.isEmpty()) {
            showMessage("No data found in " + columnName + " column");
            return;
        }
        
        // Handle edge cases
        if (result.getUniqueCount() == 1) {
            showMessage("All " + columnName + " values are the same: " + result.getMostCommon());
            return;
        }
        
        if (result.hasNoRepeats()) {
            showMessage("All " + columnName + " values are unique - no frequency sorting applied");
            return;
        }
        
        // Create custom sorter based on frequency
        sorter.setComparator(columnIndex, new FrequencyStringComparator(result.frequencies));
        
        List<RowSorter.SortKey> sortKeys = Arrays.asList(
            new RowSorter.SortKey(columnIndex, SortOrder.DESCENDING), // Primary: by frequency
            new RowSorter.SortKey(AMOUNT_COLUMN, SortOrder.DESCENDING) // Secondary: by amount
        );
        sorter.setSortKeys(sortKeys);
        
        // Provide feedback
        String message = String.format(
            "Sorted by %s frequency\nMost common: %s (%d occurrences)",
            columnName, result.getMostCommon(), result.getMaxFrequency()
        );
        showMessage(message);
    }
    
    /**
     * Calculate frequency statistics for a column
     */
    private FrequencyResult calculateFrequencies(int columnIndex) {
        Map<String, Integer> frequencies = new HashMap<>();
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, columnIndex);
            if (value != null) {
                String key = value.toString().trim();
                if (!key.isEmpty()) {
                    frequencies.put(key, frequencies.getOrDefault(key, 0) + 1);
                }
            }
        }
        
        return new FrequencyResult(frequencies);
    }
    
    /**
     * Display message to user
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            "Information", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Custom comparator for sorting strings by their frequency
     */
    private static class FrequencyStringComparator implements Comparator<Object> {
        private final Map<String, Integer> frequencies;
        
        public FrequencyStringComparator(Map<String, Integer> frequencies) {
            this.frequencies = frequencies;
        }
        
        @Override
        public int compare(Object o1, Object o2) {
            String s1 = o1 != null ? o1.toString().trim() : "";
            String s2 = o2 != null ? o2.toString().trim() : "";
            
            int freq1 = frequencies.getOrDefault(s1, 0);
            int freq2 = frequencies.getOrDefault(s2, 0);
            
            // Primary sort: by frequency (descending)
            int freqCompare = Integer.compare(freq2, freq1);
            // Secondary sort: alphabetically (ascending)
            return freqCompare != 0 ? freqCompare : s1.compareTo(s2);
        }
    }
    
    /**
     * Helper class for frequency calculation results
     */
    private static class FrequencyResult {
        private final Map<String, Integer> frequencies;
        private String mostCommon;
        private int maxFrequency;
        
        public FrequencyResult(Map<String, Integer> frequencies) {
            this.frequencies = frequencies;
            calculateStats();
        }
        
        private void calculateStats() {
            if (frequencies.isEmpty()) {
                mostCommon = "";
                maxFrequency = 0;
                return;
            }
            
            Map.Entry<String, Integer> maxEntry = null;
            for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
                if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                    maxEntry = entry;
                }
            }
            
            if (maxEntry != null) {
                mostCommon = maxEntry.getKey();
                maxFrequency = maxEntry.getValue();
            }
        }
        
        public boolean isEmpty() {
            return frequencies.isEmpty();
        }
        
        public boolean hasNoRepeats() {
            for (int count : frequencies.values()) {
                if (count > 1) return false;
            }
            return true;
        }
        
        public String getMostCommon() {
            return mostCommon;
        }
        
        public int getMaxFrequency() {
            return maxFrequency;
        }
        
        public int getUniqueCount() {
            return frequencies.size();
        }
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        trendlineTable = new javax.swing.JTable();
        searchField = new javax.swing.JTextField();
        sortDropdown = new javax.swing.JComboBox<>();
        searchLabel1 = new javax.swing.JLabel();
        searchLabel = new javax.swing.JLabel();
        trendlinesHeader = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        backBtn.setBackground(new java.awt.Color(40, 54, 24));
        backBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        backBtn.setForeground(new java.awt.Color(255, 255, 255));
        backBtn.setText("BACK TO DASHBOARD");
        backBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });
        getContentPane().add(backBtn);
        backBtn.setBounds(280, 590, 150, 30);

        jScrollPane1.setForeground(new java.awt.Color(55, 74, 34));

        trendlineTable.setFont(new java.awt.Font("DM Sans", 0, 12)); // NOI18N
        trendlineTable.setForeground(new java.awt.Color(55, 74, 34));
        trendlineTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Merchant", "Location", "Card Type", "Category", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        trendlineTable.setGridColor(new java.awt.Color(55, 74, 34));
        trendlineTable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        trendlineTable.setSelectionForeground(new java.awt.Color(55, 74, 34));
        trendlineTable.setShowGrid(true);
        jScrollPane1.setViewportView(trendlineTable);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 150, 410, 430);

        searchField.setFont(new java.awt.Font("DM Sans", 0, 12)); // NOI18N
        searchField.setForeground(new java.awt.Color(55, 74, 34));
        searchField.setToolTipText("Search");
        searchField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(55, 74, 34)));
        searchField.setCaretColor(new java.awt.Color(55, 74, 34));
        searchField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });
        getContentPane().add(searchField);
        searchField.setBounds(80, 120, 120, 18);

        sortDropdown.setFont(new java.awt.Font("DM Sans", 0, 12)); // NOI18N
        sortDropdown.setForeground(new java.awt.Color(40, 54, 24));
        sortDropdown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Most Expensive", "Most Common Category", "Most Frequent Merchant", "Reset" }));
        sortDropdown.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(40, 54, 24)));
        getContentPane().add(sortDropdown);
        sortDropdown.setBounds(280, 120, 150, 20);

        searchLabel1.setFont(new java.awt.Font("DM Sans", 1, 14)); // NOI18N
        searchLabel1.setForeground(new java.awt.Color(55, 74, 34));
        searchLabel1.setText("Filter:");
        getContentPane().add(searchLabel1);
        searchLabel1.setBounds(230, 120, 51, 17);

        searchLabel.setFont(new java.awt.Font("DM Sans", 1, 14)); // NOI18N
        searchLabel.setForeground(new java.awt.Color(55, 74, 34));
        searchLabel.setText("Search:");
        getContentPane().add(searchLabel);
        searchLabel.setBounds(20, 120, 51, 17);

        trendlinesHeader.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        trendlinesHeader.setIcon(new javax.swing.ImageIcon("C:\\Users\\Administrator\\Desktop\\Programming\\kachingko\\Kachingko\\src\\main\\resources\\images\\trendlineBG.png")); // NOI18N
        trendlinesHeader.setToolTipText("");
        getContentPane().add(trendlinesHeader);
        trendlinesHeader.setBounds(0, 0, 450, 660);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed
        DashboardFrame dashboardWindow = new DashboardFrame();
        dashboardWindow.setVisible(true);

        this.setVisible(false);        
    }//GEN-LAST:event_backBtnActionPerformed

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new SpendingTrendline().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JLabel searchLabel1;
    private javax.swing.JComboBox<String> sortDropdown;
    private javax.swing.JTable trendlineTable;
    private javax.swing.JLabel trendlinesHeader;
    // End of variables declaration//GEN-END:variables
}
