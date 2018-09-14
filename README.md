# TwitterJ
Sowrya's personal twitter project

This is a Twitter helper that works on the command line. Uses Twitter4J (Java library for Twitter API). 
Users can tweet a status straight from the command line, use an autotweeting option that will tweet at a specified time. 

Users can also save tweet ideas into a database, and tweet/autotweet straight from this database. 

Application uses OAuth so that users don't have to manually sign in. 

When users click on the authorization link, it will redirect to https://rhetocracy.org (just a placeholder website for now) after, there will be 2 parameters in the URL, "oauth_token" and "oauth_verifier", the value of "ouath_verifier" should be copied and pasted as the PIN into the application. 

Program runs until user supplies quit option, only one authorization required per run of program.
