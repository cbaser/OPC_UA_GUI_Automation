package controller;


import java.io.File;
import java.io.FileWriter;
import javax.swing.SwingUtilities;

import userInterface.Gui;


public class ResultMaker {
	private MainController controller;
	private String outputFilePath;
	private boolean deployableOrNone;
	

	
	public ResultMaker() {
		controller = new MainController();
	}

	public void setTextArea(boolean deployableOrNone) {
		this.deployableOrNone = deployableOrNone;
	}
	
	
	public void createOutputFile() {
		outputFilePath = controller.getOutputFilePath().getAbsolutePath()+File.separator+"opc_ua_automated_test_tool_output.txt";
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
	
	
	
	public void appendToTextArea(String line) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (deployableOrNone)
					Gui.deployableTextArea.append(line);
				else
					Gui.nonDeployableTextArea.append(line);
			}
			
		});
	}

}
