package ldy.twitter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import ldy.instagram.InstagramConfig;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import util.SQLUtil;

public class TwitterUserTimeLine {
	
	TwitterUser user;
	ArrayList<Tweet> tweetList;

	private TwitterInitial twitterIni;
	private Twitter twitter;
	private SQLUtil sql;
	ReadTwitter reader;
	
	private final int COUNT = 200;
	
	/**
	 * Constructor
	 */
	public TwitterUserTimeLine(){
		twitterIni = new TwitterInitial();
		twitter = twitterIni.twitter();
		sql = new SQLUtil(InstagramConfig.database);		
		reader = new ReadTwitter();
		
	}
	
	
	
	public static void main(String[] args){
		TwitterUserTimeLine tutl = new TwitterUserTimeLine();
//		tutl.getQualifiedUserList();
//		tutl.getAllUserTimeLine();
//		tutl.getVerified();
		
		tutl.getUserTimeLineArray("MikeyJonCarr");
	}

	
	
	/**
	 * 
	 * CRAWL twitter user's profile and tweet stream
	 * WRITE into database
	 * 
	 * @param userName
	 * @return
	 */
	public boolean getUserTimeLineArray(String userName){
		//Initial
		user = null;
		tweetList.clear();
		int page = 1;
		
		try {
			Paging paging = new Paging(page,COUNT);
			///////////TEST//////////
			paging.setSinceId(reader.readLatestIdOfTweets(userName));
			///////////////////////////
			ResponseList<Status> statList = null;
			
			do{
				
				/////////////TEST////////////////
				ResponseList<Status> statList2 = twitter.getUserTimeline(userName);
				System.out.println(statList2.size());
				///////////////////////////////
				twitterIni.countApi(); //Check API security
				statList = twitter.getUserTimeline(userName, paging);	//Call Twitter API
				
				Iterator<Status> iter = statList.iterator();
				
				while(iter.hasNext())
				{					
					Status stat = iter.next();
					tweetList.add(getTweet(stat));
					if(user == null){
						user = getUser(stat);	//Get TwitterUserEle
						writeUser2DB();
					}
				}
				
				paging.setPage(page++);
				
			}while(statList.size()>0);
			
			//WRITE tweet list into twitter_tweet table
			if(tweetList.size()>0){
				writeTweet2DB();
			}
			
//			System.out.println(userName);
			return true;
			
		} catch (TwitterException e) {
			
			e.printStackTrace();
			return false;
		}

	}
	
	
	/**
	 * PARSE tweet from Status
	 * 
	 * @param stat
	 * @return
	 */
	public Tweet getTweet(Status stat){
		Tweet tweet = new Tweet();
		
		//ID
		String tweetId = stat.getId()+"";
		tweet.setTweet_id(tweetId);
		
		//Created_at
//		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//		String time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(stat.getCreatedAt());
		Long timeLong = stat.getCreatedAt().getTime();
		tweet.setCreated_at("");
		tweet.setCreated_at_long(timeLong);
		
		//Geo
		GeoLocation geo = stat.getGeoLocation();
		if(geo!=null){
			double lat = geo.getLatitude();
			double longi = geo.getLongitude();
			
			tweet.setLatitude(lat);
			tweet.setLongitude(longi);
		}
		
		//Hashtag
		HashtagEntity[] he = stat.getHashtagEntities();
		String hashtags = "";
		if(he!=null && he.length > 0){			
			for(int i = 0 ; i< he.length; i++){
				hashtags += he[i].getText() + ";";
			}			
		}
		tweet.setHashtags(hashtags);

		//User mentions
		UserMentionEntity[] userMen = stat.getUserMentionEntities();
		String user_mentions = "";
		String user_mention_id = "";
		if(userMen!=null && userMen.length > 0){
			for(int i = 0; i< userMen.length; i++){
				user_mentions += userMen[i].getScreenName() + ";";
				user_mention_id += userMen[i].getId()+";";
			}			
		}
		tweet.setUser_mentions(user_mentions);
		tweet.setUser_mention_id(user_mention_id);
		
		//URL
		URLEntity[] urlEntity = stat.getURLEntities();
		String expanded_url = "";
		if(urlEntity!=null && urlEntity.length > 0){
			for(int i = 0; i<urlEntity.length; i++){
				expanded_url += urlEntity[i].getExpandedURL()+ ";";
				
			}
		}
		tweet.setExpanded_url(expanded_url);
		
		//Text
		tweet.setText(stat.getText());
		
		//In_reply
		tweet.setIn_reply_to_user_id(stat.getInReplyToUserId()+"");
		tweet.setIn_reply_to_status_id(stat.getInReplyToStatusId()+"");
		tweet.setIn_reply_to_user_screenname(stat.getInReplyToScreenName());
		
		//Contributor	
		long[] cons = stat.getContributors();
		String contributor = "";
		if(cons!=null && cons.length>0){
			for(int i = 0; i< cons.length; i++){
				contributor += cons[i] + ";";
			}
			
		}
		tweet.setContributors(contributor);
		
		//Retweet
		tweet.setRetweet_count(stat.getRetweetCount());
		tweet.setRetweeted(stat.isRetweet());
		
		//Place
		Place p = stat.getPlace();
		if(p!=null){
			tweet.setPlace(p.toString());
		}
		
		
		//User		
		User user = stat.getUser();
		tweet.setScreen_name(user.getScreenName());
		tweet.setUser_id(user.getId()+"");
		
		//Source
		tweet.setSource(stat.getSource());
		
		return tweet;
	}
	
