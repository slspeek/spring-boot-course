package nl.tochbedrijf.frontoffice.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.tochbedrijf.frontoffice.entities.Book;
import nl.tochbedrijf.frontoffice.repositories.BookRepository;
import nl.tochbedrijf.frontoffice.services.dtos.BookDTO;
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
    Optional<Book> book = bookRepository.findById(id);
    if (book.isPresent()) {
      return convertToDto(book.get());
    } else {
      throw new RuntimeException("Book not found");
    }
  }

  public BookDTO save(BookDTO bookDTO) {
    return convertToDto(bookRepository.save(convertToEntity(bookDTO)));
  }

  public void deleteId(Long id) {
    bookRepository.deleteById(id);
  }

  public BookDTO convertToDto(Book book) {
    return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
  }

  public Book convertToEntity(BookDTO bookDTO) {
    return new Book(bookDTO.id(), bookDTO.title(), bookDTO.author(), UUID.randomUUID().toString());
  }

  public BookDTO updateBook(Long id, BookDTO bookDTO) {
    Optional<Book> toBoUpdated = bookRepository.findById(id);
    if (toBoUpdated.isPresent()) {
      Book book = toBoUpdated.get();
      book.setTitle(bookDTO.title());
      book.setAuthor(bookDTO.author());
      return convertToDto(bookRepository.save(book));

    } else {
      throw new RuntimeException("Update failed, book not found");
    }
  }
}
