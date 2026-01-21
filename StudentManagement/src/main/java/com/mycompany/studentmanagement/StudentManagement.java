package com.mycompany.studentmanagement;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class StudentManagement extends JFrame {

    JTable table;
    JTextField txtRoll, txtName, txtDept, txtYear, txtPhone, txtAddress;

    JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh;

    public StudentManagement() {
        initUI();
        loadTable();
    }

    // ===== DATABASE CONNECTION =====
    private Connection getCon() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/studentdb?useSSL=false&serverTimezone=UTC",
            "root",
            "jesusjerin25"
        );
    }

    // ===== UI =====
    private void initUI() {
        setTitle("🎓 Student Management System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // ===== HEADER =====
        JLabel title = new JLabel("🎓 Student Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setOpaque(true);
        title.setBackground(new Color(52, 152, 219));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(15,10,15,10));
        add(title, BorderLayout.NORTH);

        // ===== TABLE =====
        table = new JTable(new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID","Roll No","Name","Department","Year","Phone","Address","Created Date"}
        ));

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane sp = new JScrollPane(table);

        // ===== FORM =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Student Details"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10,10,10,10);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        JLabel[] labels = {
            new JLabel("Roll No"),
            new JLabel("Name"),
            new JLabel("Department"),
            new JLabel("Year"),
            new JLabel("Phone"),
            new JLabel("Address")
        };

        txtRoll = new JTextField(15);
        txtName = new JTextField(15);
        txtDept = new JTextField(15);
        txtYear = new JTextField(15);
        txtPhone = new JTextField(15);
        txtAddress = new JTextField(15);

        Component[] fields = {
            txtRoll, txtName, txtDept, txtYear, txtPhone, txtAddress
        };

        for(int i=0;i<labels.length;i++){
            g.gridx = 0;
            g.gridy = i;
            g.weightx = 0;
            form.add(labels[i], g);

            g.gridx = 1;
            g.weightx = 1;
            form.add(fields[i], g);
        }

        // Push form to top
        g.gridy = labels.length;
        g.weighty = 1;
        form.add(new JLabel(), g);

        // ===== SPLIT PANE =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(sp);
        splitPane.setRightComponent(form);
        splitPane.setDividerLocation(850);
        splitPane.setResizeWeight(0.75);
        add(splitPane, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnAdd = new JButton("➕ Add Student");
        btnUpdate = new JButton("✏ Update");
        btnDelete = new JButton("🗑 Delete");
        btnClear = new JButton("🧹 Clear");
        btnRefresh = new JButton("🔄 Refresh");

        btns.add(btnAdd);
        btns.add(btnUpdate);
        btns.add(btnDelete);
        btns.add(btnClear);
        btns.add(btnRefresh);

        add(btns, BorderLayout.SOUTH);

        // ===== EVENTS =====
        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnRefresh.addActionListener(e -> loadTable());
        btnClear.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    // ===== LOAD TABLE =====
    private void loadTable() {
        DefaultTableModel m = (DefaultTableModel)table.getModel();
        m.setRowCount(0);

        try(Connection c = getCon()){
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM students");
            while(rs.next()){
                m.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("roll_no"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("year"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("created_date")
                });
            }
        } catch(Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ===== ADD =====
    private void addStudent() {
        try(Connection c = getCon()){
            PreparedStatement p = c.prepareStatement(
                "INSERT INTO students(roll_no,name,department,year,phone,address,created_date) VALUES(?,?,?,?,?,?,?)"
            );
            p.setString(1, txtRoll.getText());
            p.setString(2, txtName.getText());
            p.setString(3, txtDept.getText());
            p.setString(4, txtYear.getText());
            p.setString(5, txtPhone.getText());
            p.setString(6, txtAddress.getText());
            p.setDate(7, Date.valueOf(LocalDate.now()));
            p.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student added!");
            loadTable();
            clearForm();
        } catch(Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ===== UPDATE =====
    private void updateStudent() {
        int r = table.getSelectedRow();
        if(r == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first!");
            return;
        }

        int id = Integer.parseInt(table.getValueAt(r,0).toString());

        try(Connection c = getCon()){
            PreparedStatement p = c.prepareStatement(
                "UPDATE students SET roll_no=?,name=?,department=?,year=?,phone=?,address=? WHERE id=?"
            );
            p.setString(1, txtRoll.getText());
            p.setString(2, txtName.getText());
            p.setString(3, txtDept.getText());
            p.setString(4, txtYear.getText());
            p.setString(5, txtPhone.getText());
            p.setString(6, txtAddress.getText());
            p.setInt(7, id);
            p.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student updated!");
            loadTable();
        } catch(Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ===== DELETE =====
    private void deleteStudent() {
        int r = table.getSelectedRow();
        if(r == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first!");
            return;
        }

        int id = Integer.parseInt(table.getValueAt(r,0).toString());

        int ok = JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(ok != JOptionPane.YES_OPTION) return;

        try(Connection c = getCon()){
            PreparedStatement p = c.prepareStatement("DELETE FROM students WHERE id=?");
            p.setInt(1, id);
            p.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student deleted!");
            loadTable();
            clearForm();
        } catch(Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ===== FORM =====
    private void fillForm() {
        int r = table.getSelectedRow();
        if(r == -1) return;

        txtRoll.setText(table.getValueAt(r,1).toString());
        txtName.setText(table.getValueAt(r,2).toString());
        txtDept.setText(table.getValueAt(r,3).toString());
        txtYear.setText(table.getValueAt(r,4).toString());
        txtPhone.setText(table.getValueAt(r,5).toString());
        txtAddress.setText(table.getValueAt(r,6).toString());
    }

    private void clearForm() {
        txtRoll.setText("");
        txtName.setText("");
        txtDept.setText("");
        txtYear.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagement().setVisible(true));
    }
}
