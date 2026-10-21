package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.scheduler;

import org.faccordoba.springcloud.msvc.msvcsearchriu.application.port.in.SearchSyncUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SearchSyncScheduler {
    private final SearchSyncUseCase searchSyncUseCase;

    public SearchSyncScheduler(SearchSyncUseCase searchSyncUseCase) {
        this.searchSyncUseCase = searchSyncUseCase;
    }

    @Scheduled(fixedRate = 60000)
    public void syncCountsToDatabase() {
        searchSyncUseCase.syncDirtyCounts();
    }
}