	/**
	 * PARSE User from Status
	 * @param stat
	 * @return
	 */
	public TwitterUser getUser(Status stat){
		User user = stat.getUser();

		TwitterUser tUser = new TwitterUser();
		
		tUser.setScreen_name(user.getScreenName());
		tUser.setUser_id(user.getId()+"");
		
		//Created_at
//		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//		String time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(user.getCreatedAt());
		Long timeLong = stat.getCreatedAt().getTime();
		tUser.setCreated_at("");
		tUser.setCreated_at_long(timeLong);
		
		tUser.setDescription(user.getDescription());
		tUser.setName(user.getName());
		tUser.setLocation(user.getLocation());
		tUser.setProfile_image_url(user.getProfileImageURL());
		tUser.setUrl(user.getURL());
		
		tUser.setFollower_count(user.getFollowersCount());
		tUser.setFriend(user.getFriendsCount());
		tUser.setFavorite_count(user.getFavouritesCount());
		
		tUser.setTime_zone(user.getTimeZone());
		tUser.setStatus_count(user.getStatusesCount());
		tUser.setIs_geo_enabled(user.isGeoEnabled());
		tUser.setListed_count(user.getListedCount());
		tUser.setIs_verified(user.isVerified());
		
		return tUser;
	}
	
	
	/**
	 * Write TwitterUserEle into DB
	 * 
	 * @param user
	 * @return
	 */
	public boolean writeUser2DB(){
		
		String query = "insert ignore into " + TwitterConfig.twitterUserTable  + " values("
				+ "?,?,?,?,?," + "?,?,?,?,?," +"?,?,?,?,?," +"?,?)";
		PreparedStatement pstmt = sql.createPreparedStatement(query);
				
		try {
			
			pstmt.setString(1, user.getScreen_name());
			pstmt.setString(2, user.getUser_id());
			pstmt.setString(3, user.getCreated_at());
			pstmt.setLong(4, user.getCreated_at_long());
			pstmt.setString(5, user.getDescription());
			
			pstmt.setString(6, user.getName());
			pstmt.setString(7, user.getLocation());
			pstmt.setString(8, user.getProfile_image_url());
			pstmt.setString(9, user.getUrl());

			pstmt.setInt(10, user.getFollower_count());
			pstmt.setInt(11, user.getFriend());
			pstmt.setInt(12, user.getFavorite_count());
			pstmt.setString(13, user.getTime_zone());
			pstmt.setInt(14, user.getStatus_count());
			pstmt.setBoolean(15, user.isIs_geo_enabled());
			pstmt.setInt(16, user.getListed_count());
			pstmt.setBoolean(17, user.isIs_verified());

			pstmt.execute();
			pstmt.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * Write TweetEle into DB
	 * 
	 * @param tweetList
	 * @return
	 */
	public boolean writeTweet2DB(){
		
		String query = "insert ignore into " + TwitterConfig.tweetTable + " values("
				+ "?,?,?,?,?," + "?,?,?,?,?," +"?,?,?,?,?," +"?,?,?,?,?)";
		PreparedStatement pstmt = sql.createPreparedStatement(query);
		
		Iterator<Tweet> iter = tweetList.iterator();
		try {
			while(iter.hasNext()){
				Tweet tweet = iter.next();
				
				
				pstmt.setString(1, tweet.getTweet_id());
				pstmt.setString(2, tweet.getCreated_at());
				pstmt.setLong(3, tweet.getCreated_at_long());
				pstmt.setDouble(4, tweet.getLongitude());
				pstmt.setDouble(5, tweet.getLatitude());
				
				pstmt.setString(6, tweet.getHashtags());
				pstmt.setString(7, tweet.getUser_mentions());
				pstmt.setString(8, tweet.getUser_mention_id());
				pstmt.setString(9, tweet.getExpanded_url());
				pstmt.setString(10, tweet.getText());
				
				pstmt.setString(11, tweet.getIn_reply_to_user_id());
				pstmt.setString(12, tweet.getContributors());
				pstmt.setLong(13, tweet.getRetweet_count());
				pstmt.setString(14, tweet.getIn_reply_to_status_id());
				pstmt.setBoolean(15, tweet.isRetweeted());
				
				pstmt.setString(16, tweet.getPlace());
				pstmt.setString(17, tweet.getScreen_name());
				pstmt.setString(18, tweet.getUser_id());
				pstmt.setString(19, tweet.getSource());
				pstmt.setString(20, tweet.getIn_reply_to_user_screenname());
				
				pstmt.addBatch();
				
			}
			
			pstmt.executeBatch();
			pstmt.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	
}
