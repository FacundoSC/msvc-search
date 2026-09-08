package org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;

import java.util.List;
import java.util.Optional;

public interface SearchCachePort {
    Integer incrementCount(String searchId);

    Optional<SearchSession> getSearchSession(String searchId);

    void putSearchSession(SearchSession session);

    void markAsDirty(String searchId);

    List<String> getDirtyIds();

    void clearDirty(String searchId);
}
