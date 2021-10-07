package com.jdriven.blog.restdocs

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloRestController {

    @GetMapping("/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getHello(@RequestParam("name", required = false) name: String?) =
        HelloResponse(message = "Hello, ${name ?: "anonymous person"}")
}

data class HelloResponse(
    val message: String
)
