package MonitorStats;

import org.json.JSONObject;

public class ParseJSON {
	
	public static void main(String args[]){
		String str = "{ \"name\": \"Alice\", \"age\": 20 }";
		JSONObject obj = new JSONObject(str);
		String n = obj.getString("name");
		int a = obj.getInt("age");
		System.out.println(n + " " + a);  // prints "Alice 20"
	}

}
