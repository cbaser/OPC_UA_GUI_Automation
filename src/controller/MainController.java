package controller;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import configuration.ConfigurationMaker;
import deployment.DeployerMaker;
import merging.Merger;
import reporting.ReportingMaker;
import userInterface.Gui;

public class MainController {
	private File ansibleFilePath, dockerFilePath, makeFilePath, outputFilePath;

	private String username, password, connectionAddress,deviceName;

	private boolean connected = false, hasAnsibleFile = false, hasDockerFile = false, hasMakeFile = false,
			firstOrSecondOutputArea = false;
	private Session session;
	private String testingType,reportType;

	public MainController() {

	}
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public File getMakeFilePath() {
		return makeFilePath;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public void setMakeFilePath(File makeFilePath) {
		this.makeFilePath = makeFilePath;
	}

	public String getTestingType() {
		return testingType;
	}

	public File getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(File outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public void setTestingType(String testingType) {
		this.testingType = testingType;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public File getAnsibleFilePath() {
		return ansibleFilePath;
	}

	public void setAnsibleFilePath(File ansibleFilePath) {
		this.ansibleFilePath = ansibleFilePath;
	}

	public File getDockerFilePath() {
		return dockerFilePath;
	}

	public void setDockerFilePath(File dockerFilePath) {
		this.dockerFilePath = dockerFilePath;
	}

	public String getConnectionAddress() {
		return connectionAddress;
	}

	public void setConnectionAddress(String connectionAddress) {
		this.connectionAddress = connectionAddress;
	}

	public boolean controlFields() {
		if (username.isEmpty() || password.isEmpty() || connectionAddress.isEmpty())
			return false;
		else
			return true;
	}

	public void setOutputOrder(boolean firstOrSecondOutputArea) {
		this.firstOrSecondOutputArea = firstOrSecondOutputArea;
	}

	public boolean checkConnection() {
		JSch jsch = new JSch();
		try {
			// 192.168.178.59
			session = jsch.getSession(username, connectionAddress, 22);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			connected = session.isConnected();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return connected;

	}

	public boolean controlAnsibleFiles(File incomingFile) {
		hasAnsibleFile = checkFolder(incomingFile, ".yml");
		return hasAnsibleFile;
	}

	public boolean controlDockerFiles(File incomingFile) {

		// hasDockerFile = checkFolder(incomingFile,"Dockerfile");
		hasDockerFile = checkFolder(incomingFile, ".cpp");
		return hasDockerFile;
	}

	public boolean controlMakeFiles(File incomingFile) {
		hasMakeFile = checkFolder(incomingFile, "CMakeLists.txt");
		return hasMakeFile;
	}

	public boolean checkFolder(File file, String fileExtension) {
		if (file.isDirectory()) {
			File[] listOfFiles = file.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(fileExtension)) {
					return true;
				}
			}
		}

		return false;
	}
	public boolean checkFileExtension(File file, String fileExtension) {

				if (file.isFile() && file.getName().contains(fileExtension)) 
					return true;
		return false;
	}
	
	

	public void appendToTextArea(String line) {
//		new Thread() {
//			public void run() {
//				SwingUtilities.invokeLater(new Runnable() {
//					public void run() {
//						if (firstOrSecondOutputArea)
//							Gui.deployableTextArea.append(line);
//						else
//							Gui.nonDeployableTextArea.append(line);
//					}
//				});
//			}
//		}.start();
//		
	
		
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (firstOrSecondOutputArea)
					Gui.deployableTextArea.append(line);
				else
					Gui.nonDeployableTextArea.append(line);
			}
			
		});
	
		
		
	}


	public boolean startConfiguration() {

		if (connected && hasAnsibleFile && hasDockerFile) {
			ConfigurationMaker configurationMaker = new ConfigurationMaker(username, connectionAddress,
					ansibleFilePath.getAbsolutePath(), dockerFilePath.getAbsolutePath(), testingType);
			JOptionPane.showMessageDialog(null, "Configuration started", "Configuration",
					JOptionPane.INFORMATION_MESSAGE);
			if (configurationMaker.startConfiguration())
				return true;
			else {
				JOptionPane.showMessageDialog(null, "Error at Configuration", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}

		}

		else {
			JOptionPane.showMessageDialog(null, "Please check your credentials", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

	}



	public void startDeployment(String selectedTestType) {
		DeployerMaker deployermaker = new DeployerMaker(connectionAddress,password, ansibleFilePath.getAbsolutePath(),
				dockerFilePath.getAbsolutePath(),selectedTestType);
		deployermaker.startDeployment();

	}

	public void startReporting() {
		   ReportingMaker reportingmaker= new ReportingMaker(outputFilePath.getAbsolutePath(),reportType,deviceName);
		   reportingmaker.startReporting();

	}
	public void startMerging(String text, String text2, String text3) {
		Merger merger = new Merger();
		merger.setFiles(text,text2,text3);
		merger.setPath(outputFilePath.getAbsolutePath());
		merger.startMerging();
		
		
	}

}
