package Deployment;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import Controller.ResultMaker;

public class AnsibleDeployer {
	private String ansiblePath;
	private String args[];
	private ProcessBuilder pb;
	private Process process;
	private ResultMaker resultMaker;
	private String outputline;
	
	
	public  AnsibleDeployer(String ansiblePath) {
		this.ansiblePath = ansiblePath;
		resultMaker = new ResultMaker();
	}
	
	

	
	public void startDeployment() {
		args = new String[2];
		resultMaker.showResults("---------------ANSIBLE---------------\n");
		//Process p;
        try {
        	args[0] = "ansible-playbook";
        	args[1] = "deploy_opc_ua.yml";
        	pb = new ProcessBuilder(args);
        	pb.redirectErrorStream(true);
        	pb.directory(new File(ansiblePath));
        	process = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while((outputline= br.readLine()) != null) {
				resultMaker.showResults(outputline+"\n");
				
			}
			
			
			
           process.destroy();
            
			
			
			
			/** Version for ssh
			String command = "ping -c 3 google.com";
			channel = session.openChannel("exec");	
			((ChannelExec) channel).setCommand(command);
			  channel.setInputStream(null);
			  ((ChannelExec) channel).setErrStream(System.err);
			  channel.connect();
			  resultmaker.setInputStream(channel.getInputStream());
			  resultmaker.showOutput();
			  channel.connect();
			  */
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
