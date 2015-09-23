package MonitorStats;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Datagram implements Serializable{

	LocalDateTime timestamp;
	String iface;
	int prio;
	long packets;
	long bytes;
	long tailDrop;
	long redDrop;
	double speed;

	public Datagram(LocalDateTime  timestamp, String iface, int prio, long packets, long bytes, long tailDrop, long redDrop) {
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

	public LocalDateTime  getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime   timestamp) {
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

	public String prettyPrint() {
		return(prio + "\t\t" + packets + "\t\t" + bytes + "\t\t" + tailDrop + "\t\t" + redDrop + "\t" + speed);
	}
	
	public String csvPrint() {
		return("\n"+prio + "," + packets + "," + bytes + "," + tailDrop + "," + redDrop + "," + speed);
	}

}