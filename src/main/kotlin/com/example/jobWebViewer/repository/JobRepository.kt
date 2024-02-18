package com.example.jobWebViewer.repository

import com.example.jobWebViewer.model.Job
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.time.LocalDate

// TODO: Replace with QueryDSL
interface JobRepository : MongoRepository<Job, String> {
    @Query("{ 'liked' : ?0, 'datePosted' : { '\$gt' : ?1 } }")
    fun findAllByLikedAndDatePostedGreaterThan(liked: Boolean, cutoffDate: LocalDate, pageable: Pageable): Page<Job>

    @Query("{ 'liked' : ?0 }")
    fun findAllByLiked(liked: Boolean?, pageable: Pageable): Page<Job>

    @Query("{ 'datePosted' : { '\$gt' : ?0 } }")
    fun findAllByDatePostedAfter(cutoffDate: LocalDate?, pageable: Pageable): Page<Job>

    @Query("{ 'liked' : {\$exists: false}, 'datePosted' : { '\$gt' : ?0 } }")
    fun findAllByLikedNotExistAndDatePostedAfter(cutoffDate: LocalDate?, pageable: Pageable): Page<Job>

    @Query("{ 'liked' : {\$exists: false} }")
    fun findAllByLikedNotExist(pageable: Pageable): Page<Job>
}
