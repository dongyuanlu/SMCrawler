package ldy.reddit;

public class RedditComment {
	
	private String link_id = "";	/*article name*/
	
	private String id = "";	/*comment id*/
	
	private String parent_id = "";	/*if first level comment, parent*/
	
	private String name = ""; /*comment kind_id*/
	
	private String body = ""; /*surrounded by &gt;.....\n\n is referred comment*/
	
	private String author = "";
	
	private int ups;
	
	private int downs;
	
	private int score;
	
	private Long created;
	
	private Long created_utc;	/*different with created*/

	public String getLink_id() {
		return link_id;
	}

	public void setLink_id(String link_id) {
		this.link_id = link_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getUps() {
		return ups;
	}

	public void setUps(int ups) {
		this.ups = ups;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getCreated_utc() {
		return created_utc;
	}

	public void setCreated_utc(Long created_utc) {
		this.created_utc = created_utc;
	}

	public int getDowns() {
		return downs;
	}

	public void setDowns(int downs) {
		this.downs = downs;
	}

	
	
	

}
