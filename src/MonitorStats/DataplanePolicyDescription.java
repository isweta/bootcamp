package MonitorStats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataplanePolicyDescription {

	public static void main(String[] args) {
		getDataplanePolicyDescription();
	}

	static String getDataplanePolicyDescription() {
		String str = getJSONString("TSD");
		JSONObject obj = new JSONObject(str);
		System.out.println(obj);
		JSONArray policyAr=obj.getJSONArray("vyatta-policy-qos:qos");
		System.out.println(policyAr);
		JSONObject policyObj=(JSONObject)policyAr.get(0);
		System.out.println(policyObj);
		JSONObject shaperObj=(JSONObject)policyObj.get("shaper");
		System.out.println(shaperObj);
		JSONArray profileAr=shaperObj.getJSONArray("profile");
		

		// return policyname;
		return "";
	}
	
		public static String getJSONString(String policy){
			URL oracle2 = null;
			URLConnection yc2 = null;
			HttpURLConnection connection2 = null;
			int code1 = 0;
			StringBuffer response = new StringBuffer();
			try {
				oracle2 = new URL(
						"http://10.76.110.84:8181/restconf/config/opendaylight-inventory:nodes/node/vRouter-R1/yang-ext:mount/vyatta-policy:policy/vyatta-policy-qos:qos/"
								+ policy);
				// System.out.println(oracle2);

				connection2 = (HttpURLConnection) oracle2.openConnection();
				yc2 = oracle2.openConnection();
				code1 = connection2.getResponseCode();

				if (code1 == 200) {

					BufferedReader in;

					try {
						String inputLine;

						int lineno = 0;
						in = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
						while ((inputLine = in.readLine()) != null) {

							if (inputLine.equals(""))

								continue;

							response.append(inputLine + "\n");

						}
						//System.out.println(response);
					} catch (IOException e1) {

						e1.printStackTrace();
					}

				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();

			}
			return response.toString();

		}

}

