package Configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;

public class AnsibleConfigurator {
	private File mainDirectory;
	private String hostIP;
	private String testingType;
	private String username;
	public AnsibleConfigurator(String username,String hostIP, String ansiblePath,String testingType) {
		this.username = username;
		mainDirectory = new File(ansiblePath);
		this.hostIP = hostIP;
		this.testingType = testingType;
		
	}

	public void startConfiguration() {
		changeHostsFile();
		changeTasksFile();
		changeTargetsFile();
	}

	private void changeHostsFile() {
		deleteFile(mainDirectory,"hosts");
		createFile(mainDirectory,"hosts", "Hosts");

	}

	private void changeTasksFile() {
		File newDirectory = new File(mainDirectory.getAbsolutePath() + "//roles//opc.ua//tasks");
		deleteFile(newDirectory,"main.yml");
		createFile(newDirectory,"main.yml", "Tasks");

	}
	private void changeTargetsFile() {
		File newDirectory =  new File(mainDirectory.getAbsolutePath() + "//group_vars");
		deleteFile(newDirectory,"targets.yml");
		createFile(newDirectory,"targets.yml", "Targets");
	}
	
	

	private File[] getFileFromDirectory(File directory,String fileName) {
		File[] matchingFiles = directory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(fileName);
			}
		});
		return matchingFiles;

	}

	private void deleteFile(File directory,String fileName) {
		File[] files = getFileFromDirectory(directory,fileName);
		for (File file : files) {
			file.delete();
		}
	}

	private void createFile(File directory, String fileName,String fileType) {
		
		
		
		String text=null;
		File file = new File(directory.getAbsolutePath() + "//" + fileName);
		
		
		switch(fileType) {
		case "Hosts":
			text = "[targets]\n" + "\n" + hostIP;
			break;
		
		case "Targets":
			text = "---\n"+"ansible_user: " +username;
			break;
		
		case "Tasks":
			text = initialTasksText()+selectiveTasksText()+restTasksText();
			break;
		}
		try {

			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(text);
			output.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String initialTasksText() {
		
		return "---\n" + "- name: Ensure /etc/opcua dir exists\n" 
				+ "  file: \n" + 
				"    path: /etc/opcua \n"
				+ "    state: directory\n" + 
				"    owner: '{{ ansible_user }}'\n" 
				+ "  become: true\n" ;
	}
	
	
	private String selectiveTasksText() {
		String text = 
				 "\n"
				+ "# https://open62541.org/releases/b916bd0611.zip\n"
				+ "- name: Copy files extracted from the release https://open62541.org/releases/b916bd0611.zip 2017-11-16 11:07:56\n"
				+ "  copy:\n" 
				+ "    src: '{{ item }}'\n" 
				+ "    dest: '/etc/opcua/{{ item }}'\n"
				+ "    owner: '{{ ansible_user }}'\n"
				+ "  with_items:\n" ;		
		
		switch(testingType) {
		case "Read Tests":
			text+= 	
					"    - AccessControlServerClass.h\n"+
					"    - commonServerMethods.h\n"+	
					"    - MainServer.c\n" + 
					"    - CpuServerClass.h\n" +
					"    - EncryptionServerClass.h\n"+
					"    - MonitoredItemsServerClass.h\n"+
					"    - NetworkingServerClass.h\n"+
					"    - open62541.c\n" + 
					"    - open62541.h\n" + 
					"\n" 									
					+testStringBuilder("MainServer.c");
			break;
		case "Networking Tests":
			text+= 	"    - NetworkingTestingServer.c\n" + 
					"    - open62541.c\n" + 
					"    - open62541.h\n" + 
					"\n"  
					+testStringBuilder("NetworkingTestingServer.c");			
			break;
		case "Encryption Tests":
			
			text+= 	"    - EncyrptionTestingServer.c\n" + 
					"    - ./certificates\n" + 
					"    - open62541.c\n" + 
					"    - open62541.h\n" + 
					"    - common_testing_methods.h\n" + 
					"\n"  
					+testStringBuilder("EncyrptionTestingServer.c");	
			break;
		case "Multi-Thread Tests":
			text+= 	"    - MultiThreadingTestingServer.c\n" + 
					"    - ./certificates\n" + 
					"    - open62541.c\n" + 
					"    - open62541.h\n" + 
					"    - common_testing_methods.h\n" + 
					"\n"
					+testStringBuilder("MultiThreadingTestingServer.c");
			break;
				
		
		}
		
		
		return text;
	}
	private String restTasksText() {
		
		return  
				"\n- name: Check if port 4840 is used\n" + "  wait_for:\n"
				+ "    port: 4840\n" + "    state: stopped\n" + "    timeout: 2\n" + "  ignore_errors: yes\n"
				+ "  register: port_check\n" + "  when: opcua_state == \"running\"\n" + "\n"
				+ "- name: If server is running, kill it (when opcua_state == \"restarted\" or \"stopped\")\n"
				+ "  shell: fuser -k -n tcp 4840\n" + "  ignore_errors: yes\n"
				+ "  when: opcua_state == \"restarted\" or opcua_state == \"stopped\"\n" + "\n"
				+ "- name: Wait for the server to be closed\n" + "  wait_for:\n" + "    port: 4840\n"
				+ "    state: stopped\n" + "    timeout: 10\n"
				+ "  when: opcua_state == \"restarted\" or opcua_state == \"stopped\"\n" + "\n"
				;
				
	}
	
	private String testStringBuilder(String fileName) {
		
		
		String rawName = fileName.substring(0, fileName.indexOf("."));
		
		
		return 
				"- name: Build using the command 'gcc -std=c99 open62541.c -D_POSIX_C_SOURCE=199309L " + fileName +" -o "+ rawName + "'\n" + 
				"  shell: gcc -std=c99 open62541.c -lmbedtls -lmbedx509 -lmbedcrypto -D_POSIX_C_SOURCE=199309L "+fileName+ " -o " +rawName+ "\n" + 
				"  args:\n" + 
				"    chdir: /etc/opcua/ \n"+
				"\n"+
				"- name: Run "+ rawName+ " with nohup. output is forwarded to log.txt\n"+
				"  shell: nohup ./"+ rawName +" >> log.txt &\n" + "  args:\n" + "    chdir: /etc/opcua/\n"+
				"  when: (opcua_state == \"running\" and not port_check.failed) or\n"+
				"        (opcua_state == \"restarted\")\n"+"\n";
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		"- name: Build using the command 'gcc -std=c99 open62541.c -D_POSIX_C_SOURCE=199309L CPUTestingServerAddingSingleVariableNode.c -o CPUTestingServerAddingSingleVariableNode'\n" + 
//		"  shell: gcc -std=c99 open62541.c CPUTestingServerAddingSingleVariableNode.c -o CPUTestingServerAddingSingleVariableNode\n" + 
//		"  args:\n" + 
//		"    chdir: /etc/opcua/ \n"+
//		"- name: Run CPUTestingServerAddingSingleVariableNode with nohup. output is forwarded to log.txt\n"+
//		"  shell: nohup ./CPUTestingServerAddingSingleVariableNode > log.txt &\n" + "  args:\n" + "    chdir: /etc/opcua/\n"+
//		"  when: (opcua_state == \"running\" and not port_check.failed) or\n"+
//		"        (opcua_state == \"restarted\")"+
//					
//		
//		
//		
//		
//		"- name: Build using the command 'gcc -std=c99 open62541.c -D_POSIX_C_SOURCE=199309L CPUTestingServer.c -o CPUTestingServer'\n" + 
//		"  shell: gcc -std=c99 open62541.c CPUServerGeneral.c -o CPUServerGeneral\n" + 
//		"  args:\n" + 
//		"    chdir: /etc/opcua/ \n"+
//		"- name: Run myServer with nohup. output is forwarded to log.txt\n"+
//		"  shell: nohup ./myServer > log.txt &\n" + "  args:\n" + "    chdir: /etc/opcua/\n"+
//		"  when: (opcua_state == \"running\" and not port_check.failed) or\n"+
//	    "        (opcua_state == \"restarted\")";
//

		
	}
	
	
	
	
	
	
	
}
