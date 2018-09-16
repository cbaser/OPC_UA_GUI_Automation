package merging;

import java.io.BufferedReader;
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
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class Merger {

	private String path;
	private String todaysDate = "";
	private String mergedFile;
	private String tempFile;
	private ArrayList<File> files;
	private ArrayList<String> fileNames;
	private Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	private Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	public Map<String, PdfReader> filesToMerge;
	private List<String> titles;

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setFiles(String file1, String file2, String file3) {
		files = new ArrayList<>();
		files.add(new File(file1));
		files.add(new File(file2));
		files.add(new File(file3));
	}

	public void setPath(String path) {
		this.path = path;
		tempFile = path + File.separator + "output" + File.separator + "temp";

	}

	public void setFileNames() {
		fileNames = new ArrayList<>();
		for (File file : files) {
			fileNames.add(file.getName().substring(0, file.getName().indexOf("-")));
		}
	}

	public void setTodaysDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		todaysDate = cal.getTime().toString().replaceAll(" ", "-");

	}

	public void createResultFile() {
		try {
			mergedFile = path + File.separator + "merged-result-opcua" + String.join("-", fileNames) + todaysDate
					+ ".pdf";
			File file = new File(mergedFile);
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setDocuments() throws IOException {
		filesToMerge = new TreeMap<String, PdfReader>();
		for (File file : files)
			filesToMerge.put(file.getName(), new PdfReader(file.getAbsolutePath()));

	}

	public void readBookmarks() throws IOException {
		titles = new ArrayList<String>();
		PdfReader reader = new PdfReader(files.get(0).getAbsolutePath());
		List<HashMap<String, Object>> bookmarks = SimpleBookmark.getBookmark(reader);
		for (int i = 0; i < bookmarks.size(); i++) {
			HashMap<String, Object> bm = bookmarks.get(i);
			titles.add(((String) bm.get("Title")));
		}
		for (int i = 1; i < files.size(); i++) {
			ArrayList<String> otherList = new ArrayList<>();

			reader = new PdfReader(files.get(i).getAbsolutePath());
			bookmarks = SimpleBookmark.getBookmark(reader);
			for (int j = 0; j < bookmarks.size(); j++) {
				HashMap<String, Object> bm = bookmarks.get(j);
				otherList.add(((String) bm.get("Title")));
			}

			titles.retainAll(otherList);

		}
	}

	public void createTempContentFile() throws IOException {
		for (int i = 0; i < files.size(); i++) {

			PdfReader reader = new PdfReader(files.get(i).getAbsolutePath());
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			PrintWriter out = new PrintWriter(new FileOutputStream(tempFile + "_" + String.valueOf(i) + ".txt"));
			TextExtractionStrategy strategy;
			for (int j = 1; j <= reader.getNumberOfPages(); j++) {
				strategy = parser.processContent(j, new SimpleTextExtractionStrategy());
				out.println(strategy.getResultantText());
			}
			reader.close();
			out.flush();
			out.close();
		}		
	}

	public String txtToString(String filePath) throws IOException {
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				contentBuilder.append(sCurrentLine).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}

	public void createBarChart() throws IOException {
		for (int i = 0; i < titles.size(); i++) {
			if (!titles.get(i).contains("Networking")) {
				JFreeChart barChart = ChartFactory.createBarChart("Testing Results", titles.get(i),
						"Time (milliseconds)", createDataset(i), PlotOrientation.VERTICAL, true, true, false);

				CategoryItemRenderer renderer = ((CategoryPlot) barChart.getPlot()).getRenderer();

				renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
				renderer.setDefaultItemLabelsVisible(true);
				ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER);
				renderer.setDefaultPositiveItemLabelPosition(position);

				File file = new File(System.getProperty("user.dir") + File.separator + "output" + File.separator
						+ titles.get(i).replaceAll(" ", "") + ".jpg");
				ChartUtils.saveChartAsJPEG(file, barChart, 500, 300);
			} else
				continue;

		}

	}

	public CategoryDataset createDataset(int index) throws IOException {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		addToDataset(dataset, index);
		return dataset;
	}

	private Matcher findNthMatcherOccurance(int n, Pattern p, CharSequence src) {
		Matcher match = p.matcher(src);
		for (int i = 0; i <= n; i++)
			match.find();
		return match;
	}

	public void addToDataset(DefaultCategoryDataset dataset, int index) throws IOException {
		ArrayList<Double> test = new ArrayList<>();
		String regex = "(?<total>\\d+)\\s+(?<min>\\d+)\\s+(?<max>\\d+)\\s+(?<avg>\\d+)\\s+(?<co>\\d+.\\d+)+\\s+(?<std>\\d+.\\d+)";
		for (int i = 0; i < files.size(); i++) {
			String content = txtToString(tempFile + "_" + String.valueOf(i) + ".txt");
			Pattern pattern = Pattern.compile(regex);
			Matcher mat = findNthMatcherOccurance(index, pattern, content);
			dataset.addValue(Double.parseDouble(mat.group("total")), fileNames.get(i), "Total");
			dataset.addValue(Double.parseDouble(mat.group("min")), fileNames.get(i), "Min");
			dataset.addValue(Double.parseDouble(mat.group("max")), fileNames.get(i), "Max");
			dataset.addValue(Double.parseDouble(mat.group("avg")), fileNames.get(i), "Avg");
			dataset.addValue(Double.parseDouble(mat.group("co")), fileNames.get(i), "Co");
			dataset.addValue(Double.parseDouble(mat.group("std")), fileNames.get(i), "Std");
			test.add(Double.parseDouble(mat.group("total")));

		}
	}

	public void createMergedPdf() throws IOException, DocumentException {
		Document document = new Document();
		File file = new File(mergedFile);
		file.getParentFile().mkdirs();
		document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(mergedFile));
		document.open();
		Paragraph preface = new Paragraph();
		addEmptyLine(preface, 1);
		preface.add(new Paragraph("OPC UA Testing Platform", catFont));
		addEmptyLine(preface, 1);
		preface.add(new Paragraph("Report generated by: " + System.getProperty("user.name") + ", " + new Date(),
				smallBold));
		preface.add(new Paragraph("This document describes merged results of testing", smallBold));
		document.add(preface);

		for (int i = 0; i < titles.size(); i++) {
			if (!titles.get(i).contains("Networking")) {
				Paragraph paragraph = new Paragraph();
				addEmptyLine(paragraph, 1);
				document.add(paragraph);
				PdfOutline root = writer.getRootOutline();
				PdfOutline bookmarks = new PdfOutline(root,
						new PdfDestination(PdfDestination.FITH, writer.getVerticalPosition(true)), titles.get(i), true);
				File pictureFile = new File(
						path + File.separator + "output" + File.separator + titles.get(i).replaceAll(" ", "") + ".jpg");
				Image image = Image.getInstance(pictureFile.getAbsolutePath());
				document.add(image);
			}

		}
		document.close();
	}

	private void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	private void cleanUp() throws IOException {
		for (int i = 0; i < titles.size(); i++) {
			if (!titles.get(i).contains("Networking")) {
				File pictureFile = new File(
						path + File.separator + "output" + File.separator + titles.get(i).replaceAll(" ", "") + ".jpg");
				pictureFile.delete();
			}
		}

		for (int i = 0; i <= files.size(); i++) {
			File file = new File(path + File.separator + "output" + File.separator + "temp_" + i + ".txt");
			file.delete();

		}
	}

}
