package ldy.reddit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	private HashMap<String, RedditArticle> articleIndexList = new HashMap<String, RedditArticle>();
	
	

	public HashMap<String, RedditArticle> getArticleIndexList() {
		return articleIndexList;
	}


	/**
	 * Read article index list from list_reddit_... table into articleIndexList
	 * key: name
	 * value: article
	 * @param listTable
	 */
	public void readArticleIndexList(String listTable){
		SQLUtil sql = new SQLUtil("data/database.property");
		
		String q = "SELECT * FROM " + listTable + " WHERE 1";
		Statement st = sql.getStatement();
		try {
			ResultSet rs = st.executeQuery(q);
			
			while(rs.next()){
				int rank = rs.getInt("rank");
				String name = rs.getString("name");
				RedditArticle article = new RedditArticle();
				article.setRank(rank);
				article.setName(name);
				articleIndexList.put(name, article);
			}
			
			rs.close();
			st.close();
		} catch (SQLException e) {			
			e.printStackTrace();			
		}

	}
	
	/**
	 * Given articleIndexList
	 * read article value from reddit_article table
	 * Fulfill the value of articleIndexList
	 */
	public void readArticleList(){
		SQLUtil sql = new SQLUtil("data/database.property");

		Iterator<String> iter = articleIndexList.keySet().iterator();
		while(iter.hasNext()){
			String name = iter.next();
			RedditArticle article = articleIndexList.get(name);
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
					
					articleIndexList.put(name, article);
				}
				
				rs.close();
				st.close();
			} catch (SQLException e) {			
				e.printStackTrace();			
			}

		}
		
	}
	
	
	/**
	 * Given articleIndexList, read article media from reddit_articlemedia table
	 * Fulfill the media of article in articleIndexList
	 */
	public void addArticleMedia(){
		SQLUtil sql = new SQLUtil("data/database.property");

		Iterator<String> iter = articleIndexList.keySet().iterator();
		while(iter.hasNext()){
			String name = iter.next();
			RedditArticle article = articleIndexList.get(name);
			String q = "SELECT * FROM " + RedditConfig.redditArticleMediaTable 
					+ " WHERE name='" + name + "'";
			Statement st = sql.getStatement();
			try {
				ResultSet rs = st.executeQuery(q);
				
				while(rs.next()){
					RedditArticleMedia media = new RedditArticleMedia();
					media.setName(rs.getString("name"));
					media.setMedia_url(rs.getString("media_url"));
					media.setTitle(rs.getString("title"));
					media.setDescription(rs.getString("description"));
					media.setType(rs.getString("type"));
					media.setAuthor_name(rs.getString("author_name"));
					media.setProvider_name(rs.getString("provider_name"));
					article.setMedia(media);
					articleIndexList.put(name, article);
				}
				
				rs.close();
				st.close();
			} catch (SQLException e) {			
				e.printStackTrace();			
			}
		}

	}
}
