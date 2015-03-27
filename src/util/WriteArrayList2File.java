package util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class WriteArrayList2File {
	
	public static void writeArrayList2File(ArrayList list, String filePath){
		BufferedWriter fileWrite=null;
		try {
			fileWrite = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath), "utf-8"));
			for(int j = 0; j < list.size(); j++){
				fileWrite.write(list.get(j)+"");
				fileWrite.newLine();
			}
			
			fileWrite.close();
			
		}catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	


}
