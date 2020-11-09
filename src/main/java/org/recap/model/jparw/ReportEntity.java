package org.recap.model.jparw;

import org.recap.model.jpa.AbstractEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(
        name = "REPORT_T",
        schema = "RECAP",
        catalog = ""
)
@AttributeOverride(
        name = "id",
        column = @Column(
                name = "RECORD_NUM"
        )
)
public class ReportEntity extends AbstractEntity<Integer> {
    @Column(
            name = "FILE_NAME"
    )
    private String fileName;
    @Column(
            name = "TYPE"
    )
    private String type;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "CREATED_DATE"
    )
    private Date createdDate;
    @Column(
            name = "INSTITUTION_NAME"
    )
    private String institutionName;
    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL}
    )
    @JoinColumn(
            name = "RECORD_NUM"
    )
    private List<ReportDataEntity> reportDataEntities = new ArrayList();

    public ReportEntity() {
    }

    public void addAll(List<ReportDataEntity> reportDataEntities) {
        if (null == this.getReportDataEntities()) {
            reportDataEntities = new ArrayList();
        }

        this.reportDataEntities.addAll(reportDataEntities);
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getType() {
        return this.type;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public String getInstitutionName() {
        return this.institutionName;
    }

    public List<ReportDataEntity> getReportDataEntities() {
        return this.reportDataEntities;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setInstitutionName(final String institutionName) {
        this.institutionName = institutionName;
    }

    public void setReportDataEntities(final List<ReportDataEntity> reportDataEntities) {
        this.reportDataEntities = reportDataEntities;
    }
}
