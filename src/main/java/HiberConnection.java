import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.Map;

public class HiberConnection {
    private static SessionFactory sessionFactory;
    private static Session session = HiberConnection.getSessionFactory().openSession();

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(Lemma.class);
                configuration.addAnnotatedClass(Field.class);
                configuration.addAnnotatedClass(Page.class);
                configuration.addAnnotatedClass(Index.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());

            } catch (Exception e) {
                System.out.println("Исключение!" + e);
            }
        }
        return sessionFactory;
    }
    public static void insertIntoTableField() {
        session.beginTransaction();
        session.save(new Field("title", "title", 1.0));
        session.save(new Field("body", "body", 0.8));
        session.getTransaction().commit();
    }

    public static synchronized void insertIntoTablePage(String link, int code, String content) {
        Transaction transaction = session.beginTransaction();
        session.save(new Page(link, code, content));
        transaction.commit();
    }

    public static synchronized void insertIntoTablesLemmaAndIndex(String text, String title, String body, long pageId) throws IOException {
        Map<String, Integer> lemmas = LemmasGetter.getLemmas(text);
        Map<String, Integer> lemmasInTitle = LemmasGetter.getLemmas(title);
        Map<String, Integer> lemmasInBody = LemmasGetter.getLemmas(body);
        session.beginTransaction();
        lemmas.forEach((lemma, count) -> {

            session.save(new Lemma(lemma));

            long lemmaId = Integer.parseInt(getLemmaId(lemma));
            double rank = LemmasGetter.getRank(lemmasInTitle, lemmasInBody, lemma);

            session.save(new Index(new IndexKey(pageId, lemmaId), pageId, lemmaId, rank));

        });
        session.getTransaction().commit();
        session.clear();
    }

    public static synchronized void insertIntoTableLemma(String text) throws IOException {
        Map<String, Integer> lemmas = LemmasGetter.getLemmas(text);
        session.beginTransaction();
        lemmas.forEach((lemma, count) -> session.save(new Lemma(lemma)));
        session.getTransaction().commit();
        session.clear();
    }

    public static String getLemmaId(String lemma) {
        return (session.createSQLQuery("select id from lemma where `lemma`='" + lemma + "'").uniqueResult()).toString();
    }
    public static synchronized String getPageId(String url) {
        return (session.createSQLQuery("select id from page where `path`='" + url + "'").uniqueResult()).toString();
    }

    public static Lemma getLemmaFromDB(String lemma) {
        Query query = session.createQuery("from Lemma where lemma = '" + lemma + "'");
        try {
            return (Lemma) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Слово " + "\"" + lemma + "\"" + " не найдено в базе");
        }
        return null;
    }

    public static Long getCountFromTablePage () {
        Query query = session.createQuery("SELECT COUNT(*) FROM Page");
        return (Long) query.getSingleResult();
    }

    public static double getRankFromDB(long pageId, long lemmaId) {
        Index index = session.get(Index.class, new IndexKey(pageId, lemmaId));
        return index.getRank();
    }

    public static String getWeight(String selector) {
        return (session.createSQLQuery("select weight from field where `selector`='" + selector + "'").uniqueResult()).toString();
    }
}