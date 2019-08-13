package me.hebaceous.jodconverter.server;

import org.jodconverter.DocumentConverter;
import org.jodconverter.office.OfficeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class JodConverterServerTests {

    @Autowired
    private DocumentConverter documentConverter;

    @Test
    public void testConvert() throws OfficeException {
        File source = new File("a4.pdf");
        documentConverter.convert(source)
                .to(new File("a4.cbz"))
                .execute();
    }

}
