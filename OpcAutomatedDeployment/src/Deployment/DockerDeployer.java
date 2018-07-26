package Deployment;

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

import Controller.ResultMaker;

public class DockerDeployer {
	private String dockerPath;
	private String hostIP;
	private ResultMaker resultMaker;
	private ProcessBuilder pb;
	private Process compile,execute;
	private String args[];
	private String outputline;
	public DockerDeployer(String hostIP,String dockerPath) {
		this.hostIP = hostIP;
		this.dockerPath = dockerPath;
		resultMaker =new ResultMaker();
	}
	
	public void startDeployment() {
		
		//docker build . -t opcua && docker run -it --network=host --add-host raspberrypi:10.200.2.8 opcua
		try {
			
			File baseDir = new File(dockerPath);
			resultMaker.showResults("---------------DOCKER---------------\n");
			
//			File commandDir = new File(dockerPath+File.separator+"commands.txt");
//			pb = new ProcessBuilder("bash","-c");
//			pb.redirectInput(commandDir);
//			pb.directory(baseDir);
//			pb.start();
//			
			//pb= new ProcessBuilder("/usr/bin/gcc"," -std=c99 "," open62541.c "," -lmbedtls "," -lmbedx509 "," -lmbedcrypto " ," -D_POSIX_C_SOURCE=199309L "," -lm "," -o "," MainClient "," MainClient.c ");
			
			Process compile = Runtime.getRuntime().exec(new String[] {"sh","execute.sh", "logfile.log", "MainClient"},null,baseDir);
			compile.waitFor();
			int exitValue = compile.exitValue();
			System.out.println(exitValue);
			Process execute = Runtime.getRuntime().exec(new String[] {"./MainClient",hostIP},null,baseDir);
		//	Process execute = new ProcessBuilder("./MainClient").directory(baseDir).start();

			BufferedReader compileRead = new BufferedReader(new InputStreamReader(compile.getInputStream()));
			 outputline = compileRead.readLine();
			while(outputline != null) {
				resultMaker.showResults(outputline+"\n");
				outputline = compileRead.readLine();
			}
			
			
			BufferedReader read = new BufferedReader(new InputStreamReader(execute.getInputStream()));
			outputline = read.readLine();
			while(outputline != null) {
				resultMaker.showResults(outputline+"\n");
				outputline = read.readLine();
			}
			
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
			resultMaker.showResults(e.getMessage().toString());
			//e.printStackTrace();
			
		}
	}

}
