package nl.tochbedrijf.frontoffice.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.tochbedrijf.frontoffice.entities.Book;
import nl.tochbedrijf.frontoffice.repositories.BookRepository;
import nl.tochbedrijf.frontoffice.services.dtos.BookDTO;
import nl.tochbedrijf.frontoffice.web.rest.BookNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  private final BookRepository bookRepository;

  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public List<BookDTO> getBooks() {
    return bookRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
  }

  public BookDTO getBookById(Long id) {
    return bookRepository
        .findById(id)
        .map(book -> new BookDTO(book.getId(), book.getTitle(), book.getAuthor()))
        .orElseThrow(() -> new BookNotFoundException(id));
  }

  public BookDTO save(BookDTO bookDTO) {
    return convertToDto(bookRepository.save(convertToEntity(bookDTO)));
  }

  public void deleteId(Long id) {
    if (!bookRepository.existsById(id)) {
      throw new BookNotFoundException(id);
    }
    bookRepository.deleteById(id);
  }

  public BookDTO convertToDto(Book book) {
    return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
  }

  public Book convertToEntity(BookDTO bookDTO) {
    return new Book(bookDTO.id(), bookDTO.title(), bookDTO.author(), UUID.randomUUID().toString());
  }

  public BookDTO updateBook(Long id, BookDTO bookDTO) {
    return bookRepository
        .findById(id)
        .map(
            book -> {
              book.setTitle(bookDTO.title());
              book.setAuthor(bookDTO.author());
              return convertToDto(bookRepository.save(book));
            })
        .orElseThrow(() -> new BookNotFoundException(id));
  }

  public List<BookDTO> findByTitleContains(String title) {
    return bookRepository.findBooksByTitleContains(title).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }
}
