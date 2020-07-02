package com.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//  This class extends thread so it has all features of threads in java

public class ServerThread extends Thread{

	Socket socket;
	int threadID = 0;
	public static InputStream inputStream;
	public static DataInputStream dataInputStream;
	public static OutputStream outputStream;
	public static DataOutputStream dataOutputStream;
	
	// Threads here are simultaneously creating threads, so the input stream output stream of the client are set

	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public void ServerThread() {
		setSocket(new Socket());
	}
	public static InputStream getInputStream() {
		return inputStream;
	}
	public static void setInputStream(InputStream inputStream) {
		ServerThread.inputStream = inputStream;
	}
	public static DataInputStream getDataInputStream() {
		return dataInputStream;
	}
	public static void setDataInputStream(DataInputStream dataInputStream) {
		ServerThread.dataInputStream = dataInputStream;
	}
	public static OutputStream getOutputStream() {
		return outputStream;
	}
	public static void setOutputStream(OutputStream outputStream) {
		ServerThread.outputStream = outputStream;
	}
	public static DataOutputStream getDataOutputStream() {
		return dataOutputStream;
	}
	public static void setDataOutputStream(DataOutputStream dataOutputStream) {
		ServerThread.dataOutputStream = dataOutputStream;
	}
	public int getThreadID() {
		return threadID;
	}
	// unique id for each thread 
	public void setThreadID(int threadID) {
		this.threadID = threadID;
	}
	public void run() {
		
		try {
			// for each server thread created get the socket's input and output streams and create the data input, output stream for communication
			setInputStream(getSocket().getInputStream());
			setDataInputStream(new DataInputStream(getInputStream()));
			setOutputStream(getSocket().getOutputStream());
			setDataOutputStream(new DataOutputStream(getOutputStream()));
			// Send the thread id, number of clients, data array size to each client
			getDataOutputStream().writeUTF(getThreadID()+" "+Server.numOfClients+" "+Server.dataArraySize);
			// read data from the client
			getDataInputStream().readUTF();
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
