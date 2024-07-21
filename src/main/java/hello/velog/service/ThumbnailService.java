package hello.velog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class ThumbnailService {

    private static final String DEFAULT_IMAGE_PATH = "/images/post/default-image.png";
    private static final String UPLOAD_DIR = "/Users/sangjin/Desktop/likelion/velog/src/main/resources/static/images/post/";

    public String uploadThumbnail(MultipartFile thumbnailImageFile) throws IOException {
        if (thumbnailImageFile.isEmpty()) {
            return DEFAULT_IMAGE_PATH;
        }

        String uuid = UUID.randomUUID().toString();
        String originalFilename = thumbnailImageFile.getOriginalFilename();
        String storedFilename = uuid + "_" + originalFilename;

        File destFile = new File(UPLOAD_DIR + storedFilename);
        thumbnailImageFile.transferTo(destFile);

        return "/images/post/" + storedFilename;
    }
}