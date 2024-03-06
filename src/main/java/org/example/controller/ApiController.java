package org.example.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Url;
import org.example.model.UrlAccessCounts;
import org.example.service.URLShorteningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;

@Slf4j
@RestController
public class ApiController {

  @Autowired
  URLShorteningService urlShortenerService;

  @ApiOperation(value = "Redirect short URL to long URL", notes = "Redirects to the long URL associated with the given short URL")
  @GetMapping("/v1/redirect/{shortUrl}")
  public ResponseEntity<Object> redirect(@PathVariable String shortUrl) {

    if(StringUtils.isEmpty(shortUrl)) {
      log.error("URL is empty or null. Unable to redirect.");
      return ResponseEntity.badRequest().body("URL is empty or null. Unable to redirect.");
    }

    Url url = urlShortenerService.getLongURL(shortUrl);
    if(url == null || url.getLongUrl() == null) {
      return ResponseEntity.notFound().build();
    }

    if(url.getExpiryDate() != null && url.getExpiryDate().isBefore(LocalDateTime.now())) {
      log.error("URL has expired. Unable to redirect.");
      return ResponseEntity.badRequest().body("URL has expired. Unable to redirect.");
    }
    try {
      String longUrl = url.getLongUrl();
      log.debug("Long URL: {}", longUrl);
      new URL(longUrl).toURI();


      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(URI.create(longUrl));
      return new ResponseEntity<>(longUrl,headers, HttpStatus.FOUND);
    } catch (MalformedURLException | URISyntaxException e) {
      log.error("Invalid URL: {}", url.getLongUrl());
      return ResponseEntity.badRequest().body("Invalid URL: " + url.getLongUrl());
    }
  }

  @ApiOperation(value = "Shortens a long URL", notes = "Provided a long URL generates a unique short URL and persists it")
  @PostMapping("/v1/shorten")
  public ResponseEntity<Url> shortenLongURL(@RequestBody String longUrl){
    return ResponseEntity.ok(urlShortenerService.generateShortUrl(longUrl));
  }

  @ApiOperation(value = "Fetch the short URL", notes = "Provided a short URL fetches the long URL and updates the analytics for that URL")
  @GetMapping("/v1/expand")
  public ResponseEntity<String> getLongURL(@RequestParam String shortURL) {
    String longUrl = urlShortenerService.getLongURL(shortURL).getLongUrl();
    if(longUrl.equals("DEFAULT_URL") || StringUtils.isEmpty(longUrl)){
      log.error("Could not find URL{0}: "+shortURL);
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(urlShortenerService.getLongURL(shortURL).getLongUrl());
  }

  @ApiOperation(value = "Deletes the entry for short URL", notes = "Provided a short URL deletes the persisted entry for that URL")
  @DeleteMapping("/v1/delete")
  public ResponseEntity<String> deleteShortUrl(@RequestParam String shortURL) {
    boolean deleted = urlShortenerService.deleteShortUrl(shortURL);

    if (deleted) {
      return ResponseEntity.ok("Short URL deleted successfully");
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short URL not found");
    }
  }

  @ApiOperation(value = "Gets analytics for the short URL", notes = "Provided a short URL gets the access counts per day/week/hour for that URL")
  @RequestMapping(method = RequestMethod.GET ,value="/v1/accessCount")
  public  ResponseEntity<UrlAccessCounts> getAccessCounts(@RequestParam String shortUrl){
    UrlAccessCounts accessCounts = urlShortenerService.getUrlAccessCounts(shortUrl);
    if (accessCounts != null) {
      return ResponseEntity.ok(accessCounts);
    } else {
      log.error("Unable to find access counts for the Url provided. Either the Url does not exist or encountered an error fetching the Url");
      return ResponseEntity.notFound().build();
    }
  }


}