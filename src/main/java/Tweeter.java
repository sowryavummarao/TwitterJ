import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

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
    }};
    TwitterFactory tf;
    static List<String> savedTweets = new ArrayList<String>();

    /**
     * Constructor sets up necessary authorizations and starts thread
     */
    public Tweeter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(AuthStrings.OAuthConsumerKey)
                .setOAuthConsumerSecret(AuthStrings.OAuthConsumerSecret)
                .setOAuthAccessToken(AuthStrings.OAuthAccessToken)
                .setOAuthAccessTokenSecret(AuthStrings.OAuthAccessTokenSecret);
        tf = new TwitterFactory(cb.build());
        start();
    }

    /**
     * Method will simply tweet a status
     * @param status - the status to tweet
     */
    private void tweet(String status){
        Twitter twitter = this.tf.getInstance();
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

    @Override
    /**
     * Runs the thread, goes through options and asks user for time, status, etc.
     */
    public void run(){
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        while (!choices.contains(choice)) {
            System.out.println("Would you like to (t)weet, (a)utotweet, (s)ave a tweet, or (v)iew saved tweets? ");
            choice = scanner.nextLine();
        }

        if (choice.equals("t")){
            System.out.println("What do you want to update your status to be? ");
            String status = scanner.nextLine();
            tweet(status);
        }

        else if (choice.equals("a")) {
            System.out.println("What do you want to update your status to be? ");
            String status = scanner.nextLine();
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

        else if (choice.equals("s")){
            System.out.println("What tweet idea would you like to save? ");
            String tweetIdea = scanner.nextLine();
            savedTweets.add(tweetIdea);
        }

        else {
            System.out.println("Here are your saved tweets!");
            for (int i = 0; i < savedTweets.size(); i++){
                System.out.println(i + " - " + savedTweets.get(i));
            }
        }
    }

    public static void main(String[] args) {
        Tweeter tweeter = new Tweeter();
    }
}
