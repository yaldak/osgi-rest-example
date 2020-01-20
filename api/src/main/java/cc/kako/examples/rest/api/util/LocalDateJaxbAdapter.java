package cc.kako.examples.rest.api.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateJaxbAdapter extends XmlAdapter<String, LocalDate> {
    public LocalDate unmarshal(final String text) throws Exception {
        return LocalDate.parse(text);
    }

    public String marshal(final LocalDate localDate) throws Exception {
        return localDate.toString();
    }
}
