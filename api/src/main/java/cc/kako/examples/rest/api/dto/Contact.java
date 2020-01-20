package cc.kako.examples.rest.api.dto;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Indexed
@Table(name = "CONTACT")
public class Contact {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.YES)
    private String name;

    private String company;

    @Lob
    @XmlTransient
    private byte[] photo;

    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.YES)
    private String emailAddress;

    /**
     * IDE-generated getters/setters; ideally, would generate builder methods here in a generate-sources phase
     **/
    public Long getId() {
        return this.id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(final byte[] photo) {
        this.photo = photo;
    }
}
