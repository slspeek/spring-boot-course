package nl.tochbedrijf.frontoffice.web.rest;

import java.util.List;
import nl.tochbedrijf.frontoffice.services.BookService;
import nl.tochbedrijf.frontoffice.services.dtos.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book")
public class BookController {

  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping()
  public List<BookDTO> getBooks() {
    return bookService.getBooks();
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public BookDTO createBook(@RequestBody BookDTO bookDTO) {
    return bookService.save(bookDTO);
  }

  @GetMapping("/{id}")
  public BookDTO getBookById(@PathVariable Long id) {
    return bookService.getBookById(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBook(@PathVariable Long id) {
    bookService.deleteId(id);
  }

  @PutMapping("/{id}")
  public BookDTO updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
    return bookService.updateBook(id, bookDTO);
  }
}
