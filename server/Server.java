package server;

import java.io.*;
import java.net.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;

	public Server() {
		super("instant messenger");
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
		add(new JScrollPane(chatWindow));
		setSize(300, 150);
		setVisible(true);

	}

	public void startRunning() {
		try {
			server = new ServerSocket(6789, 1);
			while (true) {
				try {
					waitForConnection();
					setupStreams();
					whileChating();
				} catch (EOFException eofException) {
					showMessege("\nserver ended the connection! ");
				} finally {
					closeCrap();
				}

			}
		} catch (IOException ioException) {

		}
	}

	// wait for connection
	private void waitForConnection() throws IOException {
		showMessege("waiting for someone to connect....\n");
		connection = server.accept();
		showMessege("now connect to " + connection.getInetAddress().getHostName());
	}

	// get stream to send and recive data
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessege("\nstreams are now setup\n");
	}

	// during the conversation
	private void whileChating() throws IOException {
		String messege = "you are now connected";
		sendMessege(messege);
		ableToType(true);
		do {
			// have a conversation
			try {
				messege = (String) input.readObject();
				showMessege("\n" + messege);
			} catch (ClassNotFoundException classNotFoundException) {
				showMessege("\n idk what that user send!");
			}
		} while (!messege.equals("CLIENT - END"));
	}

	private void closeCrap() {
		showMessege("\nclosing connection...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void sendMessege(String messege) {
		try {
			output.writeObject("SERVER - " + messege);
			output.flush();
			showMessege("SERVER - " + messege);
		} catch (IOException ioException) {
			chatWindow.append("\n ERROR: CANT SEND MESSEGE");

		}
	}

	// update chatWindow
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
				//if can write to text area
				userText.setEditable(b);
			}
		});
	}
}
