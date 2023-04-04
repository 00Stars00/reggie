package org.example.reggie.config;

import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvc配置类
 */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 添加消息转换器
     *
     * @param converters 消息转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        log.info("添加Jackson消息转换器");
        // 添加Jackson消息转换器
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

        // 设置JacksonObjectMapper
        mappingJackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());

        // 添加到消息转换器列表
        converters.add(0,mappingJackson2HttpMessageConverter);
    }
}
