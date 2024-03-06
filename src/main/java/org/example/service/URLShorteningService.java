package org.example.service;


import org.example.model.Url;
import org.example.model.UrlAccessCounts;


public interface URLShorteningService {
    Url generateShortUrl(String longUrl);
    Url getLongURL(String shortUrl);
    UrlAccessCounts getUrlAccessCounts(String shortUrl);
    void updateAccessCounts(Url longUrl);
    boolean deleteShortUrl(String shortUrl);
}