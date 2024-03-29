package zju;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * PDF document.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDFDoc {
    private String title;
    private String author;
    private String abstractText;
    private String text;
    private String pdfUrl;

    /**
     * Parse the pdf document by pdf url and set the text.
     */
    public void parse() throws Exception {
        // get pdf document from url
        HttpURLConnection httpURLConnection = (HttpURLConnection) URI.create(pdfUrl).toURL().openConnection();
        httpURLConnection.setRequestMethod("GET");
        InputStream inputStream = httpURLConnection.getInputStream();

        // parse pdf document
        PDDocument document = Loader.loadPDF(inputStream);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        setText(pdfTextStripper.getText(document));
    }

}
