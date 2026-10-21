package org.faccordoba.springcloud.msvc.msvcsearchriu.application.service;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.application.port.in.SearchCommandUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchEventPublisherPort;

public class SearchSessionService implements SearchCommandUseCase {
    private final SearchEventPublisherPort searchEventPublisher;

    public SearchSessionService(SearchEventPublisherPort searchEventPublisher) {
        this.searchEventPublisher = searchEventPublisher;
    }

    @Override
    public String executeSearch(SearchSession searchSession) {
        searchEventPublisher.publishSearchEvent(searchSession);
        return searchSession.sessionId();
    }
}
