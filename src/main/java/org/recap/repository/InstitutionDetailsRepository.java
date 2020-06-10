package org.recap.repository;

import org.recap.model.jpa.InstitutionEntity;
import org.recap.repository.jpa.BaseRepository;

/**
 * Created by chenchulakshmig on 6/13/16.
 */
public interface InstitutionDetailsRepository extends BaseRepository<InstitutionEntity> {

    /**
     * Find by institution code institution entity.
     *
     * @param institutionCode the institution code
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionCode(String institutionCode);

    /**
     * Find by institution name institution entity.
     *
     * @param institutionName the institution name
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionName(String institutionName);

}
