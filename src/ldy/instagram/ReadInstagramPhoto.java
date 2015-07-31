package ldy.instagram;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import util.SQLUtil;

public class ReadInstagramPhoto {

	SQLUtil sql;
	
	public ReadInstagramPhoto(){
		this.sql = new SQLUtil(InstagramConfig.database);
	}
	
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public ArrayList<InstagramPhoto> readPhotoStreamOfUser(String userId){
		ArrayList<InstagramPhoto> photoList = new ArrayList<>();
		
		String query = "SELECT * FROM  " + InstagramConfig.instagramPhotoTable + " WHERE user_id='" + userId + "' ORDER BY created_time_long DESC";
		
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next()){
				InstagramPhoto photo = new InstagramPhoto();
				
				photo.setPhotoId(rs.getString("photoId"));
				photo.setUserId(rs.getString("user_id"));
				photo.setCreated_time_long(rs.getLong("created_time_long"));
				photo.setText(rs.getString("text"));
				photo.setTags(rs.getString("tags"));
				photo.setLink(rs.getString("link"));
				
				photoList.add(photo);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return photoList;
	}
}
