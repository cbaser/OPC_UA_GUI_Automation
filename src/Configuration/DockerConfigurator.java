package Configuration;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;

public class DockerConfigurator {
	private File directory;
	private String testingType;
	//private String hostIP;

	public DockerConfigurator(String username,String hostIP, String dockerPath,String testingType) {
		this.testingType = testingType;
		directory = new File(dockerPath);
		//this.hostIP = hostIP;
	}

	public void startConfiguration() {
		createDockerFile();

	}

	private void createDockerFile() {
		deleteFile("Dockerfile");
		createFile("Dockerfile");

	}

	private File[] getFileFromDirectory(String fileName) {
		File[] matchingFiles = directory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(fileName);
			}
		});
		return matchingFiles;

	}

	private void deleteFile(String fileName) {
		File[] files = getFileFromDirectory(fileName);
		for (File file : files) {
			file.delete();
		}
	}

	private void createFile(String fileName) {

		String text;
		File file = new File(directory.getAbsolutePath() + "//" + fileName);
		
		text =  //"raspberrypi:" + hostIP + " opcua" + "\n# docker build . -t <container_tag>\n"
				 "# docker run --add-host raspberrypi:<ip of the server> <container_tag>\n" + "# or conveniently\n"
				+ "# docker build . -t opcua && docker run -it --network=host --add-host raspberrypi:10.200.2.8 opcua\n"
				+ "# Cagatay sudo  docker build . -t opcua && docker run -it --network=host --add-host raspberrypi:10.200.2.14 opcua\n"
				+ "FROM ubuntu:xenial\n" + "RUN apt-get update && apt-get install -y gcc g++\n" + "ADD . /tests\n"
				+ "#ADD ./hosts /etc/hosts\n" + "WORKDIR /tests\n";
		
		switch(testingType) {
		
		/** Add hostIP into command line
		 *  CMD \ ./phase_x hostIP;
		 *  
		 *  
		 *  */
		
		
		case "CPU Tests":
			//text+= "RUN gcc -std=c99 open62541.c multiple_clients_single_value_test.c -o phase_1\n";
			//text+= "RUN gcc -std=c99 open62541.c multiple_clients_multiple_value_test.c -o phase_2\n";
			text+= "RUN gcc -std=c99 open62541.c MainClient.c -o phase_1\n";
			//text+= "RUN gcc -std=c99 open62541.c sorting_test.c -o phase_4\n";
			//text+= "CMD \"./phase_1\"\n";
			//text+= "CMD \"./phase_2\"\n";
			text+= "CMD \"./phase_3\"\n";
			//text+= "CMD \"./phase_4\"\n";
			break;
		case "Networking Tests":
			text+= "RUN gcc -std=c99 open62541.c networking_test.c -o phase_1\n";
			text+= "CMD \"./phase_1\"\n";			
			break;
		case "Encryption Tests":
			text+= "RUN gcc -std=c99 open62541.c encryption_test.c -o phase_1\n";
			text+= "CMD \"./phase_1\"\n";
			break;
		case "Multi-Thread Tests":
			text+= "RUN gcc -std=c99 open62541.c multithreading_test.c -o phase_1\n";
			text+= "CMD \"./phase_1\"\n";	
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

}




//
//text =  username+":" + hostIP + " opcua" + "\n# docker build . -t <container_tag>\n"
//		+ "# docker run --add-host raspberrypi:<ip of the server> <container_tag>\n" + "# or conveniently\n"
//		+ "# docker build . -t opcua && docker run -it --network=host --add-host raspberrypi:10.200.2.8 opcua\n"
//		+ "# Cagatay sudo  docker build . -t opcua && docker run -it --network=host --add-host raspberrypi:10.200.2.14 opcua\n"
//		+ "FROM ubuntu:xenial\n" + "RUN apt-get update && apt-get install -y gcc g++\n" + "ADD . /tests\n"
//		+ "#ADD ./hosts /etc/hosts\n" + "WORKDIR /tests\n"
//		+ "RUN gcc -std=c99 open62541.c method.c -o phase_1\n"
//		+ "#RUN gcc -std=c99 open62541.c filesize_test.c -o phase_1\n"
//		+ "#RUN gcc -std=c99 open62541.c encryption_test.c -o phase_2\n"
//		+ "#RUN gcc -std=c99 open62541.c multi_client_test.c -o phase_3\n" + "\n" + "\n" + "CMD \"./phase_1\"\n"
//		+ "#CMD \"./phase_2\"\n" + "#CMD \"./phase_3\"";
