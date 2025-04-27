package com.oma.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oma.model.Review;
import com.oma.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ReviewService reviewService;
    @Autowired private ObjectMapper objectMapper;
    private UUID testId;
    private Review testReview;
    private String url = "/reviews";

    @BeforeEach
    void setup() {
        testId = UUID.randomUUID();
        testReview = new Review();
        testReview.setId(testId);
        testReview.setComment("Great product!");
        testReview.setRating(5);
    }

    @Test
    @WithMockUser(roles = "SHOPPER")
    void testCreateReview() throws Exception {
        when(reviewService.createReview(any())).thenReturn(testReview);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReview)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProductReviews() throws Exception {
        when(reviewService.getProductReviews(testId)).thenReturn(List.of(testReview));
        mockMvc.perform(get(url + "/prd/" + testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Great product!"));
    }
}
