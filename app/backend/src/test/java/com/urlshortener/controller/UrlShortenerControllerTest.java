package com.urlshortener.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.exception.AliasAlreadyExistsException;
import com.urlshortener.exception.AliasNotFoundException;
import com.urlshortener.model.ShortenUrlRequest;
import com.urlshortener.model.ShortenUrlResponse;
import com.urlshortener.model.UrlDto;
import com.urlshortener.service.UrlShortenerService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UrlShortenerController.class)
class UrlShortenerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UrlShortenerService service;

  @Test
  void shouldShortenUrl() throws Exception {
    // Given
    ShortenUrlRequest request = new ShortenUrlRequest(
        "https://example.com/long/url",
        "custom-alias"
    );
    ShortenUrlResponse response = new ShortenUrlResponse("http://localhost:8080/custom-alias");

    when(service.shortenUrl(any(ShortenUrlRequest.class))).thenReturn(response);

    // When & Then
    mockMvc.perform(post("/shorten")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/custom-alias"));
  }

  @Test
  void shouldReturnBadRequestWhenUrlIsInvalid() throws Exception {
    // Given
    ShortenUrlRequest request = new ShortenUrlRequest(
        "not-a-valid-url",
        null
    );

    // When & Then
    mockMvc.perform(post("/shorten")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnBadRequestWhenAliasAlreadyExists() throws Exception {
    // Given
    ShortenUrlRequest request = new ShortenUrlRequest(
        "https://example.com/long/url",
        "existing-alias"
    );

    when(service.shortenUrl(any(ShortenUrlRequest.class)))
        .thenThrow(new AliasAlreadyExistsException("Alias 'existing-alias' is already in use"));

    // When & Then
    mockMvc.perform(post("/shorten")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Alias 'existing-alias' is already in use"));
  }

  @Test
  void shouldRedirectToFullUrl() throws Exception {
    // Given
    String alias = "test-alias";
    String fullUrl = "https://example.com";

    when(service.getFullUrl(alias)).thenReturn(fullUrl);

    // When & Then
    mockMvc.perform(get("/" + alias))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(fullUrl));
  }

  @Test
  void shouldReturnNotFoundWhenAliasDoesNotExist() throws Exception {
    // Given
    String alias = "non-existent";

    when(service.getFullUrl(alias))
        .thenThrow(new AliasNotFoundException("Alias '" + alias + "' not found"));

    // When & Then
    mockMvc.perform(get("/" + alias))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldDeleteUrl() throws Exception {
    // Given
    String alias = "test-alias";
    doNothing().when(service).deleteUrl(alias);

    // When & Then
    mockMvc.perform(delete("/" + alias))
        .andExpect(status().isNoContent());

    verify(service).deleteUrl(alias);
  }

  @Test
  void shouldReturnNotFoundWhenDeletingNonExistentAlias() throws Exception {
    // Given
    String alias = "non-existent";

    doThrow(new AliasNotFoundException("Alias '" + alias + "' not found"))
        .when(service).deleteUrl(alias);

    // When & Then
    mockMvc.perform(delete("/" + alias))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldGetAllUrls() throws Exception {
    // Given
    List<UrlDto> urls = Arrays.asList(
        new UrlDto("alias1", "https://example1.com", "http://localhost:8080/alias1"),
        new UrlDto("alias2", "https://example2.com", "http://localhost:8080/alias2")
    );

    when(service.getAllUrls()).thenReturn(urls);

    // When & Then
    mockMvc.perform(get("/urls"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].alias").value("alias1"))
        .andExpect(jsonPath("$[0].fullUrl").value("https://example1.com"))
        .andExpect(jsonPath("$[1].alias").value("alias2"));
  }
}

