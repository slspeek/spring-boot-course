package nl.tochbedrijf.frontoffice.repositories;

import java.util.List;
import nl.tochbedrijf.frontoffice.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
  List<Book> findBooksByTitleContains(String title);
}
