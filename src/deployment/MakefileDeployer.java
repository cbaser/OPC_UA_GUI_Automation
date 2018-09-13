package deployment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import controller.MainController;
import controller.ResultMaker;

public class MakefileDeployer {
	
	private ResultMaker resultMaker;
	private String path;
	private String additionalParameters;
	private String deviceName;
	private String testing_type,memory_size;
	private MainController controller;
	private String command;
	public MakefileDeployer(String path,String additionalParameters,String deviceName) {
		this.path = path;
		this.additionalParameters= additionalParameters;
		this.deviceName = deviceName;
		this.resultMaker = new ResultMaker();
		controller = new MainController();
	}
	
	
	public void setDeviceMemory() throws Exception {
		File file = new File(System.getProperty("user.dir")+File.separator+"devices"+File.separator+deviceName+".txt");
		String contents = new Scanner(file).useDelimiter("\\Z").next();
		//CPU : (\d+)(\s+)(\w+)
		//String regex = "(echo|ack)_str\\s+(?<size>\\d+)\\s+average rtt\\/request=(?<val>.*)";
		String regex = "CPU : (?<size>\\d+)(\\s+)(?<val>\\w+)";
		Pattern titlePattern = Pattern.compile(regex);
		Matcher matcher = titlePattern.matcher(contents);
		while(matcher.find()) {
		//	dataset.addValue(Double.valueOf(matcher.group("size")), "rtt", matcher.group("val"));,
			this.testing_type ="r";
			switch(matcher.group("val")) {
			case "KB":
				memory_size="s";
				break;
			case "MB":
				memory_size="m";
				break;
			case "GB":
				memory_size="l";
				break;
			
			}
		
		
		}

		
		
		
	}
	
	
	public void startDeployment() {
		try {
			if(controller.checkFolder(new File(path+File.separator), "CMakeLists.txt")) {
				File file = new File(path+File.separator+"build");
				
				file.mkdir();
				if(!additionalParameters.equals("")||!additionalParameters.isEmpty())
				changeFileCommand();
				
				
				
				setDeviceMemory();
				
				
				if(!additionalParameters.isEmpty()||additionalParameters.equals(""))
					command = "cmake "+"-DTESTING_TYPE="+testing_type+"-DMEMORY_SIZE="+memory_size+additionalParameters+" ..";
				else
					command = "cmake ..";
				Process proc = Runtime.getRuntime().exec(command,null,file);
				proc.waitFor();
				appendToOutputArea(proc);
				String secondCommand = "make -j";
				proc=Runtime.getRuntime().exec(secondCommand,null,file);
				proc.waitFor();
				appendToOutputArea(proc);
			}
			
			if(controller.checkFolder(new File(path+File.separator), ".sh")) {
				String[] cmd = new String[]{"/bin/sh", path+File.separator+"execute.sh"};
				Process proc = Runtime.getRuntime().exec(cmd);
				proc.waitFor();
				appendToOutputArea(proc);
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
	public void changeFileCommand() {
		File file = new File(path+File.separator+"CMakeLists.txt");
		FileWriter fw = null;
		BufferedWriter bw=null;
		PrintWriter out=null;
		
		try {
				 fw = new FileWriter(file.getAbsolutePath(), true);
			     bw = new BufferedWriter(fw);
			     out = new PrintWriter(bw);
			   String text = "target_compile_options(${PROJECT_NAME} PRIVATE -fno-exceptions -DWIFI_SSID=\"${WIFI_SSID}\" -DWIFI_PWD=\"${WIFI_PWD}\")";
			  
			    out.println(text);
			    if(additionalParameters.contains(" ")) {
			    	// Multiple Parameters 
			    	String parameters [] = text.split("\\s+");
			    	for(String parameter : parameters) {
			    		String []parts = parameter.split("=");
			    		text += "-D"+parts[0]+"${"+parts[1]+"}";
			    	}
			    }
			    else {
			    	//Single Parameter
			    	String parts[] = additionalParameters.split("=");
			    	text += "-D"+parts[0]+"${"+parts[1]+"}";
			    	
			    }
			    out.println(text);
			} catch (IOException e) {
			    e.printStackTrace();
			}
			finally {
				try {
					fw.close();
					bw.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}


		
	}

}
