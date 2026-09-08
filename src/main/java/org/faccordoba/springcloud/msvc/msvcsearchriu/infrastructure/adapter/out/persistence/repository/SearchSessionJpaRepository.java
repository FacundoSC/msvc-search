package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.repository;

import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.entity.SearchSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SearchSessionJpaRepository extends JpaRepository<SearchSessionEntity, Integer> {

    @Query("SELECT s FROM SearchSessionEntity s WHERE s.searchId = :searchId")
    Optional<SearchSessionEntity> findBySearchId(@Param("searchId") String searchId);

    @Modifying
    @Query("UPDATE SearchSessionEntity s SET s.visitCount = :count, s.updatedAt = CURRENT_TIMESTAMP WHERE s.searchId = :searchId")
    void updateVisitCount(@Param("searchId") String searchId, @Param("count") Integer count);

}
