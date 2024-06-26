import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.sql.*;

public class Wages extends JFrame
{
	Wages()
	{
		Color background = new Color(43,45,47);
        Color foreground = new Color(255,191,0);
        setBackground(background);
        setForeground(foreground);
                
        JLabel label = new JLabel("Wages", SwingConstants.CENTER);
        label.setForeground(foreground);
        label.setBackground(background);
        label.setFont(new Font("Arial", Font.BOLD, 50));
        
        setTitle("Wages");
        add(label, "North");
        
        HashMap<String, Double> theWages = getData();
        String[] headers = {"Name", "Wages"};
        add(new Results(theWages, headers, false), "Center");

        getContentPane().setBackground(background);
        setSize(700,700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocation(900, 200);
        setVisible(true);
        
	}
	
	private static HashMap<String, Double> getData() 
	{
	    HashMap<String, Double> theWages = new HashMap<>();

		final String DB_URL = "jdbc:mysql://localhost:3306/peerconnectionproject";

		final String USER = "root";
		final String PASS = "root";

	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    try
	    {
	        //STEP 3: Open a connection
	        conn = DriverManager.getConnection(DB_URL, USER, PASS);

	        //STEP 4: Execute a query
	        stmt = conn.createStatement();
	        String query = String.format("select name, wage from tutor");
	        rs = stmt.executeQuery(query);
	        
	        //STEP 5: Process the results
	        while(rs.next())
	        {
	            theWages.put(rs.getString("name"), rs.getDouble("wage"));
	        }
	    }
	    catch(Exception e)
	    {
	        //Handle errors for Class.forName
	        e.printStackTrace();
	    }
	    finally
	    {
	        //finally block used to close resources
	        try
	        {
	            if (rs != null)
	            {
	                rs.close();
	            }
	        }
	        catch (SQLException e)
	        {
	            e.printStackTrace();
	        }
	        try
	        {
	            if (stmt != null)
	            {
	                stmt.close();
	            }
	        }
	        catch(SQLException se2){}// nothing we can do
	        try
	        {
	            if (conn != null)
	            {
	                conn.close();
	            }
	        }
	        catch(SQLException se)
	        {
	            se.printStackTrace();
	        }//end finally try
	    }//end try
	    return theWages;
	}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Wages::new);
    }
}
