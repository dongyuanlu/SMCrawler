package ldy.instagram;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import util.PageCrawler;
import util.SQLUtil;



/**
 * 
 * Based on seed users, crawl the users' followers and followings, add the new users into table
 * Then, iteratively crawl the new users' followers and followings
 * Aim to get a social network based on seed users
 * Terminate until less than THRESHOLD new users are added
 * 
 * @author Administrator
 *
 */
public class InstagramRelationCrawlerCopy2 {
		
	private int THRESHOLD = 0;
	private int nSTEP = 1;	//the distance of neighbors from seed user
	
//	private static Random generator = new Random();
	private static SQLUtil sql = new SQLUtil(InstagramConfig.database);
	private static ReadInstagram reader;
	private static WriteInstagram writer;
	private static InstagramToken_2 accessToken;
	
	private String RELATIONTABLE;
	private String USERTABLE;
	private String BADUSERTABLE;
	
	private ArrayList<String> userIdListToCrawl;

	private ArrayList<InstagramUser> relationUserList;	// follower/followee list
	
	
	/**
	 * Constructor
	 */
	public InstagramRelationCrawlerCopy2(){
		this.relationUserList = new ArrayList<>();
		USERTABLE = "instagram_user_realfriend";
		RELATIONTABLE = "instagram_relation_realfriend";
		BADUSERTABLE = "instagram_realfriend_baduser";

		reader = new ReadInstagram("",BADUSERTABLE,USERTABLE,RELATIONTABLE);
		writer = new WriteInstagram();
		accessToken = new InstagramToken_2();
		
		
	}

	
	
