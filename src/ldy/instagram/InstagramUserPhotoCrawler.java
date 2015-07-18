package ldy.instagram;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import util.PageCrawler;
import util.SQLUtil;
import ldy.instagram.InstagramPhoto;

/**
 * 
 * CRAWL photo stream of users
 * 
 * @author ellen
 *
 */
public class InstagramUserPhotoCrawler {

	private ArrayList<InstagramPhoto> photoList;
	
	private static SQLUtil sql = new SQLUtil(InstagramConfig.database);
	private static ReadInstagram reader;
	private static WriteInstagram writer;
	
		
	/**
	 * Constructor
	 */
	public InstagramUserPhotoCrawler(){
		this.photoList = new ArrayList<>();
		reader = new ReadInstagram();
		writer = new WriteInstagram();

	}
	
	
	public static void main(String[] args){
		InstagramUserPhotoCrawler photoCrawler = new InstagramUserPhotoCrawler();
		photoCrawler.crawlUsersPhotoStream();
	}
	
	
	
	/**
	 * 
	 * Crawl the photo stream of users in instagram_user table whose photo stream not crawled
	 * AND Write photos into instagram_photo table
	 * If failed, write userId into bad user table with flag 'photostream'
	 * 
	 */
	public void crawlUsersPhotoStream(){

		ArrayList<String> userIdsToCrawl = reader.readUserIdInUserTableNotPhotoTable();
		
		for(int i = 0; i < userIdsToCrawl.size(); i++){

			String userId = userIdsToCrawl.get(i);
			String access_token = InstagramConfig.accessTokens[i%3]; //select one token

			//Crawl photo stream and write into table
			boolean flag = getUserRecentStream(userId, access_token);

			if(!flag){ //if failed
				writer.writeBadUser2DB(userId, InstagramConfig.badUserTable, "photostream");
				
			}
		}
	}
	
	
	
