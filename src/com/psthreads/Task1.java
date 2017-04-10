package com.psthreads;

public class Task1 {
	
	
	public static void main(String[] args) {
		
		Thread th = new Thread(new HelloWorldWorker());
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Good Bye World!");
	}
}
class HelloWorldWorker implements Runnable {

	@Override
	public void run() {
		System.out.println("Hello World!!");
		
	}
	
}