import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IndexKey implements Serializable {

    static final long serialVersionUID = 1L;

    @Column(name = "page_id")
    private long pageId;

    @Column(name = "lemma_id")
    private long lemmaId;

    public IndexKey() {

    }

    public IndexKey(long pageId, long lemmaId) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public long getLemmaId() {
        return lemmaId;
    }

    public void setLemmaId(long lemmaId) {
        this.lemmaId = lemmaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexKey indexKey = (IndexKey) o;
        return pageId == indexKey.pageId && lemmaId == indexKey.lemmaId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageId, lemmaId);
    }
}
