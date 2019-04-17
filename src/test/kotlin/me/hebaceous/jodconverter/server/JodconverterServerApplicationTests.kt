package me.hebaceous.jodconverter.server

import org.jodconverter.DocumentConverter
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.zeroturnaround.zip.ZipUtil
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
        documentConverter.convert(File("a4.pdf"))
                .to(File("a4.cbz"))
                .execute()
        ZipUtil.unpack(File("a4.cbz"), File("a4.cbz".substringBeforeLast(".")))
    }

}
