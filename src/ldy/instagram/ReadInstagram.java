package ldy.instagram;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

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
	 * Read userid list from badUserTable, where cause is following or followed
	 * 
	 * @return
	 */
	public ArrayList<String> readUserIdFromBadUserTable(){
		ArrayList<String> alreadyList = new ArrayList<String>();
		
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery("SELECT DISTINCT userid from " + InstagramConfig.badUserTable 
							+ "WHERE cause='following' OR cause='followedby'");
			
			
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
	 * @return userId list from intagram_user table, whose relations not crawled
	 * 
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
	 * @return user id list from instagram_user table, whose photo stream not crawled
	 * 
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
	 * READ seed user's step neighbors, who need crawling relations
	 * 
	 * @param userId
	 * @param step
	 * @return
	 */
	public ArrayList<String> readUserNeighborsNotCrawlRelation(String userId, int step){
		ArrayList<String> neighborList = readUserNeighbors(userId, step);
		ArrayList<String> relationsList = readUserIdFromRelationTable();
		ArrayList<String> badUserList = readUserIdFromBadUserTable();
		
		neighborList.removeAll(relationsList);
		neighborList.removeAll(badUserList);
		
		return neighborList;
		
	}

	
	
	
	
	
	/**
	 * 
	 * Read userId's neighbors within 'step' distance
	 * 
	 * @param userId: seed user id
	 * @param step: distance between returned neighbors with seed user
	 * @return
	 * 		a list of users contains: userId, userId's 'step' distance neighbors
	 * 
	 */
	public ArrayList<String> readUserNeighbors(String userId, int step){
		ArrayList<String> userList = new ArrayList();

		userList.add(userId);
		int start = 0;
		
		//Loop for each step
		for(int i = 0; i < step; i++)
		{
			int size = userList.size();
			for(int k = start; k < size; k++){				
				String user_id = userList.get(k);
				userList.addAll(readUserNeighbors(user_id)); //add user_id's direct neighbors into userList
			}
			start = size;

		}
		s
		return userList;
	}
	
	/**
	 * 
	 * Read userId's first step neighbors in relationTable
	 * 
	 * @param userId
	 * @return
	 */
	public ArrayList<String> readUserNeighbors(String userId){
		ArrayList<String> userList = new ArrayList();
		
		String query = "SELECT DISTINCT user2 FROM " + InstagramConfig.instagramRelationTable + " WHERE user1='" + userId + "'";
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next()){
				String id = rs.getString("user2");				
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
