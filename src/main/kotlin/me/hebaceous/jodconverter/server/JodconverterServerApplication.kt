package me.hebaceous.jodconverter.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JodconverterServerApplication

fun main(args: Array<String>) {
    runApplication<JodconverterServerApplication>(*args)
}
