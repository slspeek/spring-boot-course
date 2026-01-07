package nl.tochbedrijf.frontoffice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Optional;
import java.util.UUID;
import nl.tochbedrijf.frontoffice.entities.Book;
import nl.tochbedrijf.frontoffice.repositories.BookRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FrontofficeApplicationTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private BookRepository bookRepository;

  final Book animalFarm =
      new Book(null, "Animal farm", "George Orwell", UUID.randomUUID().toString());

  Long insertAnimalFarm() {
    Book saved = bookRepository.save(animalFarm);
    return saved.getId();
  }

  @BeforeEach
  void deleteAllBeforeTests() throws Exception {
    bookRepository.deleteAll();
  }

  @Test
  void contextLoads() {}

  @Test
  void shouldReturnNoBooks() throws Exception {
    mockMvc.perform(get("/api/book")).andExpect(status().isOk()).andExpect(content().string("[]"));
  }

  @Test
  void shouldRetrieveBook() throws Exception {
    Long id = insertAnimalFarm();
    mockMvc
        .perform(get(String.format("/api/book/%d", id)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value(animalFarm.getTitle()))
        .andExpect(jsonPath("$.author").value(animalFarm.getAuthor()));
  }

  @Test
  void shouldFindByTitleContains() throws Exception {
    insertAnimalFarm();
    mockMvc
        .perform(get("/api/book/titleContains/Animal"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value(animalFarm.getTitle()))
        .andExpect(jsonPath("$[0].author").value(animalFarm.getAuthor()));
  }

  @Test
  void findByTitleContainsIsCaseSensitive() throws Exception {
    insertAnimalFarm();
    mockMvc
        .perform(get("/api/book/titleContains/animal"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", Matchers.empty()));
  }

  @Test
  void shouldUpdateBook() throws Exception {
    Long id = insertAnimalFarm();

    mockMvc
        .perform(
            put(String.format("/api/book/%d", id))
                .header("Content-Type", "application/json")
                .content(
                    String.format(
                        "{ \"author\": \"%s\", \"title\":\"%s\"}", animalFarm.getAuthor(), "1984")))
        .andExpect(status().isOk());

    Optional<Book> updatedBook = bookRepository.findById(id);
    assertThat(updatedBook).isPresent();
    Book book = updatedBook.get();
    assertThat(book.getTitle()).isEqualTo("1984");
  }

  @Test
  void shouldNotBeAbleToUpdateBook() throws Exception {
    Long id = insertAnimalFarm();

    mockMvc
        .perform(
            put(String.format("/api/book/%d", id + 1))
                .header("Content-Type", "application/json")
                .content(
                    String.format(
                        "{ \"author\": \"%s\", \"title\":\"%s\"}", animalFarm.getAuthor(), "1984")))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Book not found with ID: " + (id + 1)));
  }

  @Test
  void shouldDeleteEntity() throws Exception {
    Long id = insertAnimalFarm();

    mockMvc.perform(delete(String.format("/api/book/%d", id))).andExpect(status().isNoContent());

    assertThat(bookRepository.findById(id)).isNotPresent();
  }

  @Test
  void shouldNotDeleteNonExistentEntity() throws Exception {
    Long id = insertAnimalFarm();
    mockMvc.perform(delete(String.format("/api/book/%d", id + 1))).andExpect(status().isNotFound());
  }

  @Test
  void shouldInsertEntity() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/api/book")
                    .header("Content-Type", "application/json")
                    .content(
                        String.format(
                            "{ \"author\": \"%s\", \"title\":\"%s\"}",
                            animalFarm.getAuthor(), animalFarm.getTitle())))
            .andExpect(status().isCreated())
            .andReturn();

    String content = mvcResult.getResponse().getContentAsString();
    JsonObject jsonObject = JsonParser.parseString(content).getAsJsonObject();
    Long id = jsonObject.get("id").getAsLong();

    Optional<Book> bookFromDatabase = bookRepository.findById(id);
    assertThat(bookFromDatabase).isPresent();
    Book book = bookFromDatabase.get();
    assertThat(book.getAuthor()).isEqualTo(animalFarm.getAuthor());
    assertThat(book.getTitle()).isEqualTo(animalFarm.getTitle());
  }

  @Test
  void shouldPartiallyInsertEntity() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/api/book")
                    .header("Content-Type", "application/json")
                    .content(
                        String.format(
                            "{ \"Wrong_Attribute_Name\": \"%s\", \"title\":\"%s\"}",
                            animalFarm.getAuthor(), animalFarm.getTitle())))
            .andExpect(status().isCreated())
            .andReturn();

    String content = mvcResult.getResponse().getContentAsString();
    JsonObject jsonObject = JsonParser.parseString(content).getAsJsonObject();
    Long id = jsonObject.get("id").getAsLong();

    Optional<Book> bookFromDatabase = bookRepository.findById(id);
    assertThat(bookFromDatabase).isPresent();
    Book book = bookFromDatabase.get();
    assertThat(book.getAuthor()).isNull();
    assertThat(book.getTitle()).isEqualTo(animalFarm.getTitle());
  }
}
