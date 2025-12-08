package nl.tochbedrijf.frontoffice.repositories;

import nl.tochbedrijf.frontoffice.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
