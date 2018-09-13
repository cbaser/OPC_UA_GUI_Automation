package deployment;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
//import com.github.dockerjava.api.command.CreateContainerResponse;
//import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.ExecStartCmd;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.ExecStartResultCallback;

import controller.ResultMaker;

public class DockerDeployer {
	private String dockerPath;
	private String hostIP;
	private String testingType;
	private ResultMaker resultMaker;
	private Process execute;
	private ProcessBuilder pb;
	private String outputline;
	private String args[];
	private boolean deploymentType;
	public DockerDeployer(String hostIP,String dockerPath,String testingType,boolean deploymentType) {
		this.hostIP = hostIP;
		this.dockerPath = dockerPath;
		this.testingType= testingType;
		this.deploymentType = deploymentType;
		resultMaker =new ResultMaker();
	}
	
	public void startDeployment() {
		args = new String[7];

		try {
			resultMaker.appendToTextArea("---------------DOCKER---------------\n");
			String[] testingParts = testingType.split(" ");
			args[0]="sh";
			args[1]="execute.sh";
			args[2]="logfile.log";
			args[3]="MainClient";
			args[4]=hostIP;
			args[5]=testingParts[0];
			args[6]="GB";
			pb = new ProcessBuilder(args);
			pb.redirectErrorStream(true);
			pb.directory(new File(dockerPath));
			execute = pb.start();
			InputStream errStream = execute.getErrorStream();
			//Process compile = Runtime.getRuntime().exec(new String[] {"sh","execute.sh", "logfile.log","MainClient",hostIP,testingParts[0],"GB" },null,baseDir);
			//compile.waitFor();
			BufferedReader compileRead = new BufferedReader(new InputStreamReader(execute.getInputStream()));
			 outputline = compileRead.readLine();

			while(outputline != null) {
				resultMaker.appendToTextArea(outputline+"\n");
				outputline = compileRead.readLine();
			}
			
			resultMaker.setTextArea(deploymentType);
			BufferedReader read = new BufferedReader(new InputStreamReader(execute.getInputStream()));
			outputline = read.readLine();
			while(outputline != null) {
				resultMaker.appendToTextArea(outputline+System.getProperty("line.separator"));
				outputline = read.readLine();
				resultMaker.writeToFile(outputline+System.getProperty("line.separator"));
			}
			if(errStream!=null) {
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(errStream));
				while((outputline = errorReader.readLine())!= null)
					resultMaker.appendToTextArea(outputline+"\n");
			}
			//docker build . -t opcua && docker run -it --network=host --add-host raspberrypi:10.200.2.8 opcua	
//			DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
//					.withDockerHost("tcp://"+hostIP)
//					.build();
//
//			DockerClient dockerClient = DockerClientBuilder.getInstance(config)
//					.build();
//			HashSet<String> hasSet = new HashSet<String>(Arrays.asList("opcua"));
//			dockerClient.buildImageCmd().withNoCache(true).withTags(hasSet).exec(new BuildImageResultCallback());
//			BuildImageCmd cfg = DockerClientBuilder.getInstance().build().buildImageCmd(baseDir);
//			String imageId = cfg.exec(new BuildImageResultCallback()).awaitImageId();
//			 
//			 
//			 dockerClient.startContainerCmd(imageId);
//			
//			ExecStartCmd startCmd = dockerClient.execStartCmd(imageId);
//			ExecStartResultCallback resultCallback = startCmd.exec(new ExecStartResultCallback(System.out, System.err));
//			resultCallback.awaitCompletion();
////			resultMaker.elapsedTime(String.valueOf( (after-before)/100000 ));
//			InputStream inputStream = startCmd.getStdin();
//			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//			while((outputline= br.readLine()) != null) {
//				resultMaker.showResults(outputline+"\n");
//				
//			}
	    	
		}catch(Exception e) {
			resultMaker.appendToTextArea(e.getMessage().toString());
			//e.printStackTrace();
			
		}
	}

}
