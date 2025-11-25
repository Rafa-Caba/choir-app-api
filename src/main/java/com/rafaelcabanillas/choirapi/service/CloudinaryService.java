package com.rafaelcabanillas.choirapi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value; // Use Spring's @Value
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    // We let Spring read the env var from application.yml
    // This is better than System.getenv because it allows Spring Profiles to override it if needed
    private final Cloudinary cloudinary;

    // Constructor Injection (Best Practice)
    public CloudinaryService(@Value("${cloudinary.url}") String cloudinaryUrl) {
        // We pass the URL explicitly.
        // This creates the config object automatically, just like the code you found!
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }

    public Map uploadFile(MultipartFile file, String folder) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto"
        ));
    }

    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}