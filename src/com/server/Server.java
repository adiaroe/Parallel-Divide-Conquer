package com.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Random;
import java.util.Scanner;

public class Server {
	
	static final int PORT = 5555;
	static int numOfClients = 0;
	static ServerSocket serverSocket;
	static ServerThread serverSendThreadArray [];
	static int dataArray [];
	static int dataArraySize = 0;
	
	// start the server class
	public Server(){
		try {
			// initialize the server socket with the specified port
			setServerSocket(new ServerSocket(PORT));
		} catch (IOException e) {
			System.out.println("IO Exception while creating a server");
			e.printStackTrace();
		}
	}
	
	public static void main(String arg[]){
		
		Server server = new Server();
		System.out.println("Server is up and waiting for clients...");
		// start the server
		
		String data = "";
		int countOfData = 0;
		PrintWriter writer = null; 
		String fileName = "";
		File newFile = null;
		Scanner scanner = new Scanner(System.in);
		// Get the number of clients required to run the mergesort parallely
		System.out.println("Enter the number of clients you want to add:");
		numOfClients = scanner.nextInt();
		// Enter the size of the array whose data you want to sort
		System.out.println("Enter the size of the array you want to sort :");
		dataArraySize = scanner.nextInt();
		
		
		try {
			// Initialize a random array of the size entered by the user
			
			Random rand = new Random();
			dataArray=new int[dataArraySize];
			for(int i=0; i<dataArray.length; i++) {
				dataArray[i] = rand.nextInt(1000);
			}
			// Write the data from the data array to files.
			// The number of files created are equal to the number of clients entered 
			
			for(int i=0; i<numOfClients; i++){
				
				int divider = dataArraySize/numOfClients;
				
				fileName = "file_"+Integer.toString(i);
				newFile = new File("src/"+fileName+".txt");
				if(!newFile.exists()){
					newFile.createNewFile();
				}
				writer = new PrintWriter("src/"+fileName+".txt", "UTF-8");
				for(int j = i*divider ; j < ((i+1)*divider) ; j++)
		           writer.println(dataArray[j]);
				writer.close();
			}
			
			// Create the clients entered by the user using threads
			// Server thread array is an array of threads
			// Each new thread accepts the socket connections to form clients
			serverSendThreadArray = new ServerThread[numOfClients];
			for(int i=0; i<numOfClients; i++){
				serverSendThreadArray[i] = new ServerThread();
				serverSendThreadArray[i].setSocket(getServerSocket().accept());
			}
			// Child threads are created from the server to achieve parallelism while merging the data back
			// The parent server is divided as threads so that a tree type structure is maintained
			// It is divided into threads until it reaches one stage before the leaf node and connects to the thread which are clients
			
			ChildThread[] cdArray = new ChildThread[2];
			
			for(int i = 0; i<2; i++) {
				
				cdArray[i] = new ChildThread();
				cdArray[i].setCarryForwardID((numOfClients/4)-1);
				if((numOfClients/4)-1 <= 1) {
					cdArray[i].setN(0);
				}
				else {
					cdArray[i].setN(numOfClients/2);
				}
				cdArray[i].setChildID(1+i*(numOfClients/2));
				
				cdArray[i].start();
				cdArray[i].join();
			}
			
			// Take the data from the sorted files received from the clients and write the data to arrays to merge it into single array
			FileInputStream textFile1 = null;
			FileInputStream textFile2 = null;
			Scanner inFile1 = null;
			Scanner inFile2 = null;
			
			textFile1 = new FileInputStream("src/sorted_file_"+Integer.toString(0)+".txt");
			textFile2 = new FileInputStream("src/sorted_file_"+Integer.toString((numOfClients/2))+".txt");
			inFile1 = new Scanner(textFile1);
			inFile2 = new Scanner(textFile2);
			int[] dataArray1 = new int[Server.dataArraySize/2];
			int[] dataArray2 = new int[Server.dataArraySize/2];
			int[] dataArray3 = new int[Server.dataArraySize];
			countOfData = 0;
			while(inFile1.hasNextLine()){
				data = inFile1.nextLine();
				if((!data.isEmpty()) && (data != " ")){
					dataArray1[countOfData] = Integer.parseInt(data);
					countOfData++;
				}
			}
			countOfData = 0;
			while(inFile2.hasNextLine()){
				data = inFile2.nextLine();
				if((!data.isEmpty()) && (data != " ")){
					dataArray2[countOfData] = Integer.parseInt(data);
					countOfData++;
				}
			}
			inFile1.close();
			inFile2.close();
			
			// merge the sorted data
			long startTime11 = System.nanoTime();
			dataArray3 = merge(dataArray1,dataArray2);
			long startTime21 = System.nanoTime();
			System.out.println("Merge time @ Server 1:"+(double)(startTime21-startTime11)/1000000000.0);
			
			writer = null; 
			newFile = null;
			// write the sorted data back to a file
			
			fileName = "src/sorted_file_"+Integer.toString(0)+".txt";
			newFile = new File(fileName);
			if(!newFile.exists()){
				newFile.createNewFile();
			}
			writer = new PrintWriter(fileName, "UTF-8");
			for(int j = 0 ; j < dataArray3.length ; j++)
	           writer.println(dataArray3[j]);
			writer.close();
			
		} catch (IOException e) {
			System.out.println("IO Exception while accepting a client");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ServerSocket getServerSocket() {
		return serverSocket;
	}
	// set the server socket according to socket accepted at port

	public static void setServerSocket(ServerSocket serverSocket) {
		Server.serverSocket = serverSocket;
	}
	// method for merging the data
	private static int[] merge(int[] integers, int[] integers2) {
		int[] c = new int[integers.length + integers2.length];
        int i = 0, j = 0;
        for (int k = 0; k < c.length; k++) {
            if      (i >= integers.length) c[k] = integers2[j++];
            else if (j >= integers2.length) c[k] = integers[i++];
            else if (integers[i] <= integers2[j])  c[k] = integers[i++];
            else                    c[k] = integers2[j++];
        }
        return c;
    }

}
