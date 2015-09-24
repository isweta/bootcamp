package MonitorStats;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Analyser {
	
	static ArrayList<Long> tailDropList10=new ArrayList<Long>();
	
	public static void monitor(long newTailDrop){

		if(Analyser.tailDropList10.size()<10)
			Analyser.buildBuffer(newTailDrop);
		else{
			Analyser.modifyBuffer(newTailDrop);
			boolean congestion=Analyser.analyse();
			if(congestion==true){
				PolicyPusher.pushNewPolicy("1024kbps");
				System.out.println("Pushed a new policy");
				
				
			}
			logPolicyChange(congestion);
				
		}
			
	}
	
	public static void logPolicyChange(boolean congestion){
		FileWriter foutLog;
		try {
			foutLog = new FileWriter("C:\\Users\\Public\\Documents\\files\\src\\MonitorStats\\policyChangeLog.txt");
			if(congestion){
				foutLog.append("Congestion Detected!!");
				foutLog.append("\n Changed the policy to manage congestion");
				System.out.println("wrote");
			}else{
				
				foutLog.append("Monitoring in Progress");
			}
				
						
			
			foutLog.close();
			
		}catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean analyse(){
		int count=0;
		for(long temp: tailDropList10){
			if(temp>0)
				count++;
		}
		
		if(count>5)
			return true;
		else
			return false;
	}
	public static void buildBuffer(long newTailDrop){
		tailDropList10.add(newTailDrop);
	}
	
	public static void modifyBuffer(long newTailDrop){
		tailDropList10.remove(0);
		tailDropList10.add(newTailDrop);
	}

}
