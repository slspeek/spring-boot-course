package nl.tochbedrijf.frontoffice.services;

import nl.tochbedrijf.frontoffice.entities.Book;
import nl.tochbedrijf.frontoffice.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private  BookRepository bookRepository;

    public BookService() {
    }

    public List<Book> getBooks() {
        List<Book> books = bookRepository.findAll();
        return books;
    }

    public Book getBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()){
            return  book.get();
        } else {
            throw new RuntimeException("Book not found");
        }
    }



    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void deleteId(Long id) {
        bookRepository.deleteById(id);
    }
}

