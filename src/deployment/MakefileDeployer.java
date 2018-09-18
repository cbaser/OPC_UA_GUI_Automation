package deployment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import controller.MainController;
import controller.ResultMaker;

public class MakefileDeployer {
	
	private ResultMaker resultMaker;
	private String path;
	private String additionalParameters;
	private String deviceName;
	private String testing_type,memory_size;
	private MainController controller;
	private String cmakeCommand,makeCommand;
	private String outputline;
	private DockerDeployer deployer;
	private String hostIP;
	public MakefileDeployer(String path,String additionalParameters,String deviceName,String testing_type) {
		this.path = path;
		this.additionalParameters= additionalParameters;
		this.deviceName = deviceName;
		this.testing_type = testing_type;
		this.resultMaker = new ResultMaker();
		controller = new MainController();
		
	}
	public void startDeployment() {
		try {
			if(controller.checkFolder(new File(path+File.separator), "CMakeLists.txt") && ! controller.checkFolder(new File(path+File.separator), ".sh")) 
				startCMakeDeployment();
			
			if(!controller.checkFolder(new File(path+File.separator), "CMakeLists.txt") && controller.checkFolder(new File(path+File.separator), ".sh")) 
				startBashDeployment();
			
			if(controller.checkFolder(new File(path+File.separator), "CMakeLists.txt") && controller.checkFolder(new File(path+File.separator), ".sh")) {
				startCMakeDeployment();
				startBashDeployment();
			}		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void startCMakeDeployment() throws Exception {
		
			File file = new File(path+File.separator+"build");
			if(file.exists()&& file.isDirectory()) {
				file.delete();
				file.mkdir();
			}
			else
			file.mkdir();
			
			setDeviceMemory();
			
			testing_type = testing_type.substring(0,testing_type.indexOf(" "));
			if(!additionalParameters.isEmpty()||!additionalParameters.equals(""))
				cmakeCommand = "cmake "+"-DTESTING_TYPE="+testing_type+"-DMEMORY_SIZE="+memory_size+additionalParameters+" ..";
			else
				cmakeCommand = "cmake ..";
			Process proc = Runtime.getRuntime().exec(cmakeCommand,null,file);
			proc.waitFor();
			appendToOutputArea(proc);
			makeCommand = "make -j";
			proc=Runtime.getRuntime().exec(makeCommand,null,file);
			proc.waitFor();
			setIp(proc);
			appendToOutputArea(proc);
	

	}
	public void startBashDeployment() throws Exception{
			String args[]=new String[5];
			args[0]="sh";
			args[1]="execute.sh";
			args[2]="None";
			args[3]=testing_type;
			args[4]=memory_size;
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.redirectErrorStream(true);
			pb.directory(new File(path));	
			Process proc = pb.start();
			proc.waitFor();
			setIp(proc);
			appendToOutputArea(proc);
	
	
	}
	
	
	public void setDeviceMemory() throws Exception {
		File file = new File(System.getProperty("user.home") + File.separator + "opc-ua-deployment-tool-devices"+File.separator+deviceName+".txt");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(file).useDelimiter("\\Z");
		String contents = scanner.next();
		String regex = "MEMORY : (?<size>\\d+)(\\s+)(?<val>\\w+)";
		Pattern titlePattern = Pattern.compile(regex);
		Matcher matcher = titlePattern.matcher(contents);
		matcher.find();
		memory_size=matcher.group("val");
		scanner.close();
		
	}
	
	public void setIp(Process process) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((outputline = bufferedReader.readLine()) != null) {
				if(outputline.contains("opc.tcp")) {
					String [] arr = outputline.split(" ");
					for(String str: arr) {
						if(str.contains("opc.tcp")) {
							str.replaceAll("/",  Matcher.quoteReplacement("\\/"));
							String regex = "opc.tcp:\\/\\/(?<IP>\\w+):4840\\/";
							Pattern titlePattern = Pattern.compile(regex);
							Matcher matcher = titlePattern.matcher(str);
							if(matcher.find()) {
								hostIP = matcher.group("IP");
								hostIP+=".local";
								break;
								
							}
							else {
								regex = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
								titlePattern = Pattern.compile(regex);
								matcher = titlePattern.matcher(outputline);
								matcher.find();
								hostIP=matcher.group();
								break;
							}
						}
						
						
						
							
					}
					
					
					
				}
			}
	
		}catch(Exception e) {
		e.printStackTrace();
	
		}
		
	}

	public void appendToOutputArea(Process proc) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		resultMaker.setTextArea(false);
		String outputline = bufferedReader.readLine();
			while((outputline = bufferedReader.readLine()) != null) {		
				resultMaker.appendToTextArea(outputline+"\n");
				outputline = bufferedReader.readLine();
			}
		
		bufferedReader.close();
	}
	public void startTesting(String dockerPath,String outputPath){
		try {
			deployer = new DockerDeployer(hostIP,dockerPath,testing_type,false,outputPath);
			deployer.startDeployment();
		}catch(Exception e) {
			e.printStackTrace();
		}

		
	}

}
