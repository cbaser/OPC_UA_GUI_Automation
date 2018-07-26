package Configuration;

public class ConfigurationMaker {
	

	
	private AnsibleConfigurator ansibleConfiguration;
	private DockerConfigurator dockerConfiguration;
	
	public ConfigurationMaker(String username,String hostIP,String ansiblePath,String dockerPath,String testingType) {
		
		dockerConfiguration = new DockerConfigurator(username,hostIP,dockerPath,testingType);
		ansibleConfiguration = new AnsibleConfigurator(username,hostIP,ansiblePath,testingType);				
	}
	
	public boolean startConfiguration() {
		ansibleConfiguration.startConfiguration();
		dockerConfiguration.startConfiguration();
	
		return true;
	}
//	closeConnection();
//	public void closeConnection(){
//		if(channel != null && session != null) {
//			channel.disconnect();
//		    session.disconnect();
//		    System.out.println("Disconnected channel and session");
//		}
//	    
//	}
//	
	
	
	
}
