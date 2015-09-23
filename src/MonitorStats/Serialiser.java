package MonitorStats;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;



public class Serialiser {

	public static void serialise(ArrayList<Datagram> allTrafficClasses) {

		try {
			FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Public\\Documents\\data.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(allTrafficClasses);
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved");
		} catch (IOException i) {
			i.printStackTrace();
		}

	}

	public static void deserialise(ArrayList<Datagram> allTrafficClasses) {
		try {
			FileInputStream fileIn = new FileInputStream("C:\\Users\\Public\\Documents\\data.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			
			//out.close();
			//fileOut.close();
			System.out.printf("Serialized data is saved");
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

}
