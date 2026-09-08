package org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;

public interface SearchEventPublisherPort {
    void publishSearchEvent(SearchSession domainEvent);
}
