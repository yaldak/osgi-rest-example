package cc.kako.examples.rest.api.dto;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import javax.persistence.Embeddable;

@Embeddable
public class ContactAddress {
    private String lineOne;

    private String lineTwo;

    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.YES)
    private String city;

    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.YES)
    private String state; /* i18n: or province */

    private String zipCode; /* i18n: or "postal code" */

    /**
     * IDE-generated getters/setters; ideally, would generate builder methods here in a generate-sources phase
     **/
    public String getLineOne() {
        return lineOne;
    }

    public void setLineOne(final String lineOne) {
        this.lineOne = lineOne;
    }

    public String getLineTwo() {
        return lineTwo;
    }

    public void setLineTwo(final String lineTwo) {
        this.lineTwo = lineTwo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }
}
