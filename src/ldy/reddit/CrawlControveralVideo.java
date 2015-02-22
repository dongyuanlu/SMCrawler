package ldy.reddit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import util.SQLUtil;
import util.WebPageCrawler;

/**
 * 
 * Crawl url of Top N videos in controversial 
 * As Seed videos for analyzing YouTube comment
 * 
 * @author ellen
 *
 */
public class CrawlControveralVideo { 
	
	private int pageTotal = 5;	/*when limit=100, the total number of pages to crawl*/
	
	private ArrayList<RedditVideoE> controVideoList = new ArrayList<RedditVideoE>();
	
	
	/**
	 * Commit
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		CrawlControveralVideo crawler = new CrawlControveralVideo();
		crawler.crawlVideoList();	/*crawl video list, fill controVideoList*/
		crawler.writeList2DB();	/*write controVideoList into database*/
	}
	
	
	
	/**
	 * Crawl all videos, fill controVideoList
	 * 
	 */
	public void crawlVideoList(){
		String apiRoot = RedditConfig.videoControversialAPIroot;
		String afterIndex = "";
		
		//crawl video by pages
		for(int i = 0; i < pageTotal; i++){
			String api = apiRoot + afterIndex;
			String jsonPage = WebPageCrawler.crawlbyUrl(api);
			
			//verify api
			if(!jsonPage.contains("{")){
				controVideoList = null;
			}
			else if(jsonPage.contains("error")){
				controVideoList = null;
				System.err.println("error: 404");
			}
			
			afterIndex = parseVideoPage(jsonPage); //add all videos in this page into list
		}
	}
	
	
	/**
	 * Given String jsonPage
	 * 
	 * add all videos in this page into controVideoList
	 * return afterIndex
	 * 
	 * @param jsonPage
	 * @return
	 */
	public String parseVideoPage(String jsonPage){
		//get next page index: after
		JSONObject pageObject = new JSONObject(jsonPage);
		String afterIndex = pageObject.getString("after");
		
		//parse videos in this page
		JSONArray children = pageObject.getJSONArray("children");
		int j = 0;
		while(children.isNull(j)){
			RedditVideoE videoE = new RedditVideoE();
			videoE.setRank(j+1);	//rank begins from 1
			JSONObject objVideo = children.getJSONObject(j);
			
			//get domain, subreddit, subreddit_id, score, num_comments,ups, id
			JSONObject objData = objVideo.getJSONObject("data");
			videoE.setDomain(objData.getString("domain"));
			videoE.setSubreddit(objData.getString("subreddit"));
			videoE.setSubreddit_id(objData.getString("subreddit_id"));
			videoE.setScore(objData.getInt("score"));
			videoE.setReddit_num_comments(objData.getInt("num_comments"));
			videoE.setUps(objData.getInt("ups"));
			videoE.setId(objData.getString("id"));
			
			//get video url, title
			JSONObject objMedia = objData.getJSONObject("secure_media");
			JSONObject objOembed = objData.getJSONObject("oembed");
			videoE.setUrl(objOembed.getString("url"));
			videoE.setTitle(objOembed.getString("title"));
			
			//add into list
			controVideoList.add(videoE);
		}
		
		return afterIndex;
	}
	
	
	public void writeList2DB(){
		SQLUtil sql = new SQLUtil("data/database.property");
		
		String query = "insert ignore into reddit_video values("
				+ "?,?,?,?,?," + "?,?,?,?,?)";
		PreparedStatement pstmt = sql.createPreparedStatement(query);
		
		Iterator<RedditVideoE> iter = controVideoList.iterator();
		while(iter.hasNext()){
			RedditVideoE video = iter.next();
			
			try {
				pstmt.setInt(1, video.getRank());
				pstmt.setString(2, video.getDomain());
				pstmt.setString(3, video.getSubreddit());
				pstmt.setString(4, video.getSubreddit_id());
				pstmt.setString(5, video.getId());
				
				pstmt.setString(6, video.getTitle());
				pstmt.setString(7, video.getUrl());
				pstmt.setInt(8, video.getUps());
				pstmt.setInt(9, video.getReddit_num_comments());
				pstmt.setInt(10, video.getScore());
				
				pstmt.addBatch();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		try {
			pstmt.executeBatch();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
