package ldy.youtube;

public class YoutubeKey {
	
	private String[] keys = {YoutubeConfig.accessKeys[0]};
	private int[] countKeys;
	private long[] startTime;
	
	private int LIMIT = 4950; //instagram visit limit, 5000 / hour
	private long HOUR = 3600000;	//less one hour in milliseconds

	
	private int currentIndex;
	private boolean flag;
	
	public YoutubeKey(){
		countKeys = new int[keys.length];
		startTime = new long[keys.length];
		
		currentIndex = 0;
	//	startTime[currentIndex] = System.currentTimeMillis();
		flag = false;

	}

	
	
	/**
	 * 
	 * Iteratively pick one valid access_key from YoutubeConfig.accessKeys
	 * Under limitation: LIMIT visits per HOUR
	 * 
	 * @return
	 */
	public String pickKey(){
		if(!flag){
			startTime[currentIndex] = System.currentTimeMillis();
			flag = true;
		}
		
		if(countKeys[currentIndex] < LIMIT)
		{
			countKeys[currentIndex]++;
			return keys[currentIndex];
			
		}
		else
		{
			//first check if current token is safe
			if(isSafe(currentIndex)){	//if safe, restart current token
				startCurrenIndex(currentIndex);
				return keys[currentIndex];
			}
			else	//if not safe, move to next token
			{
				for(int round = 1; round < keys.length; round++)
				{
					currentIndex = (currentIndex+1) % keys.length;	//move to next token
					
					//if this token is safe
					if(isSafe(currentIndex))	
					{	//start this token
						startCurrenIndex(currentIndex);
						return keys[currentIndex];	//return next token
					}					
				}
				
				//If currently no token is safe
				//pick the latest one, sleep for a while, restart this token
				int minTimeIndex = minTimeIndex(startTime);
				sleep(startTime[minTimeIndex]);
				currentIndex = minTimeIndex;
				startCurrenIndex(currentIndex);
				return keys[currentIndex];
			}
				
			
		}
	}


	public void startCurrenIndex(int currentIndex){
		countKeys[currentIndex] = 0; 
		startTime[currentIndex] = System.currentTimeMillis();
	}
	
	public boolean isSafe(int index){
		long timeInterval = System.currentTimeMillis() - startTime[index] - 100000;
		
		if(timeInterval >= HOUR){
			return true;
		}
		else{
			return false;
		}
	}
	
	public int minTimeIndex(long[] times){
		
		long min = times[0];
		int minIndex = 0;
		for(int i = 0; i < times.length; i++){
			if(min > times[i]){
				min = times[i];
				minIndex = i;
			}
		}
		return minIndex;
	}
	
	public void sleep(long currentTime){
		
		try {
			long time = HOUR - (System.currentTimeMillis() - currentTime);
			System.err.println("sleep for " + time + ": for security");
			Thread.sleep(time);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

}
