package com.example.jobWebViewer.model

import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "jobs")
data class Job(
        val _id: String,
        val companyName: String,
        val position: String,
        val blurb: String,
        val yearsExp: Int,
        val tags: Array<String>,
        val link: String,
        val datePosted: Date,
        val fullJobPost: String,
        val requirements: Array<String>?,
        val liked: Boolean?,
        val likeProbability: Float?,
        var isLiked: Boolean?,
)
