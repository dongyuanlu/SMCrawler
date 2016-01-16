package ldy.instagram;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

	private static String PHOTOTABLE;
	private static String BADUSERTABLE;
	private static String USERTABLE;
	private static String RELATIONTABLE;
		
	public ReadInstagram(){
		
		PHOTOTABLE = InstagramConfig.instagramPhotoTable;
		BADUSERTABLE = InstagramConfig.badUserTable;
		USERTABLE = InstagramConfig.instagramUserTable;
		RELATIONTABLE = InstagramConfig.instagramRelationTable;
	}
	
	public ReadInstagram(String photoTable, String badUserTable){
		this.PHOTOTABLE = photoTable;
		this.BADUSERTABLE = badUserTable;
	}

	
	/**
	 * 
	 * Read userlist from instagram_user table
	 * 
	 * @return
	 */
	public ArrayList<String> readUserIdFromUserTable(){
		ArrayList<String> userList = new ArrayList();
		String q = "SELECT id FROM " + USERTABLE + " WHERE 1";
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
			ResultSet rs = st.executeQuery("SELECT DISTINCT user1 from " + RELATIONTABLE);
			
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
	 * READ userid list from instagram_photo table
	 * 
	 * @return
	 */
	public ArrayList<String> readUserIdFromPhotoTable(){
		ArrayList<String> alreadyList = new ArrayList<String>();
		
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery("SELECT DISTINCT user_id from " + PHOTOTABLE);
			
			while(rs.next()){
				String userName = rs.getString("user_id");
				alreadyList.add(userName);
			}
			
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		return alreadyList;
	
	}
	
	
	/**
	 * Select a list of users from photoTable under condition
	 * 
	 * @param condition
	 * @return
	 */
	public ArrayList<String> readUserIdFromPhotoTable(String condition){
		ArrayList<String> alreadyList = new ArrayList<String>();
		
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery("SELECT DISTINCT user_id from " + PHOTOTABLE + condition);
			
			while(rs.next()){
				String userName = rs.getString("user_id");
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
			ResultSet rs = st.executeQuery("SELECT DISTINCT userid from " + BADUSERTABLE 
							+ " WHERE cause='following' OR cause='followedby'");
			
			
			while(rs.next()){
				String userName = rs.getString("userid");
				alreadyList.add(userName);
			}
			
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		return alreadyList;
	
	}
	
	public ArrayList<String> readUserIdFromBadUserTable(String cause){
		ArrayList<String> alreadyList = new ArrayList<String>();
		
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery("SELECT DISTINCT userid from " + BADUSERTABLE 
							+ " WHERE cause='" + cause + "'");
			
			
			while(rs.next()){
				String userName = rs.getString("userid");
				alreadyList.add(userName);
			}
			
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		return alreadyList;
	
	}
	
	
	public ArrayList<String> readUserIdFromBadUserTable(String cause1, String cause2){
		ArrayList<String> alreadyList = new ArrayList<String>();
		
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery("SELECT DISTINCT userid from " + BADUSERTABLE 
							+ " WHERE cause='" + cause1 + "' OR cause='" + cause2 + "'");
			
			
			while(rs.next()){
				String userName = rs.getString("userid");
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
				+ "id NOT IN (SELECT DISTINCT user1 FROM " + RELATIONTABLE + ")"
						+ " AND id NOT IN (SELECT userid FROM " + BADUSERTABLE + " WHERE cause='following' OR cause='followedby')";
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
		String q = "SELECT id FROM " + USERTABLE + " WHERE "
				+ "id NOT IN (SELECT DISTINCT user_id FROM " + PHOTOTABLE + ")";
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
	 * READ seed user's step neighbors, who need crawling RELATIONs
	 * 
	 * @param userId
	 * @param step
	 * @return
	 */
	public ArrayList<String> readUserNeighborsNotCrawlRelation(String userId, int step){
		ArrayList<String> neighborList = readUserNeighbors(userId, step);
		ArrayList<String> relationsList = readUserIdFromRelationTable();
		ArrayList<String> badUserList = readUserIdFromBadUserTable();
		
		
		ArrayList<String> list = new ArrayList<String>();
		
		Iterator<String> iter = neighborList.iterator();
		while(iter.hasNext()){
			String neighbor = iter.next();
			if(!relationsList.contains(neighbor)  && !badUserList.contains(neighbor)){//
				list.add(neighbor);
			}
		}
		
		return list;
		
	}

	/**
	 * 
	 * READ userId's step neighbors whose PHOTOs have not crawled
	 * 
	 * @param userId
	 * @param step
	 * @return
	 */
	public ArrayList<String> readUserNeighborsNotCrawlPhoto(String userId, int step){
		ArrayList<String> neighborList = readUserNeighbors(userId, step);
		ArrayList<String> photoUserList = readUserIdFromPhotoTable();
		ArrayList<String> badUserList = readUserIdFromBadUserTable("photostream","0photo");

		ArrayList<String> list = new ArrayList<String>();
		
		Iterator<String> iter = neighborList.iterator();
		while(iter.hasNext()){
			String neighbor = iter.next();
			if(!photoUserList.contains(neighbor)  && !badUserList.contains(neighbor)){//
				list.add(neighbor);
			}
		}
		
		return list;

	}
	
	
	
	
	
	
	/**
	 * 
	 * Read userId's neighbors within 'step' distance
	 * inlcuding followers and followings
	 * 
	 * @param userId: seed user id
	 * @param step: distance between returned neighbors with seed user
	 * @return
	 * 		a list of users contains: seedUser, seedUser's 'step' distance neighbors
	 * 
	 */
	public ArrayList<String> readUserNeighbors(String userId, int step){
		HashMap<String, Integer> neighborMap = new HashMap<String, Integer>();
		HashSet<String> set = new HashSet<>();
		
		neighborMap.put(userId, 0);
		
		//Loop for each step
		for(int i = 0; i < step; i++)
		{
			set.clear();
			set.addAll(neighborMap.keySet());
			Iterator<String> iter = set.iterator();
			while(iter.hasNext()){
				String user_id = iter.next();
				if(neighborMap.get(user_id)==i)
				{
					ArrayList<String> newNeighbors = readUserNeighbors(user_id);
					//add next level new neighbors into neighborMap
					for(int k = 0; k < newNeighbors.size(); k++)
					{
						String newNeighbor = newNeighbors.get(k);
						if(!neighborMap.containsKey(newNeighbor)){
							neighborMap.put(newNeighbor, i+1);
						}
					}
				}
			}

		}	
		
		ArrayList<String> userList = new ArrayList<String>(neighborMap.keySet());
		return userList;
	}
	
	/**
	 * 
	 * Read userId's first step neighbors in relationTable
	 * Including followers and followings
	 * 
	 * @param userId
	 * @return
	 */
	public ArrayList<String> readUserNeighbors(String userId){
		ArrayList<String> userList = new ArrayList<String>();
		
		String query = "SELECT DISTINCT user2 FROM " + RELATIONTABLE + " WHERE user1='" + userId + "'";
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
		String q = "SELECT count(id) FROM " + USERTABLE + " WHERE 1";
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
