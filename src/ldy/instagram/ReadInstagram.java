package ldy.instagram;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import util.SQLUtil;

/**
 * 
 * Read everything from Instagram database
 * 
 * @author Administrator
 *
 */
public class ReadInstagram {
	
	private static SQLUtil sql = new SQLUtil(InstagramConfig.database);
	
	public ReadInstagram(){
		
	}

	
	/**
	 * 
	 * Read userlist from instagram_user table
	 * 
	 * @return
	 */
	public ArrayList<String> readUserIdFromUserTable(){
		ArrayList<String> userList = new ArrayList();
		String q = "SELECT id FROM " + InstagramConfig.instagramUserTable + " WHERE 1";
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(q);
			
			while(rs.next()){
				String id = rs.getString("id");
				
				
				userList.add(id);
			}
			
			rs.close();
			st.close();
			return userList;
		} catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 
	 * Read source user id list from instagram_relation table
	 * 
	 * @return
	 */
	public ArrayList<String> readUserIdFromRelationTable(){
		ArrayList<String> alreadyList = new ArrayList<String>();
		
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery("SELECT DISTINCT user1 from " + InstagramConfig.instagramRelationTable);
			
			while(rs.next()){
				String userName = rs.getString("user1");
				alreadyList.add(userName);
			}
			
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		return alreadyList;
	
	}
	
	
	/**
	 * 
	 * GET user id list, whose relations not crawled
	 * 
	 * @return
	 */
	public ArrayList<String> readUserIdInUserTableNotRelationTable(){
		ArrayList<String> userList = new ArrayList();
		String q = "SELECT id FROM " + InstagramConfig.instagramUserTable + " WHERE "
				+ "id NOT IN (SELECT DISTINCT user1 FROM " + InstagramConfig.instagramRelationTable + ")";
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(q);
			
			while(rs.next()){
				String id = rs.getString("id");
				
				userList.add(id);
			}
			
			rs.close();
			st.close();
			return userList;
		} catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		}

	}
	
	
	/**
	 * 
	 * READ userid list which in user table not in relation table and baduser table
	 * 
	 * @return
	 */
	public ArrayList<String> readUserIdInUserTableNotRelationNotBaduserTable(){
		ArrayList<String> userList = new ArrayList();
		String q = "SELECT id FROM " + InstagramConfig.instagramUserTable + " WHERE "
				+ "id NOT IN (SELECT DISTINCT user1 FROM " + InstagramConfig.instagramRelationTable + ")"
						+ " AND id NOT IN (SELECT userid FROM " + InstagramConfig.badUserTable + " WHERE cause='following' OR cause='followedby')";
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(q);
			
			while(rs.next()){
				String id = rs.getString("id");
				
				userList.add(id);
			}
			
			rs.close();
			st.close();
			return userList;
		} catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		}

	}
	
	
	/**
	 * 
	 * GET user id list from instagram_user table, whose photo stream not crawled
	 * 
	 * @return
	 */
	public ArrayList<String> readUserIdInUserTableNotPhotoTable(){
		ArrayList<String> userList = new ArrayList();
		String q = "SELECT id FROM " + InstagramConfig.instagramUserTable + " WHERE "
				+ "id NOT IN (SELECT DISTINCT user_id FROM " + InstagramConfig.instagramPhotoTable + ")";
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(q);
			
			while(rs.next()){
				String id = rs.getString("id");
				
				userList.add(id);
			}
			
			rs.close();
			st.close();
			return userList;
		} catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		}

	} 
	
	
	/**
	 * 
	 * Count the number of users in instagram_user table
	 * 
	 * @return
	 */
	public int countUserInUserTable(){
		String q = "SELECT count(id) FROM " + InstagramConfig.instagramUserTable + " WHERE 1";
		Statement st = sql.getStatement();
		try {
			ResultSet rs = st.executeQuery(q);
			int count = 0;
			while(rs.next()){
				count = rs.getInt("count(id)");
			}
			
			rs.close();
			st.close();
			
			return count;
		} catch (SQLException e) {
			
			e.printStackTrace();
			return -1;
		}
	}
}
