package Server;

import java.net.*;

public class broadcastIP {

	public static void startBroadcasting(int delayInEachPulse) {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);

			// Get the server's IP address
			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			byte[] sendData = ipAddress.getBytes();

			// Broadcast the server's IP address
			DatagramPacket packet = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
			while (true) {
				socket.send(packet);
				Thread.sleep(delayInEachPulse);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("IP broadcasting was shut down due to unknown error");
		}
	}
}