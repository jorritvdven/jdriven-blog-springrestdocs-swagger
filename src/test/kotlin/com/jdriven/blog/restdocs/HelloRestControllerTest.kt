package com.jdriven.blog.restdocs

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension::class)
@WebMvcTest(HelloRestController::class)
class HelloRestControllerTest {
    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withResponseDefaults(Preprocessors.prettyPrint())
            )
            .build()
    }

    @Test
    fun `document Hello endpoint`() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/hello?name={name}", "JDriven")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(
                document("hello-controller",
                    snippets = arrayOf(
                        resource(
                            ResourceSnippetParameters.builder()
                                .summary("Greets a user.")
                                .description("""
                                    Every user of our systems want to be greeted. That's we we came up with this endpoint.
                                    Simply add the name of the user to the request to get a polite greeting.
                                """.trimIndent())
                                .responseSchema(GET_HELLO_RESPONSE_SCHEMA)
                                .tag(GREETING_TAG)
                                .requestParameters(
                                    parameterWithName("name").optional().description("Name of the user. When omitted, we'll assume the user wants to stay anonymous.")
                                )
                                .responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("A personalised greeting.")
                                )
                                .build()
                        )
                    )
                )
            )
    }

    companion object {
        private val GET_HELLO_RESPONSE_SCHEMA = Schema("get-hello-response")
        private const val GREETING_TAG = "Greeting"
    }
}
