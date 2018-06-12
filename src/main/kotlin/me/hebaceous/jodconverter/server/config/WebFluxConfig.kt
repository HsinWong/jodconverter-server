package me.hebaceous.jodconverter.server.config

import me.hebaceous.jodconverter.server.handler.JodconverterHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route

@Configuration
class WebFluxConfig(val jodconverterHandler: JodconverterHandler) : WebFluxConfigurer {

    @Bean
    fun routerFunction(): RouterFunction<*> {
        return route(POST("/lool/convert-to/{format}").and(accept(MediaType.MULTIPART_FORM_DATA)), HandlerFunction(jodconverterHandler::convert))
                .andRoute(POST("/lool/convert-to").and(accept(MediaType.MULTIPART_FORM_DATA)), HandlerFunction(jodconverterHandler::convert))
    }

}
