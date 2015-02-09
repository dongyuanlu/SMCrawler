package ldy.reddit;

/**
 * 
 * Reddit Video Element
 * 
 * @author ellen
 *
 */
public class RedditVideoE {
	
	private int rank = -1;	/*the rank position in returning list*/
	
	private String domain = "";	/*domain of video, e.g., youtube.com*/
	
	private String subreddit = "";	/*in which sub-reddit, e.g., video*/
	
	private String subreddit_id = "";
	
	private String url = ""; /*the original url of video*/
	
	private String id = "";	/*unique id in reddit*/
	
	private int score = -1;	/*score of video in reddit*/
	
	private int ups = -1; /*how many ups the video got*/
	
	private int reddit_num_comments = -1; /*how many comments the video got*/
	
	private String title = "";
	

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
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

	public int getReddit_num_comments() {
		return reddit_num_comments;
	}

	public void setReddit_num_comments(int reddit_num_comments) {
		this.reddit_num_comments = reddit_num_comments;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
	
}
