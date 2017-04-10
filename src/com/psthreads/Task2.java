package com.psthreads;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

public class Task2 extends JFrame implements Provider, ActionListener {

	private final JTextArea console = new JTextArea();
	private final JScrollPane scrollPane = new JScrollPane(console);
	private final Dimension windowSize = new Dimension(400, 600);
	private final JButton clearConsole = new JButton("Clear");
	private final JButton runThread = new JButton("Run");
	private final JButton stopThread = new JButton("Stop");
	private final JPanel bottomControlPane = new JPanel();
	private final NumberFormat format = NumberFormat.getInstance();
	private final NumberFormatter formatter = new NumberFormatter(format);
	private final JFormattedTextField field = new JFormattedTextField(formatter);
	//
	private List<Thread> messages = new ArrayList<>();

	public Task2() {
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
		bottomUI();
		this.add(bottomControlPane, BorderLayout.SOUTH);
		//
		this.pack();
		this.setVisible(true);
	}

	private void bottomUI() {
		bottomControlPane.setLayout(new BoxLayout(bottomControlPane, BoxLayout.LINE_AXIS));
		bottomControlPane.add(runThread);
		bottomControlPane.add(stopThread);
		bottomControlPane.add(clearConsole);
		bottomControlPane.add(field);
		//
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);
		//
		console.setAutoscrolls(true);
		//
		clearConsole.addActionListener(this);
		runThread.addActionListener(this);
		stopThread.addActionListener(this);
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
			while (true) {
				for (int i = 0; i < 10; i++)
					this.provider.printOut(message + " " + i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public synchronized void printOut(String message) {
		console.append(message + "\n");
	}

	public static void main(String[] args) {
		new Task2();
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
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(clearConsole))
			console.setText("");
		if (e.getSource().equals(runThread)) {
			Integer val = (Integer) field.getValue();
			printOut("Trying to start: " + val);
			try {
				messages.get(val).start();
				printOut("Started: " + val);
			} catch (IllegalThreadStateException ex) {
				ex.printStackTrace();
				retryThread(val);
				printOut("Failture to start: " + val + " " + ex.getMessage());
			}
		}
		if (e.getSource().equals(stopThread)) {
			Integer val = (Integer) field.getValue();
			printOut("Trying to stop: " + val);
			try {
				messages.get(val).stop();
				printOut("Stopped: " + val);
			} catch (ThreadDeath ex) {
				printOut("Failture to stop: " + val + " " + ex.getMessage());
			}
		}
	}

}

interface Provider {
	public void printOut(String message);
}