package ldy.youtube;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ldy.instagram.CheckJSONPage;
import util.PageCrawler;
import util.SQLUtil;

public class CrawlYoutubeComment {

	private static YoutubeKey accessKey;
	
	private static SQLUtil sql = new SQLUtil(YoutubeConfig.database);
	private CheckJSONPage checker;
	private ArrayList<YoutubeComment> commentList;
	
	public static void main(String[] args){
		CrawlYoutubeComment crawler = new CrawlYoutubeComment();
		ArrayList<String> videoIdList = new ArrayList<String>();
		
	}
	
	public CrawlYoutubeComment(){
		
		this.accessKey = new YoutubeKey();
		this.checker = new CheckJSONPage();
		commentList = new ArrayList<YoutubeComment>();
	}
	
	
	public boolean getVideoComment(String videoId){
		commentList.clear();
		
		String apiUrl = generateCommentThreadURL(videoId);
		
		//LOOP for pages
		while(apiUrl.length() > 0){
			String jsonPage = PageCrawler.readUrl(apiUrl);
			
			//Check crawled JSON page
			if(!checker.checkJsonPage(jsonPage, apiUrl)){	//check jsonPage, if false
				return false;
			}		
			
			JSONObject jObj = new JSONObject(jsonPage);
			
			JSONArray jsonArray = jObj.getJSONArray("items");
			
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject obj = jsonArray.getJSONObject(i);
				
			}
		}
		
		writeCommentList2Database();
		return true;
	}
	
	
	/**
	 * 
	 * Given videoId, generate comment thread url
	 * 
	 * @param videoId
	 * @return
	 */
	public String generateCommentThreadURL(String videoId){
		String url = YoutubeConfig.commentThreadBaseURL + "&key=" + accessKey.pickKey()
				+ "&videoId=" + videoId + "&pageToken=";
		
		return url;
	}
	
	public boolean writeCommentList2Database(){
		return true;
	}
}
