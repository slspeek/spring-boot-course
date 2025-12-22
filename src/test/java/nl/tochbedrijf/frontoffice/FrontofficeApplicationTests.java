package nl.tochbedrijf.frontoffice;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import nl.tochbedrijf.frontoffice.entities.Book;
import nl.tochbedrijf.frontoffice.repositories.BookRepository;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FrontofficeApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    public final Book animalFarm = new Book(null, "Animal farm","George Orwell", UUID.randomUUID().toString());

    @BeforeEach
    public void deleteAllBeforeTests() throws Exception {
        System.out.println("Before each");
        bookRepository.deleteAll();
    }

    @Test
	void contextLoads() {
	}

    @Test
    public void shouldReturnNoBooks() throws Exception {
        mockMvc.perform(get("/api/book")).andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void shouldDeleteEntity() throws Exception {
        Book saved = bookRepository.save(animalFarm);
        Long id = saved.getId();

        mockMvc.perform(delete(String.format("/api/book/%d", id))).andExpect(
                status().isNoContent());

        List<Book> all = bookRepository.findAll();
        assert all.isEmpty();
    }

    @Test
    public void shouldInsertEntity() throws Exception {
        mockMvc.perform(post("/api/book").header("Content-Type", "application/json").content(
                "{ \"author\": \"George Orwell\", \"title\":\"Animal farm\"}")).andExpect(
                status().isCreated());

        List<Book> booksFromDatabase = bookRepository.findAll();
        if (! booksFromDatabase.isEmpty() ){
            assert booksFromDatabase.get(0).getAuthor().equals("George Orwell");
            assert booksFromDatabase.get(0).getTitle().equals("Animal farm");

        } else {
            throw new AssertionFailure("Expected a record, but none was found");
        }
    }

    @Test
    public void shouldFailInsertEntity() throws Exception {
        mockMvc.perform(post("/api/book").header("Content-Type", "application/json").content(
                "{ \"AUTHORXXX\": \"George Orwell\", \"title\":\"Animal farm\"}")).andExpect(
                status().isCreated());

        List<Book> booksFromDatabase = bookRepository.findAll();
        if (! booksFromDatabase.isEmpty() ){
            assert booksFromDatabase.get(0).getAuthor()==null;
            assert booksFromDatabase.get(0).getTitle().equals("Animal farm");

        } else {
            throw new AssertionFailure("Expected a record, but none was found");
        }
    }

}
