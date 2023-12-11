package zju;

import com.goikosoft.crawler4j.crawler.CrawlConfig;
import com.goikosoft.crawler4j.crawler.CrawlController;
import com.goikosoft.crawler4j.fetcher.PageFetcher;
import com.goikosoft.crawler4j.robotstxt.RobotstxtConfig;
import com.goikosoft.crawler4j.robotstxt.RobotstxtServer;

import java.util.HashSet;
import java.util.Scanner;

public class Main {

    public static HashSet<PDFDoc> pdfDocs;  // store pdf docs found by crawler

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equalsIgnoreCase("search")) {
            search();
        } else {
            createIndexData();
        }
    }

    /**
     * Create index data.
     */
    public static void createIndexData() throws Exception {
        pdfDocs = new HashSet<>();

        String crawlStorageFolder = "data/crawl/root";  // folder to store crawl data
        int numberOfCrawlers = 32;                      // number of concurrent threads

        CrawlController controller = getCrawlController(crawlStorageFolder);
        controller.start(ArxivCrawler.class, numberOfCrawlers);

        System.out.println("Total pdf found: " + pdfDocs.size());

        PDFIndexer indexer = new PDFIndexer("data/index");
        int count = 0;  // count number of pdf indexed
        for (PDFDoc pdfDoc : pdfDocs) {
            try {   // parse pdf document
                pdfDoc.parse();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            System.out.println("Title: " + pdfDoc.getTitle());
            try {   // index pdf document
                indexer.addDocument(pdfDoc);
            } catch (Exception e) {
                continue;
            }
            System.out.println("Indexed: " + pdfDoc.getTitle());
            count++;
        }
        System.out.println("Total pdf indexed: " + count);
        indexer.close();
    }

    /**
     * Get crawl controller.
     *
     * @param crawlStorageFolder crawl storage folder
     */
    private static CrawlController getCrawlController(String crawlStorageFolder) throws Exception {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(2);    // max depth of crawling

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer =
                new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller =
                new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("https://arxiv.org/list/cs.CV/recent");  // add seed url

        return controller;
    }

    /**
     * Search by user input.
     */
    public static void search() {
        Scanner scanner = new Scanner(System.in);
        try {
            PDFSearcher searcher = new PDFSearcher("data/index");

            // print welcome message
            System.out.println("Welcome to Arxiv Searcher!");
            System.out.println("You can search by title, author, abstract or content.");
            System.out.println("Use following commands to search:");
            System.out.println("title [query]: search with default field `title`");
            System.out.println("author [query]: search with default field `author`");
            System.out.println("abstract [query]: search with default field `abstract`");
            System.out.println("text [query]: search with default field `text`");
            System.out.println("quit: quit");
            System.out.println("Type 'help' to show this help.");
            System.out.println();
            System.out.println("Tips: ");
            System.out.println("1. You can use `title:`, `author:`, `abstract:` or `text:` to specify the field.");
            System.out.println("2. You can use `AND`, `OR` or `NOT` to combine queries.");
            System.out.println("3. You can use `(` and `)` to group queries.");
            System.out.println("Type 'tips' to show more tips.");
            System.out.println();

            while (true) {
                try {
                    System.out.print("Query: ");    // print prompt
                    String query = scanner.nextLine();

                    // parse query type
                    int queryTypeIndex = query.indexOf(" ");
                    String queryType;
                    if (queryTypeIndex == -1) {
                        queryType = query;
                        query = "";
                    } else {
                        queryType = query.substring(0, queryTypeIndex);
                        query = query.substring(queryTypeIndex + 1);
                    }
                    if (queryType.startsWith("tit") && "title".startsWith(queryType)) {             // search by title
                        searcher.searchTitle(query);
                    } else if (queryType.startsWith("au") && "authors".startsWith(queryType)) {     // search by author
                        searcher.searchAuthor(query);
                    } else if (queryType.startsWith("ab") && "abstract".startsWith(queryType)) {    // search by abstract
                        searcher.searchAbstract(query);
                    } else if (queryType.startsWith("te") && "text".startsWith(queryType)) {        // search by text
                        searcher.searchText(query);
                    } else if (queryType.startsWith("q") && "quit".startsWith(queryType)) {         // quit
                        System.out.println("Bye.");
                        break;
                    } else if (queryType.startsWith("h") && "help".startsWith(queryType)) {         // show help
                        System.out.println("You can search by title, author, abstract or content.");
                        System.out.println("Use following commands to search:");
                        System.out.println("title [query]: search with default field `title`");
                        System.out.println("author [query]: search with default field `author`");
                        System.out.println("abstract [query]: search with default field `abstract`");
                        System.out.println("text [query]: search with default field `text`");
                        System.out.println("tips: show query tips.");
                        System.out.println("quit: quit");
                        System.out.println("help: show this help.");
                        System.out.println();
                    } else if (queryType.startsWith("tip") && "tips".startsWith(queryType)) {       // show tips
                        System.out.println("Tips: ");
                        System.out.println("1.  You can use `title:`, `author:`, `abstract:` or `text:` to specify the field.");
                        System.out.println("2.  You can use `AND`, `OR` or `NOT` to combine queries.");
                        System.out.println("3.  You can use `(` and `)` to group queries.");
                        System.out.println("4.  You can use `\"` to search for an exact phrase.");
                        System.out.println("5.  You can use `*` to search for a wildcard.");
                        System.out.println("6.  You can use `~` to search for a fuzzy query.");
                        System.out.println("7.  You can use `^` to boost a term.");
                        System.out.println("8.  You can use `[]` to search for a range.");
                        System.out.println("9.  You can use `{}` to search for a set.");
                        System.out.println("10. You can use `\\` to escape special characters.");
                        System.out.println("11. You can use `+` to require a term.");
                        System.out.println("12. You can use `-` to exclude a term.");
                        System.out.println("13. You can use `?` to match a single character.");
                        System.out.println("14. You can use `|` to search for a term in multiple fields.");
                        System.out.println("15. You can use `&&` to require a term in multiple fields.");
                        System.out.println("16. You can use `||` to search for a term in multiple fields.");
                        System.out.println("See https://lucene.apache.org/core/9_8_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html#package.description for more details.");
                        System.out.println();
                    } else {    // invalid query type
                        System.out.println("Invalid query type.");
                    }
                } catch (Exception e) {     // invalid query
                    System.out.println("Invalid query.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        scanner.close();
    }

}