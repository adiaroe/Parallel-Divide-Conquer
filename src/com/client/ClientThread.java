package com.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import com.sort.Mergesort;
// client connects to the server using IP address and port number

public class ClientThread extends Thread {

	public static int PORT = 5555;
	static Socket clientSocket;
	static int threadID = 0;
	static int numOfClients = 0;
	static int dataArraySize = 0;
	String fileName = "";
	
	static InputStream inputStream;
    static DataInputStream dataInputStream;
    static OutputStream outputStream;
    static DataOutputStream dataOutputStream;
    
	public static Socket getClientSocket() {
		return clientSocket;
	}
	public static void setClientSocket(Socket clientSocket) {
		ClientThread.clientSocket = clientSocket;
	}
	public static InputStream getInputStream() {
		return inputStream;
	}
	public static void setInputStream(InputStream inputStream) {
		ClientThread.inputStream = inputStream;
	}
	public static DataInputStream getDataInputStream() {
		return dataInputStream;
	}
	public static void setDataInputStream(DataInputStream dataInputStream) {
		ClientThread.dataInputStream = dataInputStream;
	}
	public static OutputStream getOutputStream() {
		return outputStream;
	}
	public static void setOutputStream(OutputStream outputStream) {
		ClientThread.outputStream = outputStream;
	}
	public static DataOutputStream getDataOutputStream() {
		return dataOutputStream;
	}
	public static void setDataOutputStream(DataOutputStream dataOutputStream) {
		ClientThread.dataOutputStream = dataOutputStream;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public ClientThread(){
		try {
			// initialize the socket with the server's host address and port number 
			setClientSocket(new Socket(InetAddress.getLocalHost().getHostAddress(), PORT));
			System.out.println("Connected with Server!!");
			// set the input output stream of the client's socket
			setInputStream(getClientSocket().getInputStream());
			setDataInputStream(new DataInputStream(getInputStream()));
			setOutputStream(getClientSocket().getOutputStream());
			setDataOutputStream(new DataOutputStream(getOutputStream()));
			
		} catch (IOException e) {
			System.out.println("IO Exception while creating socket for the client!");
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		try {
			// The client performs the sorting
			FileInputStream textFile = null;
			int countOfData = 0;
			Mergesort mergeSortObj = new Mergesort();
			// read the server's message to receive the thread's id, number of clients and data array size to perform the sorting functions
			String dataFromServer = getDataInputStream().readUTF();
			
			
			String splitArray[] = dataFromServer.split(" ");
			threadID = Integer.parseInt(splitArray[0]);
			numOfClients = Integer.parseInt(splitArray[1]);
			dataArraySize = Integer.parseInt(splitArray[2]);
			
			setFileName("file_"+Integer.toString(threadID-1));
			int[] dataArray = null;
			// take the data from the divided files and put it into arrays 
			textFile = new FileInputStream("src/"+getFileName()+".txt");
			Scanner inFile = new Scanner(textFile);
			dataArray = new int[dataArraySize/numOfClients];
			while(inFile.hasNextLine()){
				String data = inFile.nextLine();
				if((!data.isEmpty()) && (data != " ")){
					dataArray[countOfData] = Integer.parseInt(data);
					countOfData++;
				}
			}
			inFile.close();
			
			// perform the merge sort on divided sub arrays
			
			
			long startTime14 = System.nanoTime();
			dataArray = mergeSortObj.mergesort(dataArray);
			long startTime24 = System.nanoTime();
			System.out.println("Merge Sort time @ Child:"+(double)(startTime24-startTime14)/1000000000.0);
			System.out.println("Data sorted!");
			
			// after sorting the data send the array to write the data to files and send a confirmation message to the server when sorting is complete 
			
			deployDataToFiles(dataArray);
			getDataOutputStream().writeUTF("done");
			
			
			
		} catch (IOException e) {
			System.out.println("IO Exception while reading socket from the server!");
			e.printStackTrace();
		}
	}
	
	private static String deployDataToFiles(int[] dataArray) throws IOException {
		// writing data to a new file
		PrintWriter writer = null; 
		String fileName = "";
		File newFile = null;
		int divider = dataArraySize/numOfClients;
		
		fileName = "sorted_file_"+Integer.toString(threadID-1);
		newFile = new File("src/"+fileName+".txt");
		if(!newFile.exists()){
			newFile.createNewFile();
		}
		writer = new PrintWriter("src/"+fileName+".txt", "UTF-8");
		for(int j = 0 ; j < divider ; j++)
           writer.println(dataArray[j]);
		writer.close();

		return "src/"+fileName+".txt";
		
	}
}
