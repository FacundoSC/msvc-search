package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.config;

import org.faccordoba.springcloud.msvc.msvcsearchriu.application.port.in.SearchCommandUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.application.port.in.SearchQueryUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.application.port.in.SearchSyncUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.application.service.SearchQueryService;
import org.faccordoba.springcloud.msvc.msvcsearchriu.application.service.SearchSessionService;
import org.faccordoba.springcloud.msvc.msvcsearchriu.application.service.SearchSyncService;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchCachePort;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchEventPublisherPort;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServicesConfig {
    @Bean
    public SearchCommandUseCase searchCommandUseCase(SearchEventPublisherPort searchEventPublisher) {
        return new SearchSessionService(searchEventPublisher);
    }

    @Bean
    public SearchQueryUseCase searchQueryUseCase(SearchRepositoryPort searchRepository, SearchCachePort searchCache) {
        return new SearchQueryService(searchRepository, searchCache);
    }

    @Bean
    public SearchSyncUseCase searchSyncUseCase(SearchCachePort searchCache, SearchRepositoryPort searchRepository) {
        return new SearchSyncService(searchCache, searchRepository);
    }
}
