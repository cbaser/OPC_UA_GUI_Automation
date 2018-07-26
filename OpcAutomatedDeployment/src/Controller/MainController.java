package Controller;
import java.io.File;
import javax.swing.JOptionPane;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import Configuration.ConfigurationMaker;
import Deployment.DeployerMaker;


import org.usb4java.*;


public class MainController {
	private File ansibleFilePath,dockerFilePath,makeFilePath,outputFilePath;

	private String username,password,connectionAddress;
	private boolean connected=false,hasAnsibleFile=false,hasDockerFile=false,hasMakeFile=false;
	private Session session;
	private String testingType;

	public MainController() {
			
	}
	public File getMakeFilePath() {
		return makeFilePath;
	}

	public void setMakeFilePath(File makeFilePath) {
		this.makeFilePath = makeFilePath;
	}
	
	public String getTestingType() {
		return testingType;
	}

	public File getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(File outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public void setTestingType(String testingType) {
		this.testingType = testingType;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	
	public File getAnsibleFilePath() {
		return ansibleFilePath;
	}
	public void setAnsibleFilePath(File ansibleFilePath) {
		this.ansibleFilePath = ansibleFilePath;
	}
	public File getDockerFilePath() {
		return dockerFilePath;
	}
	public void setDockerFilePath(File dockerFilePath) {
		this.dockerFilePath = dockerFilePath;
	}
	public String getConnectionAddress() {
		return connectionAddress;
	}
	public void setConnectionAddress(String connectionAddress) {
		this.connectionAddress = connectionAddress;
	}
	/*
	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}
	*/
	public boolean controlFields() {
		if(username.isEmpty()||password.isEmpty()||connectionAddress.isEmpty())
		return false;
		else 
		return true;
	}
	public void getUSBDevice() {
		 final Context context = new Context();
		 int result = LibUsb.init(context);
        final DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(context, list);
        Device device = list.get(0);
		final int address = LibUsb.getDeviceAddress(device);
		final int busNumber = LibUsb.getBusNumber(device);
        System.out.println(String
                .format("Device %03d/%03d", busNumber, address));

            // Dump port number if available
            final int portNumber = LibUsb.getPortNumber(device);
            if (portNumber != 0)
                System.out.println("Connected to port: " + portNumber);

            // Dump parent device if available
            final Device parent = LibUsb.getParent(device);
            if (parent != null)
            {
                final int parentAddress = LibUsb.getDeviceAddress(parent);
                final int parentBusNumber = LibUsb.getBusNumber(parent);
                System.out.println(String.format("Parent: %03d/%03d",
                    parentBusNumber, parentAddress));
            }

            // Dump the device speed
            System.out.println("Speed: "
                + DescriptorUtils.getSpeedName(LibUsb.getDeviceSpeed(device)));

            // Read the device descriptor
            final DeviceDescriptor descriptor = new DeviceDescriptor();
             result = LibUsb.getDeviceDescriptor(device, descriptor);
            if (result < 0)
            {
                throw new LibUsbException("Unable to read device descriptor",
                    result);
            }
            DeviceHandle handle = new DeviceHandle();
            result = LibUsb.open(device, handle);
            System.out.print(descriptor.dump(handle));
            System.out.format(
                    "Bus %03d, Device %03d: Vendor %04x, Product %04x%n",
                    busNumber, address, descriptor.idVendor(),
                    descriptor.idProduct());

	}

	public boolean checkConnection()
	{
		JSch jsch = new JSch();
		try {
			//192.168.178.59
			session = jsch.getSession(username, connectionAddress, 22);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			connected=session.isConnected();
			
		}catch(Exception e) {
			e.printStackTrace();
		}

		return connected;
		
	}
	public boolean controlAnsibleFiles(File incomingFile){	
		hasAnsibleFile = checkFolder(incomingFile,".yml");
		return hasAnsibleFile;
	}
	public boolean controlDockerFiles(File incomingFile) {
		
	//	hasDockerFile = checkFolder(incomingFile,"Dockerfile");
		hasDockerFile = checkFolder(incomingFile,".c");
		return hasDockerFile;
	}
	public boolean controlMakeFiles(File incomingFile) {
		hasMakeFile = checkFolder(incomingFile,"CMakeLists.txt");
		return hasMakeFile;
	}
	
	
	public boolean checkFolder(File file,String fileExtension) {
		if(file.isDirectory()) {
			File [] listOfFiles = file.listFiles();
			for (int i=0;i<listOfFiles.length;i++) {
				if(listOfFiles[i].isFile() && listOfFiles[i].getName().contains(fileExtension)){
					return true;
				}
			}
		}
		
		return false;
	}
	/*
	public void showOutput() {
		try {
			String line;
			BufferedReader in = new BufferedReader(new InputStreamReader(
			        inputStream));             
			while ((line = in.readLine()) != null) {
			    textArea.append(line);
			}
			
					
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
*/	
	   public boolean startConfiguration() {

		   
		   if(connected&&hasAnsibleFile&&hasDockerFile) {
		    ConfigurationMaker configurationMaker = new ConfigurationMaker(username,connectionAddress,ansibleFilePath.getAbsolutePath(),dockerFilePath.getAbsolutePath(),testingType);
		    JOptionPane.showMessageDialog(null, "Configuration started", "Configuration", JOptionPane.INFORMATION_MESSAGE);
		    if(configurationMaker.startConfiguration()) 		
		    	return true;
		    else {
		    	JOptionPane.showMessageDialog(null, "Error at Configuration", "Error", JOptionPane.ERROR_MESSAGE);
		    	return false;
		    }
				
		   }
		 
		   else {
			   JOptionPane.showMessageDialog(null, "Please check your credentials", "Error", JOptionPane.ERROR_MESSAGE);
			   return false;
		   }
	
		   
		}
		
		  
//	   ,configurationFinished=false
//	   public boolean getConfigurationFinished() {
//		   return configurationFinished;
//	   }
	
	   public void startDeployment(String selectedTestType) {
		   DeployerMaker deployermaker = new DeployerMaker(connectionAddress,ansibleFilePath.getAbsolutePath(),dockerFilePath.getAbsolutePath());
		   deployermaker.startDeployment();
		   
	   }
	   public void startReporting() {
//		   ReportingMaker reportingmaker= new ReportingMaker();
//		   reportingmaker.startReporting();
		   
	   }
	
	

	
	
	
	


}
