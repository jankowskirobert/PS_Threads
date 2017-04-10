package com.psthreads;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.NumberFormatter;

public class Task3 extends JFrame implements Provider {

	private final JTextArea console = new JTextArea();
	private final JScrollPane scrollPane = new JScrollPane(console);
	private final Dimension windowSize = new Dimension(400, 600);
	//
	private List<Thread> messages = new ArrayList<>();

	public Task3() {
		initUI();
		setUpThreads();
	}

	private void setUpThreads() {
		for (int i = 0; i < 10; i++) {
			Message message = new Message("Out[" + i + "]", this);
			Thread th = new Thread(message);
			messages.add(th);
		}
	}

	private void initUI() {
		this.setLayout(new BorderLayout());
		this.setMinimumSize(windowSize);
		this.setPreferredSize(windowSize);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//
		this.add(scrollPane, BorderLayout.CENTER);
		//
		this.pack();
		this.setVisible(true);
	}

	private class Message implements Runnable {

		private final String message;
		private final Provider provider;

		Message(final String message, final Provider provider) {
			this.message = message;
			this.provider = provider;
		}

		@Override
		public void run() {
			// while (true) {
			synchronized (provider) {

				provider.clean();
				for (int i = 0; i < 10; i++)
					this.provider.printOut(message + " " + i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// }
		}
	}

	public void runAll() {
		messages.forEach(x -> x.start());
	}

	@Override
	public synchronized void printOut(String message) {
		console.append(message + "\n");
	}

	public static void main(String[] args) {
		Task3 t = new Task3();
		t.runAll();
	}

	private void retryThread(int pos) {
		try {
			messages.set(pos, new Thread(new Message("Out[" + pos + "]", this)));
			messages.get(pos).start();
			printOut("Started[restarted]: " + pos);
		} catch (IllegalThreadStateException e) {
			e.printStackTrace();
			printOut("Failture to start[retry]: " + pos + " " + e.getMessage());
		}
	}

	@Override
	public void clean() {
		console.setText("");

	}

}
