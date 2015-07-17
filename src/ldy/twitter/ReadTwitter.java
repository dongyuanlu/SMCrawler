package ldy.twitter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ldy.instagram.InstagramConfig;
import util.SQLUtil;

/**
 * 
 * Read everything of Twitter from database, files, etc.
 * 
 * @author Administrator
 *
 */
public class ReadTwitter {

	private SQLUtil sql;
	
	
	public ReadTwitter(){
		sql = new SQLUtil(InstagramConfig.database);
	}
	
	
	/**
	 * 
	 * READ tweet Id from user's tweet stream which is latest one in twitter_tweet table
	 * 
	 * @param username
	 * @return latest tweet id;
	 * 			IF no tweets, return 0
	 */
	public long readLatestIdOfTweets(String username){
		long sinceId = 0;
		String query = "SELECT id, MAX(created_time_long) FROM " 
				+ TwitterConfig.tweetTable + " WHERE username='" + username + "'";
		Statement st = sql.getStatement();
		try {
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next()){
				sinceId = rs.getLong("id");
				
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return sinceId;
		
	}
	
}
