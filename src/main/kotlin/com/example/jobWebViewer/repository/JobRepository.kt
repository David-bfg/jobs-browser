package com.example.jobWebViewer.repository

import com.example.jobWebViewer.model.Job
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface JobRepository : MongoRepository<Job, String> {
    fun findAllByOrderByDatePostedDesc(pageable: Pageable): Page<Job>
}
