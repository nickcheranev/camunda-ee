package com.example.workflow.config;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.List;

public class YamlPropertyLoaderFactory extends DefaultPropertySourceFactory {
    public YamlPropertyLoaderFactory() {
    }

    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
        Resource resource = encodedResource.getResource();
        List<PropertySource<?>> propertySourceList = (new YamlPropertySourceLoader()).load(resource.getFilename(), resource);
        return (PropertySource) propertySourceList.stream().findFirst().orElseThrow(RuntimeException::new);
    }
}
