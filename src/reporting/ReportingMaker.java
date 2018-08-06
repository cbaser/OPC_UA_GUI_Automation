
package reporting;



public class ReportingMaker{

private String inputPath;
private String reportType;
private String deviceName;
public ReportingMaker(String inputPath,String reportType,String deviceName) {
	this.inputPath= inputPath;
	this.reportType = reportType;
	this.deviceName = deviceName;
}

	public void startReporting() {
		
		TxtParser txtFile = new TxtParser(inputPath,deviceName,reportType);
		txtFile.startReadingFile();
	}
	
}