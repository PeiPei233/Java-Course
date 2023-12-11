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

public class PDFSearcher {

    private final IndexSearcher searcher;

    public PDFSearcher(String indexPath) throws IOException {
        Directory directory = FSDirectory.open(Path.of(indexPath));
        DirectoryReader reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    public void searchText(String query) throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("text", analyzer);
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
            String text = doc.get("text");
            TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), scoreDoc.doc, "text", analyzer);
            TextFragment[] frags = highlighter.getBestTextFragments(tokenStream, text, true, 20);
            System.out.println("\033[1;46mDocument " + scoreDoc.doc + "\033[0m");
            System.out.println("Title: " + doc.get("title"));
            System.out.println("Author: " + doc.get("author"));
            System.out.println("URL: " + doc.get("url"));
            int count = 0;
            for (TextFragment frag : frags) {
                if ((frag != null) && (frag.getScore() > 0)) {;
                    count++;
                    System.out.println("\033[1;33mFragment " + count + "\033[0m");
                    System.out.println(frag.toString());
                }
            }
        }
    }

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
            String abstractText = doc.get("abstract");
            TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), scoreDoc.doc, "abstract", analyzer);
            TextFragment[] frags = highlighter.getBestTextFragments(tokenStream, abstractText, true, 20);
            System.out.println("\033[1;46mDocument " + scoreDoc.doc + "\033[0m");
            System.out.println("Title: " + doc.get("title"));
            System.out.println("Author: " + doc.get("author"));
            System.out.println("URL: " + doc.get("url"));
            int count = 0;
            for (TextFragment frag : frags) {
                if ((frag != null) && (frag.getScore() > 0)) {;
                    count++;
                    System.out.println("\033[1;33mFragment " + count + "\033[0m");
                    System.out.println(frag.toString());
                }
            }
        }
    }

    public void searchTitle(String query) throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("title", analyzer);
        Query q = parser.parse(query);
        TopDocs docs = searcher.search(q, 10);

        if (docs.totalHits.value == 0) {
            System.out.println("\033[1;31mNo results found.\033[0m");
            return;
        }
        System.out.println("\033[1;32mFound results in " + docs.totalHits.value + " documents.\033[0m");
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String title = doc.get("title");
            // highlight the query in title (ignore case)
            title = title.replaceAll("(?i)(" + query + ")", "\033[1;43m$0\033[0m");
            System.out.println("\033[1;46mDocument " + scoreDoc.doc + "\033[0m");
            System.out.println("Title: " + title);
            System.out.println("Author: " + doc.get("author"));
            System.out.println("URL: " + doc.get("url"));
        }
    }

    public void searchAuthor(String query) throws Exception {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("author", analyzer);
        Query q = parser.parse(query);
        TopDocs docs = searcher.search(q, 10);

        if (docs.totalHits.value == 0) {
            System.out.println("\033[1;31mNo results found.\033[0m");
            return;
        }
        System.out.println("\033[1;32mFound results in " + docs.totalHits.value + " documents.\033[0m");
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String author = doc.get("author");
            // highlight the query in author (ignore case)
            author = author.replaceAll("(?i)(" + query + ")", "\033[1;43m$0\033[0m");
            System.out.println("\033[1;46mDocument " + scoreDoc.doc + "\033[0m");
            System.out.println("Title: " + doc.get("title"));
            System.out.println("Author: " + author);
            System.out.println("URL: " + doc.get("url"));
        }
    }

}
