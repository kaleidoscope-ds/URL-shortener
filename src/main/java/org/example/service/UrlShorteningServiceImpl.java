package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.UrlNotFoundException;
import org.example.repository.URLShorteningRepository;
import org.example.model.Url;
import org.example.model.UrlAccessCounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UrlShorteningServiceImpl implements URLShorteningService {

    @Autowired
    public KeyGeneratorService keyGenService;

    @Autowired
    public URLShorteningRepository urlShorteningRepository;

    private static final String DEFAULT_URL = "DEFAULT_URL";

    /**
     * Generating a short url from a long url
     * @param longUrl
     * @return Url object containing a unique shortURL
     */
    public Url generateShortUrl(String longUrl) {
        String generatedShortUrl = keyGenService.create();
        UrlAccessCounts accessCounts = UrlAccessCounts.builder()
                .allTimeCount(0)
                .last24HoursCount(0)
                .pastWeekCount(0).build();
        //setting expiry to one month from now
        Url shortUrl = Url.builder().shortUrl(generatedShortUrl)
                .longUrl(longUrl)
                .lastFetched(LocalDateTime.now())
                .expiryDate(LocalDateTime.now()
                        .plusMonths(1))
                .urlAccessCounts(accessCounts).build();
        urlShorteningRepository.saveOrUpdate(shortUrl);
        return shortUrl;
    }

    /**
     * Redirecting a short url to a long url
     * @param shortUrl
     * @return longURL
     */
    public Url getLongURL(String shortUrl) {
        try {
            Object urlObject = urlShorteningRepository.findUrl(shortUrl);

            if (urlObject != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Url url = objectMapper.convertValue(urlObject, Url.class);
                updateAccessCounts(url);
                return url;
            }
            throw new UrlNotFoundException("URL not found for short URL: " + shortUrl);
        } catch(Exception e){
            log.error("Encountered error while serializing urlObject",e);
            return Url.builder().longUrl(DEFAULT_URL).build();
        }
    }

    /**
     * Fetch all the Access counts provided the short URL
     * @param shortUrl
     * @return
     */
    @Override
    public UrlAccessCounts getUrlAccessCounts(String shortUrl) {
        try {
            Object urlObject = urlShorteningRepository.findUrl(shortUrl);
            if (urlObject != null) {

                ObjectMapper objectMapper = new ObjectMapper();
                Url url = objectMapper.convertValue(urlObject, Url.class);
                return url.getUrlAccessCounts();
            }
            throw new UrlNotFoundException("URL not found for short URL: " + shortUrl);
        } catch(Exception e){
            log.error("Encountered error while serializing urlObject",e);
            return UrlAccessCounts.builder().build();
        }
    }


    /**
     * Update the counts after each fetch
     * @param url
     */
    @Override
    public void updateAccessCounts(Url url) {
        LocalDateTime now = LocalDateTime.now();

        url.getUrlAccessCounts().setAllTimeCount(url.getUrlAccessCounts().getAllTimeCount() + 1);
        if (url.getLastFetched().isAfter(now.minusHours(24))) {
            url.getUrlAccessCounts().setLast24HoursCount(url.getUrlAccessCounts().getLast24HoursCount() + 1);
        }
        if (url.getLastFetched().isAfter(now.minusWeeks(1))) {
            url.getUrlAccessCounts().setPastWeekCount(url.getUrlAccessCounts().getPastWeekCount() + 1);
        }
       urlShorteningRepository.saveOrUpdate(url);
    }

    @Override
    public boolean deleteShortUrl(String shortUrl) {
        try {
           Object url =  urlShorteningRepository.findUrl(shortUrl);
           if(url == null){
               return false;
           }
           urlShorteningRepository.delete(shortUrl);
        } catch (Exception e){
            throw new UrlNotFoundException("URL not found for short URL: " + shortUrl);
        }
        return true;
    }


}
