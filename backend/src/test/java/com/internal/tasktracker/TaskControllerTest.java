package com.internal.tasktracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void searchTasks_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .param("q", "api")
                .param("page", "1")
                .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.pageSize", is(5)))
                .andExpect(jsonPath("$.total", greaterThan(0)));
    }

    @Test
    public void searchTasks_ExcludesArchivedTasks_EvenIfDescriptionMatches() throws Exception {
        // "Decommission legacy search endpoint" (id 20) and "Legacy API cleanup" (id 21) are archived tasks with 'search' in title/description
        mockMvc.perform(get("/api/tasks")
                .param("q", "legacy")
                .param("pageSize", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*].archived", everyItem(is(false))));
    }

    @Test
    public void searchTasks_EnforcesStatusFilter_WhenTitleMatches() throws Exception {
        // Search term 'bug' matches title "Fix login redirect bug" (status OPEN) and "Report export CSV bug" (status DONE)
        mockMvc.perform(get("/api/tasks")
                .param("q", "bug")
                .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*].status", everyItem(is("OPEN"))));
    }

    @Test
    public void searchTasks_InvalidStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Invalid status value")));
    }

    @Test
    public void searchTasks_NegativePage_DefaultsToPage1() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .param("page", "-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(1)));
    }
}
