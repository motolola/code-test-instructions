package com.tpx.urlshortener.exception;

public class AliasAlreadyExistsException extends RuntimeException {
  public AliasAlreadyExistsException(String message) {
    super(message);
  }
}
