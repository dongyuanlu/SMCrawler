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
	
	private static String accessTokenConfig = TwitterConfig.accessTokenConfig;
	
	private static TwitterFactory factory;
	private static Twitter twitter;

	
	public static Twitter twitter(){
		
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
	 * @param accesstokenConfig
	 * @return
	 */
	public static AccessToken loadAccessToken(){
		
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
	public static String[] loadConsumerKey(){
		
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

}
