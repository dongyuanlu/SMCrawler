package ldy.twitter;

public class TwitterConfig {
	
	public static String accessTokenConfig = "data/accesstoken.config";

	//***************************************************
	//DATABASE tables
	public static String tweetTable = "twitter_tweet";
	public static String twitterUserTable = "twitter_user";


	
	//***************************************************
	//URL	
	public static String userTimelineBase = "https://api.twitter.com/1.1/statuses/user_timeline.json";
}
