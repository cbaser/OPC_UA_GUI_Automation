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

import com.google.common.io.Files;

import controller.ResultMaker;

public class MakefileDeployer {
	
	private ResultMaker resultMaker;
	private String path;
	private String additionalParameters;
	
	public MakefileDeployer(String path,String additionalParameters) {
		this.path = path;
		this.additionalParameters= additionalParameters;
		this.resultMaker = new ResultMaker();
	}
	
	public void startDeployment() {
		try {
			File file = new File(path+File.separator+"build");
			
			file.mkdir();
			if(!additionalParameters.equals("")||!additionalParameters.isEmpty())
			changeFileCommand();
			
			String command;
			if(!additionalParameters.isEmpty()||additionalParameters.equals(""))
				command = "cmake "+additionalParameters+" ..";
			else
				command = "cmake ..";
			Process proc = Runtime.getRuntime().exec(command,null,file);
			proc.waitFor();
			appendToOutputArea(proc);
			String secondCommand = "make -j";
			proc=Runtime.getRuntime().exec(secondCommand,null,file);
			proc.waitFor();
			appendToOutputArea(proc);
			
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	public void appendToOutputArea(Process proc) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	
		String outputline = bufferedReader.readLine();
			while((outputline = bufferedReader.readLine()) != null) {
			
				resultMaker.showResults(outputline+"\n");
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
			 //   String text = "target_compile_options(${PROJECT_NAME} PRIVATE -fno-exceptions -DWIFI_SSID=\"${WIFI_SSID}\" -DWIFI_PWD=\"${WIFI_PWD}\")";
			    String text = "target_compile_options(${PROJECT_NAME} PRIVATE -fno-exceptions";
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
