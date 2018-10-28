package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
public class ClientTest {
	public static void main(String[] args) throws IOException {

		String ip= null;
		try(final DatagramSocket socket = new DatagramSocket()){
			  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			  ip = socket.getLocalAddress().getHostAddress();
			}
	Client theClient = new Client(ip);
	theClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	theClient.startRunning();
	}

}