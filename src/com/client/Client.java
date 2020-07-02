package com.client;

public class Client {

	public static void main(String args[]){
		// initialize the clients to connect to server
		Thread client = new Thread(new ClientThread());
		client.start();
	}
}
