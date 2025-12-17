package nl.tochbedrijf.frontoffice;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import nl.tochbedrijf.frontoffice.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

    @BeforeEach
    public void deleteAllBeforeTests() throws Exception {
        bookRepository.deleteAll();
    }

    @Test
	void contextLoads() {
	}

    @Test
    public void shouldReturnNoBooks() throws Exception {
        mockMvc.perform(get("/api/book")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void shouldDeleteEntity() throws Exception {

       mockMvc.perform(post("/api/book").header("Content-Type", "application/json").content(
                "{ \"author\": \"George Orwell\", \"title\":\"Animal farm\"}")).andDo(print()).andExpect(
                status().isCreated());

        mockMvc.perform(delete("/api/book/1")).andDo(print()).andExpect(
                status().isNoContent());

        mockMvc.perform(get("/api/book")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }
}
