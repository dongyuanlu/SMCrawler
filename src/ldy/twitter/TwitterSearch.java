package ldy.twitter;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterSearch {
	
	private static TwitterFactory factory;
	private static Twitter twitter;

	
	
	public TwitterSearch(){
		
		factory = new TwitterFactory();
		twitter = factory.getInstance();
		
		String[] keys = OAuth4J.loadConsumerKey();
		twitter.setOAuthConsumer(keys[0], keys[1]);
		AccessToken accessToken = OAuth4J.loadAccessToken();
		twitter.setOAuthAccessToken(accessToken);

	}
	
	
	
	public static void main(String[] args){
		TwitterSearch searcher = new TwitterSearch();
		searcher.searchInstagramPost();
	}
	
	
	
	public void searchInstagramPost(){
		Query q = new Query("Victoria's first set of diamond earrings.");

		try {
			QueryResult searchResult = twitter.search(q);
			List<Status> results = searchResult.getTweets();
			for(int i = 0; i < results.size(); i++){
				Status tweet = results.get(i);
				System.out.println(tweet.toString());
			}
			
			
		} catch (TwitterException e) {

			e.printStackTrace();
		}
	}

}
