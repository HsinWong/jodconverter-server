package me.hebaceous.jodconverter.server.handler

import org.jodconverter.DocumentConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Component
class JodconverterHandler(@Qualifier("localDocumentConverter") val documentConverter: DocumentConverter) {
    val logger = LoggerFactory.getLogger(JodconverterHandler::class.java)!!
    val jodconverterTempDir = createTempDir("jodconverter")

    fun convert(request: ServerRequest): Mono<ServerResponse> {
        return request.body(BodyExtractors.toMultipartData()).flatMap {
            val format = (it?.getFirst("format") as FormFieldPart?)?.value() ?: request.pathVariable("format")
            val data = it?.getFirst("data") as FilePart
            val sourceTempFile = createTempFile(suffix = ".${data.filename().substringAfterLast('.')}", directory = jodconverterTempDir)
            data.transferTo(sourceTempFile).subscribe()
            val targetTempFile = createTempFile(suffix = ".$format", directory = jodconverterTempDir)
            documentConverter.convert(sourceTempFile).to(targetTempFile).execute()
            logger.info("convert ${sourceTempFile.absolutePath} to ${targetTempFile.absolutePath}")
            ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(targetTempFile.readBytes().toMono(), ByteArray::class.java)
        }
    }
}
