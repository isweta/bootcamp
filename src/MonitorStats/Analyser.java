package MonitorStats;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Analyser {
	
	static ArrayList<Long> tailDropList10=new ArrayList<Long>();
	
	public static void monitor(String policyName, String bw, long newTailDrop){

		if(Analyser.tailDropList10.size()<10)
			Analyser.buildBuffer(newTailDrop);
		else{
			Analyser.modifyBuffer(newTailDrop);
			boolean congestion=Analyser.analyse();
			if(congestion==true){
				clearList();
				PolicyPusher.pushNewPolicy("1024kbps");
				logPolicyChange(policyName, bw, congestion);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Pushed a new policy");
				
				
			}else
			{
				logPolicyChange(policyName, bw, congestion);
			}
			
				
		}
			
	}
	public static void clearList(){
		for(int i=0; i<tailDropList10.size(); i++){
			tailDropList10.set(i, 0l);
		}
	}
	
	public static void logPolicyChange(String policyName, String bw,boolean congestion){
		FileWriter foutLog;
		try {
			foutLog = new FileWriter("C:\\Users\\Public\\Documents\\files\\src\\MonitorStats\\policyChangeLog.txt");
			foutLog.append("\nQos Policy Name: "+policyName);
			//if(!congestion)
			foutLog.append("\nReal-Time Traffic Bandwidth: "+bw);
			/*else
				foutLog.append("\nReal-Time Traffic Bandwidth: 1024kbps");*/
			
			foutLog.append("\nPacket Delay Count- in last 10 intervals");
			for(long temp:tailDropList10)
				foutLog.append("\n"+temp);
			
			foutLog.append("\nNo of Intervals that experienced packet delay (Threshold/Tolerance=7)= "+ getCountTaildropInterval());
			if(congestion){
				foutLog.append("\nCongestion Detected!!");
				foutLog.append("\nReached the threshold set, push a Qos policy change to upgrade the bandwidth ");
				foutLog.append("\nQos Policy upgrade push is complete");
				System.out.println("wrote");
			}else{
				
				
				foutLog.append("\nNo Congestion Yet.....Monitoring in Progress....");
				
			}
				
						
			
			foutLog.close();
			
		}catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static int getCountTaildropInterval(){
		int count=0;
		for(long temp: tailDropList10){
			if(temp>0)
				count++;
		}
		return count;
	}
	
	public static boolean analyse(){
		
		int count=getCountTaildropInterval();
		
		
		
		if(count>7)
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
