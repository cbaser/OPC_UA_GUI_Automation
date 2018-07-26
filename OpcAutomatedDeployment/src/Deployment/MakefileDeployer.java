package Deployment;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import Controller.ResultMaker;

public class MakefileDeployer {
	
	
	private String path;
	private String additionalParameters;
	
	public MakefileDeployer(String path,String additionalParameters) {
		this.path = path;
		this.additionalParameters= additionalParameters;
	}
	
	public void startDeployment() {
		try {
			File file = new File(path+File.separator+"\build");
			file.getParentFile().mkdirs();
			String command = "cmake "+additionalParameters+" ..";
			Process proc = Runtime.getRuntime().exec(command,null,file);
			proc.waitFor();
			String secondCommand = "make -j";
			proc=Runtime.getRuntime().exec(secondCommand);
			proc.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			ResultMaker resultMaker =new ResultMaker();
			String outputline = bufferedReader.readLine();
				while((outputline = bufferedReader.readLine()) != null) {
					resultMaker.showResults(outputline+"\n");
					outputline = bufferedReader.readLine();
				}
			
			bufferedReader.close();
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
