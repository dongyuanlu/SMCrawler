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
import util.WriteArrayList2File;


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
public class InstagramRelationCrawler {
		
	private int THRESHOLD = 0;
	private int nSTEP = 1;	//the distance of neighbors from seed user
	
//	private static Random generator = new Random();
	private static SQLUtil sql = new SQLUtil(InstagramConfig.database);
	private static ReadInstagram reader;
	private static WriteInstagram writer;
	private static InstagramToken accessToken;
	
	private ArrayList<String> userIdListToCrawl;

	private ArrayList<InstagramUser> relationUserList;	// follower/followee list
	
	
	/**
	 * Constructor
	 */
	public InstagramRelationCrawler(){
		this.relationUserList = new ArrayList<>();
		reader = new ReadInstagram();
		writer = new WriteInstagram();
		accessToken = new InstagramToken();
		
	}

	
	
	public static void main(String[] args){

		InstagramRelationCrawler crawler = new InstagramRelationCrawler();
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
			userIdListToCrawl= reader.readUserNeighborsNotCrawlRelation(InstagramConfig.seedUserId, nSTEP);
			
			System.out.println("total number: " + userIdListToCrawl.size());
			//Loop for current userList
			for(int i = 0; i < userIdListToCrawl.size(); i++){
				String userId = userIdListToCrawl.get(i);
				System.out.println(userId);
				
				getRelationOfUser(userId);

				//Rest 1s for API limit
//				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			}
			
		//until less than THRESHOLD newly users
		}while(userIdListToCrawl.size() > THRESHOLD);
		
		
	}
	
	
	/**
	 * Given userid
	 * 1. Crawl user's followings and followees
	 * 2. Write newly followings & followees into instagram_user table
	 * 3. Write user-following/followedby relations into instagram_relation table
	 * 
	 * If failed, write userId into instagram_baduser table 
	 * 
	 * @param userId
	 */
	public void getRelationOfUser(String userId){
		
		String apiUrl = "https://api.instagram.com/v1/users/" + userId + "/";
		
		//*********************************
		//GET user1 follow user2
		
		String followingAPI = apiUrl + "follows?count=100&access_token=";
		relationUserList.clear();	//clear relation user list
		
		boolean flag_ing = getRelationUserList(followingAPI);	//get current user's followings
		if(!flag_ing){
			writer.writeBadUser2DB(userId, InstagramConfig.badUserTable, "following");
		}
		else{
			System.out.println("start following " + relationUserList.size() + " " + System.currentTimeMillis());
			writeUser2DB(relationUserList, InstagramConfig.instagramUserTable);
			System.out.println("mid: " + System.currentTimeMillis());
			writeRelation2DB(userId, relationUserList, InstagramConfig.instagramRelationTable, "following");
			System.out.println("end: " + System.currentTimeMillis());
		}
		
		//*********************************
		//GET user1 followed by user2
		
		String followedbyAPI = apiUrl + "followed-by?count=100&access_token=";
		relationUserList.clear();	//clear relation user list
		
		boolean flag_edby = getRelationUserList(followedbyAPI);	//get current user's followed by users
		if(!flag_edby){
			writer.writeBadUser2DB(userId, InstagramConfig.badUserTable, "followedby");
		}
		else{
			System.out.println("start followedby " + relationUserList.size() + " " + System.currentTimeMillis());
			writeUser2DB(relationUserList, InstagramConfig.instagramUserTable);
			writeRelation2DB(userId, relationUserList, InstagramConfig.instagramRelationTable, "followedby");
			System.out.println("end: " + System.currentTimeMillis());
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
		
		while(url.length() > 0){
			//Crawl json page and json object
			String jsonPage = PageCrawler.readUrl(url);
						
			if(!checkJsonPage(jsonPage, url)){	//check jsonPage, if false
				return false;
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
				url = nextObject.getString("next_url");	
				url = url.replaceAll("access_token=.*?&", "access_token="+accessToken.pickToken()+"&");
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
