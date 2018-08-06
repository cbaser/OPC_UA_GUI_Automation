package reporting;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxtParser {
	private String path,deviceName,reportType;
	
	private ArrayList<String> titles;
	private ArrayList<String> contents;
	private PdfParser pdfParser;
	private ExcelParser excelParser;
	private File txtFile;
	
	public TxtParser(String path,String deviceName,String reportType) {
		this.path=path;
		this.deviceName = deviceName;
		this.reportType = reportType;
	}
	
	public void startReadingFile() {
		try {
			ClassLoader classLoader = this.getClass().getClassLoader();
			txtFile= new File(classLoader.getResource(path).getFile());
			BufferedReader	bufferedReader = new BufferedReader(new FileReader(txtFile));
			StringBuilder stringBuilder = new StringBuilder();
			
			String line = bufferedReader.readLine();

		    while (line != null) {
		    	stringBuilder.append(line);
		    	stringBuilder.append(System.lineSeparator());
		        line = bufferedReader.readLine();
		    }
		    String everything = stringBuilder.toString();
		    bufferedReader.close();
		    findTitles(everything);
		    findContent(everything);
		    startParsing();
		   
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public void findTitles(String everything) {
		titles = new ArrayList<>();
		Pattern titlePattern = Pattern.compile("\\bStarting.*?Test\\b");
		Matcher matcher = titlePattern.matcher(everything);
		while(matcher.find()) {
			
			titles.add(matcher.group().substring(matcher.group().indexOf("g")+1, matcher.group().indexOf("T")));
		//	System.out.println(matcher.group());
			//(?s)Starting CPU Single Value from Node (.*?)Ending CPU Single Value from Node 
		}	
	}
	public void findContent(String everything) {
		contents = new ArrayList<>();
		for(int i=0;i<titles.size();i++) {
			String title = titles.get(i);
			String content = "no match";
		    Pattern pattern =Pattern.compile("(?s)Starting"+title+"(.*?)"+"Ending"+title);
			Matcher matcher =pattern.matcher(everything);
			while(matcher.find()) 
				content = matcher.group(1);
			contents.add(content);			
		}
		}
	public void startParsing() {
		switch(reportType) {
		case ".pdf" :
			 pdfParser = new PdfParser();
			    pdfParser.setPath(path);
			    pdfParser.setTitles(titles);
			    pdfParser.setContents(contents);
			    pdfParser.setDeviceName(deviceName);
			    pdfParser.startReporting();
			break;
		case ".csv" :
			excelParser = new ExcelParser();
			excelParser.setTxtFile(txtFile);
			excelParser.setPath(path);
			excelParser.startExcelParsing();
			break;
			
		}
	}
	
	
	
	}

	
