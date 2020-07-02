/*
 * Title : MergeSort, MultiThreaded Merge sort 
 * Date : 5/5/2018
 * Availability: https://algs4.cs.princeton.edu/14analysis/Mergesort.java,https://stackoverflow.com/questions/3466242/multithreaded-merge-sort
 **/
package com.test;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class MergeSortThread {


	private static int[] merge(int[] integers, int[] integers2) {
		int[] c = new int [integers.length + integers2.length];
		int i = 0, j = 0;
		for (int k = 0; k < c.length; k++) {
			if      (i >= integers.length) c[k] = integers2[j++];
			else if (j >= integers2.length) c[k] = integers[i++];
			else if (integers[i] <= integers2[j])  c[k] = integers[i++];
			else                    c[k] = integers2[j++];
		}
		return c;
	}

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		// Enter the size of array you want to generate
		Scanner scanner= new Scanner(System.in);
		System.out.println("Enter the size of the array for which you want to see comparison: ");
		int size= scanner.nextInt();
		//Random number generator to form large size arrays
		Random rand = new Random();
		int[] original = new int[size];
		for (int i=0; i<original.length; i++) {
			original[i] = rand.nextInt(1000);
		}
		// auxiliary arrays for merging and sorting
		int [] regularMergeSort;
		int [] parallelMerge;
		int [] parallelMerge1;
		int [] parallelMerge2;
		int [] parallelMerge3;
	
		
		
		// record performance of 2 threads 
		
		long start2Thread= System.currentTimeMillis();
		
		// Divide the array into 2 halves for 2 threads
		int [] subA1= new int [original.length/2];
		int [] subA2= new int [original.length/2];
		
		System.arraycopy(original, 0, subA1, 0, original.length/2);
		System.arraycopy(original, original.length/2, subA2, 0, original.length/2);
		// Create threads to perform mergeSort on divided arrays parallely
		
		DivideThreads thread1= new DivideThreads(subA1);
		DivideThreads thread2= new DivideThreads(subA2);
		thread1.start();
		thread2.start();

		thread1.join();
		thread2.join();
		parallelMerge=merge(thread1.getInternal(), thread2.getInternal());
		long stop2Thread= System.currentTimeMillis();
		
		
		// record performance for 4 threads
		long startTime = System.currentTimeMillis();
		
		// Divide array into 4 sub arrays
		int[] subArray1 = new int[original.length/4];
		int[] subArray2 = new int[original.length/4];
		int[] subArray3 = new int[original.length/4];
		int[] subArray4 = new int[original.length/4];

		System.arraycopy(original, 0, subArray1, 0, original.length/4);
		System.arraycopy(original, original.length/4, subArray2, 0, original.length/4);
		System.arraycopy(original, original.length/2, subArray3, 0, original.length/4);
		System.arraycopy(original, (3*original.length)/4, subArray4, 0, original.length/4);
		// Create the threads for parallel merge sort
		
		DivideThreads thread3 = new DivideThreads(subArray1);
		DivideThreads thread4 = new DivideThreads(subArray2);
		DivideThreads thread5 = new DivideThreads(subArray3);
		DivideThreads thread6 = new DivideThreads(subArray4);

		thread3.start();
		thread4.start();
		thread5.start();
		thread6.start();

		thread3.join();
		thread4.join();
		thread5.join();
		thread6.join();

		parallelMerge1=merge(thread3.getInternal(), thread4.getInternal());
		parallelMerge2=merge(thread5.getInternal(), thread6.getInternal());
		parallelMerge3= merge(parallelMerge1, parallelMerge2);

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		//  Record the performance for regular merge sort
		long regular= System.currentTimeMillis();
		RegularMerge regularMerge= new RegularMerge();
		regularMergeSort=regularMerge.mergesort(original);
		long regularStop= System.currentTimeMillis();
		long total= regularStop-regular;
		long total2Thread= stop2Thread-start2Thread;
		// Print times for different merge sort sub types
		System.out.println("regular mergesort takes: "+ (float)total/1000 + " seconds");
		System.out.println("2-thread MergeSort takes: " + (float)total2Thread/1000 + " seconds");
		System.out.println("4-thread MergeSort takes: " + (float)elapsedTime/1000 + " seconds");

	}

}

class DivideThreads extends Thread {
	private int[] internal;

	public int[] getInternal() {
		return internal;
	}

	public DivideThreads(int[] incoming) {
		internal = incoming;
	}


	private static int[] merge(int[] integers, int[] integers2) {
		int[] c = new int [integers.length + integers2.length];
		int i = 0, j = 0;
		for (int k = 0; k < c.length; k++) {
			if      (i >= integers.length) c[k] = integers2[j++];
			else if (j >= integers2.length) c[k] = integers[i++];
			else if (integers[i] <= integers2[j])  c[k] = integers[i++];
			else                    c[k] = integers2[j++];
		}

		return c;
	}

	public static int[] mergesort(int[] input) {
		int N = input.length;
		if (N <= 1) return input;
		int[] a = new int[N/2];
		int[] b = new int[N - N/2];
		for (int i = 0; i < a.length; i++)
			a[i] = input[i];
		for (int i = 0; i < b.length; i++)
			b[i] = input[i + N/2];
		return merge(mergesort(a), mergesort(b));
	}
	


	public void run() {
		internal=mergesort(internal);
	}
}


