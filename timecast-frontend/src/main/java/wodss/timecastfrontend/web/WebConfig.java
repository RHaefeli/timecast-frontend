package wodss.timecastfrontend.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * Web Config to add rocker message converter
 *
 * @author linux_china
 * https://github.com/linux-china/rocker-template-demo/blob/master/src/main/java/org/mvnsearch/WebConfig.java
 */

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new RockerMessageConverter());
        super.addDefaultHttpMessageConverters(converters);
    }
}
