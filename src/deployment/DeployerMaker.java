package deployment;

public class DeployerMaker {
	private AnsibleDeployer ansibleDeployer;
	private DockerDeployer dockerDeployer;

	
	
	public DeployerMaker(String hostIP,String password,String ansiblePath,String dockerPath,String testingType) {
		ansibleDeployer = new AnsibleDeployer(ansiblePath,password);
		dockerDeployer = new DockerDeployer(hostIP,dockerPath,testingType);
	}
	public void startDeployment() {
		ansibleDeployer.startDeployment();
		dockerDeployer.startDeployment();
	}
	


}
