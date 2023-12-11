package zju;

import com.goikosoft.crawler4j.crawler.Page;
import com.goikosoft.crawler4j.crawler.WebCrawler;
import com.goikosoft.crawler4j.parser.HtmlParseData;
import com.goikosoft.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

public class ArxivCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && (href.startsWith("https://arxiv.org/list/") || href.startsWith("https://arxiv.org/abs/"));
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);
        if (!url.startsWith("https://arxiv.org/abs/")) {
            return;
        }

        if (page.getParseData() instanceof HtmlParseData htmlParseData) {
            String html = htmlParseData.getHtml();
            Document doc = Jsoup.parse(html);
            Elements title_elements = doc.select("h1.title");
            Elements author_elements = doc.select("div.authors");
            Elements abstract_elements = doc.select("blockquote.abstract.mathjax");
            Elements pdf_elements = doc.select("a.download-pdf");
            if (title_elements.isEmpty() || author_elements.isEmpty() || abstract_elements.isEmpty() || pdf_elements.isEmpty()) {
                return;
            }
            String title = title_elements.get(0).text();
            String author = author_elements.get(0).text();
            String abstract_text = abstract_elements.get(0).text();
            String pdf_url = "https://arxiv.org" + pdf_elements.get(0).attr("href");
            if (title.startsWith("Title:")) {
                title = title.substring(7);
            }
            if (author.startsWith("Authors:")) {
                author = author.substring(9);
            }
            if (abstract_text.startsWith("Abstract:")) {
                abstract_text = abstract_text.substring(10);
            }
            System.out.println("Title: " + title);
            System.out.println("Author: " + author);
            System.out.println("Abstract: " + abstract_text);
            System.out.println("PDF URL: " + pdf_url);
            Main.pdfDocs.add(new PDFDoc(title, author, abstract_text, null, pdf_url));
        }
    }

}
