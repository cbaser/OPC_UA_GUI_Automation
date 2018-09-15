package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.SwingUtilities;

import userInterface.Gui;

public class ResultMaker {
	private String outputFilePath;
	private boolean deployableOrNone;
	private BufferedWriter buffwriter;
	private FileWriter writer;

	public ResultMaker() {
		
	}

	public void setTextArea(boolean deployableOrNone) {
		this.deployableOrNone = deployableOrNone;
	}
	public void setOutputPath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public void createOutputFile() {
		try {
			File file = new File(outputFilePath+File.separator
					+ "opc_ua_automated_test_tool_output.txt");
			
			if (file.exists() && file.isFile()) {
				file.delete();
				file.createNewFile();
			} else {
				try {
					if (!file.exists()) {
						file.createNewFile();
					}
						
				} catch (Exception e) {
					System.out.println("prompt for error");
				}
			}
			writer = new FileWriter(outputFilePath+File.separator+ "opc_ua_automated_test_tool_output.txt");
			buffwriter = new BufferedWriter(writer);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void writeToFile(String line) {
		
		try {		
			buffwriter.write(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void closeStream() {
		try{
		      if(buffwriter!=null)
		    	  buffwriter.close();
		 	   }catch(Exception ex){
		 	       System.out.println("Error in closing the BufferedWriter"+ex);
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
