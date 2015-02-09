package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;



public class WebPageCrawler {
	
	public static Logger log = Logger.getLogger(WebPageCrawler.class);
	
	
	/**
	 * Crawl webpage given URL
	 * @param url
	 * @return
	 */
	public static String crawlbyUrl(String url){
		
		HttpClient httpClient = new HttpClient();
		DefaultHttpParams.getDefaultParams().setParameter("http.protocol.cookie-policy", CookiePolicy.IGNORE_COOKIES);
        GetMethod getMethod = new GetMethod(url);
        
        //using the default restart rule
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());
        int statusCode;
		try {
			statusCode = httpClient.executeMethod(getMethod);
			if(statusCode != HttpStatus.SC_OK){
				log.error(" Method failed: " + getMethod.getStatusLine());
	  //     	System.err.println( " Method failed: " + getMethod.getStatusLine());
	        	return "-1";
	        	
	        }
		} catch (HttpException e) {
			System.err.println("Protocol exception occurs! "+e);
			log.error("Protocol exception occurs! "+e);
			return "-1";
		} catch (IOException e) {
			System.err.println("IO stream in HttpClient error! "+e);
			log.error("IO stream in HttpClient error! "+e);
			return "-1";
		} 
        
		//save the html content
        String htmlContent = null;
		try {
			htmlContent = getStringFromInputStream(getMethod.getResponseBodyAsStream());
//			htmlContent = getMethod.getResponseBodyAsString();

		} catch (IOException e) {
			System.err.println("IO stream in getResponseBody error! "+e);
			log.error("IO stream in getResponseBody error! "+e);
			return null;
		}

        return htmlContent;    
	}
	
	
	// convert InputStream to String
		private static String getStringFromInputStream(InputStream is) {
	 
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder("utf-8");
	 
			String line;
			try {

				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
	 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	 
			return sb.toString().replaceFirst("utf-8", "");
	 
		}
	 
	
	
	public static void main(String[] args){
		WebPageCrawler wpc = new WebPageCrawler();
		String content = wpc.crawlbyUrl("http://instagram.com/p/ZwUS7sl-m3/");
		System.out.println(content);
	}

}
