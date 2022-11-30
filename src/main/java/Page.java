
import javax.persistence.*;
import javax.persistence.Index;
import java.util.List;

@Entity
@Table(name = "page", indexes = {@Index(name = "path_index", columnList = "path")})
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    //@Column(columnDefinition = "text")
    private String path;

    private int code;

    @Column(columnDefinition = "mediumtext")
    private String content;


    @ManyToMany(mappedBy = "pages")
    private List<Lemma> lemmas;

    public Page() {

    }

    public Page(String path, int code, String content) {
        this.path = path;
        this.code = code;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Lemma> getLemmas() {
        return lemmas;
    }

    public void setLemmas(List<Lemma> lemmas) {
        this.lemmas = lemmas;
    }
}

