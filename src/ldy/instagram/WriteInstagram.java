package ldy.instagram;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import util.SQLUtil;

/**
 * Write everything of instagram into database, file, etc.
 * @author ellen
 *
 */
public class WriteInstagram {
	
	private static SQLUtil sql = new SQLUtil(InstagramConfig.database);
	
	/**
	 * 
	 * Write photo stream into photoTable
	 * 
	 * @param photoTable
	 * @param photoList
	 */
	public boolean writePhotoStream2Database(String photoTable, ArrayList<InstagramPhoto> photoList){
		
		String query = "insert ignore into " + photoTable + " values("
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
	 * IF failed to get user's relation, write into badusertable
	 * 
	 * @param userId
	 * @param tableName
	 * @param cause
	 */
	public void writeBadUser2DB(String userId, String tableName, String cause){
		String query = "INSERT IGNORE INTO " + tableName + " values('" + userId + "', '" + cause + "')";
		Statement st = sql.getStatement();
		
		try {
			st.execute(query);
			
			st.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
			
	}

	
	/**
	 * 
	 * Delete userId from baduser table
	 * 
	 * @param userId
	 * @param tableName
	 */
	public void deleteBadUser(String userId, String tableName){
		String query = "DELETE FROM " + tableName + " WHERE userId='" + userId + "'";
		Statement st = sql.getStatement();
		
		try {
			st.execute(query);
			
			st.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
}
