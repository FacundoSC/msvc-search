package org.faccordoba.springcloud.msvc.msvcsearchriu.application.service;


import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchCachePort;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class SearchSyncScheduler {
    private final Logger logger = LoggerFactory.getLogger(SearchSyncScheduler.class);
    private final SearchCachePort searchCache;
    private final SearchRepositoryPort searchRepository;

    public SearchSyncScheduler(SearchCachePort searchCache, SearchRepositoryPort searchRepository) {
        this.searchCache = searchCache;
        this.searchRepository = searchRepository;
    }

    /**
     * Job que se ejecuta cada 5 minutos para sincronizar los contadores de Redis con Oracle.
     */
    @Scheduled(fixedRate = 60000) // 1 minutos
    public void syncCountsToDatabase() {
        logger.info("Iniciando sincronización de contadores Redis -> Oracle");

        List<String> dirtyIds = searchCache.getDirtyIds();
        if (dirtyIds.isEmpty()) {
            logger.info("No hay IDs sucios para sincronizar.");
            return;
        }
        logger.info("Sincronizando {} sesiones...", dirtyIds.size());
        for (String searchId : dirtyIds) {
            try {
                // 1. Obtener el contador actual de Redis
                // Necesitamos un método en el puerto para obtener solo el count o la sesión completa
                Optional<SearchSession> sessionOpt = searchCache.getSearchSession(searchId);
                if (sessionOpt.isPresent()) {
                    SearchSession session = sessionOpt.get();
                    // 2. Actualizar Oracle (UPDATE ... SET count = ? WHERE id = ?)
                    searchRepository.updateVisitCount(searchId, session.count());

                    // 3. Limpiar marca de sucio
                    searchCache.clearDirty(searchId);

                    logger.debug("Sincronizado ID: {} -> Count: {}", searchId, session.count());
                }
            } catch (Exception e) {
                logger.error("Error sincronizando ID: {}", searchId, e);
                // No limpiar el dirty para reintentar en el siguiente ciclo
            }
        }
        logger.info("Sincronización finalizada.");
    }
}