	/**
	 * 
	 * Crawl user's recent public photo stream
	 * Write the photos into instagram_photo table
	 * 
	 * @param userId: the id of user
	 * @return 
	 * 		true: successfully crawled user's photos
	 * 		false: failed to get user's photos; 
	 * 	  
	 */
	public boolean getUserRecentStream(String userId, String access_token){
		photoList.clear();
		
		//GET photos that are uploaded later than existing ones
//		long latestPhotoTimestamp = getLatestTimestamp(userId);
		String api = InstagramConfig.userRecentBaseUrl + userId + "/media/recent/?access_token=" + access_token
				+ "&count=40";
		String jsonPage = PageCrawler.readUrl(api);
		
		//LOOP for pages
		while(api.length() > 0){
			
			//Check crawled json page
			if(jsonPage == null){
				System.err.println(api + ": null");
				return false;
			}
			if(!jsonPage.contains("{")){
				System.err.println(api + ": not contains '{'");
				return false;
			}		
			
			JSONObject jObj = new JSONObject(jsonPage);
			
			//PAESE next page api
			JSONObject pagiObj = jObj.getJSONObject("pagination");
			if(!pagiObj.isNull("next_url")){	//if next page is null, break loop
				api = "";
			}
			else{
				api = pagiObj.getString("next_url") + "&count=40";
			}
			
			//Parse meta data
			JSONObject obj = jObj.getJSONObject("meta");			
			if(!obj.isNull("code")){
				if(obj.getInt("code") == 200){	//If state is OK
					
					JSONArray jsonA = jObj.getJSONArray("data");
					
					for(int i = 0; i<jsonA.length(); i++){
						JSONObject o = jsonA.getJSONObject(i);
						InstagramPhoto photo = parsePhotoFromObj(o);
						photo.setUserId(userId);
						photoList.add(photo);
						
					}
				}
				
			}else{
				return false;
			}
			
		}
		
		//WRITE Instagram photo list into table
		writePhoto2DB();
		
		return true;
	}
	
	
	/**
	 * 
	 * Given one photo JSONObject, parse InstagramPhoto photo
	 * 
	 * @param obj
	 * @return InstagramPhoto
	 */
	public InstagramPhoto parsePhotoFromObj(JSONObject obj){
		
		InstagramPhoto photo = new InstagramPhoto();
		
		String photoId = obj.getString("id");
		photo.setPhotoId(photoId);
		
		String tags = "";
		JSONArray a = obj.getJSONArray("tags");
		if(a!=null && a.length()>0){
			for(int i = 0; i<a.length(); i++){
				tags += a.getString(i) + ";";
			}
		}
		photo.setTags(tags);
		
		String timestamp = obj.getString("created_time");
		long timeS = Long.parseLong(timestamp)*1000;
//		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//		String time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date (timeS));
		photo.setCreated_time("");
		photo.setCreated_time_long(timeS);
		
		String text = "";
		if(!obj.isNull("caption")){
			JSONObject oo = obj.getJSONObject("caption");
			text = oo.getString("text");
			photo.setText(text);
		}
		
		photo.setLink(obj.getString("link"));
		
		String imageUrl = "";
		if(!obj.isNull("images")){
			JSONObject oo = obj.getJSONObject("images");
			JSONObject ooo = oo.getJSONObject("standard_resolution");
			imageUrl = ooo.getString("url");
			photo.setImage_url(imageUrl);
		}
		
		if(!obj.isNull("location")){
			JSONObject oo = obj.getJSONObject("location");
			if(!oo.isNull("latitude")){
				double lati = oo.getDouble("latitude");
				double longi = oo.getDouble("longitude");
				
				photo.setLatitude(lati);
				photo.setLongitude(longi);
			}else if(!oo.isNull("id")){
				String id = oo.getLong("id")+"";
				photo.setLocation_id(id);
			}
			
		}
		
		String likes = "";
		if(!obj.isNull("likes")){
			JSONObject o = obj.getJSONObject("likes");
			int count = o.getInt("count");
			photo.setLikes_count(count);
			
			JSONArray array = o.getJSONArray("data");
			
			for(int i = 0; i< array.length(); i++){
				JSONObject oo = array.getJSONObject(i);
				String id = oo.getString("id");
				likes += id +";";
			}
		}
		photo.setLikes(likes);
		
		String comments = "";
		if(!obj.isNull("comments")){
			JSONObject o = obj.getJSONObject("comments");
			int count = o.getInt("count");
			photo.setComment_count(count);
			
			JSONArray array = o.getJSONArray("data");
			for(int i = 0; i< array.length(); i++){
				JSONObject oo = array.getJSONObject(i);
				String t = oo.getString("created_time");
				String te = oo.getString("text");
				JSONObject ooo = oo.getJSONObject("from");
				String idd = ooo.getString("id");
				
				comments += t +"&" + idd + "&" + te + "//";
			}
			
		}
		photo.setComments(comments);
		
		String users_in_photo = "";
		JSONArray aa = obj.getJSONArray("users_in_photo");
		if(aa!=null && aa.length()>0){
			for(int i = 0; i<aa.length(); i++){
				JSONObject oo = aa.getJSONObject(i);
				
				JSONObject ou = oo.getJSONObject("user");
				String uid = ou.getString("id");
				
				JSONObject op = oo.getJSONObject("position");
				double x = op.getDouble("x");
				double y = op.getDouble("y");
				
				String pp = "x=" + x + " " + "y=" + y +" " + "id=" + uid;
				users_in_photo += pp + ";";
			}
		}
		photo.setUsers_in_photo(users_in_photo);
		
		photo.setUser_has_liked(obj.getBoolean("user_has_liked"));
		
		
		if(!obj.isNull("filter")){
			
			String filter = obj.getString("filter");
			photo.setFilter(filter);;
		}
		
		
		return photo;
	}
	
	
	/**
	 * Write photoList into database
	 * 
	 * @param photoList
	 * @return
	 */
	public boolean writePhoto2DB(){
		String query = "insert ignore into " + InstagramConfig.instagramPhotoTable + " values("
				+ "?,?,?,?,?," + "?,?,?,?,?," +"?,?,?,?,?," + "?,?,?,?)";
		PreparedStatement pstmt = sql.createPreparedStatement(query);
		
		Iterator<InstagramPhoto> iter = photoList.iterator();
		
		try {
			
			while(iter.hasNext()){
				
				InstagramPhoto photo = iter.next();
				
				pstmt.setString(1, photo.getPhotoId());
				pstmt.setString(2, photo.getUserId());
				pstmt.setString(3, photo.getTags());
				pstmt.setString(4, photo.getCreated_time());
				pstmt.setLong(5, photo.getCreated_time_long());
				pstmt.setString(6, photo.getText());
				
				pstmt.setString(7, photo.getLink());
				pstmt.setDouble(8, photo.getLatitude());
				pstmt.setDouble(9, photo.getLongitude());
				pstmt.setString(10, photo.getLikes());
				pstmt.setInt(11, photo.getLikes_count());
				
				pstmt.setString(12, photo.getComments());
				pstmt.setInt(13, photo.getComment_count());
				pstmt.setString(14, photo.getImage_url());
				pstmt.setString(15, photo.getUsers_in_photo());
				
				pstmt.setBoolean(16, photo.isUser_has_liked());
				pstmt.setString(17, photo.getLocation_id());
				pstmt.setInt(18, -1);
				pstmt.setString(19, photo.getFilter());
				
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
	 * GET latest created_time_long (second) of userid's photos 
	 * 
	 * @param userId
	 * @return latest created_time_long (format in second, not millisecond)
	 * 
	 */
	public long getLatestTimestamp(String userId){
		long timestamp = 0;
		
		String query = "SELECT max(created_time_long) FROM " + InstagramConfig.instagramPhotoTable + " WHERE user_id='" + userId + "'";
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(query);
			while(rs.next()){
				timestamp = rs.getLong("max(created_time_long)") / 1000;
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
		return timestamp;
	}

}
