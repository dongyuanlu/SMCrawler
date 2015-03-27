package ldy.reddit;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import util.PageCrawler;
import util.SQLUtil;
import util.PageCrawler;

/**
 * 
 * Crawl Top N articles in front page
 * Write N articles into 3 tables:
 * 	1. list_reddit_..._... : record ranking list of article names
 *  2. reddit_article: record article attributes
 *  3. reddit_articlemedia: if article embedded media
 *  
 * ( list_reddit_..._... can be As Seed videos for analyzing YouTube comment)
 * 
 * @author ellen
 *
 */
public class CrawlRedditArticle { 
	
	private int pageTotal = 5;	/*when limit=100, the total number of pages to crawl*/
	private String apiRoot = RedditConfig.videoControversialAPIroot;	
	private ArrayList<RedditArticle> articleList = new ArrayList<RedditArticle>();
	
	private int rankIndex = 1;

	/**
	 * Constructors
	 * 
	 */
	public CrawlRedditArticle(){
		
	}
	public CrawlRedditArticle(String apiRoot){
		this.apiRoot = apiRoot;
	}
	public CrawlRedditArticle(int pageTotal, String apiRoot){
		this.pageTotal = pageTotal;
		this.apiRoot = apiRoot;
	}
	
	
	
	/**
	 * Commit
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		String apiRoot = RedditConfig.videoControversialAPIroot;
		CrawlRedditArticle crawler = new CrawlRedditArticle(apiRoot);
		crawler.crawlArticleList();	/*crawl video list, fill controVideoList*/
		
