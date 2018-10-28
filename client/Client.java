package client;

import java.io.*;
import java.net.*;
import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.*;

public class Client extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String messege = "";
	private String serverIP;
	private Socket connection;

	public Client(String host) {
		super("client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				sendMessege(event.getActionCommand());
				userText.setText("");

			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300, 150);
		setVisible(true);
	}

	// connect to server
	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChating();
		} catch (EOFException eofException) {
			showMessege("\nclient terminated connection\n");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			closeCrap();
		}
	}

	// connectto server
	private void connectToServer() throws IOException {
		showMessege("\nattempting connection\n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessege("\nconnected to " + connection.getInetAddress().getHostName());
	}
 
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessege("streams good to go!\n");

	}

	private void whileChating() throws IOException {
		ableToType(true);
		do {
			try {
				messege = (String) input.readObject();
				showMessege("\n" + messege);
			} catch (ClassNotFoundException classNotFoundException) {
				showMessege("dont know object type\n");
			}
		} while (!messege.equals("SERVER - END"));
	}

	// close everything
	private void closeCrap(){
		showMessege("closing the connection, something went wrong\n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// sendData
	private void sendMessege(String messege){
		try {
			output.writeObject("CLIENT - " + messege);
			output.flush();
			showMessege("\nCLIENT - " + messege);
		} catch (IOException ioException) {
			chatWindow.append("\nsomething messed up");
		}
	}

	private void showMessege(final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				chatWindow.append(text);

			}
		});
	}

	private void ableToType(final boolean b) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				// if can write to text area
				userText.setEditable(b);
			}
		});
	}
}
