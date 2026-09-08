package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.mapper;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.entity.SearchSessionEntity;

public interface SearchSessionMapper {
    SearchSessionEntity toEntity(SearchSession searchPayload);
    SearchSession toDomain(SearchSessionEntity searchSessionEntity);
}
