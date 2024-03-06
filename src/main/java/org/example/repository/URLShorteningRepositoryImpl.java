package org.example.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Url;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class URLShorteningRepositoryImpl implements URLShorteningRepository{
    @Autowired
    private RedisTemplate<String, Url> redisTemplate;

    @Override
    public void saveOrUpdate(Url url) {
        try {
            redisTemplate.opsForValue().set(url.getShortUrl(), url);
        } catch (Exception e) {
            log.error("Encountered error while persisting URL"+e);
        }
    }

    @Override
    public Object findUrl(String shortUrl) {
        return redisTemplate.opsForValue().get(shortUrl);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

//    public void clearCache() {
//       redisTemplate.getConnectionFactory().getConnection().flushAll();
//    }

}
