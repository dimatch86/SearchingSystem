import lombok.Data;
import org.hibernate.annotations.SQLInsert;
import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "lemma")
@SQLInsert(sql = "INSERT INTO lemma (id, lemma, frequency) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE frequency = frequency + 1")
public class Lemma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(unique = true)
    private String lemma;

    private Integer frequency;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "Index", joinColumns = {@JoinColumn(name = "lemma_id")},
    inverseJoinColumns = {@JoinColumn(name = "page_id")})
    private List<Page> pages;

    public Lemma() {

    }

    public Lemma(String lemma) {
        this.lemma = lemma;
    }

    public Lemma(String lemma, Integer frequency) {
        this.lemma = lemma;
        this.frequency = frequency;
    }

    /*public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/
}