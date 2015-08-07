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
	
	private static Twitter twitter;
	
	private Status tweet;

	public TwitterSearch(){
		
		twitter = TwitterInitial.twitter();
		tweet = null;
	}
	
	
	
	public static void main(String[] args){
		TwitterSearch searcher = new TwitterSearch();
	
	}
	
	
	/**
	 * 
	 * Given Instagram Link Id, search cross-sharing tweet in twitter
	 * 
	 * @param insLinkId
	 * @return
	 * 		Status tweet if find correct tweet; if not return null
	 */
	public Status searchInstagramId(String insLinkId){
		Query q = generateQuery(insLinkId);
		
		tweet = null;		

		try {
			QueryResult searchResult = twitter.search(q);
			List<Status> results = searchResult.getTweets();
			for(int i = 0; i < results.size(); i++){
				tweet = results.get(i);

				System.out.println(tweet.getText() + "\t" + tweet.getSource() + "\t" + tweet.getUser().getScreenName());
			
				if(checkTweet(insLinkId)){
					break;
				}
			}
			
			
		} catch (TwitterException e) {

			e.printStackTrace();
		}
		
		return tweet;
	}
	
	/**
	 * 
	 * Check whether this tweet is the corresponding cross-shared instagram photo
	 * 
	 * @param insLinkId
	 * @return
	 */
	public boolean checkTweet(String insLinkId){
		
		//if tweet text not contain linkId, return false
		if(!tweet.getText().contains(insLinkId)){
			return false;
		}
		
		//if source is not from instagram.com, return false
		if(!tweet.getSource().contains("instagram")){
			return false;
		}
		
		
		return true;
	}

	
	public Query generateQuery(String insLinkId){
		Query q = new Query();
		q.setCount(5);
		q.setQuery("\"" +  insLinkId + "\"");
		q.setResultType(Query.MIXED);

		return q;
	}
}
