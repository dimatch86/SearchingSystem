import javax.persistence.*;

@Entity
@Table(name = "`index`")
public class Index {

    @EmbeddedId
    private IndexKey key;

    @Column(name = "page_id", insertable = false, updatable = false)
    private Long pageId;

    @Column(name = "lemma_id", insertable = false, updatable = false)
    private Long lemmaId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Page page;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Lemma lemma;

    @Column(name = "`rank`")
    private double rank;

    public Index() {

    }

    public Index(long pageId, long lemmaId, double rank) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.rank = rank;
    }

    public Index(IndexKey key, Long pageId, Long lemmaId, double rank) {
        this.key = key;
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.rank = rank;
    }

    public IndexKey getKey() {
        return key;
    }

    public void setKey(IndexKey key) {
        this.key = key;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Lemma getLemma() {
        return lemma;
    }

    public void setLemma(Lemma lemma) {
        this.lemma = lemma;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
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
}