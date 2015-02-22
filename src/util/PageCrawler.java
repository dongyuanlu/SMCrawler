package util;

import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStream; 
import java.io.InputStreamReader; 
import java.io.OutputStream; 
import java.net.HttpURLConnection;
import java.net.URL; 
import java.net.URLConnection; 
import java.util.regex.Matcher; 
import java.util.regex.Pattern; 

//import sun.net.www.protocol.http.HttpURLConnection; 


/**
 * Crawl webpage content given url
 * 
 * call method PageCrawler.readUrl();
 * 
 * @author ellen
 *
 */
public class PageCrawler { 
    private static int TIME_OUT=15000; 
    private static String DEFAULT_CHARSET="utf-8"; 

    private static Pattern pattern = Pattern.compile(".*charset\\s*=\\s*(.*?)",Pattern.CASE_INSENSITIVE); 
    public static class ResponseResult{ 
        public final String content; 
        public final String contentType; 
        private ResponseResult(String content,String contentType){ 
            this.content=content; 
            this.contentType=contentType; 
        } 
     } 
    
   /* 
    public static void main(String[] args){
    	String remoteUrl = "http://www.reddit.com/r/videos/controversial/.json?limit=100&t=year&after=";
    	System.out.println(PageCrawler.readUrl(remoteUrl));
    }
    
    */
    /** 
     * Util method
     * 
     * @param remoteUrl 
     * @return page content. or NULL 
     *    
     *        
     *      
     */ 
    public static String readUrl(String remoteUrl) { 
        BufferedReader content=null; 
        HttpURLConnection conn=null; 
        try{ 
             URL url=new URL(remoteUrl); 
             conn = (HttpURLConnection) url.openConnection(); 
             conn.setReadTimeout(TIME_OUT); 
             conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)"); 
             conn.connect(); 
             String charset = getCharset(conn,DEFAULT_CHARSET); 
             content = new BufferedReader(new InputStreamReader( conn.getInputStream(),charset)); 
             StringBuffer result=new StringBuffer(); 
             for(String line="";line!=null;line=content.readLine()){ 
                 result.append(line); 
             } 
             return result.toString(); 
          } 
          catch(java.net.MalformedURLException e){ 
              throw new RuntimeException(e); 
          }catch(java.net.UnknownHostException e){ 
              //log.error("UnknownHostException:"+remoteUrl); 
              return null;//主机找不到多数是网络问题 
          } 
          catch(java.io.IOException ioe){ 
              //log.error(ioe.getMessage()+":"+remoteUrl); 
              return null; 
          }finally{ 
              if(conn!=null){ 
                  try { 
                      conn.disconnect(); 
                } catch (RuntimeException e) { 
                    System.out.println(e.getMessage()); 
                } 
              } 

              if(content!=null){ 
                  try { 
                    content.close(); 
                } catch (IOException e) { 
                    System.out.println(e.getMessage()); 
                } 
              } 
          } 
    } 

    public static String getCharset(URLConnection conn,String defaultCharset) { 
        String contentType=conn.getContentType(); 
        if(contentType==null){ 
            return defaultCharset; 
        } 
        Matcher matcher = pattern.matcher(contentType); 
        if(matcher.matches()){ 
            return matcher.group(1);//第一个匹配项就是要找的 
        } 
        return defaultCharset; 
    } 

    /** 
     * 
     * Read remoteUrl content and write into out
     * for agent 
     * 
     * @param remoteUrl 
     * @param out
     */ 
    public static void writeTo(String remoteUrl,OutputStream out) { 
        //System.out.println("Net.writeTo:"+remoteUrl); 
        BufferedReader content=null; 
        HttpURLConnection conn=null; 
        try{ 
             URL url=new URL(remoteUrl); 
             conn = (HttpURLConnection) url.openConnection(); 
             conn.setReadTimeout(TIME_OUT); 
             conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)"); 
             conn.connect(); 
             InputStream in=conn.getInputStream(); 
             
             int length; 
             byte buffer[] = new byte[1024]; 
             while ((length = in.read(buffer, 0, 1024)) != -1) { 
                 out.write(buffer, 0, length); 
             } 
          } 
          catch(java.net.MalformedURLException e){ 
              throw new RuntimeException(e); 
          }catch(java.net.UnknownHostException e){ 
             // log.error("UnknownHostException:"+remoteUrl); 
          } 
          catch(java.io.IOException ioe){ 
             // log.debug(ioe.getMessage()+":"+remoteUrl); 
          }finally{ 
              if(conn!=null){ 
                  try { 
                      conn.disconnect(); 
                } catch (RuntimeException e) { 
                    System.out.println(e.getMessage()); 
                } 
              } 

              if(content!=null){ 
                  try { 
                    content.close(); 
                } catch (IOException e) { 
                    System.out.println(e.getMessage()); 
                } 
              } 
              
          } 
    } 

} 