package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchDomainException;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisSearchCacheTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @Mock
    private ObjectMapper objectMapper;

    private RedisSearchCache redisSearchCache;
    private SearchSession session;

    @BeforeEach
    void setUp() {
        redisSearchCache = new RedisSearchCache(redisTemplate, objectMapper);
        session = new SearchSession(
                "redis-sess-1",
                "HOTEL01",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 5),
                List.of(25, 30),
                2
        );
    }

    @AfterEach
    void tearDown() {
        redisSearchCache = null;
        session = null;
    }

    @Test
    public void incrementCountShouldIncrementAndReturnValueTest() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("search:count:redis-sess-1")).thenReturn(5L);

        Integer result = redisSearchCache.incrementCount("redis-sess-1");

        assertThat(result).isEqualTo(5);
        verify(valueOperations).increment("search:count:redis-sess-1");
    }

    @Test
    public void incrementCountShouldThrowSearchDomainExceptionWhenRedisSystemExceptionOccursTest() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("search:count:redis-sess-1"))
                .thenThrow(new RedisSystemException("Redis down", new RuntimeException()));

        assertThatThrownBy(() -> redisSearchCache.incrementCount("redis-sess-1"))
                .isInstanceOf(SearchDomainException.class)
                .hasMessageContaining("Error al incrementar contador en Redis");
    }

    @Test
    public void getSearchSessionShouldReturnEmptyWhenKeyNotFoundInRedisTest() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("search:session:redis-sess-1")).thenReturn(null);

        Optional<SearchSession> result = redisSearchCache.getSearchSession("redis-sess-1");

        assertThat(result).isEmpty();
        verify(valueOperations).get("search:session:redis-sess-1");
    }

    @Test
    public void getSearchSessionShouldReturnSessionWithUpdatedCountWhenFoundInRedisTest() throws Exception {
        String json = "{\"sessionId\":\"redis-sess-1\"}";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("search:session:redis-sess-1")).thenReturn(json);
        when(objectMapper.readValue(json, SearchSession.class)).thenReturn(session);
        when(valueOperations.get("search:count:redis-sess-1")).thenReturn("7");

        Optional<SearchSession> result = redisSearchCache.getSearchSession("redis-sess-1");

        assertThat(result).isPresent();
        assertThat(result.get().sessionId()).isEqualTo("redis-sess-1");
        assertThat(result.get().count()).isEqualTo(7);
    }

    @Test
    public void getSearchSessionShouldReturnSessionWithInternalCountWhenCountKeyMissingTest() throws Exception {
        String json = "{\"sessionId\":\"redis-sess-1\"}";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("search:session:redis-sess-1")).thenReturn(json);
        when(objectMapper.readValue(json, SearchSession.class)).thenReturn(session);
        when(valueOperations.get("search:count:redis-sess-1")).thenReturn(null);

        Optional<SearchSession> result = redisSearchCache.getSearchSession("redis-sess-1");

        assertThat(result).isPresent();
        assertThat(result.get().sessionId()).isEqualTo("redis-sess-1");
        assertThat(result.get().count()).isEqualTo(2);
    }

    @Test
    public void getSearchSessionShouldReturnEmptyWhenJsonParsingFailsTest() throws Exception {
        String json = "invalid-json";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("search:session:redis-sess-1")).thenReturn(json);
        when(objectMapper.readValue(json, SearchSession.class)).thenThrow(new RuntimeException("Parsing error"));

        Optional<SearchSession> result = redisSearchCache.getSearchSession("redis-sess-1");

        assertThat(result).isEmpty();
    }

    @Test
    public void putSearchSessionShouldStoreSessionAndCountInRedisTest() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(session)).thenReturn("{\"sessionId\":\"redis-sess-1\"}");

        redisSearchCache.putSearchSession(session);

        verify(valueOperations).set("search:session:redis-sess-1", "{\"sessionId\":\"redis-sess-1\"}", Duration.ofHours(24));
        verify(valueOperations).setIfAbsent("search:count:redis-sess-1", "2", Duration.ofHours(24));
    }

    @Test
    public void putSearchSessionShouldThrowSearchDomainExceptionWhenSerializationFailsTest() throws Exception {
        when(objectMapper.writeValueAsString(session)).thenThrow(new JsonProcessingException("Serialization error") {});

        assertThatThrownBy(() -> redisSearchCache.putSearchSession(session))
                .isInstanceOf(SearchDomainException.class)
                .hasMessageContaining("Error serializing session to cache");
    }

    @Test
    public void markAsDirtyShouldAddKeyToDirtySetTest() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        redisSearchCache.markAsDirty("redis-sess-1");

        verify(setOperations).add("search:dirty:ids", "redis-sess-1");
    }

    @Test
    public void getDirtyIdsShouldReturnListOfDirtyKeysTest() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members("search:dirty:ids")).thenReturn(Set.of("sess-1", "sess-2"));

        List<String> dirtyIds = redisSearchCache.getDirtyIds();

        assertThat(dirtyIds).containsExactlyInAnyOrder("sess-1", "sess-2");
    }

    @Test
    public void getDirtyIdsShouldReturnEmptyListWhenDirtySetIsNullTest() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members("search:dirty:ids")).thenReturn(null);

        List<String> dirtyIds = redisSearchCache.getDirtyIds();

        assertThat(dirtyIds).isEmpty();
    }

    @Test
    public void clearDirtyShouldRemoveKeyFromDirtySetTest() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        redisSearchCache.clearDirty("redis-sess-1");

        verify(setOperations).remove("search:dirty:ids", "redis-sess-1");
    }
}
