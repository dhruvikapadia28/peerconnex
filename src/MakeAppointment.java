import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MakeAppointment extends JFrame {
    private JComboBox<String> subjectDropdown;
    private JComboBox<String> tutorDropdown;
    private JButton bookButton;
    private JTextField dateField;

    public MakeAppointment() {
        setTitle("Make an Appointment");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        subjectDropdown = new JComboBox<>();
        tutorDropdown = new JComboBox<>();
        dateField = new JTextField(10);
        bookButton = new JButton("Book");

        add(new JLabel("Subject:"));
        add(subjectDropdown);
        add(new JLabel("Tutor:"));
        add(tutorDropdown);
        add(new JLabel("Date (YYYY-MM-DD):"));
        add(dateField);
        add(bookButton);

        loadSubjects();
        subjectDropdown.addActionListener(this::loadTutorsForSubject);
        bookButton.addActionListener(this::bookAppointment);

        setVisible(true);
    }

    private void loadSubjects() {
        final String DB_URL = "jdbc:mysql://localhost:3306/peerconnectionproject";
        final String USER = "root";
        final String PASS = "root";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT subject FROM Tutor")) {
            while (rs.next()) {
                subjectDropdown.addItem(rs.getString("subject"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTutorsForSubject(ActionEvent event) {
        tutorDropdown.removeAllItems();
        String selectedSubject = (String) subjectDropdown.getSelectedItem();

        final String DB_URL = "jdbc:mysql://localhost:3306/peerconnectionproject";
        final String USER = "root";
        final String PASS = "root";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("SELECT name FROM Tutor WHERE subject = ?")) {
            pstmt.setString(1, selectedSubject);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tutorDropdown.addItem(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void bookAppointment(ActionEvent event) {
        String selectedTutor = (String) tutorDropdown.getSelectedItem();
        String date = dateField.getText();
        // Additional logic to validate the date and check the tutor's availability should be implemented here

        final String DB_URL = "jdbc:mysql://localhost:3306/peerconnectionproject";
        final String USER = "root";
        final String PASS = "root";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Appointments (tutee_id, tutor_id, date, subject, no_show) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setInt(1, 1); // Assuming the tutee_id is known or selected elsewhere
            pstmt.setInt(2, 1); // You need to fetch the actual tutor_id based on the selected name
            pstmt.setDate(3, Date.valueOf(date));
            pstmt.setString(4, (String) subjectDropdown.getSelectedItem());
            pstmt.setBoolean(5, false); // no_show is false by default
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to book appointment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MakeAppointment::new);
    }
}