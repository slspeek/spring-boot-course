package nl.tochbedrijf.frontoffice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FrontofficeApplicationTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private BookRepository bookRepository;

  public final Book animalFarm =
      new Book(null, "Animal farm", "George Orwell", UUID.randomUUID().toString());

  @BeforeEach
  public void deleteAllBeforeTests() throws Exception {
    bookRepository.deleteAll();
  }

  @Test
  void contextLoads() {}

  @Test
  public void shouldReturnNoBooks() throws Exception {
    mockMvc.perform(get("/api/book")).andExpect(status().isOk()).andExpect(content().string("[]"));
  }

  @Test
  public void shouldDeleteEntity() throws Exception {
    Book saved = bookRepository.save(animalFarm);
    Long id = saved.getId();

    mockMvc.perform(delete(String.format("/api/book/%d", id))).andExpect(status().isNoContent());

    List<Book> all = bookRepository.findAll();
    assert all.isEmpty();
  }

  @Test
  public void shouldInsertEntity() throws Exception {
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
    if (bookFromDatabase.isPresent()) {
      assert bookFromDatabase.get().getAuthor().equals(animalFarm.getAuthor());
      assert bookFromDatabase.get().getTitle().equals(animalFarm.getTitle());
    } else {
      throw new AssertionFailure("Expected a record, but none was found");
    }
  }

  @Test
  public void shouldPartiallyInsertEntity() throws Exception {
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
    if (bookFromDatabase.isPresent()) {
      assert bookFromDatabase.get().getAuthor() == null;
      assert bookFromDatabase.get().getTitle().equals("Animal farm");
    } else {
      throw new AssertionFailure("Expected a record, but none was found");
    }
  }
}
