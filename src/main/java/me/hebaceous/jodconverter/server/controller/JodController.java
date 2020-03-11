package me.hebaceous.jodconverter.server.controller;

import me.hebaceous.jodconverter.server.service.JodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

@RestController
public class JodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JodController.class);

    @Autowired
    private JodService jodService;

    @PostMapping(value = "/lool/convert-to/{format}")
    public ResponseEntity<Object> convertToFormat(@PathVariable String format, @RequestPart MultipartFile data) {
        return convert0(format, data);
    }

    @PostMapping(value = "/lool/convert-to/")
    public ResponseEntity<Object> convertTo(@RequestPart String format, @RequestPart MultipartFile data) {
        return convert0(format, data);
    }

    private ResponseEntity<Object> convert0(String format, MultipartFile data) {
        try {
            String originalFilename = data.getOriginalFilename();
            if (StringUtils.isEmpty(originalFilename)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("cannot extract filename.");
            }

            File file = jodService.convert(data, format);

            String filename = originalFilename.substring(0, originalFilename.lastIndexOf('.') + 1) + format;
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .body(new InputStreamResource(new FileInputStream(file)));
        } catch (Exception e) {
            LOGGER.error("convert failed.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}
