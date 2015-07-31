package ldy.twitter;

import java.util.ArrayList;

import twitter4j.Status;
import ldy.instagram.InstagramPhoto;
import ldy.instagram.ReadInstagram;
import ldy.instagram.ReadInstagramPhoto;

/**
 * 
 * Find twitter account given Instagram photo stream
 * 
 * @author ellen
 *
 */
public class AlignInstagramTwitter {

	ArrayList<InstagramPhoto> photoList;
	
	ReadInstagramPhoto insPhotoReader;
	TwitterSearch searcher;

	public AlignInstagramTwitter(){
		insPhotoReader = new ReadInstagramPhoto();
		searcher = new TwitterSearch();
		
	}
	
	
	public AlignUnit getAlignToTwitter(String instagramId){
		photoList = insPhotoReader.readPhotoStreamOfUser(instagramId);
		
		for(int i = 0; i < photoList.size(); i++)
		{
			InstagramPhoto photo = photoList.get(i);
			String linkId = photo.getLink().substring(photo.getLink().lastIndexOf("/")+1);
			Status tweet = searcher.searchInstagramId(linkId);
		}
	}
	
	
	class AlignUnit{
		
		private String instagramId = "";
		private String instagramScreenName = "";
		
		private String twitterId = "";
		private String twitterScreenName = "";
		
		private String envidence = "";
		
	}
}
