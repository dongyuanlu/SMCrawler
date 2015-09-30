package ldy.youtube;

public class YoutubeConfig {
	
	public static String[] accessKeys = {"AIzaSyDx-ivZrMQOKGUiewsvS4jou8AfZ9m0kMQ"};
	
	//**********************************************************
		//DATABASE
	public static String youtubeCommentTable = "youtube_comment";
	public static String database = "data/database.property";
	
	//***********************************************************
		//URLString
	public static String commentThreadBaseURL = "https://www.googleapis.com/youtube/v3/commentThreads?"
			+ "part=snippet,replies&maxResults=100";
	
	public static String commentsBaseURL = "https://www.googleapis.com/youtube/v3/comments?&part=snippet&maxResults=100";
}
