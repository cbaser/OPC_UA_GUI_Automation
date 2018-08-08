package merging;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfCopy.PageStamp;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
 
public class Merger {
 
	//private String firstFile = System.getProperty("user.dir")+File.separator+"output"+File.separator+"result_omega.pdf";
//	private String secondFile = System.getProperty("user.dir")+File.separator+"output"+File.separator+"result_raspberry.pdf";
	//private String tempFile = System.getProperty("user.dir")+File.separator+"output"+File.separator+"temp";
	private String path;
	private File[] files;
	private ArrayList<String >fileNames;
	
	
	private String todaysDate="";
	private String mergedFile;
	private  Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
  private  Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
 private  Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);
	
	
	public Map<String,PdfReader> filesToMerge;
	private List<String> titles;
	private JFreeChart barChart;
	
	public void setFiles(String file1,String file2,String file3) {
		files[0] = new File(file1);
		files[1] = new File(file2);
		files[2] = new File(file3);
	}
	public void setPath(String path) {
		this.path=path;
		
	}
	
	public void setFileNames() {
		fileNames = new ArrayList<>();
		for (File file:files) {
			fileNames.add(file.getName().substring(0, file.getName().lastIndexOf("_")));
			
			
		}
	}
	
	
	
	public void startMerging() {
		setFileNames();
		setTodaysDate();
		createResultFile();
		try {
			readBookmarks();
			createTempContentFile();
			setDocuments();
			createBarChart();
			createMergedPdf();
			cleanUp();
		//	createPdf(mergedFile);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public void setTodaysDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		todaysDate = cal.getTime().toString().replaceAll(" ", "_");
		
	}
	
	public void createResultFile() {
		//mergedFile= System.getProperty("user.dir")+File.separator+"output"+File.separator+"result_"+todaysDate+".pdf";
		mergedFile = path+File.separator+"merged_result"+todaysDate+".pdf";
		File file = new File(mergedFile);
		file.getParentFile().mkdirs();
	}
	
	public void setDocuments() throws IOException {
		filesToMerge = new TreeMap<String,PdfReader>();
		for(int i=0;i<files.length;i++) 
			filesToMerge.put(fileNames.get(i), new PdfReader(files[i].getAbsolutePath()));
	//		filesToMerge.put("Second", new PdfReader(files[i].getAbsolutePath()));
		
		
		

		
	}
	public void readBookmarks() throws IOException {
		titles = new ArrayList<String>();
		PdfReader reader = new PdfReader(files[0].getAbsolutePath());
		List<HashMap<String,Object>> bookmarks = SimpleBookmark.getBookmark(reader);
		for(int i = 0; i < bookmarks.size(); i++){
		    HashMap<String, Object> bm = bookmarks.get(i);
		    titles.add(((String)bm.get("Title")));
		}
	}
	
	public void createTempContentFile() throws IOException {
		/** This will be for loop of input files */
			//for (File file : files) {
		for(int i=0;i<files.length;i++) {
			
				PdfReader reader = new PdfReader(files[i].getAbsolutePath());
		        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		        PrintWriter out = new PrintWriter(new FileOutputStream(files[i].getAbsolutePath().substring(0, files[i].getAbsolutePath().lastIndexOf("."))+".txt"));
		        TextExtractionStrategy strategy;
		        for (int j = 1; j <= reader.getNumberOfPages(); j++) {
		            strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
		            out.println(strategy.getResultantText());
		        }
		        reader.close();
		        out.flush();
		        out.close();
			}

//		    PdfReader reader = new PdfReader(firstFile);
//	        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
//	        PrintWriter out = new PrintWriter(new FileOutputStream(tempFile+"_1.txt"));
//	        TextExtractionStrategy strategy;
//	        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
//	            strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
//	            out.println(strategy.getResultantText());
//	        }
//	        reader.close();
//	        out.flush();
//	        out.close();
//	         reader = new PdfReader(secondFile);
//	         parser = new PdfReaderContentParser(reader);
//	         out = new PrintWriter(new FileOutputStream(tempFile+"_2.txt"));
//	        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
//	            strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
//	            out.println(strategy.getResultantText());
//	        }
//	        reader.close();
//	        out.flush();
//	        out.close();
//		
	}
	
	public String txtToString(String filePath) throws IOException{
		StringBuilder contentBuilder = new StringBuilder();
	    try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
	    {
	 
	        String sCurrentLine;
	        while ((sCurrentLine = br.readLine()) != null)
	        {
	            contentBuilder.append(sCurrentLine).append("\n");
	        }
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	    return contentBuilder.toString();
	}
	public void createBarChart() throws IOException {
		for(String title :titles) {
			 barChart = ChartFactory.createBarChart
						("Testing Results",title, "Time(ms)", createDataset(),PlotOrientation.VERTICAL,           
						         true, true, false);		
					//File file = new File(System.getProperty("user.dir")+File.separator+"output"+File.separator+title.replaceAll(" ", "")+".jpg");
			 		File file = new File(path+File.separator+title.replaceAll(" ", "")+".jpg");
					ChartUtils.saveChartAsJPEG(file, barChart, 500, 300);
		}
	
			
	}
	public CategoryDataset createDataset() throws IOException {
		final DefaultCategoryDataset dataset=new DefaultCategoryDataset();	
		addToDataset(dataset);
		return dataset;	
	}
	public void addToDataset(DefaultCategoryDataset dataset) throws IOException {
		
			//for(File file : files) {
		
		for(int i=0;i<files.length;i++) {
				//String regex = "(?<total>\\d+)\\s(?<min>\\d+)\\s(?<max>\\d+)\\s(?<avg>\\d+)\\s(?<co>\\d+)\\s(?<std>\\d+)";
				String regex = "(?<total>\\d+)\\s(?<min>\\d+)\\s(?<max>\\d+)\\s(?<avg>\\d+)\\s(?<co>\\d+)\\s(?<std>\\d+)";
				//String regex = "(\\d+)\\s(\\d+)\\s(\\d+)\\s(\\d+)\\s(\\d+)\\s(\\d+)";
				String content=txtToString(files[i].getAbsolutePath().substring(0, files[i].getAbsolutePath().lastIndexOf("."))+".txt");
				Pattern titlePattern = Pattern.compile(regex);
				Matcher matcher = titlePattern.matcher(content);
				while(matcher.find()) {
					dataset.addValue(Double.valueOf(matcher.group("total")), fileNames.get(i), "Total");
					dataset.addValue(Double.valueOf(matcher.group("min")), fileNames.get(i), "Min");
					dataset.addValue(Double.valueOf(matcher.group("max")), fileNames.get(i), "Max");
					dataset.addValue(Double.valueOf(matcher.group("avg")), fileNames.get(i), "Avg");
					dataset.addValue(Double.valueOf(matcher.group("co")), fileNames.get(i), "Co");
					dataset.addValue(Double.valueOf(matcher.group("std")), fileNames.get(i), "Std");
				}
			}
		
//			
//			
//			
//			
//			 content=txtToString(tempFile+"_2.txt");
//			 titlePattern = Pattern.compile(regex);
//			 matcher = titlePattern.matcher(content);
//			 while(matcher.find()) {
//				 dataset.addValue(Double.valueOf(matcher.group("total")), "raspberry", "Total");
//				 dataset.addValue(Double.valueOf(matcher.group("min")), "raspberry", "Min");
//				 dataset.addValue(Double.valueOf(matcher.group("max")), "raspberry", "Max");
//				 dataset.addValue(Double.valueOf(matcher.group("avg")), "raspberry", "Avg");
//				 dataset.addValue(Double.valueOf(matcher.group("co")), "raspberry", "Co");
//				 dataset.addValue(Double.valueOf(matcher.group("std")), "raspberry", "Std");
			 }		
	
	public void createMergedPdf() throws IOException, DocumentException {
		Document document = new Document();
		File file = new File(mergedFile);
		file.getParentFile().mkdirs();
		document = new Document();
		PdfWriter writer=	 PdfWriter.getInstance(document, new FileOutputStream(mergedFile));
	    document.open();
		Paragraph preface = new Paragraph();
		addEmptyLine(preface, 1);
        preface.add(new Paragraph("OPC UA Testing Platform", catFont));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(
                "Report generated by: " + System.getProperty("user.name") + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallBold));
        preface.add(new Paragraph(
                "This document describes merged results of testing",
                smallBold));
        document.add(preface);
        
        for(int i=0;i<titles.size();i++) {
    	Paragraph paragraph = new Paragraph();
		//paragraph.add(new Paragraph(titles.get(i),subFont));
		addEmptyLine(paragraph, 1);
		document.add(paragraph);
		PdfOutline root = writer.getRootOutline();
		PdfOutline bookmarks = new PdfOutline(root, 
			    new PdfDestination(
			        PdfDestination.FITH, writer.getVerticalPosition(true)),
			        titles.get(i), true);
		
		//File pictureFile =new File(System.getProperty("user.dir")+File.separator+"output"+File.separator+titles.get(i).replaceAll(" ", "")+".jpg");
		File pictureFile = new File(path+File.separator+titles.get(i).replaceAll(" ", "")+".jpg");
		Image image = Image.getInstance(pictureFile.getAbsolutePath());
		document.add(image);
        
        }
        document.close();
	}

	
	private  void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
	private void cleanUp() throws IOException {
		for(int i=0;i<titles.size();i++) {
			//File pictureFile =new File(System.getProperty("user.dir")+File.separator+"output"+File.separator+titles.get(i).replaceAll(" ", "")+".jpg");
			File pictureFile =new File(path+File.separator+titles.get(i).replaceAll(" ", "")+".jpg");
			pictureFile.delete();
		}
		
		/** Will be available also other temp files */
		for(int i=0;i<files.length;i++) {
			//File file =new File(System.getProperty("user.dir")+File.separator+"output"+File.separator+"temp_"+i+".txt");
			File file = new File(files[i].getAbsolutePath().lastIndexOf(".")+".txt");
			file.delete();
			
		}
	}
	
}
