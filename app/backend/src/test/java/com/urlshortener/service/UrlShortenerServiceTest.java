package com.urlshortener.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.model.ShortenUrlRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldShortenUrlAndRedirect() throws Exception {
    // Shorten URL
    ShortenUrlRequest request = new ShortenUrlRequest(
        "https://example.com/test",
        "test-alias"
    );

    mockMvc.perform(post("/shorten")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.shortUrl").exists());

    // Verify redirect works
    mockMvc.perform(get("/test-alias"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("https://example.com/test"));

    // Verify URL appears in list
    mockMvc.perform(get("/urls"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.alias == 'test-alias')]").exists());

    // Delete URL
    mockMvc.perform(delete("/test-alias"))
        .andExpect(status().isNoContent());

    // Verify URL is deleted
    mockMvc.perform(get("/test-alias"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldHandleDuplicateAlias() throws Exception {
    // Create first URL
    ShortenUrlRequest request1 = new ShortenUrlRequest(
        "https://example.com/test1",
        "duplicate-alias"
    );

    mockMvc.perform(post("/shorten")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request1)))
        .andExpect(status().isCreated());

    // Try to create with same alias
    ShortenUrlRequest request2 = new ShortenUrlRequest(
        "https://example.com/test2",
        "duplicate-alias"
    );

    mockMvc.perform(post("/shorten")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request2)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Alias 'duplicate-alias' is already in use"));

    // Cleanup
    mockMvc.perform(delete("/duplicate-alias"));
  }

  @Test
  void shouldGenerateRandomAlias() throws Exception {
    ShortenUrlRequest request = new ShortenUrlRequest(
        "https://example.com/random",
        null
    );

    mockMvc.perform(post("/shorten")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.shortUrl").exists());
  }

  @Test
  void shouldValidateUrlFormat() throws Exception {
    ShortenUrlRequest request = new ShortenUrlRequest(
        "not-a-valid-url",
        null
    );

    mockMvc.perform(post("/shorten")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}

