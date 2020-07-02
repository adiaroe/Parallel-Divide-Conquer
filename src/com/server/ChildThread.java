package com.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
// Additional threads of this type are children of server and support parallelism for merging data 
public class ChildThread extends Thread{

	Socket socket;
	int childID;
	int carryForwardID;
	int N;
	public int getChildID() {
		return childID;
	}
	public void setChildID(int childID) {
		this.childID = childID;
	}
	public int getCarryForwardID() {
		return carryForwardID;
	}
	public void setCarryForwardID(int carryForwardID) {
		this.carryForwardID = carryForwardID;
	}
	public int getN() {
		return N;
	}
	public void setN(int n) {
		N = n;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public void run(){
		
		FileInputStream textFile1 = null;
		FileInputStream textFile2 = null;
		Scanner inFile1 = null;
		Scanner inFile2 = null;
		setSocket(Server.serverSendThreadArray[0].getSocket());
		// if the leaf nodes are reached, the sorted data received from the clients is written from files to arrays
		if(getCarryForwardID() == 0){
			
			try {
				// run the child threads
				for(int i=0; i<2; i++) {
					Server.serverSendThreadArray[getChildID()-1+i].setThreadID(getChildID()+i);
					Server.serverSendThreadArray[getChildID()-1+i].start();
					Server.serverSendThreadArray[getChildID()-1+i].join();
				}
				// get the sorted data from clients and put them into arrays
				
				textFile1 = new FileInputStream("src/sorted_file_"+Integer.toString(getChildID()-1)+".txt");
				textFile2 = new FileInputStream("src/sorted_file_"+Integer.toString(getChildID())+".txt");
				inFile1 = new Scanner(textFile1);
				inFile2 = new Scanner(textFile2);
				int[] dataArray1 = new int[Server.dataArraySize/Server.numOfClients];
				int[] dataArray2 = new int[Server.dataArraySize/Server.numOfClients];
				int[] dataArray3 = new int[(Server.dataArraySize/Server.numOfClients)*2];
				int countOfData = 0;
				while(inFile1.hasNextLine()){
					String data = inFile1.nextLine();
					if((!data.isEmpty()) && (data != " ")){
						dataArray1[countOfData] = Integer.parseInt(data);
						countOfData++;
					}
				}
				countOfData = 0;
				while(inFile2.hasNextLine()){
					String data = inFile2.nextLine();
					if((!data.isEmpty()) && (data != " ")){
						dataArray2[countOfData] = Integer.parseInt(data);
						countOfData++;
					}
				}
				inFile1.close();
				inFile2.close();
				// merge the arrays received from sorted files
				long startTime12 = System.nanoTime();
				dataArray3 = merge(dataArray1,dataArray2);
				long startTime22 = System.nanoTime();
				System.out.println("Merge time @ Child Thread 1 :"+(double)(startTime22-startTime12)/1000000000.0);
				// write the sorted data back to a file
				PrintWriter writer = null; 
				File newFile = null;
				
				String fileName = "src/sorted_file_"+Integer.toString(getChildID()-1)+".txt";
				newFile = new File(fileName);
				if(!newFile.exists()){
					newFile.createNewFile();
				}
				writer = new PrintWriter(fileName, "UTF-8");
				for(int j = 0 ; j < dataArray3.length ; j++)
		           writer.println(dataArray3[j]);
				writer.close();
				
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// if the threads are not the leaf threads, continue the process of forming child threads
		}else{
			
			ChildThread[] cdArray = new ChildThread[2];
			
			try {
				
				for(int i = 0; i<2; i++) {
					
					cdArray[i] = new ChildThread();
					cdArray[i].setCarryForwardID((Server.numOfClients/4)-1);
					cdArray[i].setN(getN()/2);
					cdArray[i].setChildID(getChildID() + i*getN()/2);
					
					cdArray[i].start();
					cdArray[i].join();
				}
				// created child threads are run
				// if the threads are leaf threads, then the get the data from sorted files and put them to arrays
				textFile1 = new FileInputStream("src/sorted_file_"+Integer.toString(getChildID()));
				textFile2 = new FileInputStream("src/sorted_file_"+Integer.toString(getChildID()+1));
				inFile1 = new Scanner(textFile1);
				inFile2 = new Scanner(textFile2);
				int[] dataArray1 = new int[(Server.dataArraySize/Server.numOfClients)*(2*getCarryForwardID())];
				int[] dataArray2 = new int[(Server.dataArraySize/Server.numOfClients)*(2*getCarryForwardID())];
				int[] dataArray3 = new int[(Server.dataArraySize/Server.numOfClients)*(2*2*getCarryForwardID())];
				int countOfData = 0;
				while(inFile1.hasNextLine()){
					String data = inFile1.nextLine();
					if((!data.isEmpty()) && (data != " ")){
						dataArray1[countOfData] = Integer.parseInt(data);
						countOfData++;
					}
				}
				countOfData = 0;
				while(inFile2.hasNextLine()){
					String data = inFile2.nextLine();
					if((!data.isEmpty()) && (data != " ")){
						dataArray2[countOfData] = Integer.parseInt(data);
						countOfData++;
					}
				}
				inFile1.close();
				inFile2.close();
				
				// merge the arrays to receive final sorted array
				
				long startTime13 = System.nanoTime();
				dataArray3 = merge(dataArray1,dataArray2);
				long startTime23 = System.nanoTime();
				System.out.println("Merge time @ Child Thread 2:"+(double)(startTime23-startTime13)/1000000000.0);
				
				PrintWriter writer = null; 
				File newFile = null;
				
				// write data from the sorted data array to file
				
				String fileName = "src/sorted_file_"+Integer.toString(getChildID());
				newFile = new File(fileName);
				if(!newFile.exists()){
					newFile.createNewFile();
				}
				writer = new PrintWriter(fileName, "UTF-8");
				for(int j = 0 ; j < dataArray3.length ; j++)
		           writer.println(dataArray3[j]); 
				writer.close();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private int[] merge(int[] integers, int[] integers2) {
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
