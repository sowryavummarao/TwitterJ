import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TwitterAuth {

    /**
     * This method will use OAuth to get necessary access tokens to allow users to easily authorize this app to function on their twitter
     * @param twitter TwitterInstance needed to run all twitter related activity
     * @throws TwitterException if tweet cant be tweeted for some reason
     * @throws IOException if reading user's stdin results in an error
     */
    static long setAccessTokens(Twitter twitter) throws TwitterException, IOException {
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if available) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try{
                if(pin.length() > 0){
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                }else{
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if(401 == te.getStatusCode()){
                    System.out.println("Unable to get the access token.");
                }else{
                    te.printStackTrace();
                }
            }
        }
        //persist to the accessToken for future reference.
        twitter.setOAuthAccessToken(accessToken);
        return accessToken.getUserId();
    }
}
