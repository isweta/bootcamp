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

import oracle.jrockit.jfr.openmbean.PresetFileType;
import sun.misc.IOUtils;

public class StatsCollector {
	static ArrayList<Datagram> prevCumulativeAllTrafficClasses;
	static long startTime;

	static class Datagram {
		long timestamp;
		String iface;

		int prio;
		long packets;
		long bytes;
		long tailDrop;
		long redDrop;
		double speed;

		public Datagram(long timestamp, String iface, int prio, long packets, long bytes, long tailDrop, long redDrop) {
			super();
			this.timestamp = timestamp;
			this.iface = iface;
			this.prio = prio;
			this.packets = packets;
			this.bytes = bytes;
			this.tailDrop = tailDrop;
			this.redDrop = redDrop;
		}

		public double getSpeed() {
			return speed;
		}

		public void setSpeed(double speed) {
			this.speed = speed;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public String getIface() {
			return iface;
		}

		public void setIface(String iface) {
			this.iface = iface;
		}

		public int getPrio() {
			return prio;
		}

		public void setPrio(int prio) {
			this.prio = prio;
		}

		public long getPackets() {
			return packets;
		}

		public void setPackets(long packets) {
			this.packets = packets;
		}

		public long getBytes() {
			return bytes;
		}

		public void setBytes(long bytes) {
			this.bytes = bytes;
		}

		public long getTailDrop() {
			return tailDrop;
		}

		public void setTailDrop(long tailDrop) {
			this.tailDrop = tailDrop;
		}

		public long getRedDrop() {
			return redDrop;
		}

		public void setRedDrop(long redDrop) {
			this.redDrop = redDrop;
		}

		@Override
		public String toString() {
			return "Datagram [timestamp= within x ms =" + timestamp + ", iface=" + iface + ", prio=" + prio
					+ ", packets=" + packets + ", bytes=" + bytes + ", tailDrop=" + tailDrop + ", redDrop=" + redDrop
					+ "]";
		}

		public void prettyPrint() {

			System.out.println(
					prio + "\t\t" + packets + "\t\t" + bytes + "\t\t" + tailDrop + "\t\t" + redDrop + "\t" + speed);
		}

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

			long currTime = System.currentTimeMillis();
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "*/*");
			connection.setRequestProperty("Accept", "application/json*");
			connection.setRequestProperty("Vyatta-Specification-Version", "0.1");
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);

			resCode = connection.getResponseCode();
			if (resCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				int lineno = 0;
				ArrayList<Datagram> CumulativeAllTrafficClasses = new ArrayList<Datagram>();
				ArrayList<Datagram> allTrafficClasses = new ArrayList<Datagram>();
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
				//System.out.println(CumulativeAllTrafficClasses);
				
					
				for (int di = 0; di < CumulativeAllTrafficClasses.size(); di++) {
					if (prevCumulativeAllTrafficClasses == null) {
						//ignore the first datagram
						prevCumulativeAllTrafficClasses = CumulativeAllTrafficClasses;
						return resCode;
					}
					Datagram prevcum = prevCumulativeAllTrafficClasses.get(di);
					Datagram newcum = CumulativeAllTrafficClasses.get(di);
					
					Datagram datagram = new Datagram(newcum.timestamp - prevcum.timestamp, newcum.iface, newcum.prio,
							newcum.packets - prevcum.packets, newcum.bytes - prevcum.bytes,
							newcum.tailDrop - prevcum.tailDrop, newcum.redDrop - prevcum.redDrop);
					
					long num=1000*datagram.getBytes();
					long denom=1024*datagram.getTimestamp();
					double speed=num/denom;
								
					datagram.setSpeed(speed);
					allTrafficClasses.add(datagram);
					
					
				}
				double sec=(allTrafficClasses.get(0).timestamp) / 1000.0;
				if (!allTrafficClasses.isEmpty())
					System.out.println("\n\n\nIn the last " + sec + " sec ");
				System.out.println("Class\t\tPackets\t\tbytes\t\ttaildrop\tredDrop\tspeed(kbps)");
				for (Datagram datagram : allTrafficClasses) {
					// System.out.println(datagram);
					datagram.prettyPrint();
				}

				prevCumulativeAllTrafficClasses = CumulativeAllTrafficClasses;

				in.close();
				
				analyseAllClasses(allTraffic);
				// System.out.println("Get request");
				// System.out.println(connection.getResponseMessage());
				// System.out.println(connection.getResponseCode());

			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;

		}
		return resCode;

	}

	public static void main(String[] args) {
		startTime = System.currentTimeMillis();
		//int geti = 0;
		while (true) {
			//System.out.println("called for the getith time: " + geti + "");
			String loc = executeOp();
			//geti++;
			//int outputIndex = 0;
			int res;
			while (true) {
				//System.out.println("called for the outith time: " + outi + "");
				res = getOutput(loc);
				//outputIndex++;
				if (res == 200)
					break;

			}

		}
	}
	
	
	public static void analyseDatagram(){
		
	}
}
