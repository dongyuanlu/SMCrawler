package ldy.instagram;

import org.json.JSONArray;
import org.json.JSONObject;



/**
 * 
 * This class parse InstagramPhoto from JSONObject
 * 
 * 
 * 
 * @author Administrator
 *
 */
public class InstagramPhotoObjectParser {
	
	/**
	 * 
	 * Parse InstagramPhoto from JSONObject
	 * 
	 * @param obj
	 * @return
	 */
	public static InstagramPhoto parser(JSONObject obj){
		
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

}
