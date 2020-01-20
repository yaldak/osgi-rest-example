package cc.kako.examples.rest.api.dto;

import cc.kako.examples.rest.api.util.LocalDateJaxbAdapter;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@Entity
@Indexed
@Table(name = "CONTACT")
public class Contact {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String company;

    @Column(nullable=false)
    @XmlJavaTypeAdapter(LocalDateJaxbAdapter.class)
    private LocalDate birthDate;

    @Column(nullable=false)
    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.YES)
    private String emailAddress;

    @Column(nullable=false)
    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.YES)
    private String phoneNumberPersonal;

    @Column(nullable=false)
    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.YES)
    private String phoneNumberWork;

    @Column(nullable=false)
    @Embedded
    @IndexedEmbedded
    private ContactAddress address;

    @Lob
    @XmlTransient
    private byte[] photo;

    /**
     * IDE-generated getters/setters; ideally, would generate builder methods here in a generate-sources phase
     **/
    public Long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumberPersonal() {
        return phoneNumberPersonal;
    }

    public void setPhoneNumberPersonal(final String phoneNumberPersonal) {
        this.phoneNumberPersonal = phoneNumberPersonal;
    }

    public String getPhoneNumberWork() {
        return phoneNumberWork;
    }

    public void setPhoneNumberWork(final String phoneNumberWork) {
        this.phoneNumberWork = phoneNumberWork;
    }

    public ContactAddress getAddress() {
        return address;
    }

    public void setAddress(final ContactAddress address) {
        this.address = address;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(final byte[] photo) {
        this.photo = photo;
    }
}
