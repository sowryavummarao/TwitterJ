import java.sql.*;
class DBTest{
    public static void main(String args[]){
        //TwitterDB.saveTweet(2, "Second tweet in database!");
        System.out.println(TwitterDB.getTweetById(4));
    }
}