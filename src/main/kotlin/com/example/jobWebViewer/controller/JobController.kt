package com.example.jobWebViewer.controller

import com.example.jobWebViewer.model.Job
import com.example.jobWebViewer.repository.JobRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Controller
class JobController(private val jobRepository: JobRepository) {

    @GetMapping("/jobs")
    fun getJobs(model: Model, @RequestParam(defaultValue = "1") page: Int,
                @RequestParam(required = false) liked: Char?,
                @RequestParam(required = false) days: Int?): String {
        val pageSize = 25
        val pageable: PageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "datePosted"))

        val cutoffDate: LocalDate = if (days == null) LocalDate.now() else LocalDate.now().minusDays(days.toLong())
        val jobsPage: Page<Job> = when (liked?.lowercaseChar()) {
            'y' -> if (days == null) jobRepository.findAllByLiked(true, pageable) else
                jobRepository.findAllByLikedAndDatePostedGreaterThan(true, cutoffDate, pageable)

            'n' -> if (days == null) jobRepository.findAllByLiked(false, pageable) else
                jobRepository.findAllByLikedAndDatePostedGreaterThan(false, cutoffDate, pageable)

            'u' -> if (days == null) jobRepository.findAllByLikedNotExist(pageable) else
                jobRepository.findAllByLikedNotExistAndDatePostedAfter(cutoffDate, pageable)

            else -> if (days == null) jobRepository.findAll(pageable) else
                jobRepository.findAllByDatePostedAfter(cutoffDate, pageable)
        }

        val jobs: List<Job> = jobsPage.content.map { it.copy(blurb = it.blurb.replace("\n", "<br>"), isLiked = it.liked != null) }

        model.addAttribute("jobs", jobs)
        model.addAttribute("currentPage", jobsPage.number + 1)
        model.addAttribute("totalPages", jobsPage.totalPages)

        val browseMode: String = (if(liked != null) "&liked=${liked}" else "") +
                (if(days != null) "&days=${days}" else "")
        if (jobsPage.number != 0) {
            val prevPageLink: String = "/jobs?page=${jobsPage.number}" + browseMode
            model.addAttribute("prevPageLink", prevPageLink)
        }
        if (jobsPage.number != jobsPage.totalPages - 1) {
            val nextPageLink: String = "/jobs?page=${jobsPage.number + 2}" + browseMode
            model.addAttribute("nextPageLink", nextPageLink)
        }


        return "jobs"
    }

    @PatchMapping("/job-liked/{jobId}")
    @ResponseBody
    fun like(@PathVariable jobId: String): ResponseEntity<String> {
        val job = jobRepository.findById(jobId)
        if (job.isPresent) {
            val updatedJob = job.get().copy(liked = true)
            jobRepository.save(updatedJob)
            return ResponseEntity.ok("Job liked")
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Job not found")  // HTTP 422
        }
    }

    @PatchMapping("/job-disliked/{jobId}")
    @ResponseBody
    fun dislike(@PathVariable jobId: String): ResponseEntity<String> {
        val job = jobRepository.findById(jobId)
        if (job.isPresent) {
            val updatedJob = job.get().copy(liked = false)
            jobRepository.save(updatedJob)
            return ResponseEntity.ok("Job disliked")
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Job not found")  // HTTP 422
        }
    }

    @PatchMapping("/job-applied/{jobId}")
    @ResponseBody
    fun applied(@PathVariable jobId: String): ResponseEntity<String> {
        val job = jobRepository.findById(jobId)
        if (job.isPresent) {
            val updatedJob = job.get().copy(status = "applied")
            jobRepository.save(updatedJob)
            return ResponseEntity.ok("Job status: applied")
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Job not found")  // HTTP 422
        }
    }

    @GetMapping("/job/{jobId}")
    fun getJobDetails(@PathVariable jobId: String, model: Model): String {
        val job = jobRepository.findById(jobId)
        if (job.isPresent) {
            val responseJob = job.get()
            responseJob.isLiked = responseJob.liked != null
            model.addAttribute("job", responseJob)
            return "job_details"
        } else {
            // Handle job not found
            return "job_not_found"
        }
    }
}
