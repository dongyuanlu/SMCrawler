package ldy.instagram;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import util.PageCrawler;
import util.SQLUtil;


/**
 * 
 * Crawl a sample small dataset for real_friend analysis for TMM
 * 
 * The followers and followings of the seed user construct the first level friends.
 * This class collect the photo streams of the first level friends;
 * 
 * Write the photo streams into database table instagram_photo_realfriend
 * 
 * 
 * @author Administrator
 *
 */
public class Instagram_UserPhotoCrawler_RealFriend {
	
	private int nSTEP = 1;	//the distance of neighbors from seed user

	private static InstagramToken_5 accessToken;	//select the second accessToken

	private ArrayList<InstagramPhoto> photoList;
	
//	private static SQLUtil sql = new SQLUtil(InstagramConfig.database);
	private static ReadInstagram reader;
	private static WriteInstagram writer;
	private CheckJSONPage checker;
	
	private String PHOTOTABLE;
	private String BADUSERTABLE;
	private String RELATIONTABLE;
	
	//Constructor
	public Instagram_UserPhotoCrawler_RealFriend(){
		this.photoList = new ArrayList<>();
		
		PHOTOTABLE = "instagram_photo_realfriend";
		BADUSERTABLE = "instagram_realfriend_baduser";
		RELATIONTABLE = InstagramConfig.instagramRelationTable;
		
		reader = new ReadInstagram(PHOTOTABLE, BADUSERTABLE,"",RELATIONTABLE);
		writer = new WriteInstagram();
		checker = new CheckJSONPage();
		accessToken = new InstagramToken_5();	//select the second accessToken

			}
	
	public static void main(String[] args) {
		Instagram_UserPhotoCrawler_RealFriend photoOfRealCrawler = new Instagram_UserPhotoCrawler_RealFriend();
		photoOfRealCrawler.crawlUsersPhotoStream();

	}

	
	/**
	 * 
	 * Crawl the photo stream of seedUser and his nStep neighbors
	 * 
	 * AND Write photos into PHOTOTABLE table
	 * If failed, write userId into BADUSERTABLE with flag 'photostream'
	 * 
	 */
	public void crawlUsersPhotoStream(){

	//	ArrayList<String> userIdsToCrawl = reader.readUserNeighborsNotCrawlPhoto(InstagramConfig.seedUserId, nSTEP);
		ArrayList<String> userIdsToCrawl = reader.readUserIdFromBadUserTable("photostream");
		System.out.println("Total Users: " + userIdsToCrawl.size());
		
		for(int i = 0; i<userIdsToCrawl.size(); i++){

			String userId = userIdsToCrawl.get(i);
			
			System.out.println(i + ": " + userId);

			//****Crawl photo stream and write into table****///////
			boolean flag = getUserRecentStream(userId);

			if(!flag){ //if failed
				writer.writeBadUser2DB(userId, BADUSERTABLE, "photostream");
				
			}
			else
			{
				writer.deleteBadUser(userId, BADUSERTABLE);
			}
		}
	}
	
	/**
	 * 
	 * Crawl user's recent public photo stream
	 * Write the photos into instagram_photo table
	 * 
	 * @param userId: the id of user
	 * @return 
	 * 		true: successfully crawled user's photos
	 * 		false: failed to get user's photos; 
	 * 	  
	 */
	public boolean getUserRecentStream(String userId){
		photoList.clear();

		String api = InstagramConfig.userRecentBaseUrl + userId + "/media/recent/?access_token=" + accessToken.pickToken()
				+ "&count=40";
				
		//LOOP for pages
		while(api.length() > 0){
			String jsonPage = PageCrawler.readUrl(api);
			
			//Check crawled JSON page
			if(!checker.checkJsonPage(jsonPage, api)){	//check jsonPage, if false
				return false;
			}		
			
			JSONObject jObj = new JSONObject(jsonPage);
			
			//Check status code
			if(!checker.checkMetaCode(jObj)){	//check status code, if not 200, return false
				return false;
			}

			//*******Parse photo stream*********////
			JSONArray jsonA = jObj.getJSONArray("data");
			
			for(int i = 0; i<jsonA.length(); i++){
				JSONObject o = jsonA.getJSONObject(i);
				InstagramPhoto photo = InstagramPhotoObjectParser.parser(o);	////Parse photo object
				photo.setUserId(userId);
				photoList.add(photo);
				
			}		
			
			//Get next page api
			if(jObj.isNull("pagination")){
				api = "";
			}
			else
			{
				JSONObject nextObject = jObj.getJSONObject("pagination");
				if(!nextObject.isNull("next_url"))
				{ 
					api = nextObject.getString("next_url");	
					api = api.replaceAll("access_token=.*?&", "access_token="+accessToken.pickToken()+"&");
				}else
				{
					api = "";
				}
			}
			
		}
		
		////*****WRITE Instagram photo list into table****///////////
		
		System.out.println("Start write " + photoList.size() + " " + System.currentTimeMillis());
		if(photoList.size() == 0){
			writer.writeBadUser2DB(userId, BADUSERTABLE, "0photo");
		}else{
			writer.writePhotoStream2Database(PHOTOTABLE, photoList);;
		}
		
		/////////////////////
		
		System.out.println("End: " + System.currentTimeMillis());
		return true;
	}


}
