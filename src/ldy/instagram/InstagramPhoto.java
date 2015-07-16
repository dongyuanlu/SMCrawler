package ldy.instagram;

public class InstagramPhoto {
	
	private String photoId = "";
	
	private String userId = "";
	
	private String tags = "";
	
	private String created_time = "";
	
	private long created_time_long = 0;
	
	private String text = "";
	
	private String link = "";
	
	private double latitude = 0.0;
	
	private double longitude = 0.0;
	
	private String likes = ""; 	//userIds, who like this photo
	
	private int likes_count = 0;
	
	private String comments = "";
	
	private int comment_count = 0;
	
	private String image_url = "";
	
	private String users_in_photo = "";
	
	private boolean user_has_liked = false;

	private String location_id = "";
	
	private String filter = "";
	
	
	public String getLocation_id() {
		return location_id;
	}

	public void setLocation_id(String location_id) {
		this.location_id = location_id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public long getCreated_time_long() {
		return created_time_long;
	}

	public void setCreated_time_long(long created_time_long) {
		this.created_time_long = created_time_long;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getLikes() {
		return likes;
	}

	public void setLikes(String likes) {
		this.likes = likes;
	}

	public int getLikes_count() {
		return likes_count;
	}

	public void setLikes_count(int likes_count) {
		this.likes_count = likes_count;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getUsers_in_photo() {
		return users_in_photo;
	}

	public void setUsers_in_photo(String users_in_photo) {
		this.users_in_photo = users_in_photo;
	}

	public boolean isUser_has_liked() {
		return user_has_liked;
	}

	public void setUser_has_liked(boolean user_has_liked) {
		this.user_has_liked = user_has_liked;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	
	
	

}
