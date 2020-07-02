/******************************************************************************
 *  Compilation:  javac Mergesort.java
 *  Execution:    java Mergesort N
 *  Dependencies: StdOut.java StdRandom.java
 *  
 *  Generate N pseudo-random numbers between 0 and 1 and mergesort them.
 *
 ******************************************************************************/

 package com.sort;

import java.util.Random;
import java.util.Scanner;

public class Mergesort {

    private static int[] merge(int[] a, int[] b) {
        int[] c = new int[a.length + b.length];
        int i = 0, j = 0;
        for (int k = 0; k < c.length; k++) {
            if      (i >= a.length) c[k] = b[j++];
            else if (j >= b.length) c[k] = a[i++];
            else if (a[i] <= b[j])  c[k] = a[i++];
            else                    c[k] = b[j++];
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


    // generate N real numbers between 0 and 1, and mergesort them
    public static void main(String[] args) {
    	System.out.println("Enter the size of data that you want to sort: ");
    	Scanner scanner= new Scanner(System.in);
    	int size=scanner.nextInt();
    	
    	// Create a random array of size entered by the user 
    	Random rand = new Random();
		int[] dataArray = new int[size];
		for(int i=0; i<dataArray.length; i++) {
			dataArray[i] = rand.nextInt(1000);
		}
		
		// Analyze the time complexity of regular merge sort
		long startTime1 = System.nanoTime();
		dataArray = mergesort(dataArray);
		long startTime2 = System.nanoTime();
		System.out.println("Total time for regular merge sort: "+(float)(startTime2 - startTime1)/1000000000);


    }
}

