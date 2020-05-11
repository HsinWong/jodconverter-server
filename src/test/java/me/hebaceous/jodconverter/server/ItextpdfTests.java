package me.hebaceous.jodconverter.server;


import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.io.FileChannelRandomAccessSource;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ItextpdfTests {
    @Test
    public void testTiffToPdf() {
        File tiffFile = new File("C:\\Users\\xinh\\Downloads\\0009(1).tif");
        File pdfFile = new File("C:\\Users\\xinh\\Downloads\\0009(1).pdf");
        try {
            RandomAccessFileOrArray tiffRAF = new RandomAccessFileOrArray(new FileChannelRandomAccessSource(FileChannel.open(tiffFile.toPath())));
            int numberOfPages = TiffImage.getNumberOfPages(tiffRAF);
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            pdfWriter.setStrictImageSequence(true);
            document.open();
            Image tempImage;
            for (int i = 1; i <= numberOfPages; i++) {
                tempImage = TiffImage.getTiffImage(tiffRAF, i);
                Rectangle pageSize = new Rectangle(tempImage.getWidth(), tempImage.getHeight());
                document.setPageSize(pageSize);
                document.newPage();
                document.add(tempImage);
            }
            document.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
