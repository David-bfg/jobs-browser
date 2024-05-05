package com.example.jobWebViewer.controller
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.view.RedirectView

@Controller
class DefaultRedirectController  : ErrorController {

    @RequestMapping("/error", method = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH])
    fun handleError(): RedirectView {
        // Redirect to /jobs when a 404 error occurs
        return RedirectView("/jobs")
    }
}