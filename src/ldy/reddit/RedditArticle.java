package ldy.reddit;

import java.util.ArrayList;

/**
 * 
 * Reddit Video Element
 * 
 * @author ellen
 *
 */
public class RedditArticle {
	
	private int rank = -1;	/*the rank position in returning list*/
	
	private String id = "";	/*unique id in reddit*/
	
	private String name = "";
	
	private String domain = "";	/*domain of video, e.g., youtube.com*/
	
	private String author = "";	/*the author who submits the article to reddit*/
	
	private String subreddit = "";	/*in which sub-reddit, e.g., video*/
	
	private String subreddit_id = "";
	
	private String url = ""; /**/
	
	private String permalink = "";	/*the permalink of comments*/

	private String title = "";	/*the title on reddit*/
	
	private int num_comments = -1; /*how many comments the video got*/

	private int ups = 0; /*how many ups the video got*/

	private int score = 0;	/*score of video in reddit*/
	
	private String kind = "";
	
	private String selftext = "";
	
	private Long created;
	
	private Long created_utc;
	
	private String media_url = null;	/*the embedded media url on original website*/
	
	private RedditArticleMedia media = null;
	
	private ArrayList<RedditComment> commentList = null;
	

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public int getNum_comments() {
		return num_comments;
	}

	public void setNum_comments(int num_comments) {
		this.num_comments = num_comments;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getSelftext() {
		return selftext;
	}

	public void setSelftext(String selftext) {
		this.selftext = selftext;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getCreated_utc() {
		return created_utc;
	}

	public void setCreated_utc(Long created_utc) {
		this.created_utc = created_utc;
	}

	public String getMedia_url() {
		return media_url;
	}

	public void setMedia_url(String media_url) {
		this.media_url = media_url;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSubreddit() {
		return subreddit;
	}

	public void setSubreddit(String subreddit) {
		this.subreddit = subreddit;
	}

	public String getSubreddit_id() {
		return subreddit_id;
	}

	public void setSubreddit_id(String subreddit_id) {
		this.subreddit_id = subreddit_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getUps() {
		return ups;
	}

	public void setUps(int ups) {
		this.ups = ups;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public RedditArticleMedia getMedia() {
		return media;
	}

	public void setMedia(RedditArticleMedia media) {
		this.media = media;
	}

	public ArrayList<RedditComment> getCommentList() {
		return commentList;
	}

	public void setCommentList(ArrayList<RedditComment> commentList) {
		this.commentList = commentList;
	}
	
	
	
	
}
