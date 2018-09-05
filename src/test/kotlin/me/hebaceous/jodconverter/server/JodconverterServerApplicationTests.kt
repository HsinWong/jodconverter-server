package me.hebaceous.jodconverter.server

import org.jodconverter.DocumentConverter
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.io.File

@RunWith(SpringRunner::class)
@SpringBootTest(
        "jodconverter.online.enabled=true",
        "jodconverter.local.enabled=false",
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
class JodconverterServerApplicationTests {

    @Autowired
    @Qualifier("onlineDocumentConverter")
    lateinit var documentConverter: DocumentConverter

    @Test
    fun testConvert() {
        documentConverter.convert(File("C:\\Users\\Hebaceous\\Desktop\\336a6e77-f0b0-4e1e-b773-28482da47769.docx"))
                .to(File("x.png"))
                .execute()
    }

}
