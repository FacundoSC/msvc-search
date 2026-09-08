package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.adapter;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchDomainException;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchNotFoundException;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchRepositoryPort;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.entity.SearchSessionEntity;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.mapper.SearchSessionMapper;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.repository.SearchSessionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class SearchRepositoryAdapter implements SearchRepositoryPort {
   private final SearchSessionMapper searchMapper;
   private final SearchSessionJpaRepository searchSessionJpaRepository;

    public SearchRepositoryAdapter(SearchSessionMapper searchMapper, SearchSessionJpaRepository searchSessionJpaRepository) {
        this.searchMapper = searchMapper;
        this.searchSessionJpaRepository = searchSessionJpaRepository;
    }

    @Override
    @Transactional
    public void save(SearchSession searchPayload) {
        SearchSessionEntity searchSessionEntity = searchMapper.toEntity(searchPayload);
        try {
            searchSessionJpaRepository.save(searchSessionEntity);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new SearchDomainException("Error saved session: " + e.getMessage());
        }

    }

    @Override
    public Optional<SearchSession> findBySearchId(String searchId) {
        return Optional.of(searchSessionJpaRepository.findBySearchId(searchId)
                .map(searchMapper::toDomain)
                .orElseThrow(() -> new SearchNotFoundException(searchId)));
    }

    @Override
    @Transactional
    public void updateVisitCount(String searchId, int count) {
        try {
            searchSessionJpaRepository.updateVisitCount(searchId,count);
        }
        catch (Exception e){
            // Convertir errores de BD a dominio
            throw new SearchDomainException("Error updated count: " + e.getMessage());
        }
    }
}
