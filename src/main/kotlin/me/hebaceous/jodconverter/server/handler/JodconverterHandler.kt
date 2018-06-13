package me.hebaceous.jodconverter.server.handler

import org.jodconverter.DocumentConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.io.File

@Component
class JodconverterHandler(@Qualifier("localDocumentConverter") val documentConverter: DocumentConverter) {
    val logger = LoggerFactory.getLogger(JodconverterHandler::class.java)!!
    val jodconverterTempDir = createTempDir("jodconverter")

    fun convert(request: ServerRequest): Mono<ServerResponse> {
        return request.body(BodyExtractors.toMultipartData()).flatMap {
            val format = (it?.getFirst("format") as FormFieldPart?)?.value() ?: request.pathVariable("format")
            val isToHtml = format.equals("html", true)

            val data = it?.getFirst("data") as FilePart?
                    ?: return@flatMap ServerResponse.badRequest().body(fromObject("missing data"))
            val sourceFileExtension = data.filename().substringAfterLast('.', "").toLowerCase()
            if (sourceFileExtension == "") return@flatMap ServerResponse.badRequest().body(fromObject("missing data file extension"))
            val isFromPdf = sourceFileExtension == "pdf"

            val sourceTempFile = createTempFile(suffix = ".$sourceFileExtension", directory = jodconverterTempDir)
            data.transferTo(sourceTempFile).subscribe()
            var targetTempFile = File(sourceTempFile.absolutePath.substringBeforeLast('.') + "." + if (isToHtml) "pdf" else format)

            if (isFromPdf)
                targetTempFile = sourceTempFile
            else {
                try {
                    documentConverter.convert(sourceTempFile).to(targetTempFile).execute()
                    logger.info("convert ${sourceTempFile.absolutePath} to ${targetTempFile.absolutePath} success")
                } catch (e: Exception) {
                    logger.error("convert ${sourceTempFile.absolutePath} to ${targetTempFile.absolutePath} failed", e)
                    return@flatMap ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fromObject("convert to pdf failed"))
                }
            }
            if (isToHtml) {
                val process = Runtime.getRuntime().exec("pdf2htmlEX --dest-dir " + targetTempFile.parent + " " + targetTempFile.absolutePath)
                if (process.waitFor() != 0) {
                    logger.error("convert ${sourceTempFile.absolutePath} to html failed")
                    return@flatMap ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fromObject("convert to html failed"))
                }
                logger.info("convert ${targetTempFile.absolutePath} to html success")
                targetTempFile = File(targetTempFile.absolutePath.substringBeforeLast('.') + ".html")
            }
            ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(targetTempFile.readBytes().toMono(), ByteArray::class.java)
        }
    }
}
