package ldy.instagram;

import java.sql.SQLException;
import java.sql.Statement;

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

}
