package org.recap.repositoryrw;

import org.recap.model.jparw.ExportStatusEntity;
import org.recap.repository.jpa.BaseRepository;

public interface ExportStatusDetailsRepository extends BaseRepository<ExportStatusEntity> {

    ExportStatusEntity findByExportStatusCode(String statusCode);
}
