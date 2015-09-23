package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DataplanePolicy {
public String getDataplanePolicyName(){
		URL oracle=null;
		URLConnection yc=null;
		HttpURLConnection connection=null;
		int code=0;
		String policy="";
		URL oracle2=null;
		URLConnection yc2=null;
		HttpURLConnection connection2=null;
		int code1=0;

		try {
			
			oracle = new URL("http://10.76.110.84:8181/restconf/config/opendaylight-inventory:nodes/node/vRouter-R1/yang-ext:mount/vyatta-interfaces:interfaces/vyatta-interfaces-dataplane:dataplane/dp0p224p1/");
			
			connection = (HttpURLConnection)oracle.openConnection();
			
			yc = oracle.openConnection();
			code = connection.getResponseCode();
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();

		}
		
		if(code!=200){
			System.out.println("No Such Policy");
			System.out.println(code);
			System.exit(0);
			
		}
		

		if (code == 200) {

		BufferedReader in;
		try {
			String inputLine;

			StringBuffer response = new StringBuffer();

			int lineno = 0;
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((inputLine = in.readLine()) != null) {

				if (inputLine.equals(""))

					continue;
				
					
				response.append(inputLine + "\n");
				int i=response.indexOf("vyatta-policy-qos:qos-policy");
				int j=response.indexOf("address");
				System.out.println(i+" "+j);
				policy=response.substring(i+31,j-3);
			    System.out.println("policy" + policy+"\n");
			    
			    
			    
		} 
			System.out.println(response);
		}catch (IOException e1) {
			
			e1.printStackTrace();
		}

		
	}
		return policy;
}
	
}
