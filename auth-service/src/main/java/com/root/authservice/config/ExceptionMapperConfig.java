package com.root.authservice.config;

import com.google.gson.Gson;
import com.root.authservice.mappers.ClientResponseErrorMapper;
import feign.FeignException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionMapperConfig {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> dtoExceptionHandler(MethodArgumentNotValidException exception) {
        Map<String, Object> responseMap = new LinkedHashMap<>();

        List<String> listErrors = exception.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        responseMap.put("statusCode", 422);
        responseMap.put("errors", listErrors);

        return ResponseEntity.status(422).body(responseMap);
    }

    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<Object> feignException(FeignException exception) {
        ClientResponseErrorMapper feignErrorMapper = new Gson().fromJson(
                exception.contentUTF8(),
                ClientResponseErrorMapper.class
        );
        
        return ResponseEntity.status(exception.status()).body(feignErrorMapper);
    }
}
