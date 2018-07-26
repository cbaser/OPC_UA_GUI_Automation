package Controller;


import javax.swing.SwingUtilities;

import UserInterface.GUI;


public class ResultMaker {
//	private JTextArea textArea;
//	
//	public void setTextArea(JTextArea textArea) {
//		this.textArea = textArea;
//	}
	
	public ResultMaker() {
		
	}
	public void writeToFile() {
		
	}
	
	public void showResults(String line) {
		new Thread() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                    GUI.textArea.append(line);
	                }
				});
			}
		}.start();
	}
//	public void elapsedTime(String time) {
//		new Thread() {
//			public void run() {
//				SwingUtilities.invokeLater(new Runnable() {
//	                public void run() {
//	                    GUI.resultTimelbl.setText(time);
//	                }
//				});
//			}
//		}.start();
//		
//	}

}
