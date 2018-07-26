package Controller;


import java.io.File;
import java.io.FileWriter;

import javax.swing.SwingUtilities;

import UserInterface.GUI;


public class ResultMaker {
	private MainController controller;
	private String outputFilePath;

	
	public ResultMaker() {
		controller = new MainController();
		outputFilePath = controller.getOutputFilePath().getAbsolutePath()+File.separator+"output.txt";
		File file =new File(outputFilePath);
		file.mkdirs();
		
	}
	public void writeToFile(String line) {
		
		try {
			FileWriter writer = new FileWriter(outputFilePath);
			writer.write(line);
			writer.close();
	}catch(Exception e) {
		e.printStackTrace();
	}
		
		
		}
	
	public void showResults(String line) {
		controller.appendToTextArea(line);
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
