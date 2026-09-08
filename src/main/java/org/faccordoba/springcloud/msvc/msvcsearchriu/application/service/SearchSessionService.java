package org.faccordoba.springcloud.msvc.msvcsearchriu.application.service;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.in.SearchCommandUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchEventPublisherPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SearchSessionService implements SearchCommandUseCase {
    private final SearchEventPublisherPort searchEventPublisher;

    public SearchSessionService(SearchEventPublisherPort searchEventPublisher) {
        this.searchEventPublisher = searchEventPublisher;
    }



    @Transactional
    public String executeSearch(SearchSession searchSession) {
        //3. Publish the search event
        searchEventPublisher.publishSearchEvent(searchSession);
        //4. Return the search ID
        return searchSession.sessionId();
    }


}
