package com.kaminari.messengerserver;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Server extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server() {
		super("Messenger");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		
		setSize(800, 600);
		setVisible(true);
	}
	
	//setup and run the server
	public void startRunning(){
		try{
			server = new ServerSocket(6789, 100);
			try{
				waitForConnection();
				setupStreams();
				whileChatting();
			}catch(EOFException eofException){
				showMessage("\n Server ended the connection!!!");
			}finally{
				close();
			}
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	//wait for a connection to be made
	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect... \n");
		connection = server.accept();
		showMessage("Connected to : " + connection.getInetAddress().getHostName());
	}
	
	//get the stream to send and recieve data
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup! \n");
	}
	
	//while you are chatting
	private void whileChatting() throws IOException{
		String message = "You are now Connected!";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n idk wtf the user sent!!!");
			}
		}while(!message.equals("CLIENT - END"));
	}
	
	//close all connections after chatting
	private void close(){
		showMessage("\n Closing connections... \n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//sends a message to the client
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\n SERVER - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n ERROR : wtf did you type dude!!!");
		}
	}
	
	//updates chat window
	private void showMessage(final String text){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				chatWindow.append(text);
			}
		});
	}
	
	//gives the user permission to type
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				userText.setEditable(tof);
			}
		});
	}

}
