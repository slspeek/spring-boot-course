package nl.tochbedrijf.frontoffice.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(Long bookID) {
    super(String.format("Book not found with ID: %d", bookID));
  }
}
