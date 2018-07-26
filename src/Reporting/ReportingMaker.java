
package Reporting;



public class ReportingMaker{

private String inputPath;
private String reportPath;
private String reportType;

public ReportingMaker(String inputPath,String reportPath,String reportType) {
	this.reportPath = reportPath;
	this.inputPath= inputPath;
	this.reportType = reportType;
}

	public void startReporting() {
		TxtParser txtFile = new TxtParser();
		txtFile.startReadingFile();
	}
	
}