package Deployment;

public class DeployerMaker {
	private AnsibleDeployer ansibleDeployer;
	private DockerDeployer dockerDeployer;

	
	
	public DeployerMaker(String hostIP,String ansiblePath,String dockerPath,String testingType) {
		ansibleDeployer = new AnsibleDeployer(ansiblePath);
		dockerDeployer = new DockerDeployer(hostIP,dockerPath,testingType);
	}
	public void startDeployment() {
		ansibleDeployer.startDeployment();
		dockerDeployer.startDeployment();
	}
	


}
