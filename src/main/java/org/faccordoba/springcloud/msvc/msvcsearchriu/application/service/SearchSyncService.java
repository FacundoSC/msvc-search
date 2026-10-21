package org.faccordoba.springcloud.msvc.msvcsearchriu.application.service;

import org.faccordoba.springcloud.msvc.msvcsearchriu.application.port.in.SearchSyncUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchCachePort;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchSyncService implements SearchSyncUseCase {
    private static final Logger LOGGER = Logger.getLogger(SearchSyncService.class.getName());

    private final SearchCachePort searchCache;
    private final SearchRepositoryPort searchRepository;

    public SearchSyncService(SearchCachePort searchCache, SearchRepositoryPort searchRepository) {
        this.searchCache = searchCache;
        this.searchRepository = searchRepository;
    }

    @Override
    public void syncDirtyCounts() {
        List<String> dirtyIds = searchCache.getDirtyIds();
        for (String searchId : dirtyIds) {
            try {
                Optional<SearchSession> sessionOpt = searchCache.getSearchSession(searchId);
                if (sessionOpt.isPresent()) {
                    SearchSession session = sessionOpt.get();
                    searchRepository.updateVisitCount(searchId, session.count());
                    searchCache.clearDirty(searchId);
                }
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "Error sincronizando ID: " + searchId, exception);
            }
        }
    }
}
