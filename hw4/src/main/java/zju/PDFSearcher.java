package zju;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Searcher for PDF documents.
 */
public class PDFSearcher {

    private final IndexSearcher searcher;

    /**
     * Create a PDF searcher.
     *
     * @param indexPath The path of index directory.
     */
    public PDFSearcher(String indexPath) throws IOException {
        Directory directory = FSDirectory.open(Path.of(indexPath));
        DirectoryReader reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    /**
     * Print highlighted title.
     *
     * @param doc         document id
     * @param analyzer    analyzer
     * @param highlighter highlighter
     * @throws Exception exception
     */
    void printTitle(int doc, StandardAnalyzer analyzer, Highlighter highlighter) throws Exception {
        Document document = searcher.doc(doc);
        String title = document.get("title");
        TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), doc, "title", analyzer);
        TextFragment[] frags = highlighter.getBestTextFragments(tokenStream, title, true, 20);
        String highlightedTitle = title;
        // print highlighted title
        for (TextFragment frag : frags) {
            if ((frag != null) && (frag.getScore() > 0)) {
                highlightedTitle = frag.toString();
            }
        }
        System.out.println("Title: " + highlightedTitle);
    }

    /**
     * Print highlighted author.
     *
     * @param doc         document id
     * @param analyzer    analyzer
     * @param highlighter highlighter
     * @throws Exception exception
     */
    void printAuthor(int doc, StandardAnalyzer analyzer, Highlighter highlighter) throws Exception {
        Document document = searcher.doc(doc);
        String author = document.get("author");
        TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), doc, "author", analyzer);
        TextFragment[] frags = highlighter.getBestTextFragments(tokenStream, author, true, 20);
        String highlightedAuthor = author;
        // print highlighted author
        for (TextFragment frag : frags) {
            if ((frag != null) && (frag.getScore() > 0)) {
                highlightedAuthor = frag.toString();
            }
        }
        System.out.println("Author: " + highlightedAuthor);
    }

    /**
     * Print highlighted abstract.
     *
     * @param doc         document id
     * @param analyzer    analyzer
     * @param highlighter highlighter
     * @throws Exception exception
     */
    void printAbstract(int doc, StandardAnalyzer analyzer, Highlighter highlighter) throws Exception {
        Document document = searcher.doc(doc);
        String abstractText = document.get("abstract");
        TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), doc, "abstract", analyzer);
        TextFragment[] frags = highlighter.getBestTextFragments(tokenStream, abstractText, true, 20);
        if (frags.length == 0) {    // no abstract matched
            return;
        }
        System.out.println("Abstract: ");
        for (TextFragment frag : frags) {
            if ((frag != null) && (frag.getScore() > 0)) {
                System.out.println(frag);
            }
        }
    }

    /**
     * Print highlighted text.
     *
     * @param doc         document id
     * @param analyzer    analyzer
     * @param highlighter highlighter
     * @throws Exception exception
     */
    void printText(int doc, StandardAnalyzer analyzer, Highlighter highlighter) throws Exception {
        Document document = searcher.doc(doc);
        String text = document.get("text");
        TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), doc, "text", analyzer);
        TextFragment[] frags = highlighter.getBestTextFragments(tokenStream, text, true, 20);
        if (frags.length == 0) {    // no text matched
            return;
        }
        int count = 0;
        for (TextFragment frag : frags) {
            if ((frag != null) && (frag.getScore() > 0)) {
                count++;
                System.out.println("\033[1;33mFragment " + count + "\033[0m");
                System.out.println(frag);
            }
        }
    }

    /**
     * Search by user input with default field `text`.
     *
     * @param query user input
     * @throws Exception exception
     */
    public void searchText(String query) throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("text", analyzer);
        Query q = parser.parse(query);
        TopDocs docs = searcher.search(q, 10);

        QueryScorer scorer = new QueryScorer(q);
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("\033[1;43m", "\033[0m");   // highlight with yellow
        Highlighter highlighter = new Highlighter(formatter, scorer);
        if (docs.totalHits.value == 0) {
            System.out.println("\033[1;31mNo results found.\033[0m");
            return;
        }
        System.out.println("\033[1;32mFound results in " + docs.totalHits.value + " documents.\033[0m");    // print number of results in green
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("\033[1;46mDocument " + scoreDoc.doc + "\033[0m");   // print document id in cyan
            printTitle(scoreDoc.doc, analyzer, highlighter);
            printAuthor(scoreDoc.doc, analyzer, highlighter);
            System.out.println("URL: " + doc.get("url"));
            printText(scoreDoc.doc, analyzer, highlighter);
        }
    }

    /**
     * Search by user input with default field `abstract`.
     *
     * @param query user input
     * @throws Exception exception
     */
    public void searchAbstract(String query) throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("abstract", analyzer);
        Query q = parser.parse(query);
        TopDocs docs = searcher.search(q, 10);

        QueryScorer scorer = new QueryScorer(q);
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("\033[1;43m", "\033[0m");
        Highlighter highlighter = new Highlighter(formatter, scorer);
        if (docs.totalHits.value == 0) {
            System.out.println("\033[1;31mNo results found.\033[0m");
            return;
        }
        System.out.println("\033[1;32mFound results in " + docs.totalHits.value + " documents.\033[0m");
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("\033[1;46mDocument " + scoreDoc.doc + "\033[0m");
            printTitle(scoreDoc.doc, analyzer, highlighter);
            printAuthor(scoreDoc.doc, analyzer, highlighter);
            System.out.println("URL: " + doc.get("url"));
            printAbstract(scoreDoc.doc, analyzer, highlighter);
        }
    }

    /**
     * Search by user input with default field `title`.
     *
     * @param query user input
     * @throws Exception exception
     */
    public void searchTitle(String query) throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("title", analyzer);
        Query q = parser.parse(query);
        TopDocs docs = searcher.search(q, 10);

        QueryScorer scorer = new QueryScorer(q);
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("\033[1;43m", "\033[0m");
        Highlighter highlighter = new Highlighter(formatter, scorer);
        if (docs.totalHits.value == 0) {
            System.out.println("\033[1;31mNo results found.\033[0m");
            return;
        }
        System.out.println("\033[1;32mFound results in " + docs.totalHits.value + " documents.\033[0m");
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("\033[1;46mDocument " + scoreDoc.doc + "\033[0m");
            printTitle(scoreDoc.doc, analyzer, highlighter);
            printAuthor(scoreDoc.doc, analyzer, highlighter);
            System.out.println("URL: " + doc.get("url"));
        }
    }

    /**
     * Search by user input with default field `author`.
     *
     * @param query user input
     * @throws Exception exception
     */
    public void searchAuthor(String query) throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("author", analyzer);
        Query q = parser.parse(query);
        TopDocs docs = searcher.search(q, 10);

        QueryScorer scorer = new QueryScorer(q);
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("\033[1;43m", "\033[0m");
        Highlighter highlighter = new Highlighter(formatter, scorer);
        if (docs.totalHits.value == 0) {
            System.out.println("\033[1;31mNo results found.\033[0m");
            return;
        }
        System.out.println("\033[1;32mFound results in " + docs.totalHits.value + " documents.\033[0m");
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("\033[1;46mDocument " + scoreDoc.doc + "\033[0m");
            printTitle(scoreDoc.doc, analyzer, highlighter);
            printAuthor(scoreDoc.doc, analyzer, highlighter);
            System.out.println("URL: " + doc.get("url"));
        }
    }

}
