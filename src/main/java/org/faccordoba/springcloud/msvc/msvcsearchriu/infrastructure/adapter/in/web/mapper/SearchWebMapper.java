package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.mapper;


import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.request.SearchRequest;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.CountSearchSessionResponse;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.SearchSessionResponse;

public interface SearchWebMapper {
     SearchSession toDomain(SearchRequest searchRequest,String searchId);
     SearchSessionResponse toSearchSessionResponse(String searchId);
     CountSearchSessionResponse toCountSearchSessionResponse(SearchSession searchSession);


}
