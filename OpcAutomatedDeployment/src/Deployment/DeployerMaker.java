package Deployment;

public class DeployerMaker {
	private AnsibleDeployer ansibleDeployer;
	private DockerDeployer dockerDeployer;
	
	
	public DeployerMaker(String hostIP,String ansiblePath,String dockerPath) {
		ansibleDeployer = new AnsibleDeployer(ansiblePath);
		dockerDeployer = new DockerDeployer(hostIP,dockerPath);
		
	}
	public void startDeployment() {
		ansibleDeployer.startDeployment();
		dockerDeployer.startDeployment();
	}
	


}
