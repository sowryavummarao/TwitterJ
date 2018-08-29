import java.sql.*;
public class TwitterDB{
    public static boolean saveTweet(int id, String status){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","root",AuthStrings.DBPassword);
            Statement stmt=con.createStatement();
            String sql  = String.format("insert into tweets values(%d, '%s')", id, status);
            stmt.execute(sql);
            con.close();
            return true;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

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

    public static String getTweetById(int id){
        try{
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
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
}