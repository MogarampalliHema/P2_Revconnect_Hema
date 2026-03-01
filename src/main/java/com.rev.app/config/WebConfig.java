package com.rev.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Path to the 'uploads' directory in the project root
                Path uploadPath = Paths.get("uploads").toAbsolutePath();
                String location = "file:///" + uploadPath.toString().replace("\\", "/") + "/";

                // Map any request starting with /uploads/ to the physical uploads folder
                registry.addResourceHandler("/uploads/**")
                                .addResourceLocations(location)
                                .setCachePeriod(0);

                // Explicit handlers for subfolders to be extra safe
                registry.addResourceHandler("/uploads/profile-pictures/**")
                                .addResourceLocations(location + "profile-pictures/")
                                .setCachePeriod(0);

                registry.addResourceHandler("/uploads/post-images/**")
                                .addResourceLocations(location + "post-images/")
                                .setCachePeriod(0);
        }
}
