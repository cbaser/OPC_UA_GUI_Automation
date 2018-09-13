package deployment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import controller.ResultMaker;

public class AnsibleDeployer {
	private String ansiblePath;
	private String args[];
	private ProcessBuilder pb;
	private Process process;
	private ResultMaker resultMaker;
	private String outputline;
	private String password;
	
	
	public  AnsibleDeployer(String ansiblePath,String password) {
		this.ansiblePath = ansiblePath;
		this.password = password;
		resultMaker = new ResultMaker();
	}
	
	

	
	public void startDeployment() {
		args = new String[5];
		 resultMaker.setTextArea(true);
		resultMaker.appendToTextArea("---------------ANSIBLE---------------\n");
		//Process p;
        try {
        	args[0] = "ansible-playbook";
        	args[1] = "deploy_opc_ua.yml";
        	args[2] = "--ask-pass";
        	args[3] = "--extra-vars";
        	args[4] = "ansible_become_pass="+password;
        	pb = new ProcessBuilder(args);
        	pb.redirectErrorStream(true);
        	pb.directory(new File(ansiblePath));
        	process = pb.start();
        	InputStream errStream = process.getErrorStream();
        	InputStream inStream = process.getInputStream();
        	OutputStream outStream = process.getOutputStream();
        	
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
            
            writer.write(password);
            writer.flush();
            writer.close();
            
            
		//	BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            resultMaker.setTextArea(true);
			while((outputline= br.readLine()) != null) {
				
				resultMaker.appendToTextArea(outputline+"\n");	
			}
			if(errStream!=null) {
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(errStream));
				while((outputline = errorReader.readLine())!= null)
					resultMaker.appendToTextArea(outputline+"\n");
			}

        //   process.destroy();
            
			
			
			
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
