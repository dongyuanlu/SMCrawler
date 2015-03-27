package util;

import java.util.ArrayList;

public class MyLog {
	
	private ArrayList<String> logList = new ArrayList<String>();
	private String logName = "";
	
	public MyLog(String className){
		this.logName = className + "_" + System.currentTimeMillis();
	}
	
	public MyLog(String className, String articleListName){
		this.logName = className + "_" + articleListName + "_" + System.currentTimeMillis();
	}
	public void addLogList(String s){
		this.logList.add(s);
	}

	public void writeLogList(){
		WriteArrayList2File.writeArrayList2File(logList, "data/log/" + logName + ".txt");
	}
}
