package ldy.reddit;

public class RedditArticleMedia {
	private String name = ""; /*article name*/
	
	private String media_url = "";
	
	private String title = "";
	
	private String description = "";
	
	private String type = "";
	
	private String author_name = "";
	
	private String provider_name = "";
	
	/**
	 * Constructor
	 */
	public RedditArticleMedia(){
		
	}
	
	public RedditArticleMedia(String media_url){
		this.media_url = media_url;
	}
	
	public RedditArticleMedia(String name, String media_url){
		this.name = name;
		this.media_url = media_url;
	}

	///////////////////////////
	
	
	
	public String getMedia_url() {
		return media_url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMedia_url(String media_url) {
		this.media_url = media_url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAuthor_name() {
		return author_name;
	}

	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}

	public String getProvider_name() {
		return provider_name;
	}

	public void setProvider_name(String provider_name) {
		this.provider_name = provider_name;
	}

	
	
}
