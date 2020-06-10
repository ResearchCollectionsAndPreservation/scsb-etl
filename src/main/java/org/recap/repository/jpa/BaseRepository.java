package org.recap.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E extends org.recap.model.jpa.IdentifiableBase> extends JpaRepository<E, Long> {

}