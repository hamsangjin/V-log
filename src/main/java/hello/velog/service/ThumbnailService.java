package hello.velog.service;

import hello.velog.exception.ImageUploadException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class ThumbnailService {
    private static final String POST_DEFAULT_PATH = "/images/post/";
    private static final String USER_DEFAULT_PATH = "/images/user/";
    private static final String POST_UPLOAD_DIR = "/Users/sangjin/Desktop/likelion/velog/src/main/resources/static/images/post/";
    private static final String USER_UPLOAD_DIR = "/Users/sangjin/Desktop/likelion/velog/src/main/resources/static/images/user/";

    @Transactional
    public String uploadThumbnail(MultipartFile thumbnailImageFile, String userOrPost){
        String defaultPath = null;
        String defaultImagePath = null;
        String uploadDir = null;

        if(userOrPost.equals("USER")){
            defaultPath = USER_DEFAULT_PATH;
            defaultImagePath = USER_DEFAULT_PATH + "default-image.png";
            uploadDir = USER_UPLOAD_DIR;
        } else if(userOrPost.equals("POST")){
            defaultPath = POST_DEFAULT_PATH;
            defaultImagePath = POST_DEFAULT_PATH + "default-image.png";
            uploadDir = POST_UPLOAD_DIR;
        }

        if (thumbnailImageFile.isEmpty()) {
            return defaultImagePath;
        }

        String uuid = UUID.randomUUID().toString();
        String originalFilename = thumbnailImageFile.getOriginalFilename();
        String storedFilename = uuid + "_" + originalFilename;

        File destFile = new File(uploadDir + storedFilename);
        try {
            thumbnailImageFile.transferTo(destFile);
        } catch (IOException e) {
            throw new ImageUploadException("이미지 업로드 중 오류가 발생했습니다.");
        }

        return defaultPath + storedFilename;
    }
}