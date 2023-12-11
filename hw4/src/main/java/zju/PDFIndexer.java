package zju;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;

public class PDFIndexer {

    private final IndexWriter writer;

    public PDFIndexer(String indexPath) throws IOException {
        Directory directory = FSDirectory.open(Path.of(indexPath));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(directory, config);
    }

    public void addDocument(PDFDoc pdfDoc) throws IOException {
        Document document = new Document();
        document.add(new TextField("title", pdfDoc.getTitle(), Field.Store.YES));
        document.add(new TextField("author", pdfDoc.getAuthor(), Field.Store.YES));
        document.add(new TextField("abstract", pdfDoc.getAbstractText(), Field.Store.YES));
        document.add(new TextField("text", pdfDoc.getText(), Field.Store.YES));
        document.add(new TextField("url", pdfDoc.getPdfUrl(), Field.Store.YES));
        writer.addDocument(document);
    }

    public void close() throws IOException {
        writer.close();
    }

}
