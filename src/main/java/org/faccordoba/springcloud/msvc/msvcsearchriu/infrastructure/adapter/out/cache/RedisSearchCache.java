package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchDomainException;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchCachePort;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class RedisSearchCache implements SearchCachePort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String SESSION_KEY_PREFIX = "search:session:";
    private static final String COUNT_KEY_PREFIX = "search:count:";
    private static final String DIRTY_SET_KEY = "search:dirty:ids";
    private static final Duration SESSION_TTL = Duration.ofHours(24);

    public RedisSearchCache(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public Integer incrementCount(String searchId) {
        try {
            return redisTemplate.opsForValue().increment(COUNT_KEY_PREFIX + searchId).intValue();
        } catch (RedisSystemException e) {
            throw new SearchDomainException("Error al incrementar contador en Redis: " + e.getMessage());        }
    }
    @Override
    public Optional<SearchSession> getSearchSession(String searchId) {
        String json = redisTemplate.opsForValue().get(SESSION_KEY_PREFIX + searchId);
        if (json == null) return Optional.empty();

        try {
            SearchSession session = objectMapper.readValue(json, SearchSession.class);
            // Recuperar el contador actual de Redis para tener el valor real
            String countStr = redisTemplate.opsForValue().get(COUNT_KEY_PREFIX + searchId);
            int currentCount = countStr != null ? Integer.parseInt(countStr) : session.count();

            // Devolver sesión con el count actualizado de Redis
            return Optional.of(new SearchSession(
                    session.sessionId(), session.hotelId(), session.checkIn(),
                    session.checkOut(), session.ages(), currentCount
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void putSearchSession(SearchSession session) {
        try {
            String json = objectMapper.writeValueAsString(session);
            redisTemplate.opsForValue().set(SESSION_KEY_PREFIX + session.sessionId(), json, SESSION_TTL);
            // Inicializar el contador en Redis si no existe (o usar el de la sesión)
            redisTemplate.opsForValue().setIfAbsent(
                    COUNT_KEY_PREFIX + session.sessionId(),
                    String.valueOf(session.count()),
                    SESSION_TTL
            );
        } catch (Exception e) {
            throw new SearchDomainException("Error serializing session to cache: " + e.getMessage());
        }
    }


    @Override
    public void markAsDirty(String searchId) {
        redisTemplate.opsForSet().add(DIRTY_SET_KEY, searchId);
    }


    @Override
    public List<String> getDirtyIds() {
        Set<String> members = redisTemplate.opsForSet().members(DIRTY_SET_KEY);
        return members != null ? new ArrayList<>(members) : List.of();
    }

    @Override
    public void clearDirty(String searchId) {
        redisTemplate.opsForSet().remove(DIRTY_SET_KEY, searchId);
    }
}