		crawler.writeArticleList2DB();	/*write controVideoList into database*/
	}
	
	
	
	/**
	 * Crawl all videos, fill controVideoList
	 * 
	 */
	public void crawlArticleList(){

		String afterIndex = "";
		
		//crawl video by pages
		for(int i = 0; i < pageTotal; i++){
			String api = apiRoot + afterIndex;
			String jsonPage = PageCrawler.readUrl(api);
			
			//verify api
			if(!jsonPage.contains("{")){
				articleList = null;
			}
			else if(jsonPage.equals("{\"error\": 404}")){
				articleList = null;
				System.err.println("error: 404");
			}
			
			afterIndex = parseVideoPage(jsonPage); //add all videos in this page into list
			
			System.out.println("crawl " + i + " page");
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
		JSONObject dataObject = pageObject.getJSONObject("data");
		String afterIndex = dataObject.getString("after");
		
		//parse videos in this page
		JSONArray children = dataObject.getJSONArray("children");
		int j = 0;
		while(!children.isNull(j)){
			
			RedditArticle article = new RedditArticle();
			article.setRank(this.rankIndex++);	//rank begins from 1
			JSONObject objArticle = children.getJSONObject(j);
			
			article.setKind(objArticle.getString("kind"));
			//get article attributes
			JSONObject objData = objArticle.getJSONObject("data");
			article.setName(objData.getString("name"));
			article.setAuthor(objData.getString("author"));
			article.setTitle(objData.getString("title"));
			article.setCreated(objData.getLong("created"));
			article.setCreated_utc(objData.getLong("created_utc"));
			article.setUrl(objData.getString("url"));
			article.setPermalink(objData.getString("permalink"));
			article.setDomain(objData.getString("domain"));
			article.setSubreddit(objData.getString("subreddit"));
			article.setSubreddit_id(objData.getString("subreddit_id"));
			article.setScore(objData.getInt("score"));
			article.setNum_comments(objData.getInt("num_comments"));
			article.setUps(objData.getInt("ups"));
			article.setId(objData.getString("id"));
			article.setSelftext(objData.getString("selftext"));
			
			//get media
			if(!objData.isNull("media")){
				JSONObject objMedia = objData.getJSONObject("media");
				JSONObject objOembed = objMedia.getJSONObject("oembed");
				//parse media_url
				String media_url = "";
				if(objOembed.isNull("url")){
					media_url = parseUrlFromHtml(objOembed.getString("html"));
				}else{
					media_url = objOembed.getString("url");
				}
				article.setMedia_url(media_url);
				
				RedditArticleMedia media = new RedditArticleMedia(article.getName(),media_url);
				if(!objOembed.isNull("title")){
					media.setTitle(objOembed.getString("title"));
				}				
				if(!objOembed.isNull("author_name")){
					media.setAuthor_name(objOembed.getString("author_name"));
				}				
				if(!objOembed.isNull("description")){
					media.setDescription(objOembed.getString("description"));
				}
				if(!objOembed.isNull("provider_name")){
					media.setProvider_name(objOembed.getString("provider_name"));
				}
				media.setType(objOembed.getString("type"));
				article.setMedia(media);
			}
						
			//add into list
			articleList.add(article);
			j++;
		}
		
		return afterIndex;
	}
	
	/**
	 * Write article list into 3 tables: 
	 * 	list_reddit; 
	 * 	reddit_article; 
	 * 	reddit_articlemedia
	 * 
	 */
	public void writeArticleList2DB(){
		SQLUtil sql = new SQLUtil("data/database.property");
		boolean hasMedia = false;
		String query1 = "INSERT IGNORE INTO list_reddit_contro_video VALUES("
				+ "?,?)";
		PreparedStatement pstmt1 = sql.createPreparedStatement(query1);
		
		String query2 = "INSERT IGNORE INTO reddit_article VALUES("
				+ "?,?,?,?,?," + "?,?,?,?,?," + "?,?,?,?,?," + "?,?,?)";
		PreparedStatement pstmt2 = sql.createPreparedStatement(query2);
		
		
		String query3 = "INSERT IGNORE INTO reddit_articlemedia VALUES("
				+ "?,?,?,?,?," + "?,?)";
		PreparedStatement pstmt3 = sql.createPreparedStatement(query3);

		
		Iterator<RedditArticle> iter = articleList.iterator();
		while(iter.hasNext()){
			RedditArticle article = iter.next();
			try {
				pstmt1.setInt(1, article.getRank());
				pstmt1.setString(2, article.getName());
				
				pstmt1.addBatch();
				
				
				pstmt2.setString(1, article.getId());
				pstmt2.setString(2, article.getName());
				pstmt2.setString(3, article.getDomain());
				pstmt2.setString(4, article.getAuthor());
				pstmt2.setString(5, article.getSubreddit());
				
				pstmt2.setString(6, article.getSubreddit_id());
				pstmt2.setString(7, article.getUrl());
				pstmt2.setString(8, article.getPermalink());
				pstmt2.setString(9, article.getTitle());
				pstmt2.setInt(10, article.getNum_comments());

				pstmt2.setInt(11, article.getUps());
				pstmt2.setInt(12, article.getScore());
				pstmt2.setString(13, article.getKind());
				pstmt2.setString(14, article.getSelftext());
				pstmt2.setLong(15, article.getCreated());
				
				pstmt2.setLong(16, article.getCreated_utc());
				pstmt2.setString(17, article.getMedia_url());
				if(article.getMedia() != null){
					pstmt2.setBoolean(18, true);						
				}else{
					pstmt2.setBoolean(18, false);						
				}				
				
				pstmt2.addBatch();
				
				if(article.getMedia() != null){
					pstmt3.setString(1, article.getName());
					pstmt3.setString(2, article.getMedia_url());
					pstmt3.setString(3, article.getMedia().getTitle());
					pstmt3.setString(4, article.getMedia().getDescription());
					pstmt3.setString(5, article.getMedia().getType());

					pstmt3.setString(6, article.getMedia().getAuthor_name());					
					pstmt3.setString(7, article.getMedia().getProvider_name());
					
					pstmt3.addBatch();
					hasMedia = true;
				}
				
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		try {
			pstmt1.executeBatch();
			pstmt1.close();
			
			pstmt2.executeBatch();
			pstmt2.close();
			
			if(hasMedia){
				pstmt3.executeBatch();
				pstmt3.close();
			}else{
				pstmt3.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * If url is not included in secure_media, we need to parse url from the html part
	 * @param html
	 * @return
	 */
	public String parseUrlFromHtml(String html){
		String url = "";
		
		String templet = "url=(http.+?)&amp";
		Pattern pName = Pattern.compile(templet);
		Matcher mName = pName.matcher(html);
		while(mName.find()){
			url = mName.group(1);
		}
		try {
			url = URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}
}
