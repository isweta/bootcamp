package test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DataplanePolicyDescription {
	String getDataplanePolicyDescription(String policy){
		URL oracle2=null;
		URLConnection yc2=null;
		HttpURLConnection connection2=null;
		int code1=0;
		
		try {
			oracle2 = new URL("http://10.76.110.84:8181/restconf/config/opendaylight-inventory:nodes/node/vRouter-R1/yang-ext:mount/vyatta-policy:policy/vyatta-policy-qos:qos/"+policy);
			System.out.println(oracle2);
			
			connection2 = (HttpURLConnection)oracle2.openConnection();
			yc2 = oracle2.openConnection();
			code1 = connection2.getResponseCode();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();

		}
		return "";
		
}
		
		
	}


