package com.urlshortener.service;

import com.urlshortener.exception.AliasAlreadyExistsException;
import com.urlshortener.exception.AliasNotFoundException;
import com.urlshortener.model.ShortenUrlRequest;
import com.urlshortener.model.ShortenUrlResponse;
import com.urlshortener.model.ShortenedUrl;
import com.urlshortener.model.UrlDto;
import com.urlshortener.repository.ShortenedUrlRepository;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortenerService {

  private static final String CHARACTERS =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int ALIAS_LENGTH = 6;
  private static final int MAX_RETRIES = 10;
  private static final SecureRandom RANDOM = new SecureRandom();

  private final ShortenedUrlRepository repository;

  @Value("${app.base-url}")
  private String baseUrl;

  @Transactional
  public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {
    String alias = determineAlias(request.customAlias());

    if (repository.existsByAlias(alias)) {
      throw new AliasAlreadyExistsException("Alias '" + alias + "' is already in use");
    }

    ShortenedUrl shortenedUrl =
        ShortenedUrl.builder().alias(alias).fullUrl(request.fullUrl()).build();

    repository.save(shortenedUrl);

    log.info("Created shortened URL: {} -> {}", alias, request.fullUrl());

    return new ShortenUrlResponse(baseUrl + "/" + alias);
  }

  @Transactional(readOnly = true)
  public String getFullUrl(String alias) {
    return repository
        .findByAlias(alias)
        .map(ShortenedUrl::getFullUrl)
        .orElseThrow(() -> new AliasNotFoundException("Alias '" + alias + "' not found"));
  }

  @Transactional
  public void deleteUrl(String alias) {
    ShortenedUrl url =
        repository
            .findByAlias(alias)
            .orElseThrow(() -> new AliasNotFoundException("Alias '" + alias + "' not found"));

    repository.delete(url);
    log.info("Deleted shortened URL: {}", alias);
  }

  @Transactional(readOnly = true)
  public List<UrlDto> getAllUrls() {
    return repository.findAll().stream()
        .map(url -> new UrlDto(url.getAlias(), url.getFullUrl(), baseUrl + "/" + url.getAlias()))
        .collect(Collectors.toList());
  }

  private String determineAlias(String customAlias) {
    if (customAlias != null && !customAlias.isBlank()) {
      return customAlias;
    }
    return generateRandomAlias();
  }

  private String generateRandomAlias() {
    for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
      String alias = generateRandomString();
      if (!repository.existsByAlias(alias)) {
        return alias;
      }
    }
    throw new RuntimeException(
        "Failed to generate unique alias after " + MAX_RETRIES + " attempts");
  }

  private String generateRandomString() {
    StringBuilder sb = new StringBuilder(ALIAS_LENGTH);
    for (int i = 0; i < ALIAS_LENGTH; i++) {
      sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
    }
    return sb.toString();
  }
}

