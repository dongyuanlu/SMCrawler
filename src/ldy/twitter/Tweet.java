package ldy.twitter;

public class Tweet {
	
	private String tweet_id = "";
	
	private String created_at = "";
	
	private long created_at_long = 0;
	
	private double longitude = 0.0;
	
	private double latitude = 0.0;
	
	private String hashtags = "";
	
	private String user_mentions = "";
	
	private String user_mention_id = "";
	
	private String expanded_url = "";
	
	private String text = "";
	
	private String in_reply_to_user_id = "";
	
	private String contributors = "";
	
	private long retweet_count = 0;
			
	private String in_reply_to_status_id = "";
	
	private boolean retweeted = false;
	
	private String place = "";
	
	private String screen_name = "";
	
	private String user_id = "";
	
	private String source = "";
	
	private String in_reply_to_user_screenname = "";

	public String getIn_reply_to_user_screenname() {
		return in_reply_to_user_screenname;
	}

	public void setIn_reply_to_user_screenname(String in_reply_to_user_screenname) {
		this.in_reply_to_user_screenname = in_reply_to_user_screenname;
	}

	public String getTweet_id() {
		return tweet_id;
	}

	public void setTweet_id(String tweet_id) {
		this.tweet_id = tweet_id;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public long getCreated_at_long() {
		return created_at_long;
	}

	public void setCreated_at_long(long created_at_long) {
		this.created_at_long = created_at_long;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}

	public String getUser_mentions() {
		return user_mentions;
	}

	public void setUser_mentions(String user_mentions) {
		this.user_mentions = user_mentions;
	}

	public String getUser_mention_id() {
		return user_mention_id;
	}

	public void setUser_mention_id(String user_mention_id) {
		this.user_mention_id = user_mention_id;
	}

	public String getExpanded_url() {
		return expanded_url;
	}

	public void setExpanded_url(String expanded_url) {
		this.expanded_url = expanded_url;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIn_reply_to_user_id() {
		return in_reply_to_user_id;
	}

	public void setIn_reply_to_user_id(String in_reply_to_user_id) {
		this.in_reply_to_user_id = in_reply_to_user_id;
	}

	public String getContributors() {
		return contributors;
	}

	public void setContributors(String contributors) {
		this.contributors = contributors;
	}

	public long getRetweet_count() {
		return retweet_count;
	}

	public void setRetweet_count(long retweet_count) {
		this.retweet_count = retweet_count;
	}

	public String getIn_reply_to_status_id() {
		return in_reply_to_status_id;
	}

	public void setIn_reply_to_status_id(String in_reply_to_status_id) {
		this.in_reply_to_status_id = in_reply_to_status_id;
	}

	public boolean isRetweeted() {
		return retweeted;
	}

	public void setRetweeted(boolean retweeted) {
		this.retweeted = retweeted;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	
}
