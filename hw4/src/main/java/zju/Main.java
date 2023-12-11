package zju;

import com.goikosoft.crawler4j.crawler.CrawlConfig;
import com.goikosoft.crawler4j.crawler.CrawlController;
import com.goikosoft.crawler4j.fetcher.PageFetcher;
import com.goikosoft.crawler4j.robotstxt.RobotstxtConfig;
import com.goikosoft.crawler4j.robotstxt.RobotstxtServer;

import java.util.HashSet;
import java.util.Scanner;

public class Main {

    public static HashSet<PDFDoc> pdfDocs;
    public static final int limit = 100;

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equalsIgnoreCase("search")) {
            search();
        } else {
            createIndexData();
        }
    }

    public static void createIndexData() throws Exception {
        pdfDocs = new HashSet<>();

        String crawlStorageFolder = "data/crawl/root";
        int numberOfCrawlers = 16;

        CrawlController controller = getCrawlController(crawlStorageFolder);
        controller.start(ArxivCrawler.class, numberOfCrawlers);

        System.out.println(pdfDocs.size());

        PDFIndexer indexer = new PDFIndexer("data/index");
        int count = 0;
        for (PDFDoc pdfDoc : pdfDocs) {
            try {
                pdfDoc.parse();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            System.out.println("Title: " + pdfDoc.getTitle());
            try {
                indexer.addDocument(pdfDoc);
            } catch (Exception e) {
                continue;
            }
            System.out.println("Indexed: " + pdfDoc.getTitle());
            count++;
            if (count >= limit) {
                break;
            }
        }
        indexer.close();
    }

    private static CrawlController getCrawlController(String crawlStorageFolder) throws Exception {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setProxyHost("127.0.0.1");
        config.setProxyPort(7890);
        config.setMaxDepthOfCrawling(2);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer =
                new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller =
                new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("https://arxiv.org/list/cs.CV/recent");
        controller.addSeed("https://arxiv.org/list/cs.AI/recent");
        controller.addSeed("https://arxiv.org/list/cs.LG/recent");
        controller.addSeed("https://arxiv.org/list/cs.CL/recent");
        return controller;
    }

    public static void search() {
        Scanner scanner = new Scanner(System.in);
        try {
            PDFSearcher searcher = new PDFSearcher("data/index");
            while (true) {
                System.out.print("Query: ");
                String query = scanner.nextLine();
                int queryTypeIndex = query.indexOf(" ");
                String queryType;
                if (queryTypeIndex == -1) {
                    queryType = query;
                    query = "";
                } else {
                    queryType = query.substring(0, queryTypeIndex);
                    query = query.substring(queryTypeIndex + 1);
                }
                if (queryType.startsWith("ti")) {
                    searcher.searchTitle(query);
                } else if (queryType.startsWith("au")) {
                    searcher.searchAuthor(query);
                } else if (queryType.startsWith("ab")) {
                    searcher.searchAbstract(query);
                } else if (queryType.startsWith("c")) {
                    searcher.searchText(query);
                } else if (queryType.startsWith("q")) {
                    System.out.println("Bye.");
                    break;
                } else if (queryType.startsWith("h")) {
                    System.out.println("title: search title");
                    System.out.println("author: search author");
                    System.out.println("abstract: search abstract");
                    System.out.println("content: search content");
                    System.out.println("quit: quit the search engine");
                    System.out.println("help: show this help");
                } else {
                    System.out.println("Invalid query type.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}