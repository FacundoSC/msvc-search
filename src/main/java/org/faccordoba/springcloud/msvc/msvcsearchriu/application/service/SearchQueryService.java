package org.faccordoba.springcloud.msvc.msvcsearchriu.application.service;

import org.faccordoba.springcloud.msvc.msvcsearchriu.application.exception.SearchNotFoundException;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.in.SearchQueryUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchCachePort;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SearchQueryService implements SearchQueryUseCase {

    private final SearchRepositoryPort searchRepository;
    private final SearchCachePort searchCache;

    public SearchQueryService(SearchRepositoryPort searchRepository,
                              SearchCachePort searchCache) {
        this.searchRepository = searchRepository;
        this.searchCache = searchCache;
    }

    @Override
    public SearchSession count(String searchId) {
        // 1. Intentar obtener de caché
        Optional<SearchSession> cachedOpt = searchCache.getSearchSession(searchId);

        if (cachedOpt.isPresent()) {
            // Incrementar contador en Redis (Atómico)
            long newCount = searchCache.incrementCount(searchId);

            // Marcar como "sucio" para que el scheduler lo sincronice a Oracle
            searchCache.markAsDirty(searchId);

            // Actualizar el objeto en memoria para devolverlo (sin volver a leer Redis)
            SearchSession session = cachedOpt.get();
            return new SearchSession(
                    session.sessionId(),
                    session.hotelId(),
                    session.checkIn(),
                    session.checkOut(),
                    session.ages(),
                    (int) newCount
            );
        }

        // 2. Cache Miss: Buscar en Oracle
        Optional<SearchSession> dbSession = searchRepository.findBySearchId(searchId);
        if (dbSession.isEmpty()) {
            throw new SearchNotFoundException("Search session not found for id: " + searchId);
        }

        SearchSession session = dbSession.get();

        // 3. Cargar en caché (con el count actual de BD)
        searchCache.putSearchSession(session);

        // 4. Incrementar contador en Redis
        long newCount = searchCache.incrementCount(searchId);

        // 5. Marcar como sucio
        searchCache.markAsDirty(searchId);

        return new SearchSession(
                session.sessionId(),
                session.hotelId(),
                session.checkIn(),
                session.checkOut(),
                session.ages(),
                (int) newCount
        );
    }
}