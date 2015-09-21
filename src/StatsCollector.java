import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import sun.misc.IOUtils;

public class StatsCollector {
	
	static class Datagram{
		String iface;
		long timestamp;
		int prio;
		long packets;
		long bytes;
		long tailDrop;
		long redDrop;
		
		public Datagram(long timestamp, )
	}

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
			//System.out.println("POST reuest");
			//System.out.println(connection.getResponseMessage());
			//System.out.println(connection.getResponseCode());
			return loc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return " ";
		

	}

	public static int getOutput(String loc) {
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
			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			int lineno=0;
			ArrayList<Datagram> allTrafficClasses=new ArrayList<Datagram>();
			while ((inputLine = in.readLine()) != null) {
				
				response.append(inputLine+"\n");
				String iface;
				
				
				if(lineno>1){
					String ar[] =inputLine.split("\\s++");
					if(lineno==2)
						iface=ar[0];
									
					Datagram temp=new Datagram(System.currentTimeMillis(),iface,ar[1],ar[2],ar[3],ar[4],ar[5]);
				}
				lineno++;
			}
			in.close();
			
			//System.out.println("Get request");
			//System.out.println(connection.getResponseMessage());
			//System.out.println(connection.getResponseCode());
			
			if(connection.getResponseCode()==200){
				System.out.println("Timestamp: "+System.currentTimeMillis()+"\n"+response.toString());
				
			}
			
			return connection.getResponseCode();

		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return 0;

	}

	public static void main(String[] args) {
		while(true){
			String loc=executeOp();
			int res;
			while(true){
				res=getOutput(loc);
				
				if(res==200)
					break;
				
			}
			
	}
	}
}
