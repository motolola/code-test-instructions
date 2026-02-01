package com.urlshortener.controller;

import com.urlshortener.model.ShortenUrlRequest;
import com.urlshortener.model.ShortenUrlResponse;
import com.urlshortener.model.UrlDto;
import com.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class UrlShortenerController {

  private final UrlShortenerService service;

  @PostMapping("/shorten")
  public ResponseEntity<ShortenUrlResponse> shortenUrl(
      @Valid @RequestBody ShortenUrlRequest request) {
    log.info("Received request to shorten URL: {}", request.fullUrl());
    ShortenUrlResponse response = service.shortenUrl(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{alias}")
  public RedirectView redirectToFullUrl(@PathVariable String alias) {
    log.info("Redirecting alias: {}", alias);
    String fullUrl = service.getFullUrl(alias);
    RedirectView redirectView = new RedirectView();
    redirectView.setUrl(fullUrl);
    redirectView.setStatusCode(HttpStatus.FOUND);
    return redirectView;
  }

  @DeleteMapping("/{alias}")
  public ResponseEntity<Void> deleteUrl(@PathVariable String alias) {
    log.info("Deleting alias: {}", alias);
    service.deleteUrl(alias);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/urls")
  public ResponseEntity<List<UrlDto>> getAllUrls() {
    log.info("Fetching all URLs");
    List<UrlDto> urls = service.getAllUrls();
    return ResponseEntity.ok(urls);
  }
}

