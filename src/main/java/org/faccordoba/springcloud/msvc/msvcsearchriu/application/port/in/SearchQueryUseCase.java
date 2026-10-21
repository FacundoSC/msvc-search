package org.faccordoba.springcloud.msvc.msvcsearchriu.application.port.in;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;

public interface SearchQueryUseCase {
    SearchSession count(String searchId);
}
