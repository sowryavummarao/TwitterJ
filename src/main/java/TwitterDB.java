import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TwitterDB{
    /**
     * Method will save a tweet to tweet database
     * @param status status of tweet to save
     * @return whether tweet is saved or not
     * @throws ClassNotFoundException if JDBC driver class not found
     * @throws SQLException if can't be saved for some reason regarding MySQL
     */
    public static boolean saveTweet(String status) throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        Connection con=DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb","root",AuthStrings.DBPassword);

        //This block will get the list of IDs already in database, and find the minimum ID that isn't
        //already in the database
        Statement stmt=con.createStatement();
        String sql  = "select id from tweets";
        ResultSet rs = stmt.executeQuery(sql);
        List<Integer> IDList = new ArrayList<Integer>();
        while (rs.next()){
            IDList.add(rs.getInt(1));
        }
        int id = 1;
        while (IDList.contains(id)){
            id++;
        }

        //This block will actually save the tweet into the database
        Statement stmt1=con.createStatement();
        String sql1  = String.format("insert into tweets values(%d, '%s')", id, status);
        stmt1.execute(sql1);
        con.close();
        return true;
    }

    /**
     * Shows all tweets in the database
     */
    public static void viewTweets(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","root",AuthStrings.DBPassword);
            Statement stmt=con.createStatement();
            String sql  = "select * from tweets";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()){
                System.out.printf("ID: %d || STATUS: %s\n",rs.getInt(1), rs.getString(2));
            }
            con.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    /**
     * Returns the status of the desired tweet based on id given
     * @param id of the status that is desired
     * @return status that is desired
     * @throws ClassNotFoundException if JDBC driver class is not found
     * @throws SQLException if tweets can't be shown for some reason regarding MySQL
     */
    public static String getTweetById(int id) throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        Connection con=DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb","root",AuthStrings.DBPassword);
        Statement stmt=con.createStatement();
        String sql  = String.format("select status from tweets where id = %d", id);
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String tweet = rs.getString(1);
        con.close();
        return tweet;
    }

    /**
     * Deletes tweet from database
     * @param id of tweet to delete
     * @return whether deletion occurred or not
     */
    public static boolean deleteTweet(int id){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","root",AuthStrings.DBPassword);
            Statement stmt=con.createStatement();
            String sql  = String.format("delete from tweets where id = %d", id);
            boolean removed = stmt.execute(sql);
            con.close();
            return removed;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
}