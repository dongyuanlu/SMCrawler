package ldy.instagram;


public class InstagramToken {
	
	private String[] tokens = InstagramConfig.accessTokens;
	private int[] countTokens;
	private long[] startTime;
	
	private int LIMIT = 5; //instagram visit limit, 5000 / hour
	private long HOUR = 3600000;	//one hour in milliseconds
	
	private int currentIndex;
	
	public InstagramToken(){
	
		countTokens = new int[tokens.length];
		startTime = new long[tokens.length];
		
		currentIndex = 0;
		startTime[currentIndex] = System.currentTimeMillis();

	}
	
	
	
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
					currentIndex = (currentIndex++) % tokens.length;	//move to next token
					
					//if this token is safe
					if(isSafe(currentIndex))	
					{	//start this token
						startCurrenIndex(currentIndex);
						return tokens[currentIndex];	//return next token
					}					
				}
				
				//If currently no token is safe
				//pick the latest one, sleep for a while, restart this token
				int maxTimeIndex = maxTimeIndex(startTime);
				sleep(startTime[maxTimeIndex]);
				currentIndex = maxTimeIndex;
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
		long timeInterval = System.currentTimeMillis() - startTime[index];
		
		if(timeInterval >= HOUR){
			return true;
		}
		else{
			return false;
		}
	}
	
	public int maxTimeIndex(long[] times){
		
		long max = times[0];
		int maxIndex = 0;
		for(int i = 0; i < times.length; i++){
			if(max < times[i]){
				max = times[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	public void sleep(long currentTime){
		
		try {
			Thread.sleep(HOUR - (System.currentTimeMillis() - currentTime));
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}
}