	public static void main(String[] args){

		InstagramRelationCrawlerCopy2 crawler = new InstagramRelationCrawlerCopy2();
		crawler.evolveCrawling();
		
	}
	
	
	/**
	 * Read users from instagram_user table whose relations are not crawled
	 * Iteratively crawl their relations
	 * 
	 */
	public void evolveCrawling(){
		
		//Iteratively crawl the relations of n step neighbors of seed user
		//whose relations has not been crawled
		do{
		//	userIdListToCrawl= reader.readUserNeighborsNotCrawlRelation(InstagramConfig.seedUserId, nSTEP);
			userIdListToCrawl = reader.readUserIdFromBadUserTable();
			System.out.println("total number: " + userIdListToCrawl.size());
			//Loop for current userList
			int i = userIdListToCrawl.indexOf("5872671");
			for(i = i-1; i > 0; i--){
				String userId = userIdListToCrawl.get(i);
				System.out.println(i + ": "+userId);
				
				//******Crawl followings and followers of current user********//
				getRelationOfUser(userId);

			}
			
		//until less than THRESHOLD newly users
		}while(userIdListToCrawl.size() > THRESHOLD);
		
		
	}
	
	
	/**
	 * Given userid
	 * 1. Crawl user's followings and followers
	 * 2. Write newly followings & followers into instagram_user table
	 * 3. Write user-following/followedby relations into instagram_relation table
	 * 
	 * If failed, write userId into instagram_baduser table 
	 * 
	 * @param userId
	 */
	public void getRelationOfUser(String userId){
		
		String apiUrl = "https://api.instagram.com/v1/users/" + userId + "/";
		
		//*********************************
		//GET user1 follow user2: following
		
		String followingAPI = apiUrl + "follows?count=100&access_token=";
		relationUserList.clear();	//clear relation user list
		
		//*****Crawl followings of current user****///////
		boolean flag_ing = getRelationUserList(followingAPI);	
		
		//If failed, write current userid into baduser table
		if(!flag_ing){
			writer.writeBadUser2DB(userId, BADUSERTABLE, "following");
		}
		//If successful, write user1_user2_following into relation table;
		//write news users into usertable
		else{
			System.out.println("start following " + relationUserList.size() + " " + System.currentTimeMillis());
			writeUser2DB(relationUserList, USERTABLE);
			writeRelation2DB(userId, relationUserList, RELATIONTABLE, "following");
			System.out.println("end: " + System.currentTimeMillis());
		}
		
		//*********************************
		//GET user1 followed by user2: followedby
		
		String followedbyAPI = apiUrl + "followed-by?count=100&access_token=";
		relationUserList.clear();	//clear relation user list
		
		boolean flag_edby = getRelationUserList(followedbyAPI);	//get current user's followed by users
		if(!flag_edby){
			writer.writeBadUser2DB(userId, BADUSERTABLE, "followedby");
		}
		else{
			System.out.println("start followedby " + relationUserList.size() + " " + System.currentTimeMillis());
			writeUser2DB(relationUserList, USERTABLE);
			writeRelation2DB(userId, relationUserList, RELATIONTABLE, "followedby");
			System.out.println("end: " + System.currentTimeMillis());
		}
		
		if(flag_ing && flag_edby){
			writer.deleteBadUser(userId, BADUSERTABLE);
		}
	}
	
	
	
	
	/**
	 * 
	 * Given APIUrl, Get relationUserList
	 * 
	 * 
	 * @param apiUrl
	 * @return
	 */
	public boolean getRelationUserList(String apiUrl){
		String url = apiUrl  + accessToken.pickToken();
		
		boolean firstPageFlag = true;
		
		while(url.length() > 0){
			//Crawl json page and json object
			String jsonPage = PageCrawler.readUrl(url);
						
			if(!checkJsonPage(jsonPage, url)){	//check jsonPage, if false
				//if this is the first page, return false
				if(firstPageFlag){
					return false;
				}
				else//if not the first page, re-crawl current page;
				{
					try {Thread.sleep(1500);} catch (InterruptedException e) {}
					continue;
				}
			}
			
			JSONObject pageObject = new JSONObject(jsonPage);
			
			//Check status code
			if(!checkMetaCode(pageObject)){	//check status code, if not 200, return false
				return false;
			}
						
			//Get followList from current page		
			JSONArray dataArray = pageObject.getJSONArray("data");
			
			for(int i = 0; i<dataArray.length(); i++)
			{			
				JSONObject o = dataArray.getJSONObject(i);	//parse InstagramUser from jsonobject
				relationUserList.add(parseUserObj(o));
			}	//for end

			
			//Get followList from next page if there is any
			JSONObject nextObject = pageObject.getJSONObject("pagination");
			if(!nextObject.isNull("next_url"))
			{
				firstPageFlag = false;
				url = nextObject.getString("next_url");	
				url = url.replaceAll("access_token=.*?&", "access_token="+accessToken.pickToken()+"&");
				try {Thread.sleep(500);} catch (InterruptedException e) {}
			}else
			{
				url = "";
			}

		}
		
		return true;
	}
	
	
	
	
	/**
	 * Write Newly InstagramUser List into table
	 * If user is already in table, ignore it
	 * 
	 * @param user
	 * @return
	 */
	public boolean writeUser2DB(ArrayList<InstagramUser> userList, String tableName){
		
		//Get the user list from current user profile table
//		ArrayList<String> alreadyUserList = reader.readUserIdFromUserTable();
		
		//If user already in user profile table, continue
		String query = "insert ignore into " + tableName + " values("
				+ "?,?,?,?,?," + "?)";
		PreparedStatement pstmt = sql.createPreparedStatement(query);
		
		try {
			for(int i = 0; i < userList.size(); i++){
				InstagramUser user = userList.get(i);
//				if(alreadyUserList.contains(user.getId())){
//					continue;
//				}
				
				pstmt.setString(1, user.getUsername());
				pstmt.setString(2, user.getBio());
				pstmt.setString(3, user.getWebsite());
				pstmt.setString(4, user.getProfile_picture());
				pstmt.setString(5, user.getFull_name());
				pstmt.setString(6, user.getId());
				
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
	
	
	/**
	 * Write the relation into database
	 * 
	 * @param userId
	 * @param userList
	 * @param tableName
	 * @return
	 */
	public boolean writeRelation2DB(String userId, ArrayList<InstagramUser> userList, String tableName, String option){
		String query = "insert ignore into " + tableName + " values("
				+ "?,?,?)";
		PreparedStatement pstmt = sql.createPreparedStatement(query);
		
		try {
			for(int i = 0; i < userList.size(); i++){
				InstagramUser user = userList.get(i);
				pstmt.setString(1, userId);
				pstmt.setString(2, user.getId());
				pstmt.setString(3, option);
				
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
	
	
	/**
	 * 
	 * Check jsonPage
	 * 
	 * @param jsonPage
	 * @param apiUrl
	 * @return
	 */
	public boolean checkJsonPage(String jsonPage, String apiUrl){
		if(jsonPage == null){
			System.err.println(apiUrl + ": null");
			return false;
		}
		if(!jsonPage.contains("{")){
			System.err.println(apiUrl + ": not contains '{'");
			return false;
		}
		return true;

	}
	
	
	public boolean checkMetaCode(JSONObject pageObject){
		JSONObject metaObject = pageObject.getJSONObject("meta");
		if(metaObject.isNull("code") || ( !metaObject.isNull("code") && metaObject.getInt("code") != 200 )){
			return false;
		}
		return true;
	}
	
	
	public InstagramUser parseUserObj(JSONObject o){
		InstagramUser user = new InstagramUser();
		
		user.setUsername(o.getString("username"));			
		if(!o.isNull("bio")){ user.setBio(o.getString("bio")); }
		user.setFull_name(o.getString("full_name"));
		user.setId(o.getString("id"));
		user.setProfile_picture(o.getString("profile_picture"));
		if(!o.isNull("website")){ user.setWebsite(o.getString("website"));}

		return user;
	}

}
