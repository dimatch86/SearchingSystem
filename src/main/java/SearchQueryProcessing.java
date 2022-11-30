import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.*;
public class SearchQueryProcessing {

    private static long recordsCount = HiberConnection.getCountFromTablePage();
    private static List<Lemma> listOfValidQueryLemmas = new ArrayList<>();
    private static List<FoundPage> pageList = new ArrayList<>();

    public static void search(String query) throws IOException {

        long start = System.currentTimeMillis();
        Set<String> lemmasFromQuery = LemmasGetter.getLemmas(query).keySet();
        lemmasFromQuery.forEach(System.out::println);

        lemmasFromQuery.forEach(lfq -> {
            Lemma lemmaFromDB = HiberConnection.getLemmaFromDB(lfq);
            if (!(lemmaFromDB == null) && (!((float) lemmaFromDB.getFrequency()/recordsCount > 0.85))) {

                listOfValidQueryLemmas.add(lemmaFromDB);
            }
        });

        Comparator<Lemma> compareByFrequency = Comparator.comparing(Lemma::getFrequency);
        listOfValidQueryLemmas.sort(compareByFrequency);
        listOfValidQueryLemmas.forEach(s -> System.out.println(s.getLemma() + " ==== " + s.getFrequency() + " === " + s.getPages().size()));

        Lemma rarestLemma;
        try {
            rarestLemma = listOfValidQueryLemmas.get(0);
            List<Page> pages = rarestLemma.getPages();

            pages.forEach(page -> {
                double relevance = 0.0;
                List<Lemma> lemmasOnPage = page.getLemmas();

                if (lemmasOnPage.containsAll(listOfValidQueryLemmas)) {
                    for (Lemma lemma : listOfValidQueryLemmas) {
                        double rank = HiberConnection.getRankFromDB(page.getId(), lemma.getId());
                        relevance += rank;
                    }
                    String body = Jsoup.parse(page.getContent()).body().text();

                    //System.out.println(" =======================  ");
                    String title = Jsoup.parse(page.getContent()).title();
                    String snippet = getSnippet(body);
                    pageList.add(new FoundPage(page.getPath(), title, snippet, relevance));
                }
            });

        } catch (Exception e) {
            System.out.println("Нет валидных лемм для запроса. Введите новый запрос");
        }

        Comparator<FoundPage> compareByRelevance = Comparator.comparing(FoundPage::getRelevance).reversed();

        pageList.sort(compareByRelevance);
        pageList.forEach(System.out::println);
        System.out.println(pageList.size());
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(recordsCount);
    }

    public static String getSnippet(String body) {

        Set<String> uniqueSentence = new HashSet<>();
        Set<String> setOfQueryWords = new HashSet<>();

        String[] sentences = body.split("\\.");

        String[] contentWords = body.split("\\s");
        Set<String> setOfContentWords = new HashSet<>(Arrays.asList(contentWords));

        for (String wordOfContent : setOfContentWords) {
            try {
                Set<String> lemmaOfContentWord = LemmasGetter.getLemmas(wordOfContent).keySet();
                listOfValidQueryLemmas.forEach(validLemma -> {
                    if (lemmaOfContentWord.contains(validLemma.getLemma())) {
                        for (String sentenceOfBody : sentences) {
                            if (sentenceOfBody.contains(wordOfContent)) {
                                setOfQueryWords.add(wordOfContent);
                                uniqueSentence.add(sentenceOfBody);
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String snippet = "";
        for (String uS : uniqueSentence) {
            for (String qW : setOfQueryWords) {
                uS = uS.replaceAll(qW, "<b>" + qW + "</b>");
            }
            int end;
            if ((uS.length() - uS.lastIndexOf("</b>")) > 20) {

                end = uS.lastIndexOf("</b>") + 20;
            } else {
                end = uS.length() - 1;
            }
            snippet = uS.trim().substring(0, end).concat("...");
            break;
        }
        return snippet;
    }
}