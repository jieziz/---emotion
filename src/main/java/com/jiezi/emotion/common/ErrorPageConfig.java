package com.jiezi.emotion.common;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ErrorPageConfig implements ErrorPageRegistrar {
   @Override
   public void registerErrorPages(ErrorPageRegistry registry) {
       registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404"));
       registry.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/403"));
       registry.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/400"));
       registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500"));
   }
}

