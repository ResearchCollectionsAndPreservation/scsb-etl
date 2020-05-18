package org.recap.camel;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.HoldingsDetailsRepository;
import org.recap.repository.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by premkb on 28/6/17.
 */
@Service
public class EtlDataLoadDAOService {

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves the bibliographic entity.
     *
     * @param bibliographicEntity the bibliographic entity
     */
    @Transactional
    public void saveBibliographicEntity(BibliographicEntity bibliographicEntity) {
        bibliographicDetailsRepository.save(bibliographicEntity);
        flushAndClearSession();
    }

    /**
     * Saves list of bibliographic entity.
     *
     * @param bibliographicEntityList the bibliographic entity list
     */
    @Transactional
    public void saveBibliographicEntityList(List<BibliographicEntity> bibliographicEntityList) {
        bibliographicDetailsRepository.saveAll(bibliographicEntityList);
        flushAndClearSession();
    }

    /**
     * Saves holdings entity.
     *
     * @param holdingsEntity the holdings entity
     * @return the holdings entity
     */
    @Transactional
    public HoldingsEntity savedHoldingsEntity(HoldingsEntity holdingsEntity){
        HoldingsEntity savedHoldingsEntity = holdingsDetailsRepository.save(holdingsEntity);
        flushAndClearSession();
        return savedHoldingsEntity;
    }

    /**
     * Saves item entity.
     *
     * @param itemEntity the item entity
     * @return the item entity
     */
    @Transactional
    public ItemEntity saveItemEntity(ItemEntity itemEntity){
        ItemEntity savedItemEntity = itemDetailsRepository.save(itemEntity);
        flushAndClearSession();
        return savedItemEntity;
    }

    /**
     * Flush and clears the session.
     */
    public void flushAndClearSession() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Clears the session.
     */
    public void clearSession() {
        entityManager.clear();
    }
}
