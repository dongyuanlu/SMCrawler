package ldy.instagram;

public class InstagramConfig {
	
	public static String[] accessTokens = {"422455902.56b262f.617ca892fc4c448f9a8d4c3426e41449",
		"145967399.7760ab2.25fed0b13a554d7eab885aa207894393",
		"422455902.2fb31fe.e0ba0ee797224bc7b1fd1193aa93d72a",
		"145967399.1677ed0.9b3bba45986f472192ef5f1066a64a62",
		"53526039.1677ed0.c210f4dbf9a44707ae558b92ad0f2c9e",
		"1939911201.1677ed0.21c1b9e305ef4adca00e91bd3db852a3",
		"2009911205.1677ed0.0765bb09cf4e4c3c8d31783be0514d93"};
	
	public static String seedUserId = "1072213";

	//**********************************************************
	//DATABASE
	
	public static String database = "data/instagramDatabase.property";
	
	public static String instagramUserTable = "instagram_user";
	public static String instagramRelationTable = "instagram_relation";
	public static String badUserTable = "instagram_badUser";
	
	public static String instagramPhotoTable = "instagram_photo";
	
	
	//***********************************************************
	//URL
	
	public static String userRecentBaseUrl = "https://api.instagram.com/v1/users/";
	
	
}
