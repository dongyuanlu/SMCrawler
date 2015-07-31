package ldy.instagram;

/**
 * 
 * Select access_token for usage
 * 
 * @author Administrator
 *
 */
public class InstagramToken_1 {
	
	//ONLY USE first two tokens in InstagramConfig/////
	private String[] tokens = {InstagramConfig.accessTokens[0],InstagramConfig.accessTokens[1]};
	private int[] countTokens;
	private long[] startTime;
	

	private int LIMIT = 5000; //instagram visit limit, 5000 / hour
	private long HOUR = 3600000;	//less one hour in milliseconds

	
	private int currentIndex;
	
	public InstagramToken_1(){
	
		countTokens = new int[tokens.length];
		startTime = new long[tokens.length];
		
		currentIndex = 0;
		startTime[currentIndex] = System.currentTimeMillis();

	}
	
	
	/**
	 * 
	 * Iteratively pick one valid access_token from InstagramConfig.accessTokens
	 * Under limitation: 5000 visits per hour
	 * 
	 * @return
	 */
	public String pickToken(){
		
		if(countTokens[currentIndex] < LIMIT)
		{
			countTokens[currentIndex]++;
			return tokens[currentIndex];
			
		}
		else
		{
			//first check if current token is safe
			if(isSafe(currentIndex)){	//if safe, restart current token
				startCurrenIndex(currentIndex);
				return tokens[currentIndex];
			}
			else	//if not safe, move to next token
			{
				for(int round = 1; round < tokens.length; round++)
				{
					currentIndex = (currentIndex+1) % tokens.length;	//move to next token
					
					//if this token is safe
					if(isSafe(currentIndex))	
					{	//start this token
						startCurrenIndex(currentIndex);
						return tokens[currentIndex];	//return next token
					}					
				}
				
				//If currently no token is safe
				//pick the latest one, sleep for a while, restart this token
				int minTimeIndex = minTimeIndex(startTime);
				sleep(startTime[minTimeIndex]);
				currentIndex = minTimeIndex;
				startCurrenIndex(currentIndex);
				return tokens[currentIndex];
			}
				
			
		}
	}


	public void startCurrenIndex(int currentIndex){
		countTokens[currentIndex] = 0; 
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
			long time = HOUR - (System.currentTimeMillis() - currentTime) + 100000;
			System.err.println("sleep for " + time + ": for security");
			Thread.sleep(time);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}
}
