import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.HashMap;
import java.sql.*;

public class findTutor extends JFrame
{
    private String subject;

    findTutor() {
        Color background = new Color(43,45,47);
        Color foreground = new Color(255,191,0);

        String[] subjects = {"Math", "Computer Science", "Physics", "Chemistry", "Biology", "Business", "Graphic Design"};
        JComboBox<String> dropdown = new JComboBox<>(subjects);
        dropdown.setFont(dropdown.getFont().deriveFont(30f));

        JLabel label = new JLabel("Find Tutor", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 50));
        label.setForeground(foreground);
        JLabel searchLabel = new JLabel("Select subject: ");
        searchLabel.setForeground(foreground);
        searchLabel.setFont(searchLabel.getFont().deriveFont(30f));

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(background);
        center.add(searchLabel);
        center.add(dropdown);
        JButton findButton = new JButton("  Find  ");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(100, 80));
        buttonPanel.setBackground(background);
        buttonPanel.add(findButton);
        findButton.setForeground(foreground);
        findButton.setFont(findButton.getFont().deriveFont(30f));
        findButton.setPreferredSize(new Dimension(130, 45));
        findButton.setBackground(background);
        findButton.setBorder(new MatteBorder(1,1,1,1, foreground));
        findButton.addActionListener(e -> {
            subject = (String)dropdown.getSelectedItem();
            HashMap<String, Double> ratings = getData(subject);
            remove(center);
            remove(buttonPanel);
            String[] headers = {"Name", "Rating"};
            add(new Results(ratings, headers, true), "Center");
            repaint();
            validate();
        });

        setTitle("Find Tutor");
        setLayout(new BorderLayout(5,5));
        add(center, "Center");
        add(label, "North");
        add(buttonPanel, "South", SwingUtilities.CENTER);
        setSize(700,700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocation(900, 200);
        getContentPane().setBackground(background);
        setVisible(true);
    }

    private static HashMap<String, Double> getData(String sub) {
        HashMap<String, Double> ratings = new HashMap<>();

	    final String DB_URL = "jdbc:mysql://localhost:3306/peerconnectionproject";

        final String USER = "root";
        final String PASS = "root";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            stmt = conn.createStatement();
            String query = String.format("select name, avg_rating from findtutor where subject = '%s'", sub);
            rs = stmt.executeQuery(query);
            //STEP 5: Process the results
            while(rs.next()){
                System.out.println(rs.getString("name") + " " + rs.getDouble("avg_rating"));
                ratings.put(rs.getString("name"), rs.getDouble("avg_rating"));
            }
        } catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if (rs!=null)
                    rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){}// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try

        return ratings;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(findTutor::new);
    }
}
