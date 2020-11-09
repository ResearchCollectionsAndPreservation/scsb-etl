
package org.recap.model.jparw;

import org.recap.model.jpa.AbstractEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
        name = "REPORT_DATA_T",
        schema = "RECAP",
        catalog = ""
)
@AttributeOverride(
        name = "id",
        column = @Column(
                name = "REPORT_DATA_ID"
        )
)
public class ReportDataEntity extends AbstractEntity<Integer> {
    @Column(
            name = "HEADER_NAME"
    )
    private String headerName;
    @Column(
            name = "HEADER_VALUE"
    )
    private String headerValue;
    @Column(
            name = "RECORD_NUM"
    )
    private String recordNum;

    public ReportDataEntity() {
    }

    public String getHeaderName() {
        return this.headerName;
    }

    public String getHeaderValue() {
        return this.headerValue;
    }

    public String getRecordNum() {
        return this.recordNum;
    }

    public void setHeaderName(final String headerName) {
        this.headerName = headerName;
    }

    public void setHeaderValue(final String headerValue) {
        this.headerValue = headerValue;
    }

    public void setRecordNum(final String recordNum) {
        this.recordNum = recordNum;
    }
}
