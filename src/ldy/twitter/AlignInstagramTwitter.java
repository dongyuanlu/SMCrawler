package ldy.twitter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import twitter4j.Status;
import util.SQLUtil;
import ldy.instagram.InstagramConfig;
import ldy.instagram.InstagramPhoto;
import ldy.instagram.InstagramUser;
import ldy.instagram.ReadInstagram;
import ldy.instagram.ReadInstagramPhoto;

/**
 * 
 * Find corresponding twitter account given Instagram user id and his photo stream
 * 
 * @author ellen
 *
 */
public class AlignInstagramTwitter {
	
	private ArrayList<String> instagramUserIdList;
	private ArrayList<InstagramPhoto> photoList;
	
	private ReadInstagram insReader;
	private ReadInstagramPhoto insPhotoReader;
	private TwitterSearch searcher;
	
	private ArrayList<AlignUnit> alignList;

	public AlignInstagramTwitter(){
		insReader = new ReadInstagram();
		insPhotoReader = new ReadInstagramPhoto();
		searcher = new TwitterSearch();
		
		alignList = new ArrayList<>();
//		instagramUserIdList = insReader.readUserNeighbors(InstagramConfig.seedUserId, 1);
		instagramUserIdList = new ArrayList();
		instagramUserIdList.add("1446381698");
		
	}
	
	
	
	public static void main(String[] args){
		AlignInstagramTwitter aligner = new AlignInstagramTwitter();
		aligner.alignAllInstagramToTwitter();
	}
	
	
	/**
	 * 
	 * Align all Instagram userid in list
	 * Write align result into database
	 * 
	 */
	public void alignAllInstagramToTwitter(){
		 		
		for(int i = 0; i < instagramUserIdList.size(); i++){
			String insUserId = instagramUserIdList.get(i);
			AlignUnit align = getAlignToTwitter(insUserId);
			alignList.add(align);
		}
		
		writeAlignList();
	}
	
	
	
	
	/**
	 * 
	 * Given Instagram user Id, get corresponding twitter user id
	 * 
	 * @param instagramId
	 * @return
	 * 		AlignUnit
	 * 		if no corresponding twitter user, return align.envidence="nocorrespondingtwitter"
	 */
	public AlignUnit getAlignToTwitter(String instagramId){
		
		photoList = insPhotoReader.readPhotoStreamOfUser(instagramId);
		AlignUnit align = null;

		for(int i = 0; i < photoList.size(); i++)
		{
			InstagramPhoto photo = photoList.get(i);
			
			String link = photo.getLink();
			String linkId = link.substring(link.indexOf("/p/")+3,link.length()-1);
			if(!linkId.equals("0GU-VYGoyT")){
				continue;
			}
			Status tweet = searcher.searchInstagramId(linkId);
			if(tweet != null){
				align = new AlignUnit();
				align.instagramId = instagramId;
				align.twitterId = tweet.getUser().getId() + "";
				align.twitterScreenName = tweet.getUser().getScreenName();
				align.envidence = tweet.toString();
				return align;
			}
			
		}
		
		if(align == null){
			align = new AlignUnit();
			align.instagramId = instagramId;
			if(photoList == null || photoList.size() == 0){
				align.envidence = "instagram no photo";
			}
			else{
				align.envidence = "nocorrespondingtwitter";
			}
			
		}
		return align;
	}
	
	
	
	
	/**
	 * Write alignlist into database
	 * 
	 */
	public void writeAlignList(){
		SQLUtil sql = new SQLUtil(InstagramConfig.database);
		
		String query = "INSERT ignore INTO " + TwitterConfig.instagramTwitterAlignTable + " values("
				+ "?,?,?,?,?)";
		
		PreparedStatement pstmt = sql.createPreparedStatement(query);
		try {
			for(int i = 0; i < alignList.size(); i++){
				AlignUnit align = alignList.get(i);
				
				pstmt.setString(1, align.instagramId);
				pstmt.setString(2, align.instagramScreenName);
				pstmt.setString(3, align.twitterId);
				pstmt.setString(4, align.twitterScreenName);
				pstmt.setString(5, align.envidence);
				
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			pstmt.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	
	class AlignUnit{
		
		private String instagramId = "";
		private String instagramScreenName = "";
		
		private String twitterId = "";
		private String twitterScreenName = "";
		
		private String envidence = ""; //the cross-shared tweet
		
	}
}
