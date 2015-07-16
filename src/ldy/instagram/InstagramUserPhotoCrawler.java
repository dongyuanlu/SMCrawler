package ldy.instagram;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import util.WebPageCrawler;
import wing.ldy.instagram.InstagramPhotoEle;

public class InstagramUserPhotoCrawler {
	
	
	
	
	
	
	/**
	 * Given userId, get recent public photo stream
	 * Return an ArrayList
	 * 
	 * @param userId
	 * @return
	 */
	public ArrayList<InstagramPhoto> getUserRecentStream(String userId){
		
		ArrayList<InstagramPhoto> photoList = new ArrayList<InstagramPhotoEle>();
		
		String api = userRecentBaseUrl + userId + "/media/recent/?access_token=" + access_token + "&count=10000";
		String jsonPage = WebPageCrawler.crawlbyUrl(api);
		
		if(!jsonPage.contains("{")){
			return null;
		}
		
		JSONObject jObj = new JSONObject(jsonPage);
		JSONObject obj = jObj.getJSONObject("meta");
		
		if(!obj.isNull("code")){
			if(obj.getInt("code") == 200){	//If state is OK
				
				JSONArray jsonA = jObj.getJSONArray("data");
				
				for(int i = 0; i<jsonA.length(); i++){
					JSONObject o = jsonA.getJSONObject(i);
					InstagramPhotoEle photo = parsePhotoFromObj(o);
					photo.setUserId(userId);
					photoList.add(photo);
					
				}
			}
			
		}else{
			return null;
		}
		
		return photoList;
	}
	
	
	/**
	 * Given one photo JSONObject, extract InstagramPhotoEle
	 * 
	 * @param obj
	 * @return
	 */
	public InstagramPhotoEle parsePhotoFromObj(JSONObject obj){
		
		InstagramPhotoEle photo = new InstagramPhotoEle();
		
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
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date (timeS));
		photo.setCreated_time(time);
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
	public boolean writePhoto2DB(ArrayList<InstagramPhotoEle> photoList){
		String query = "insert ignore into " + instagramPhotoTable + " values("
				+ "?,?,?,?,?," + "?,?,?,?,?," +"?,?,?,?,?," + "?,?,?,?)";
		PreparedStatement pstmt = sql.createPreparedStatement(query);
		
		Iterator<InstagramPhotoEle> iter = photoList.iterator();
		
		try {
			
			while(iter.hasNext()){
				
				InstagramPhotoEle photo = iter.next();
				
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


}
