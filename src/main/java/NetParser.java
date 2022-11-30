import org.apache.commons.io.FilenameUtils;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveAction;

public class NetParser extends RecursiveAction {

    private String url;
    private static CopyOnWriteArrayList<String> visitedSites = new CopyOnWriteArrayList<>();
    private static final String rootLink = Main.getRootUrl();

    public NetParser(String url) {
        this.url = url;
        visitedSites.add(url);
    }

    @Override
    protected void compute() {
        List<NetParser> subtasks = new ArrayList<>();
        int code = 0;
        Document document = null;

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Connection connection = Jsoup.connect(url);
            document = connection.get();
            code = connection.response().statusCode();
        } catch (HttpStatusException hsEx) {

            System.out.println("Ooops");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (document != null) {


            String linkForDB = url.replace(rootLink, "/");
            System.out.println(url + " === " + code);
            String content = document.html();
            HiberConnection.insertIntoTablePage(linkForDB, code, content);

            int pageId = 0;
            try {
                pageId = Integer.parseInt(HiberConnection.getPageId(linkForDB));
                System.out.println(pageId);
            } catch (Exception e) {
                System.out.println("Oops!");
            }


            String title = document.title();
            String body = document.body().text();
            String textInPage = body.concat(" ").concat(title);
            try {
                LemmasGetter.getLemmas(title).forEach((a,b) -> System.out.println(a + " === " + b));
                HiberConnection.insertIntoTablesLemmaAndIndex(textInPage, title, body, pageId);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements elements = document.select("a[href]");
            for (Element e : elements) {
                String childLink = e.attr("abs:href");

                if (visitedSites.contains(childLink)) {
                    continue;
                }

                if (isValid(childLink)) {
                    NetParser task = new NetParser(childLink);
                    task.fork();
                    subtasks.add(task);
                }
            }
            for (NetParser task : subtasks) {
                task.join();
            }
        }
    }
    private boolean isValid(String url) {
        return url.startsWith(rootLink)
                && !url.contains("?month")
                && !url.contains("login?")
                && !url.contains("#")
                && !url.contains("_info")
                && !FilenameUtils.getExtension(url).matches("(pptx|jpg|png|gif|bmp|pdf|xml|doc|ppt|docx|JPG|eps|PDF|jpeg|xlsx|webp|xls|nc|zip|m|fig)");
    }
}