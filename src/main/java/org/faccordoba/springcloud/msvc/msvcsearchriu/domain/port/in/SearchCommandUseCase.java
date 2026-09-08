package org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.in;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;

public interface SearchCommandUseCase {
    /**
     * executes a search operation based on the provided domain data.
     * @param domain The domain data containing the search parameters.
     * @return A string representing the result of the search operation.
     */
    String executeSearch(SearchSession domain);
}
