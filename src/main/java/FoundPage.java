import java.util.Objects;

public class FoundPage {

    private String path;
    private double relevance;
    private String title;
    private String snippet;

    public FoundPage(String path, String title, String snippet, double relevance) {
        this.path = path;
        this.title = title;
        this.snippet = snippet;
        this.relevance = relevance;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoundPage foundPage = (FoundPage) o;
        return Double.compare(foundPage.relevance, relevance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, relevance, title);
    }

    @Override
    public String toString() {
        return  path + " ==== " + title + " ========= " + snippet + " ======= " + relevance;
    }
}
