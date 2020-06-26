package org.recap.model.export;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by premkb on 29/9/16.
 */
@Data
public class DeletedRecord implements Serializable {
    private Bib bib;
}
