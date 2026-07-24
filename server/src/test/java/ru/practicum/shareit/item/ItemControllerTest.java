package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;

    private ItemDto item() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Drill");
        dto.setDescription("desc");
        dto.setAvailable(true);
        return dto;
    }

    private ItemBookingDto itemBooking() {
        ItemBookingDto dto = new ItemBookingDto();
        dto.setId(1L);
        dto.setName("Drill");
        return dto;
    }

    @Test
    void create() throws Exception {
        when(itemService.create(eq(1L), any())).thenReturn(item());
        mvc.perform(post("/items")
                        .header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void createWithoutHeaderIsBadRequest() throws Exception {
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        when(itemService.update(eq(1L), eq(1L), any())).thenReturn(item());
        mvc.perform(patch("/items/1")
                        .header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item())))
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        when(itemService.findById(1L, 1L)).thenReturn(itemBooking());
        mvc.perform(get("/items/1").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findByOwner() throws Exception {
        when(itemService.findByOwner(1L)).thenReturn(List.of(itemBooking()));
        mvc.perform(get("/items").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void search() throws Exception {
        when(itemService.search("drill")).thenReturn(List.of(item()));
        mvc.perform(get("/items/search").param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void addComment() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Отлично");
        comment.setAuthorName("Booker");
        when(itemService.addComment(eq(1L), eq(1L), any())).thenReturn(comment);
        mvc.perform(post("/items/1/comment")
                        .header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorName").value("Booker"));
    }
}
