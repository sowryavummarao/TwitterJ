import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Tweeter extends Thread{
    final static String duplicateMessage = "Status is a duplicate.";
    final static String datePattern = "yyyy-MM-dd hh:mm:ssa";
    final static List<String> choices = new ArrayList<String>() {{
        add("t"); //(t)weet
        add("a"); //(a)utotweet
        add("s"); //(s)ave tweet
        add("v"); //(v)iew saved tweets
        add("e"); //(e)nter tweet database
    }};
    TwitterFactory tf;
    Twitter twitter;
    /**
     * Constructor sets up necessary authorizations and starts thread
     */
    public Tweeter() {
        //Permissions to sign into twitter account
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(AuthStrings.OAuthConsumerKey)
                .setOAuthConsumerSecret(AuthStrings.OAuthConsumerSecret)
                .setOAuthAccessToken(AuthStrings.OAuthAccessToken)
                .setOAuthAccessTokenSecret(AuthStrings.OAuthAccessTokenSecret);
        tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
        /*try {
            TwitterAuth.setAccessTokens(twitter);
        }catch (Exception e){
            System.out.println("Authorization failed.");
        }*/
        start();
    }

    /**
     * Method will simply tweet a status
     * @param status - the status to tweet
     */
    private void tweet(String status){
        try {
            twitter.updateStatus(status);
        }catch (TwitterException e){
            //meant to deal with duplicate messages
            if (e.getErrorMessage().equals(duplicateMessage)){
                System.out.println("Sorry! You've already tweeted this, try tweeting something different!");
            }else {
                System.out.println(e.getErrorMessage());
            }
        }
    }

    /**
     * Method will tweet a status at a certain time
     * @param postDate - date/time to tweet
     * @param status - status to tweet
     * @throws InterruptedException - thrown if process is interrupted
     */
    private void autotweet(Date postDate, String status) throws InterruptedException{
        Date current = new Date();
        long waitTime = postDate.getTime() - current.getTime();
        Thread.sleep(waitTime);
        tweet(status);
    }

    /**
     * Method will convert a string to date with our specific format
     * @param dateString - string to convert
     * @return valid date, or null if string is invalid
     */
    private Date stringToDate(String dateString){
        Date postDate;
        try {
            SimpleDateFormat ft = new SimpleDateFormat (datePattern);
            postDate = ft.parse(dateString);
        }catch (Exception e){
            return null;
        }
        return postDate;
    }

    /**
     * Method is meant to autotweet once status is already obtained
     * @param scanner scanner used to read user input
     * @param status status to autotweet
     */
    private void autotweetWithoutInput(Scanner scanner, String status){
        System.out.printf("When do you want to tweet this (in the format of %s)?\n", datePattern);
        String dateString = scanner.nextLine();

        Date postDate;
        Date current = new Date();
        while ((postDate = stringToDate(dateString)) == null || postDate.before(current)) {
            if (postDate == null) {
                System.out.printf("Please follow the given format: %s\n", datePattern);
            } else {
                System.out.printf("Please pick a FUTURE date with the given format: %s\n", datePattern);
            }
            dateString = scanner.nextLine();
        }

        try {
            autotweet(postDate, status);
        } catch (Exception e){
        }
    }

    /**
     * Method to ask about deleting a tweet after it has been tweeted or autotweeted from the database
     * @param tweetID id of the tweet to delete
     * @param scanner scanner used to read user input
     */
    private void deleteAfterTweeting(int tweetID, Scanner scanner){
        System.out.println("Would you like to now remove the tweet from your database? (y)es or (n)o");
        String choice = scanner.nextLine();
        if (choice.equals("y")){
            TwitterDB.deleteTweet(tweetID);
        }
    }

    @Override
    /**
     * Runs the thread, goes through options and asks user for time, status, etc.
     */
    public void run(){
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        while (!choices.contains(choice)) {
            System.out.println("Would you like to (t)weet, (a)utotweet, (s)ave a tweet, (v)iew saved tweets, or (e)nter your tweet database? ");
            choice = scanner.nextLine();
        }

        //normal tweeting
        if (choice.equals("t")){
            System.out.println("What do you want to update your status to be? ");
            String status = scanner.nextLine();
            tweet(status);
        }

        //autotweeting
        else if (choice.equals("a")) {
            System.out.println("What do you want to update your status to be? ");
            String status = scanner.nextLine();
            autotweetWithoutInput(scanner, status);
        }

        //saving a tweet to database
        else if (choice.equals("s")){
            System.out.println("What tweet idea would you like to save? ");
            String tweetIdea = scanner.nextLine();
            try {
                TwitterDB.saveTweet(tweetIdea);
            }catch (Exception e){
            }
        }

        //viewing tweets in database
        else if (choice.equals("v")){
            System.out.println("Here are your saved tweets!");
            TwitterDB.viewTweets();
        }

        //entering the tweet database
        else if (choice.equals("e")){
            TwitterDB.viewTweets();
            System.out.println("Type in the ID of the status that you would like to tweet, or delete: ");
            String tweetIDString = scanner.nextLine();
            int tweetID = -1;
            String selectedTweet = "";
            while (true){
                try {
                    tweetID = Integer.parseInt(tweetIDString);
                    selectedTweet = TwitterDB.getTweetById(tweetID);
                    break;
                }catch (NumberFormatException e) {
                    System.out.println("Make sure your ID is a valid integer!");
                    tweetIDString = scanner.nextLine();
                }catch (SQLException e){
                    System.out.println("Make sure your ID has a corresponding tweet!");
                    tweetIDString = scanner.nextLine();
                }catch (Exception e){}
            }

            //tweetID is obtained, now program checks what to do with that tweet
            System.out.printf("Here is your selected tweet: %s\n", selectedTweet);
            System.out.println("Would you like to (t)weet, (a)utotweet, or (d)elete? ");
            String dbChoice = scanner.nextLine();
            while (!dbChoice.equals("t") && !dbChoice.equals("a") && !dbChoice.equals("d")) {
                System.out.println("Would you like to (t)weet, (a)utotweet, or (d)elete? ");
                dbChoice = scanner.nextLine();
            }

            //normal tweeting
            if (dbChoice.equals("t")){
                tweet(selectedTweet);
                deleteAfterTweeting(tweetID, scanner);
            }

            //autotweeting
            else if (dbChoice.equals("a")) {
                autotweetWithoutInput(scanner, selectedTweet);
                deleteAfterTweeting(tweetID, scanner);
            }

            //deleting the tweet
            else if (dbChoice.equals("d")){
                TwitterDB.deleteTweet(tweetID);
            }
        }
        System.out.println("Success!");
    }

    public static void main(String[] args) {
        Tweeter tweeter = new Tweeter();
    }
}
