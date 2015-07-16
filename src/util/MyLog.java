package util;

import java.util.ArrayList;

public class MyLog {
	
	private ArrayList<String> logList = new ArrayList<String>();
	private String logName = "";
	
	
	
	/*******************Constructor*******************************************/
	public MyLog(String className){
		this.logName = className + "_" + System.currentTimeMillis();
	}
	
	public MyLog(String className, String articleListName){
		this.logName = className + "_" + articleListName + "_" + System.currentTimeMillis();
	}
	
	
	
	/*******************Methods********************************************/
	public void addLogList(String s){
		this.logList.add(s);
	}
	
	public void clearLogList(){
		this.logList.clear();
	}
	
	public int getLogListSize(){
		return this.logList.size();
	}

	public void writeLogList(){
		WriteArrayList2File.writeArrayList2FileAppend(logList, "data/log/" + logName + ".txt");
	}
}
