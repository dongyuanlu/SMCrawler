package ldy.reddit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import util.SQLUtil;

/**
 * Read reddit article list from 3 tables into articleList
 * 
 * @author ellen
 *
 */
public class ReadRedditArticle {

//	private ArrayList<RedditArticle> articleList = new ArrayList<RedditArticle>();
	private String listTableName = "";
	private HashMap<String, RedditArticle> articleIndexMap = new HashMap<String, RedditArticle>();
	
	private SQLUtil sql = new SQLUtil("data/database.property");
	
	public ReadRedditArticle(String listTableName){
		this.listTableName = listTableName;
	}
	
	
	public HashMap<String, RedditArticle> getArticleIndexList() {
		return articleIndexMap;
	}


	/****************aticleIndexMap*********************************/
	/**
	 * Read article index list from list_reddit_... table into articleIndexList
	 * key: name
	 * value: article
	 * @param listTable
	 */
	public void readArticleIndexMap(){
		
		String q = "SELECT * FROM " + listTableName + " WHERE 1";
		Statement st = sql.getStatement();
		try {
			ResultSet rs = st.executeQuery(q);
			
			while(rs.next()){
				int rank = rs.getInt("rank");
				String name = rs.getString("name");
				RedditArticle article = new RedditArticle();
				article.setRank(rank);
				article.setName(name);
				articleIndexMap.put(name, article);
			}
			
			rs.close();
			st.close();
		} catch (SQLException e) {			
			e.printStackTrace();			
		}

	}
	/*************************************************/
	
	/****************articleList*********************************/	
	/**
	 * Given articleIndexList
	 * read article value from reddit_article table
	 * Fulfill the value of articleIndexList
	 */
	public void readArticleList(){
		
		Iterator<String> iter = articleIndexMap.keySet().iterator();
		while(iter.hasNext()){
			String name = iter.next();
			RedditArticle article = articleIndexMap.get(name);
			String q = "SELECT * FROM " + RedditConfig.redditArticleTable 
					+ " WHERE name='" + name + "'";
			Statement st = sql.getStatement();
			try {
				ResultSet rs = st.executeQuery(q);
				
				while(rs.next()){
					article.setId(rs.getString("id"));
					article.setDomain(rs.getString("domain"));
					article.setAuthor(rs.getString("author"));
					article.setSubreddit(rs.getString("subreddit"));
					article.setSubreddit_id(rs.getString("subreddit_id"));
					article.setUrl(rs.getString("url"));
					article.setPermalink(rs.getString("permalink"));
					article.setTitle(rs.getString("title"));
					article.setNum_comments(rs.getInt("num_comments"));
					article.setUps(rs.getInt("ups"));
					article.setScore(rs.getInt("score"));
					article.setKind(rs.getString("kind"));
					article.setSelftext(rs.getString("selftext"));
					article.setCreated(rs.getLong("created"));
					article.setCreated_utc(rs.getLong("created_utc"));
					article.setMedia_url(rs.getString("media_url"));
					
					articleIndexMap.put(name, article);
				}
				
				rs.close();
				st.close();
			} catch (SQLException e) {			
				e.printStackTrace();			
			}

		}
		
	}
	/*************************************************/
	
	
	/*******************add Media******************************/	
	
	/**
	 * Given article name, return media if has
	 * 
	 * @param name
	 * @return
	 */
	public RedditArticleMedia readArticleMediaByArticleName(String name){
		String q = "SELECT * FROM " + RedditConfig.redditArticleMediaTable 
				+ " WHERE name='" + name + "'";
		Statement st = sql.getStatement();
		RedditArticleMedia media = null;
		try {
			ResultSet rs = st.executeQuery(q);
			
			while(rs.next()){
				media = new RedditArticleMedia();
				media.setName(rs.getString("name"));
				media.setMedia_url(rs.getString("media_url"));
				media.setTitle(rs.getString("title"));
				media.setDescription(rs.getString("description"));
				media.setType(rs.getString("type"));
				media.setAuthor_name(rs.getString("author_name"));
				media.setProvider_name(rs.getString("provider_name"));
				
			}
			
			rs.close();
			st.close();
			
		} catch (SQLException e) {			
			e.printStackTrace();			
		}
		return media;
	}
	
	/**
	 * Given article, add media into this article
	 * 
	 * @param article
	 */
	public RedditArticle addArticleMedia(RedditArticle article){
		RedditArticleMedia media = readArticleMediaByArticleName(article.getName());
		if(media != null){
			article.setMedia(media);
		}
		return article;
	}

	
	
	/**
	 * Given articleIndexList, read article media from reddit_articlemedia table
	 * Fulfill the media of all articles in articleIndexList
	 */
	public void addArticleListMedia(){

		Iterator<String> iter = articleIndexMap.keySet().iterator();
		while(iter.hasNext()){
			String name = iter.next();
			RedditArticle article = addArticleMedia(articleIndexMap.get(name));
			articleIndexMap.put(article.getName(), article);

		}
	}
	/*************************************************/	
	
	
	/******************add comment*******************************/	
	
	public ArrayList<RedditComment> readCommentsByArticleName(String articleName){
		String q = "SELECT * FROM " + RedditConfig.redditComment 
				+ " WHERE link_id='" + articleName + "'";
		Statement st = sql.getStatement();
		
		ArrayList<RedditComment> commentList = null;
		
		try {
			ResultSet rs = st.executeQuery(q);
			if(!rs.wasNull()){
				commentList = new ArrayList<RedditComment>();
				while(rs.next()){
					RedditComment comment = new RedditComment();
					
					comment.setAuthor(rs.getString("author"));
					comment.setBody(rs.getString("body"));
					comment.setCreated(rs.getLong("created"));
					comment.setCreated_utc(rs.getLong("created_utc"));
					comment.setDowns(rs.getInt("downs"));
					comment.setId(rs.getString("id"));
					comment.setLink_id(rs.getString("link_id"));
					comment.setName(rs.getString("name"));
					comment.setParent_id(rs.getString("parent_id"));
					comment.setScore(rs.getInt("score"));
					comment.setUps(rs.getInt("ups"));
				
					commentList.add(comment);
				}
			}
			
			
			rs.close();
			st.close();
			
		} catch (SQLException e) {			
			e.printStackTrace();			
		}
		return commentList;

	}
	
	
	/**
	 * Given article object, add comment list into this article
	 * 
	 * @param article
	 * @return
	 */
	public RedditArticle addArticleComment(RedditArticle article){
		ArrayList<RedditComment> commentList = readCommentsByArticleName(article.getName());
		article.setCommentList(commentList);
		return article;
	}
	
	
	/**
	 * Add comment list for each of article in the articleIndexMap
	 * 
	 */
	public void addArticleListComment(){
		Iterator<String> iter = articleIndexMap.keySet().iterator();
		while(iter.hasNext()){
			String name = iter.next();
			RedditArticle article = addArticleComment(articleIndexMap.get(name));
			articleIndexMap.put(article.getName(), article);

		}
	}
	
	/*******************************************************/
}
