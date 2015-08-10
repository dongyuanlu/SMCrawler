package ldy.twitter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * 
 * Initial Twitter4j
 * 
 * @author ellen
 *
 */
public class TwitterInitial {
	
	private String accessTokenConfig = TwitterConfig.accessTokenConfig;
	
	private TwitterFactory factory;
	private Twitter twitter;

	private int LIMIT = 170;  //limit 180
	private int QUAT = 900000;  //15mins
	private int countTokens = 0;
	private long startTime;
	
	public TwitterInitial(){
		startTime = System.currentTimeMillis();
	}

	
	public Twitter twitter(){
		
		factory = new TwitterFactory();
		twitter = factory.getInstance();
		
		String[] keys = loadConsumerKey();
		twitter.setOAuthConsumer(keys[0], keys[1]);
		AccessToken accessToken = loadAccessToken();
		twitter.setOAuthAccessToken(accessToken);

		return twitter;
	}
	
	
	/**
	 * 
	 * Count visit twitter API, control visiting 
	 * 
	 */
	public void countApi(){
		
		if(countTokens < LIMIT)
		{
			countTokens++;
		}
		else
		{
			long timeInterval = System.currentTimeMillis() - startTime - 10000;
			if(timeInterval > QUAT){
				countTokens = 0;
				startTime = System.currentTimeMillis();
			}else{
				sleep(startTime);
			}
		}
		
	}

	
	
	/**
	 * 
	 * @param accesstokenConfig
	 * @return
	 */
	public AccessToken loadAccessToken(){
		
		Properties pro = new Properties();
		
		try {
			
			pro.load(new FileInputStream(accessTokenConfig));
			String token = pro.getProperty("token");
			String tokenSecret = pro.getProperty("tokenSecret");
			return new AccessToken(token, tokenSecret);
						
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	/**
	 * 
	 * @param accesstokenConfig
	 * @return
	 */
	public String[] loadConsumerKey(){
		
		Properties pro = new Properties();
		String[] keys = {"",""};
		
		try {
			
			pro.load(new FileInputStream(accessTokenConfig));
			keys[0] = pro.getProperty("key");
			keys[1] = pro.getProperty("secret");
			return keys;
						
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			return keys;
		} catch (IOException e) {
			
			e.printStackTrace();
			return keys;
		}
	}
	
	
	public void sleep(long currentTime){
		
		try {
			long time = QUAT - (System.currentTimeMillis() - currentTime);
			System.err.println("sleep for " + time + ": for security");
			Thread.sleep(time);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

}
