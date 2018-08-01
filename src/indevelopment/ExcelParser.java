package Excel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExcelParser {
	private static List<String> formats = Arrays.asList(new String[] { "csv", "xml", "xlsx-single", "xlsx-multiple" });
	private String txtFile = System.getProperty("user.dir")+File.separator+"input"+File.separator+"omega2plus.txt";
	private String csvFile=  System.getProperty("user.dir")+File.separator+"output"+File.separator+"omega2plus.csv";
	public void startExcelParsing() {
		final Path path = Paths.get(System.getProperty("user.dir")+File.separator+"output");
	    final Path txt = path.resolve(txtFile);
	    final Path csv = path.resolve(csvFile);
	    final Charset utf8 = Charset.forName("UTF-8");
		 
		    try (Stream<String> stream = Files.lines(Paths.get(txtFile))) {
		        String result = stream.map(s -> s.split("\\s+"))
		                              .map(s -> Arrays.stream(s).collect(Collectors.joining(","))+"\n")
		                              .collect(Collectors.joining());
		        
		    //    final PrintWriter pw = new PrintWriter(Files.newBufferedWriter(csv, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW));
		        PrintWriter pw = new PrintWriter(csvFile);
		        pw.println(result);
		        System.out.println(result);
		        pw.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		
		

//	    
//	    try{
//	    	 final Scanner scanner = new Scanner(Files.newBufferedReader(txt, utf8));
//	         final PrintWriter pw = new PrintWriter(Files.newBufferedWriter(csv, utf8, StandardOpenOption.CREATE_NEW));
//	        while (scanner.hasNextLine()) 
//	            pw.println(scanner.nextLine().replace('|', ','));
//	            
//	          
//	            
//	    }  catch(Exception e) {
//			e.printStackTrace();
//		}
	}
}
	    
	
	
	
