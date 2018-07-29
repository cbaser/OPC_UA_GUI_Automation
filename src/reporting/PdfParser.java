package reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfWriter;


public class PdfParser {
	private ArrayList<String> titles,contents;
	private Document document;
	private PdfWriter writer;
	private String path;
	  private  Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
	            Font.BOLD);
	  private  Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
	            Font.BOLD);
	 private  Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	            Font.BOLD);
//	private String dest = System.getProperty("user.dir")+File.separator+"output/result.pdf";
//	private String output=System.getProperty("user.dir")+File.separator+"output/";

		
	 private String dest = path+File.separator+
			 "OPC_UA_Testing_"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
			 +".pdf";
	 
	 
	public void startReporting() {
		startCreatingPdf();
		addMetaData();
		addTitle();
		addContent();
		document.close();
	}
	public void setPath(String path) {
		this.path = path;
	}
	
private void startCreatingPdf() {
		 try {
		File file = new File(dest);
		file.getParentFile().mkdirs();
		document = new Document();
		writer=	 PdfWriter.getInstance(document, new FileOutputStream(dest));
	    document.open();			 
		 }catch(Exception e) {
			 System.out.println(e.getMessage());
		 }
	}
	private void addMetaData() {
		document.addTitle("OPC UA Testing Platform");
		document.addAuthor("Cagatay Akin Baser");
		
	}
	private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
	private void addTitle() {
		try {
			Paragraph preface = new Paragraph();
			addEmptyLine(preface, 1);
	        preface.add(new Paragraph("OPC UA Testing Platform", catFont));
	        addEmptyLine(preface, 1);
	        preface.add(new Paragraph(
	                "Report generated by: " + System.getProperty("user.name") + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	                smallBold));
	        preface.add(new Paragraph(
	                "This document describes results of testing",
	                smallBold));
	        document.add(preface);
		}catch(Exception e) {
			e.printStackTrace();
		}
	
		
	}
	private void addContent() {
		for(int i=0;i<titles.size();i++) {
			try {
			Paragraph paragraph = new Paragraph();
			paragraph.add(new Paragraph(titles.get(i),subFont));
			addEmptyLine(paragraph, 1);
			document.add(paragraph);
			createGraphics(titles.get(i),contents.get(i));
		}catch(Exception e) {
			e.printStackTrace();
		}
		}
	}
	private void createGraphics(String title,String content) {
		if(title.contains("Networking"))
			createChart(title,content);
		else {
			createTable(content);
		}
		
	}	
	private void createTable(String content) {
		try {
			PdfPTable table = new PdfPTable(6);
			String[] lines = content.split(System.getProperty("line.separator"));
			createHeaders(table,lines);
			createRows(table,lines);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void createHeaders(PdfPTable table,String[] lines) throws Exception {	
		for(int i=0;i<lines.length;i++) {
			if(lines[i].contains("--"))
				continue;
			PdfPCell cell = new PdfPCell(new Phrase(getFirstWord(lines[i])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    table.addCell(cell);			
		    table.setHeaderRows(1);
		} 
		document.add(table);
		
	}
	private void createRows(PdfPTable table,String[] lines) throws Exception{
		for(int i=0;i<lines.length;i++) {
			if(lines[i].contains("--"))
				continue;
			PdfPCell cell = new PdfPCell(new Phrase(getLastWord(lines[i])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    table.addCell(cell);			
		} 
		document.add(table);
	}
	private String getFirstWord(String text) {
		if (text.indexOf(' ') > -1) {
			// Check if there is more than one word. 
			text= text.substring(0, text.indexOf(' '));
			if(text.contains("Standard"))
				return text+" Deviation";
				else
					if(text.contains("Coefficient"))
						return text+" ";
					else
						return text+ "\n Time (ms)";		
		}
		return text;
	}
	private String getLastWord(String text) {
		return text.substring(text.lastIndexOf(" ")+1);
	}
	
	
	
	private void createChart(String title,String content) {
		JFreeChart rttChart = ChartFactory.createLineChart(title,
				"Message Size (bytes)",
				"Rtt",
				createRttDataset(content),PlotOrientation.VERTICAL,true,true,false);
			
		JFreeChart bandWidthChart = ChartFactory.createLineChart(title,
				"Message Size (bytes)",
				"Bandwidth",
				createBandwidthDataset(content),PlotOrientation.VERTICAL,true,true,false);
		addChartToPdf(rttChart,"rtt");
		addChartToPdf(bandWidthChart,"bandwidth");
	}
	private void addChartToPdf(JFreeChart chart,String chartname) {
		try {
			File file = new File(path+File.separator+chartname+".jpg");
			ChartUtils.saveChartAsJPEG(file, chart, 500, 300);	
			Image image = Image.getInstance(file.getAbsolutePath());
			document.add(image);
			file.delete();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private DefaultCategoryDataset createRttDataset(String content) {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String regex = "(echo|ack)_str\\s+(?<size>\\d+)\\s+average rtt\\/request=(?<val>.*)";
		Pattern titlePattern = Pattern.compile(regex);
		Matcher matcher = titlePattern.matcher(content);
		while(matcher.find()) {
			dataset.addValue(Double.valueOf(matcher.group("size")), "rtt", matcher.group("val"));
		}

		return dataset;
		
	}
	private DefaultCategoryDataset createBandwidthDataset(String content) {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String regex = "(echo|ack)_str\\s+(?<size>\\d+)\\s+(?<val>.*)\\s+kB/s";
		Pattern titlePattern = Pattern.compile(regex);
		Matcher matcher = titlePattern.matcher(content);
		while(matcher.find()) {
			dataset.addValue(Double.valueOf(matcher.group("size")), "bandwidth", matcher.group("size"));
		}
		
		return dataset;
		
	}
	
	
	public ArrayList<String> getTitles() {
		return titles;
	}

	public void setTitles(ArrayList<String> titles) {
		this.titles = titles;
	}

	public ArrayList<String> getContents() {
		return contents;
	}

	public void setContents(ArrayList<String> contents) {
		this.contents = contents;
	}
}