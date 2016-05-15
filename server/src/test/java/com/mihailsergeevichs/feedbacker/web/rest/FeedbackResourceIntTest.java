package com.mihailsergeevichs.feedbacker.web.rest;

import com.mihailsergeevichs.feedbacker.Application;
import com.mihailsergeevichs.feedbacker.domain.Feedback;
import com.mihailsergeevichs.feedbacker.repository.FeedbackRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the FeedbackResource REST controller.
 *
 * @see FeedbackResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class FeedbackResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_STR = dateTimeFormatter.format(DEFAULT_DATE);
    private static final String DEFAULT_QUALITY = "AAAAA";
    private static final String UPDATED_QUALITY = "BBBBB";
    private static final String DEFAULT_SPEED = "AAAAA";
    private static final String UPDATED_SPEED = "BBBBB";
    private static final String DEFAULT_PRICE = "AAAAA";
    private static final String UPDATED_PRICE = "BBBBB";
    private static final String DEFAULT_COMMENTARY = "AAAAA";
    private static final String UPDATED_COMMENTARY = "BBBBB";

    @Inject
    private FeedbackRepository feedbackRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFeedbackMockMvc;

    private Feedback feedback;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FeedbackResource feedbackResource = new FeedbackResource();
        ReflectionTestUtils.setField(feedbackResource, "feedbackRepository", feedbackRepository);
        this.restFeedbackMockMvc = MockMvcBuilders.standaloneSetup(feedbackResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        feedback = new Feedback();
        feedback.setDate(DEFAULT_DATE);
        feedback.setQuality(DEFAULT_QUALITY);
        feedback.setSpeed(DEFAULT_SPEED);
        feedback.setPrice(DEFAULT_PRICE);
        feedback.setCommentary(DEFAULT_COMMENTARY);
    }

    @Test
    @Transactional
    public void createFeedback() throws Exception {
        int databaseSizeBeforeCreate = feedbackRepository.findAll().size();

        // Create the Feedback

        restFeedbackMockMvc.perform(post("/api/feedbacks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedback)))
                .andExpect(status().isCreated());

        // Validate the Feedback in the database
        List<Feedback> feedbacks = feedbackRepository.findAll();
        assertThat(feedbacks).hasSize(databaseSizeBeforeCreate + 1);
        Feedback testFeedback = feedbacks.get(feedbacks.size() - 1);
//        assertThat(testFeedback.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testFeedback.getQuality()).isEqualTo(DEFAULT_QUALITY);
        assertThat(testFeedback.getSpeed()).isEqualTo(DEFAULT_SPEED);
        assertThat(testFeedback.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testFeedback.getCommentary()).isEqualTo(DEFAULT_COMMENTARY);
    }

    @Test
    @Transactional
    public void getAllFeedbacks() throws Exception {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedback);

        // Get all the feedbacks
        restFeedbackMockMvc.perform(get("/api/feedbacks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(feedback.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE_STR)))
                .andExpect(jsonPath("$.[*].quality").value(hasItem(DEFAULT_QUALITY.toString())))
                .andExpect(jsonPath("$.[*].speed").value(hasItem(DEFAULT_SPEED.toString())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.toString())))
                .andExpect(jsonPath("$.[*].commentary").value(hasItem(DEFAULT_COMMENTARY.toString())));
    }

    @Test
    @Transactional
    public void getFeedback() throws Exception {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedback);

        // Get the feedback
        restFeedbackMockMvc.perform(get("/api/feedbacks/{id}", feedback.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(feedback.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE_STR))
            .andExpect(jsonPath("$.quality").value(DEFAULT_QUALITY.toString()))
            .andExpect(jsonPath("$.speed").value(DEFAULT_SPEED.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.toString()))
            .andExpect(jsonPath("$.commentary").value(DEFAULT_COMMENTARY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFeedback() throws Exception {
        // Get the feedback
        restFeedbackMockMvc.perform(get("/api/feedbacks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFeedback() throws Exception {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedback);

		int databaseSizeBeforeUpdate = feedbackRepository.findAll().size();

        // Update the feedback
        feedback.setDate(UPDATED_DATE);
        feedback.setQuality(UPDATED_QUALITY);
        feedback.setSpeed(UPDATED_SPEED);
        feedback.setPrice(UPDATED_PRICE);
        feedback.setCommentary(UPDATED_COMMENTARY);

        restFeedbackMockMvc.perform(put("/api/feedbacks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedback)))
                .andExpect(status().isOk());

        // Validate the Feedback in the database
        List<Feedback> feedbacks = feedbackRepository.findAll();
        assertThat(feedbacks).hasSize(databaseSizeBeforeUpdate);
        Feedback testFeedback = feedbacks.get(feedbacks.size() - 1);
        assertThat(testFeedback.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testFeedback.getQuality()).isEqualTo(UPDATED_QUALITY);
        assertThat(testFeedback.getSpeed()).isEqualTo(UPDATED_SPEED);
        assertThat(testFeedback.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testFeedback.getCommentary()).isEqualTo(UPDATED_COMMENTARY);
    }

    @Test
    @Transactional
    public void deleteFeedback() throws Exception {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedback);

		int databaseSizeBeforeDelete = feedbackRepository.findAll().size();

        // Get the feedback
        restFeedbackMockMvc.perform(delete("/api/feedbacks/{id}", feedback.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Feedback> feedbacks = feedbackRepository.findAll();
        assertThat(feedbacks).hasSize(databaseSizeBeforeDelete - 1);
    }
}
