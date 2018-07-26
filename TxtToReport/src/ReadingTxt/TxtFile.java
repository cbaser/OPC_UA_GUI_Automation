package ReadingTxt;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Reporting.Reporting;

public class TxtFile {
	
	private ArrayList<String> titles;
	private ArrayList<String> contents;
	private Reporting reporter;
	
	
	public void startReadingFile() {
		try {
			String filePath = System.getProperty("user.dir")+File.separator+"input"+File.separator+"raspberrypi.txt";
		//	File file = new File(classLoader.getResource("input/raspberypi.txt").getFile());
			File file = new File(filePath);
			BufferedReader	bufferedReader = new BufferedReader(new FileReader(file));
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
		    reporter = new Reporting();
		    reporter.setTitles(titles);
		    reporter.setContents(contents);
		    reporter.startReporting();
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
	}

	
