package org.recap.model.export;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by premkb on 12/7/17.
 */
@Data
public class Bib implements Serializable {
    private String bibId;
    private String owningInstitutionBibId;
    private String owningInstitutionCode;
    private boolean deleteAllItems;
    private List<Item> items;
}
