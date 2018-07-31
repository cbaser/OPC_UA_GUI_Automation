package indevelopment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfCopy.PageStamp;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
 
public class Merger {

	private String firstFile = System.getProperty("user.dir")+File.separator+"output"+File.separator+"result_omega.pdf";
	private String secondFile = System.getProperty("user.dir")+File.separator+"output"+File.separator+"result_second.pdf";
	private String todaysDate;
	private String mergedFile = System.getProperty("user.dir")+File.separator+"output"+File.separator+"result_"+todaysDate+".pdf";
	
	public Map<String,PdfReader> filesToMerge;
	
	
	public void startMerging() {
		setTodaysDate();
		createResultFile();
		try {
			setDocuments();
			createPdf(mergedFile);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public void setTodaysDate() {
		Calendar today = Calendar.getInstance();
		today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
		todaysDate = today.getTime().toString();
		
	}
	
	public void createResultFile() {
		File file = new File(mergedFile);
		file.getParentFile().mkdirs();
	}
	
	public void setDocuments() throws IOException {
		filesToMerge = new TreeMap<String,PdfReader>();
		filesToMerge.put("First", new PdfReader(firstFile));
		filesToMerge.put("Second", new PdfReader(secondFile));
		
	}
	public void createPdf(String filename) throws IOException, DocumentException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<Integer, String> toc = new TreeMap<Integer, String>();
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, baos);
        PageStamp stamp;
        document.open();
        int n;
        int pageNo = 0;
        PdfImportedPage page;
        Chunk chunk;
        for (Map.Entry<String, PdfReader> entry : filesToMerge.entrySet()) {
            n = entry.getValue().getNumberOfPages();
            toc.put(pageNo + 1, entry.getKey());
            for (int i = 0; i < n; ) {
                pageNo++;
                page = copy.getImportedPage(entry.getValue(), ++i);
                stamp = copy.createPageStamp(page);
                chunk = new Chunk(String.format("Page %d", pageNo));
                if (i == 1)
                    chunk.setLocalDestination("p" + pageNo);
                ColumnText.showTextAligned(stamp.getUnderContent(),
                        Element.ALIGN_RIGHT, new Phrase(chunk),
                        559, 810, 0);
                stamp.alterContents();
                copy.addPage(page);
            }
        }
        PdfReader reader = new PdfReader(secondFile);
        page = copy.getImportedPage(reader, 1);
        stamp = copy.createPageStamp(page);
        Paragraph p;
        PdfAction action;
        PdfAnnotation link;
        float y = 770;
        ColumnText ct = new ColumnText(stamp.getOverContent());
        ct.setSimpleColumn(36, 36, 559, y);
        for (Map.Entry<Integer, String> entry : toc.entrySet()) {
            p = new Paragraph(entry.getValue());
            p.add(new Chunk(new DottedLineSeparator()));
            p.add(String.valueOf(entry.getKey()));
            ct.addElement(p);
            ct.go();
            action = PdfAction.gotoLocalPage("p" + entry.getKey(), false);
            link = new PdfAnnotation(copy, 36, ct.getYLine(), 559, y, action);
            stamp.addAnnotation(link);
            y = ct.getYLine();
        }
        ct.go();
        stamp.alterContents();
        copy.addPage(page);
        document.close();
        for (PdfReader r : filesToMerge.values()) {
            r.close();
        }
        reader.close();
 
        reader = new PdfReader(baos.toByteArray());
        n = reader.getNumberOfPages();
        reader.selectPages(String.format("%d, 1-%d", n, n-1));
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(filename));
        stamper.close();
	}
	
}
