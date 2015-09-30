package ldy.youtube;

public class YoutubeComment {

	private String id = "";
	
	private String videoId = "";
	
	private String parentId = "";
	
	private String authorDisplayName = "";
	
	private String authorChannelId = "";
	
	private int likeCount = 0;
	
	private String publishedAt = "";
	
	private String updatedAt = "";
	
	private int totalReplyCount = 0;
	
	private String myParentAuthorDisplayName;
	
	
	public YoutubeComment(){
		
	}
	
	public YoutubeComment(String id){
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getAuthorDisplayName() {
		return authorDisplayName;
	}

	public void setAuthorDisplayName(String authorDisplayName) {
		this.authorDisplayName = authorDisplayName;
	}

	public String getAuthorChannelId() {
		return authorChannelId;
	}

	public void setAuthorChannelId(String authorChannelId) {
		this.authorChannelId = authorChannelId;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public String getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(String publishedAt) {
		this.publishedAt = publishedAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getTotalReplyCount() {
		return totalReplyCount;
	}

	public void setTotalReplyCount(int totalReplyCount) {
		this.totalReplyCount = totalReplyCount;
	}

	public String getMyParentAuthorDisplayName() {
		return myParentAuthorDisplayName;
	}

	public void setMyParentAuthorDisplayName(String myParentAuthorDisplayName) {
		this.myParentAuthorDisplayName = myParentAuthorDisplayName;
	}
	
	
	
	
}
