package temp;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import util.SQLUtil;
import ldy.instagram.InstagramConfig;
import ldy.instagram.InstagramPhoto;
import ldy.instagram.ReadInstagram;
import ldy.instagram.ReadInstagramPhoto;


public class TransferDateBetweenTable {

	SQLUtil sql = new SQLUtil(InstagramConfig.database);
	ReadInstagramPhoto photoReader;
	ReadInstagram reader;
//	InstagramUserPhotoCrawler crawler;
	ArrayList<InstagramPhoto> photoList;
	String tableName;
	
	public TransferDateBetweenTable(){
		photoReader = new ReadInstagramPhoto();
		reader = new ReadInstagram();
//		crawler = new InstagramUserPhotoCrawler();
	}
	
	public static void main(String[] args){
		TransferDateBetweenTable transfer = new TransferDateBetweenTable();
		transfer.transferPhotoBetweeenTable();
	}
	
	
	/**
	 * Transfer photos from existing table to a new table
	 * 
	 */
	public void transferPhotoBetweeenTable(){
		tableName = "instagram_photo_usershasphy";
		
		ArrayList<String> userList = reader.readUserIdFromPhotoTable(
				" WHERE length(users_in_photo)>0");
		
		for(int i = 0; i < userList.size(); i++){
			String userId = userList.get(i);
			System.out.println(i);
			photoList = photoReader.readPhotoStreamOfUser(userId);
			writePhoto2DB();
			photoList.clear();
		}
		
		
	}
	
	/**
	 * Write photoList into database
	 * 
	 * @param photoList
	 * @return
	 */
	public boolean writePhoto2DB(){
		String query = "insert ignore into " + tableName + " values("
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

	
}
