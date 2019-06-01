package com.example.es.common.controller;

import com.example.es.common.dto.BaseResponse;
import com.example.es.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @ExceptionHandler
    public BaseResponse exceptionHandler(Exception e) {
        logger.info("Exception", e);
        BaseResponse response = new BaseResponse();
        if (e instanceof BusinessException) {
            response.setCode(-3);
            response.setMsg(e.getMessage());
        } else {
            response.setCode(-4);
            response.setMsg(e.getMessage());
        }
        return response;
    }

}
