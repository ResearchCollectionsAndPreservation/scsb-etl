package org.recap.model.jaxb.marc;

import org.recap.RecapConstants;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by SheikS on 6/22/2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "content", propOrder = {
        "collection"
})

@XmlRootElement(name = "content")
public class ContentType implements Serializable {
    /**
     * The Collection.
     */
    @XmlElement(required = true, nillable = true, namespace= RecapConstants.ETL_DATA_LOAD_NAMESPACE)
    protected CollectionType collection;

    /**
     * Gets collection.
     *
     * @return the collection
     */
    public CollectionType getCollection() {
        return collection;
    }

    /**
     * Sets collection.
     *
     * @param collection the collection
     */
    public void setCollection(CollectionType collection) {
        this.collection = collection;
    }
}
