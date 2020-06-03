package org.recap.model.export;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by premkb on 12/7/17.
 */
@Data
public class Item implements Serializable {
    private String itemId;
    private String owningInstitutionItemId;
    private String barcode;
}
