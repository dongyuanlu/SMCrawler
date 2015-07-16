package ldy.reddit;

public class RedditConfig {
	
	public static String redditRoot = "http://www.reddit.com";
	
	public static String videoControversialAPIroot = "http://www.reddit.com/r/videos/controversial/.json?limit=100&t=year&after=";
	public static String videoTopAPIroot = "http://www.reddit.com/r/videos/top/.json?limit=100&t=year&after=";
	
	public static String videoTopAPIDayroot = "http://www.reddit.com/r/videos/top/.json?limit=100&t=day&after=";
	
	public static String videoTopAPIMonthroot = "http://www.reddit.com/r/videos/top/.json?sort=top&t=month&limit=100&after=";
	public static String videoTopAPIYearroot = "http://www.reddit.com/r/videos/top/.json?sort=top&t=year&limit=100&after=";
	
	public static String videoControArticleListTable = "list_reddit_contro_video";
	public static String videoTopArticleListTable = "list_reddit_top_video";
	public static String listRedditTable = "list_reddit";

	public static String redditArticleTable = "reddit_article";
	
	public static String redditArticleMediaTable = "reddit_articlemedia";
	
	public static String redditComment = "reddit_comment";
	
	public static String redditCommenterNetworkTable = "reddit_commenter_network";
	

}
