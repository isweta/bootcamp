package gui;



import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class PolicyPusher {
	
	public static void main(String[] args) {
		pushNewPolicy("1024kbps");
	}
	
	public static void pushNewPolicy(String newBw){
		String policyName=DataplanePolicy.getDataplanePolicyName();
		String payload=formPayload(policyName, newBw);
		pushPolicy(policyName, payload);
	}
	
	public static String formPayload(String policyName, String newBw){
		
		JSONObject obj=DataplanePolicyDescription.getJSONObject(policyName);
		System.out.println(obj);
		JSONArray policyAr=obj.getJSONArray("vyatta-policy-qos:qos");
		JSONObject policyObj=(JSONObject)policyAr.get(0);
		JSONObject shaperObj=(JSONObject)policyObj.get("shaper");
		
		
		JSONArray classAr=shaperObj.getJSONArray("class");
		//System.out.println("printingggg");
		for(int j=0; j<classAr.length(); j++){
			//System.out.println(classAr.get(j));
			JSONObject shaperClassObj=classAr.getJSONObject(j);
			JSONArray matchAr=shaperClassObj.getJSONArray("match");
			JSONObject matchObj=matchAr.getJSONObject(0);
			if(matchObj.getString("tagnode").equals("AUDIO_SRC") || matchObj.getString("tagnode").equals("VIDEO_SRC")){
				JSONObject police=matchObj.getJSONObject("police");
				police.put("bandwidth", newBw);
			}
			
		}
		
		JSONArray profileAr=shaperObj.getJSONArray("profile");
		JSONObject tsd_profileObj=profileAr.getJSONObject(0);
		JSONArray trafficClassAr=tsd_profileObj.getJSONArray("traffic-class");
		for(int i=0; i<trafficClassAr.length(); i++){
			//System.out.println(trafficClassAr.get(i));
			JSONObject classObj=(JSONObject)trafficClassAr.get(i);
			if(classObj.getInt("tagnode")==0){
				classObj.put("bandwidth", newBw);
			}
		}
		System.out.println(obj);
		return obj.toString();
	}
	
	public static void pushPolicy(String policyName, String payload){
		URL oracle=null;
		URLConnection yc=null;
		int code=0;

		try {
			oracle = new URL("http://10.76.110.84:8181/restconf/config/opendaylight-inventory:nodes/node/vRouter-R1/yang-ext:mount/vyatta-policy:policy/vyatta-policy-qos:qos/"+policyName);
			
			HttpURLConnection connection = (HttpURLConnection)oracle.openConnection();
			
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type","application/json");
			connection.setRequestProperty("Accept","*/*");
			//String payload= "{'vyatta-policy-qos:qos':[{'tagnode':'TSD','shaper':{'bandwidth':'5000kbps','default':'TSD_default','class':[{'tagnode':12,'match':[{'tagnode':'VIDEO_SRC','police':{'then':{'mark':{'dscp':'23'}},'bandwidth':'2048kbps'},'protocol':'tcp','source':{'port':'32400'}}],'profile':'TSD_profile'},{'tagnode':230,'match':[{'tagnode':'HTTPS_DST','destination':{'port':'443'},'protocol':'tcp','mark':{'dscp':'24'}}],'profile':'TSD_profile'},{'tagnode':250,'match':[{'tagnode':'FTP_DST_20','destination':{'port':'20'},'protocol':'tcp','mark':{'dscp':'56'}}],'profile':'TSD_profile'},{'tagnode':252,'match':[{'tagnode':'FTP_DST_21','destination':{'port':'21'},'protocol':'tcp','mark':{'dscp':'56'}}],'profile':'TSD_profile'},{'tagnode':20,'match':[{'tagnode':'AUDIO_DST','destination':{'port':'4040'},'protocol':'tcp','mark':{'dscp':'23'}}],'profile':'TSD_profile'},{'tagnode':220,'match':[{'tagnode':'HTTP_DST','destination':{'port':'80'},'protocol':'tcp','mark':{'dscp':'16'}}],'profile':'TSD_profile'},{'tagnode':22,'match':[{'tagnode':'AUDIO_SRC','police':{'then':{'mark':{'dscp':'23'}},'bandwidth':'2048kbps'},'protocol':'tcp','source':{'port':'4040'}}],'profile':'TSD_profile'},{'tagnode':240,'match':[{'tagnode':'SSH_DST_22','destination':{'port':'22'},'protocol':'tcp','mark':{'dscp':'48'}}],'profile':'TSD_profile'},{'tagnode':210,'match':[{'tagnode':'HTTPSS_DST','destination':{'port':'8000'},'protocol':'tcp','mark':{'dscp':'8'}}],'profile':'TSD_profile'},{'tagnode':10,'match':[{'tagnode':'VIDEO_DST','destination':{'port':'32400'},'protocol':'tcp','mark':{'dscp':'23'}}],'profile':'TSD_profile'}],'profile':[{'tagnode':'TSD_profile','map':{'dscp':[{'tagnode':'56','to':1},{'tagnode':'48','to':1},{'tagnode':'8','to':1},{'tagnode':'16','to':1},{'tagnode':'24','to':1},{'tagnode':'23','to':3}]},'queue':[{'tagnode':0,'traffic-class':3},{'tagnode':1,'traffic-class':2},{'tagnode':2,'traffic-class':1},{'tagnode':3,'traffic-class':0}],'traffic-class':[{'tagnode':0,'bandwidth':'2048kbps','description':'Video and Audio streaming'},{'tagnode':1,'bandwidth':'400kbps','description':'Application transaction data'},{'tagnode':2,'bandwidth':'300kbps','description':'FTP, HTTP, HTTPS, SSH'},{'tagnode':3,'bandwidth':'300kbps','description':'Default'}]},{'tagnode':'TSD_default','queue':[{'tagnode':0,'traffic-class':3}],'traffic-class':[{'tagnode':3,'bandwidth':'300kbps'}]}]}}]}";

			//String payload= "%7B%0A++%22vyatta-policy-qos=qos%22%3A+%5B&%7B%0A++++++%22tagnode%22=%22policy62%22%2C&%22shaper%22=%7B&%22default%22=%22p2_default%22%2C&%22profile%22=%5B&%7B%0A++++++++++++%22tagnode%22=%22p2_default%22%2C&%22bandwidth%22=%22120kbps%22";
			
			OutputStreamWriter osw =new OutputStreamWriter(connection.getOutputStream());
			osw.write(payload);
			osw.flush();
			osw.close();
			System.out.println(connection.getResponseCode());
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException p){
			p.printStackTrace();

		}
		
		
	}

}
