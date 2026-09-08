package org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out;


import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;

import java.util.Optional;

public interface SearchRepositoryPort {
    /**
     * Saves a SearchSession to the repository.
     */
    void save(SearchSession searchSession);

    /**
     * Finds a SearchSession by its searchId.
     */
    Optional<SearchSession> findBySearchId(String searchId);


    /**
     * Atomically increments the visit count for the given searchId.
     * Returns number of rows affected (should be 0 or 1).
     */
    void updateVisitCount(String searchId, int count);
}
