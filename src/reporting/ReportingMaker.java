
package reporting;



public class ReportingMaker{

private String inputPath;
private String reportType;

public ReportingMaker(String inputPath,String reportType) {
	this.inputPath= inputPath;
	this.reportType = reportType;
}

	public void startReporting() {
		TxtParser txtFile = new TxtParser(inputPath);
		txtFile.startReadingFile();
	}
	
}