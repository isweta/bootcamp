package MonitorStats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import sun.misc.IOUtils;

public class StatsCollector {
	static ArrayList<Datagram> prevCumulativeAllTrafficClasses;
	static long startTime;

	public static String executeOp() {
		try {
			String name = "vyatta";
			String pass = "vyatta";
			String authString = name + ":" + pass;
			byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			URL url = new URL("http://10.76.110.94/rest/op/show/queuing");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "*/*");
			connection.setRequestProperty("Accept", "application/json*");
			connection.setRequestProperty("Vyatta-Specification-Version", "0.1");
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
			osw.write(" ");
			osw.close();

			String loc = connection.getHeaderField("Location");
			// System.out.println("POST reuest");
			// System.out.println(connection.getResponseMessage());
			connection.getResponseCode();
			return loc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return " ";

	}

	public static int getOutput(String loc) {
		int resCode = 0;
		try {
			String name = "vyatta";
			String pass = "vyatta";
			String authString = name + ":" + pass;
			byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			URL url = new URL("http://10.76.110.94/" + loc);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "*/*");
			connection.setRequestProperty("Accept", "application/json*");
			connection.setRequestProperty("Vyatta-Specification-Version", "0.1");
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);

			resCode = connection.getResponseCode();
			if (resCode == 200) {

				StringBuffer response = new StringBuffer();
				ArrayList<Datagram> CumulativeAllTrafficClasses = new ArrayList<Datagram>();
				ArrayList<Datagram> allTrafficClasses = new ArrayList<Datagram>();

				LocalDateTime currTime = readData(connection, CumulativeAllTrafficClasses, allTrafficClasses, response);
				double secDiff=buildDataPacket(CumulativeAllTrafficClasses, allTrafficClasses);
				String policyName=DataplanePolicy.getDataplanePolicyName();
				DataplanePolicyDescription.getDataplanePolicyDescription(policyName);
				HashMap<Integer, String> descMap=DataplanePolicyDescription.getDescMap();
				HashMap<Integer, String> bwMap=DataplanePolicyDescription.getBwMap();
				if(secDiff!=-1.0)
					prettyprintData(descMap, bwMap, secDiff, currTime, allTrafficClasses);
				prevCumulativeAllTrafficClasses = CumulativeAllTrafficClasses;

			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;

		}
		return resCode;

	}

	public static LocalDateTime readData(HttpURLConnection connection, ArrayList<Datagram> CumulativeAllTrafficClasses,
			ArrayList<Datagram> allTrafficClasses, StringBuffer response) {

		String inputLine;
		int lineno = 0;
		LocalDateTime currTime = LocalDateTime.now();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((inputLine = in.readLine()) != null) {

				if (inputLine.equals(""))
					continue;
				response.append(inputLine + "\n");
				String iface = "";

				if (lineno > 1) {
					String ar[] = inputLine.split("\\s++");
					if (lineno == 2)
						iface = ar[0];

					Datagram temp = new Datagram(currTime, iface, Integer.parseInt(ar[1]), Integer.parseInt(ar[2]),
							Integer.parseInt(ar[3]), Integer.parseInt(ar[4]), Integer.parseInt(ar[5]));
					CumulativeAllTrafficClasses.add(temp);

				}
				lineno++;
			}
			in.close();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currTime;
	}

	public static void printData(LocalDateTime currTime, ArrayList<Datagram> allTrafficClasses) {
		FileWriter fout;
		try {
			fout = new FileWriter("C:\\Users\\Public\\Documents\\data2.txt", true);

			// fout.append("\n\n\nIn the last " + sec + " sec ");
			System.out.println("\n" + currTime);
			fout.append("\n" + currTime);
			
			System.out.println("\nClass\t\tPackets\t\tbytes\t\ttaildrop\tredDrop\tspeed(kBps)");
			// fout.append("\nClass,Packets,bytes,taildrop,redDrop,speed(kbps)");
			fout.append("\nClass\t\tPackets\t\tbytes\t\ttaildrop\tredDrop\tspeed(kBps)");
			for (Datagram datagram : allTrafficClasses) {
				// System.out.println(datagram);
				System.out.println(datagram.prettyPrint());
				fout.append("\n" + datagram.prettyPrint());
			}
			fout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void prettyprintData(HashMap<Integer, String> descMap, HashMap<Integer, String> bwMap,double secDiff, LocalDateTime currTime, ArrayList<Datagram> allTrafficClasses) {
		FileWriter fout;
		FileWriter foutCurr;
		try {
			fout = new FileWriter("C:\\Users\\Public\\Documents\\files\\src\\MonitorStats\\data2.txt", true);
			foutCurr=new FileWriter("C:\\Users\\Public\\Documents\\files\\src\\MonitorStats\\dataCurr.txt");
			// fout.append("\n\n\nIn the last " + sec + " sec ");
			System.out.println("\nTimeStamp:" + currTime);
			fout.append("\nTimeStamp:" + currTime);
			foutCurr.append("\nTimeStamp:" + currTime);
			System.out.println("In_the_last_"+secDiff+"_seconds");
			fout.append("\nIn_the_last_"+secDiff+"_seconds");
			foutCurr.append("\nIn_the_last_"+secDiff+"_seconds");
			
			
			//System.out.println("TypeofService\t\t\tPriority\t\t\tBandwidthAllocated\t\t\tPacketsSent\t\t\tBytesSent\t\t\tDelayedPackets\t\t\tRateofTransfer(kBps)");
			// fout.append("\nClass,Packets,bytes,taildrop,redDrop,speed(kbps)");
			System.out.println(String.format("%-30s%-20s%-20s%-20s%-20s%-20s","TypeofService","Priority","BandwidthAllocated","PacketsCount","BytesCount","DelayedPackets"));
			fout.append("\n");
			foutCurr.append("\n");
			fout.append(String.format("%-30s%-20s%-20s%-20s%-20s%-20s","TypeofService","Priority","BandwidthAllocated","PacketsCount","BytesCount","DelayedPackets"));
			foutCurr.append(String.format("%-30s%-20s%-20s%-20s%-20s%-20s","TypeofService","Priority","BandwidthAllocated","PacketsCount","BytesCount","DelayedPackets"));
			for (Datagram datagram : allTrafficClasses) {
				System.out.print(String.format ("%-30s", (descMap.get(datagram.getPrio()).replaceAll(" ", "_") )));
				System.out.print(String.format("%-20s",datagram.getPrio()));
				System.out.print(String.format("%-20s",bwMap.get(datagram.getPrio())));
				System.out.print(String.format("%-20s",datagram.getPackets()));
				System.out.print(String.format("%-20s",datagram.getBytes()));
				System.out.print(String.format("%-20s",datagram.getTailDrop()));
				//System.out.print(String.format("%-30s",datagram.getSpeed()));
				System.out.println("");
				
				fout.append("\n");
				fout.append(String.format ("%-30s", (descMap.get(datagram.getPrio()).replaceAll(" ", "_") )));
				fout.append(String.format("%-20s",datagram.getPrio()));
				fout.append(String.format("%-20s",bwMap.get(datagram.getPrio())));
				fout.append(String.format("%-20s",datagram.getPackets()));
				fout.append(String.format("%-20s",datagram.getBytes()));
				fout.append(String.format("%-20s",datagram.getTailDrop()));
				//fout.append(String.format("%-30s",datagram.getSpeed()));
				
				
				foutCurr.append("\n");
				foutCurr.append(String.format ("%-30s", (descMap.get(datagram.getPrio()).replaceAll(" ", "_") )));
				foutCurr.append(String.format("%-20s",datagram.getPrio()));
				foutCurr.append(String.format("%-20s",bwMap.get(datagram.getPrio())));
				foutCurr.append(String.format("%-20s",datagram.getPackets()));
				foutCurr.append(String.format("%-20s",datagram.getBytes()));
				foutCurr.append(String.format("%-20s",datagram.getTailDrop()));
				//foutCurr.append(String.format("%-30s",datagram.getSpeed()));
				
			}
			fout.close();
			foutCurr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		startTime = System.currentTimeMillis();
		String QosPolicy = DataplanePolicy.getDataplanePolicyName();
		System.out.println("\nInterface: dp0p224p1");
		System.out.println("Qos Policy :" + QosPolicy);
		while (true) {

			String loc = executeOp();
			int res;
			while (true) {
				res = getOutput(loc);
				if (res == 200)
					break;

			}

		}
	}

	public static double buildDataPacket(ArrayList<Datagram> CumulativeAllTrafficClasses,
			ArrayList<Datagram> allTrafficClasses) {
		double secDiff = 0;

		for (int di = 0; di < CumulativeAllTrafficClasses.size(); di++) {
			if (prevCumulativeAllTrafficClasses == null) {
				// ignore the first datagram
				prevCumulativeAllTrafficClasses = CumulativeAllTrafficClasses;
				return -1.0;
			}
			Datagram prevcum = prevCumulativeAllTrafficClasses.get(di);
			Datagram newcum = CumulativeAllTrafficClasses.get(di);

			Datagram datagram = new Datagram(newcum.timestamp, newcum.iface, newcum.prio,
					newcum.packets - prevcum.packets, newcum.bytes - prevcum.bytes, newcum.tailDrop - prevcum.tailDrop,
					newcum.redDrop - prevcum.redDrop);

			double prevSec = prevcum.getTimestamp().getMinute() * 60 + prevcum.getTimestamp().getSecond();
			double newSec = newcum.getTimestamp().getMinute() * 60 + newcum.getTimestamp().getSecond();
			secDiff = newSec - prevSec;
			long num = datagram.getBytes();
			double denom = 1024 * secDiff;
			double speed = num / denom;
						
			datagram.setSpeed(speed);
			allTrafficClasses.add(datagram);

		}
		return secDiff;
	}

	public static void analyseDatagram() {

	}
}
