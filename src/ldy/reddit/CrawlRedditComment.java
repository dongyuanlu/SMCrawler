package ldy.reddit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import util.MyLog;
import util.PageCrawler;
import util.SQLUtil;

public class CrawlRedditComment {
	private MyLog log;
	
	private ArrayList<RedditComment> commentList = new ArrayList<RedditComment>();
	private ArrayList<String> moreIdList = new ArrayList<String>(); /*more id list, to be crawled*/
	
//	private String articleName = "";
	private String permalink = "";
	private String commentApi = "";
	
	public CrawlRedditComment(String articleName, String permalink){
//		this.articleName = articleName;
		this.permalink = permalink;
		this.commentApi = RedditConfig.redditRoot + permalink +".json?limit=500";
		this.log = new MyLog(this.getClass().getSimpleName(), articleName);	
	}
	
	public CrawlRedditComment(String permalink, MyLog log){
		this.log = log;	//each article has one log file
		this.permalink = permalink;
		this.commentApi = RedditConfig.redditRoot + permalink +".json?limit=500";

	}
	
	/**
	 * Execute
	 * @param args
	 */
	
	public static void main(String[] args){
		MyLog log = new MyLog(CrawlRedditComment.class.getName(), "topVideoListApril21");
		
		ReadRedditArticle articleReader = new ReadRedditArticle(RedditConfig.videoTopArticleListTable);
		articleReader.readArticleIndexMap();
		articleReader.readArticleList();
		HashMap<String, RedditArticle> articleIndexMap = articleReader.getArticleIndexList();
		
		Iterator<String> iter = articleIndexMap.keySet().iterator();
		while(iter.hasNext()){
			String name = iter.next();
			RedditArticle article = articleIndexMap.get(name);
			String permalink = article.getPermalink();
			CrawlRedditComment crawlCmmer = new CrawlRedditComment(permalink, log);
			crawlCmmer.crawlComment();
			crawlCmmer.writeComment2DB();
			System.out.println(name);
		}
	}
	
	
	/**
	 * test
	 */
/*
	public static void main(String[] args){
		MyLog log = new MyLog(CrawlRedditComment.class.getName(), "1zgwte");
		CrawlRedditComment crawlCmmer = new CrawlRedditComment(
				"/r/videos/comments/2watgh/rhababerbarbarabarbabarenb?rtbarbierbierbarb?rbel/", log);
		crawlCmmer.crawlComment();
		crawlCmmer.writeComment2DB();
	}
	*/
	/**
	 * Crawl Comments of current article
	 * Add comments into commentList
	 * 
	 */
	public void crawlComment(){
		/*crawl comments on the page*/
		System.out.println("Crawl comments: " + permalink);
		String pageContent = PageCrawler.readUrl(commentApi);
		if(pageContent == null){
			log.addLogList("null: " + commentApi);
		}
		else if(pageContent.equals("{\"error\": 404}")){
			log.addLogList("error 404: " + commentApi);
		}
		else{
			JSONArray commentArray = new JSONArray(pageContent);
			int j = 0;
			while(!commentArray.isNull(j)){
				JSONObject kindData = commentArray.getJSONObject(j);
				parseKindData(kindData);
				j++;
			}
		}
		try {	//Sleep 2s, guarantee security
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*crawl comments hidden in the 'more' */
		System.out.println("Crawl 'more' comments: " + moreIdList.size() + "  " + permalink);
		for(int in = 0; in < moreIdList.size(); in++){ //NOTE: moreIdList may add new moreIds during this loop
			String id = moreIdList.get(in);
			String moreCmmApi = RedditConfig.redditRoot + permalink + id + "/.json";
			String pageMoreContent = PageCrawler.readUrl(moreCmmApi);
			if(pageMoreContent==null){
				log.addLogList("null: " + moreCmmApi);
			}
			else if(pageMoreContent.equals("{\"error\": 404}")){
				log.addLogList("error 404: " + moreCmmApi);
			}
			else{
				JSONArray commentMoreArray = new JSONArray(pageMoreContent);
				int i = 0;
				while(!commentMoreArray.isNull(i)){
					JSONObject kindData = commentMoreArray.getJSONObject(i);
					parseKindData(kindData);
					i++;
				}
			}
			try {	//Sleep 2s, guarantee security
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		log.writeLogList();
	}
	
	/**
	 * Parse JSONobject like:
	 * 
	 * kind:"..."
	 * data:"{...}"
	 * 
	 * @param kindData
	 */
	public void parseKindData(JSONObject kindData){
		String kind = kindData.getString("kind");
		JSONObject data = kindData.getJSONObject("data");
		if(kind.equals("t1")){
			parseDataIfKindt1(data);
		}
		else if(kind.equals("more")){
			parseDataIfKindmore(data);
		}
		else if(kind.equals("Listing")){
			JSONArray children = data.getJSONArray("children");
			int k = 0;
			while(!children.isNull(k)){
				JSONObject child = children.getJSONObject(k);
				parseKindData(child);
				k++;
			}
		}
		else{
			return;
		}
	}
	
	
	/**
	 * Parse data if kind="t1" (means comment)
	 * 
	 * @param data
	 */
	public void parseDataIfKindt1(JSONObject data){
		
		RedditComment comment = new RedditComment();
		comment.setLink_id(data.getString("link_id"));
		comment.setId(data.getString("id"));
		comment.setParent_id(data.getString("parent_id"));
		comment.setName(data.getString("name"));
		comment.setBody(data.getString("body"));
		comment.setAuthor(data.getString("author"));
		comment.setUps(data.getInt("ups"));
		comment.setDowns(data.getInt("downs"));
		comment.setScore(data.getInt("score"));
		comment.setCreated(data.getLong("created"));
		comment.setCreated_utc(data.getLong("created_utc"));
		
		commentList.add(comment);
		
		if(!data.isNull("replies")){
			if(!data.get("replies").equals("")){
				JSONObject replies = data.getJSONObject("replies");
				parseKindData(replies);	
			}		
		}
	}
	
	
	/**
	 * 
	 * Parse data if kind="more"
	 * 
	 * 
	 * @param data
	 */
	public void parseDataIfKindmore(JSONObject data){
		JSONArray children = data.getJSONArray("children");
		int j = 0;
		while(!children.isNull(j)){
			String id = children.getString(j);
			moreIdList.add(id);
			j++;
		}
	}
	
	
	
	/**
	 * Write commentList into database
	 * 
	 */
	public void writeComment2DB(){
		SQLUtil sql = new SQLUtil("data/database.property");
		String query = "INSERT IGNORE INTO " + RedditConfig.redditComment + "  VALUES("
				+ "?,?,?,?,?," + "?,?,?,?,?," + "?)";
		PreparedStatement ps = sql.createPreparedStatement(query);
		
		Iterator<RedditComment> iter = this.commentList.iterator();
		try {
			while(iter.hasNext()){
				RedditComment cmm = iter.next();
					ps.setString(1, cmm.getName());
					ps.setString(2, cmm.getLink_id());
					ps.setString(3, cmm.getId());
					ps.setString(4, cmm.getParent_id());
					ps.setString(5, cmm.getBody());
					
					ps.setString(6, cmm.getAuthor());
					ps.setInt(7, cmm.getUps());
					ps.setInt(8, cmm.getDowns());
					ps.setInt(9, cmm.getScore());
					ps.setLong(10, cmm.getCreated());
					ps.setLong(11, cmm.getCreated_utc());
					
					ps.addBatch();
			}
			
			ps.executeBatch();
			ps.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
