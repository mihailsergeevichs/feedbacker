package com.mihailsergeevichs.feedbacker.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mihailsergeevichs.feedbacker.domain.Feedback;
import com.mihailsergeevichs.feedbacker.repository.FeedbackRepository;
import com.mihailsergeevichs.feedbacker.web.rest.util.HeaderUtil;
import com.mihailsergeevichs.feedbacker.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Feedback.
 */
@RestController
@RequestMapping("/api")
public class FeedbackResource {

    private final Logger log = LoggerFactory.getLogger(FeedbackResource.class);
        
    @Inject
    private FeedbackRepository feedbackRepository;
    
    /**
     * POST  /feedbacks -> Create a new feedback.
     */
    @RequestMapping(value = "/feedbacks",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Feedback> createFeedback(@RequestBody Feedback feedback) throws URISyntaxException {
        log.debug("REST request to save Feedback : {}", feedback);
        if (feedback.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("feedback", "idexists", "A new feedback cannot already have an ID")).body(null);
        }
        Feedback result = feedbackRepository.save(feedback);
        return ResponseEntity.created(new URI("/api/feedbacks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("feedback", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /feedbacks -> Updates an existing feedback.
     */
    @RequestMapping(value = "/feedbacks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Feedback> updateFeedback(@RequestBody Feedback feedback) throws URISyntaxException {
        log.debug("REST request to update Feedback : {}", feedback);
        if (feedback.getId() == null) {
            return createFeedback(feedback);
        }
        Feedback result = feedbackRepository.save(feedback);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("feedback", feedback.getId().toString()))
            .body(result);
    }

    /**
     * GET  /feedbacks -> get all the feedbacks.
     */
    @RequestMapping(value = "/feedbacks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Feedback>> getAllFeedbacks(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Feedbacks");
        Page<Feedback> page = feedbackRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/feedbacks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /feedbacks/:id -> get the "id" feedback.
     */
    @RequestMapping(value = "/feedbacks/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Feedback> getFeedback(@PathVariable Long id) {
        log.debug("REST request to get Feedback : {}", id);
        Feedback feedback = feedbackRepository.findOne(id);
        return Optional.ofNullable(feedback)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /feedbacks/:id -> delete the "id" feedback.
     */
    @RequestMapping(value = "/feedbacks/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        log.debug("REST request to delete Feedback : {}", id);
        feedbackRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("feedback", id.toString())).build();
    }
}
