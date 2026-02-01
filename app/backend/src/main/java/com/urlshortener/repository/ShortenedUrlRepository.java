package com.urlshortener.repository;

import com.urlshortener.model.ShortenedUrl;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {

  Optional<ShortenedUrl> findByAlias(String alias);

  boolean existsByAlias(String alias);
}

