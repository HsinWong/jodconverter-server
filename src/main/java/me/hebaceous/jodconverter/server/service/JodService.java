package me.hebaceous.jodconverter.server.service;

import org.jodconverter.DocumentConverter;
import org.jodconverter.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Service
@EnableScheduling
public class JodService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JodService.class);

    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    @Autowired
    private DocumentConverter documentConverter;

    @Value("${temp-file-retain-seconds}")
    private Long tempFileRetainSeconds;

    @Value("${mupdf.max-width}")
    private Integer maxWidth;

    @Value("${mupdf.max-height}")
    private Integer maxHeight;

    private final Path tempDir;

    public JodService() throws IOException {
        tempDir = Files.createTempDirectory("jodconverter");
        LOGGER.info("use tempDir:{}", tempDir.toFile().getAbsolutePath());
    }

    public File convert(MultipartFile file, String targetFormat) throws IOException, OfficeException, InterruptedException {
        String fileName = Objects.requireNonNull(file.getOriginalFilename());
        LOGGER.info("name: {}, isEmpty: {}, targetFormat: {}", fileName, file.isEmpty(), targetFormat);

        String sourceFormat = fileName.substring(fileName.lastIndexOf('.') + 1);
        File sourceTempFile = Files.createTempFile(tempDir, "", "." + sourceFormat).toFile();
        file.transferTo(sourceTempFile);

        String tempPath = sourceTempFile.getAbsolutePath().substring(0, sourceTempFile.getAbsolutePath().lastIndexOf('.') + 1);
        File targetTempFile = new File(tempPath + targetFormat);


        boolean isToHtml = "html".equalsIgnoreCase(targetFormat);
        boolean isToCbz = "cbz".equalsIgnoreCase(targetFormat);
        boolean isFromPdf = "pdf".equalsIgnoreCase(sourceFormat);

        if (!isToHtml && !isToCbz) {
            try {
                documentConverter.convert(sourceTempFile).to(targetTempFile).execute();
            } catch (OfficeException e) {
                throw new OfficeException(String.format("%s converting to %s failed. the conversion on your file is not support.", sourceFormat, targetFormat));
            }
            LOGGER.info("{} converting to {} succeed.", sourceFormat, targetFormat);
            return targetTempFile;
        }

        if (!isFromPdf) {
            File pdfTempFile = new File(tempPath + "pdf");
            try {
                documentConverter.convert(sourceTempFile).to(pdfTempFile).execute();
            } catch (OfficeException e) {
                throw new OfficeException(String.format("%s converting to %s failed. before to html or to cbz. the conversion on your file is not support.", sourceFormat, "pdf"));
            }
            LOGGER.info("{} converting to {} succeed. before to html or to cbz.", sourceFormat, "pdf");
            sourceTempFile = pdfTempFile;
        }

        String exec = null;
        if (isToHtml) {
            exec = String.format("pdf2htmlEX --no-drm 1 --dest-dir %s %s", targetTempFile.getParent(), sourceTempFile.getAbsolutePath());
        }
        if (isToCbz) {
            exec = String.format("mutool convert -O width=%d -O height=%d -o %s %s", maxWidth, maxHeight, targetTempFile.getAbsolutePath(), sourceTempFile.getAbsolutePath());
        }

        LOGGER.info("executing: {}", exec);
        Process process = Runtime.getRuntime().exec(exec);
        solveStd(process);
        if (process.waitFor() != 0) {
            throw new OfficeException(String.format("%s converting to %s failed. the conversion on your file is not support.", sourceFormat, targetFormat));
        }
        return targetTempFile;
    }

    private void solveStd(Process proc) throws IOException {
        String line;

        InputStream stderr = proc.getErrorStream();
        InputStreamReader esr = new InputStreamReader(stderr);
        BufferedReader ebr = new BufferedReader(esr);
        while ((line = ebr.readLine()) != null) {
            LOGGER.debug("exec: {}", line);
        }

        InputStream stdout = proc.getInputStream();
        InputStreamReader osr = new InputStreamReader(stdout);
        BufferedReader obr = new BufferedReader(osr);
        while ((line = obr.readLine()) != null) {
            LOGGER.debug("exec: {}", line);
        }
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void deleteTempFilesSchedule() {
        LOGGER.info("start deleteTempFilesSchedule");
        File[] files = tempDir.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                long current = System.currentTimeMillis();
                long lastModified = file.lastModified();
                if (current - lastModified > tempFileRetainSeconds * 1000) {
                    boolean isFailed = !FileSystemUtils.deleteRecursively(file);

                    SimpleDateFormat simpleDateFormat = SIMPLE_DATE_FORMAT_THREAD_LOCAL.get();
                    LOGGER.info("deleteTempFilesSchedule. file:{}, current:{}, lastModified:{}", file.getName(),
                            simpleDateFormat.format(new Date(current)), simpleDateFormat.format(new Date(lastModified)));

                    if (isFailed) {
                        LOGGER.error("deleteTempFilesSchedule error. file:{}", file.getName());
                    }
                }
            }
        }
        LOGGER.info("end deleteTempFilesSchedule");
    }

}
