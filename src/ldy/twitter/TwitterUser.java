package ldy.twitter;

public class TwitterUser {

	private String screen_name = "";
	
	private String user_id = "";
	
	private String created_at = "";
	
	private long created_at_long = 0;
	
	private String description = "";
	
	private String name = "";
	
	private String location = "";
	
	private String profile_image_url = "";
	
	private String url = "";
	
	private int follower_count = 0;
	
	private int favorite_count = 0;
	
	private int friend_count = 0;
	
	private String time_zone = "";
	
	private int status_count = 0;
	
	private boolean is_geo_enabled = false;
	
	private int listed_count = 0;
	
	private boolean is_verified = false;
	

	
	
	
	public int getFriend_count() {
		return friend_count;
	}

	public void setFriend_count(int friend_count) {
		this.friend_count = friend_count;
	}

	public boolean isIs_verified() {
		return is_verified;
	}

	public void setIs_verified(boolean is_verified) {
		this.is_verified = is_verified;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getFollower_count() {
		return follower_count;
	}

	public void setFollower_count(int follower_count) {
		this.follower_count = follower_count;
	}

	public int getFavorite_count() {
		return favorite_count;
	}

	public void setFavorite_count(int favorite_count) {
		this.favorite_count = favorite_count;
	}

	public int getFriend() {
		return friend_count;
	}

	public void setFriend(int friend) {
		this.friend_count = friend;
	}

	public String getTime_zone() {
		return time_zone;
	}

	public void setTime_zone(String time_zone) {
		this.time_zone = time_zone;
	}

	public int getStatus_count() {
		return status_count;
	}

	public void setStatus_count(int status_count) {
		this.status_count = status_count;
	}

	public boolean isIs_geo_enabled() {
		return is_geo_enabled;
	}

	public void setIs_geo_enabled(boolean is_geo_enabled) {
		this.is_geo_enabled = is_geo_enabled;
	}

	public int getListed_count() {
		return listed_count;
	}

	public void setListed_count(int listed_count) {
		this.listed_count = listed_count;
	}
	
	
}
