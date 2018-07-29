package controller;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingUtilities;

import userInterface.Gui;


public class ResultMaker {
	private MainController controller;
	private String outputFilePath;

	
	public ResultMaker() {
		controller = new MainController();
	}
	
	
	public void createOutputFile() {
		outputFilePath = controller.getOutputFilePath().getAbsolutePath()+File.separator+"output.txt";
		File directory =new File(outputFilePath);
		    if (directory.exists() && directory.isFile())
		    {
		        System.out.println("The dir with name could not be" +
		        " created as it is a normal file");
		    }
		    else
		    {
		        try
		        {
		            if (!directory.exists())
		                directory.mkdir();
		        }
		        catch (Exception e)
		        {
		            System.out.println("prompt for error");
		        }
		    }
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
