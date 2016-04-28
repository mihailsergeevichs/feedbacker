package com.mihailsergeevichs.feedbacker.repository;

import com.mihailsergeevichs.feedbacker.domain.Feedback;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Feedback entity.
 */
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {

}
