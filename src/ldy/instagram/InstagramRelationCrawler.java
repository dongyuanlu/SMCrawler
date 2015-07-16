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
 * Based on seed users, crawl the users' followers and followees, add the new users into table
 * Then, iteratively crawl the new users' followers and followees
 * Aim to get a social network based on seed users
 * Terminate until less than THRESHOLD new users are added
 * 
 * @author Administrator
 *
 */
public class InstagramRelationCrawler {
	
	private String access_token;
	
	private int THRESHOLD = 10;
	
	private static Random generator = new Random();
	private static SQLUtil sql = new SQLUtil(InstagramConfig.database);
	private static ReadInstagram reader = new ReadInstagram();
	
	private ArrayList<String> userIdListToCrawl;

	private ArrayList<InstagramUser> relationUserList;	// follower/followee list
	
	/**
	 * Constructor
	 */
	public InstagramRelationCrawler(){
		this.access_token = InstagramConfig.accessTokens[generator.nextInt(InstagramConfig.accessTokens.length)];
		this.relationUserList = new ArrayList<>();
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
		
		//Iteratively crawl the relations of users in instagram_user table, whose relations has not been crawled
		do{
			userIdListToCrawl= reader.readUserIdInUserTableNotRelationTable();
			
			//Loop for current userList
			for(int i = 0; i < userIdListToCrawl.size(); i++){
				String userId = userIdListToCrawl.get(i);
				getRelationOfUser(userId);
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
		
		String followingAPI = apiUrl + "follows?access_token=" + access_token;
		relationUserList.clear();	//clear relation user list
		
		boolean flag_ing = getRelationUserList(followingAPI);	//get current user's followings
		if(!flag_ing){
			writeBadUser2DB(userId, InstagramConfig.badUserTable, "following");
		}
		else{
			writeUser2DB(relationUserList, InstagramConfig.instagramUserTable);
			writeRelation2DB(userId, relationUserList, InstagramConfig.instagramRelationTable, "following");
		}
		
		//*********************************
		//GET user1 followed by user2
		
		String followedbyAPI = apiUrl + "followed-by?access_token=" + access_token;
		relationUserList.clear();	//clear relation user list
		
		boolean flag_edby = getRelationUserList(followedbyAPI);	//get current user's followed by users
		if(!flag_edby){
			writeBadUser2DB(userId, InstagramConfig.badUserTable, "followedby");
		}
		else{
			writeUser2DB(relationUserList, InstagramConfig.instagramUserTable);
			writeRelation2DB(userId, relationUserList, InstagramConfig.instagramRelationTable, "followedby");
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
		
		//Crawl json page and json object
		String jsonPage = PageCrawler.readUrl(apiUrl);
		if(jsonPage == null){
			System.err.println(apiUrl + ": null");
			return false;
		}
		if(!jsonPage.contains("{")){
			System.err.println(apiUrl + ": not contains '{'");
			return false;
		}		
		JSONObject pageObject = new JSONObject(jsonPage);
		
		//Check status
		JSONObject metaObject = pageObject.getJSONObject("meta");
		if(metaObject.isNull("code") || ( !metaObject.isNull("code") && metaObject.getInt("code") != 200 )){
			return false;
		}
		
		//Get followList from next page if there is any
		JSONObject nextObject = pageObject.getJSONObject("pagination");
		if(!nextObject.isNull("next_cursor")){ 
			String nextApi = apiUrl + "&cursor=" + nextObject.getString("next_cursor");
			boolean flag = getRelationUserList(nextApi);
			if(!flag){
				return false;
			}
			
		}
		
		//Get followList from current page
		
		JSONArray dataArray = pageObject.getJSONArray("data");
		
		for(int i = 0; i<dataArray.length(); i++){
			InstagramUser user = new InstagramUser();
			
			JSONObject o = dataArray.getJSONObject(i);
			
			user.setUsername(o.getString("username"));
			user.setBio(o.getString("bio"));
			user.setFull_name(o.getString("full_name"));
			user.setId(o.getString("id"));
			user.setProfile_picture(o.getString("profile_picture"));
			user.setWebsite(o.getString("website"));
			
			relationUserList.add(user);
		}	//for end
		
		try {
			Thread.sleep(3000);	//For security
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		ArrayList<String> alreadyUserList = reader.readUserIdFromUserTable();
		
		//If user already in user profile table, continue
		String query = "insert ignore into " + tableName + " values("
				+ "?,?,?,?,?," + "?)";
		PreparedStatement pstmt = sql.createPreparedStatement(query);
		
		try {
			for(int i = 0; i < userList.size(); i++){
				InstagramUser user = userList.get(i);
				if(alreadyUserList.contains(user.getId())){
					continue;
				}
				
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
				+ "?,?)";
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
	 * IF failed to get user's relation, write into badusertable
	 * 
	 * @param userId
	 * @param tableName
	 * @param cause
	 */
	public void writeBadUser2DB(String userId, String tableName, String cause){
		String query = "INSERT INTO " + tableName + " values('" + userId + "', '" + cause + "')";
		Statement st = sql.getStatement();
		
		try {
			st.execute(query);
			
			st.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
			
	}

}
