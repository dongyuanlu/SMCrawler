package ldy.instagram;

import org.json.JSONObject;

public class CheckJSONPage {

	/**
	 * 
	 * Check jsonPage
	 * 
	 * @param jsonPage
	 * @param apiUrl
	 * @return
	 */
	public boolean checkJsonPage(String jsonPage, String apiUrl){
		if(jsonPage == null){
			System.err.println(apiUrl + ": null");
			return false;
		}
		if(!jsonPage.contains("{")){
			System.err.println(apiUrl + ": not contains '{'");
			return false;
		}
		return true;

	}
	public boolean checkMetaCode(JSONObject pageObject){
		JSONObject metaObject = pageObject.getJSONObject("meta");
		if(metaObject.isNull("code") || ( !metaObject.isNull("code") && metaObject.getInt("code") != 200 )){
			return false;
		}
		return true;
	}

}
