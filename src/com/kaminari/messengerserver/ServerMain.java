package com.kaminari.messengerserver;

import javax.swing.JFrame;

public class ServerMain {

	public static void main(String[] args) {
		Server server = new Server();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.startRunning();
	}

}
