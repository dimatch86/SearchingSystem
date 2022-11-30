import org.apache.commons.io.FilenameUtils;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Main {
    static List<String> links = new ArrayList<>();
    static String rootUrl = "https://ipfran.ru";

    static final String url = "https://ipfran.ru";
    static final String url2 = "https://et-cetera.ru/mobile";
    static final String query1 = "Несомненно, это одна из самых трудных и одновременно самых благородных тем"; //https://nikoartgallery.com/
    static final String query2 = "Единственная дочь, Бекки, звезда школы, внезапно погружается"; //https://www.svetlovka.ru/
    static final String query3 = "Выставка работ самолеты посвящена столетию"; //https://nikoartgallery.com/
    static final String query4 = "Это не те парни, которых в кино показывают"; //https://et-cetera.ru/mobile/
    static final String query5 = "Премьера музыкального мобильного устройства"; //http://www.playback.ru/
    static final String query6 = "Физический процесс";

    public static void main(String[] args) throws IOException, SQLException {


        ForkJoinPool pool = new ForkJoinPool();

        long start = System.currentTimeMillis();

        //HiberConnection.insertIntoTableField();
        //pool.invoke(new NetParser(url));
        SearchQueryProcessing.search(query6);

        System.out.println("Parsing duration: " + (System.currentTimeMillis() - start) + " ms");
        /*getLinks(url);
        rootUrl = "https://et-cetera.ru/mobile";
        getLinks(url2);*/

    }

    public static void getLinks(String path) throws IOException, SQLException {

        try {

            Connection connection = Jsoup.connect(path);
            Document document = connection.get();
            int statusCode = connection.response().statusCode();

            Elements elements = document.select("a[href]");
            String content = document.html();
            //String content = String.valueOf(document);
            String title = document.title();
            String body = document.body().text();
            String textInPage = body.concat(" ").concat(title);
            //getLemmas(title);
            System.out.println(path);
            //getLemmas(body).forEach((s,f) -> System.out.println(s + " === " + f));
            //insertLemmas(title);
            //HiberConnection.insertLemmas(title);

            HiberConnection.insertIntoTablePage(path, statusCode, content);
            HiberConnection.insertIntoTableLemma(textInPage);
            String pageId = "";
            /*try {
                pageId = HiberConnection.getPageId(path);
                System.out.println(pageId);
            } catch (Exception e) {
                System.out.println("Oops!");
            }*/

            for (Element e : elements) {
                String childLink = e.attr("abs:href");
                String linkForDB = e.attr("href");

                if (links.contains(childLink)) {
                    continue;
                }

                if (isValid(childLink)) {

                    //System.out.println(childLink + " ==== " + Thread.currentThread().getId());
                    links.add(childLink);
                    getLinks(childLink);
                }
            }

        } catch (HttpStatusException ht) {
            System.out.println(ht.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static boolean isValid(String url) {
        return url.startsWith(rootUrl)
                && !url.contains("?")
                && !url.contains("#")
                && !url.contains("_info")
                && !FilenameUtils.getExtension(url).matches("(jpg|png|gif|bmp|pdf|xml|doc|ppt|docx|JPG|eps|PDF|jpeg|xlsx|webp)");
    }
    public static String getRootUrl() {
        return url;
    }
}