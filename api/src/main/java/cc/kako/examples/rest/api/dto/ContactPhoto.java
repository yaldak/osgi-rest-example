package cc.kako.examples.rest.api.dto;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Indexed
@Table(name="CONTACTPHOTO")
public class ContactPhoto {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Lob
    private byte[] photo;

    //@OneToOne(fetch = FetchType.LAZY)
    //private Contact contact;

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
