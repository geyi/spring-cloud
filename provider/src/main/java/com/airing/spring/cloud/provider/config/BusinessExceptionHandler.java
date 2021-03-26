package com.airing.spring.cloud.provider.config;

import com.airing.spring.cloud.provider.enums.ExceptionEnum;
import com.airing.spring.cloud.provider.utils.MessageSourceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class BusinessExceptionHandler {

    @Autowired
    private MessageSourceUtils messageSourceUtils;

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest req) {
        Map<String, Object> response = new HashMap<>(3);
        // 业务异常
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            ExceptionEnum exceptionEnum = be.getExceptionEnum();
            String msg;
            if (exceptionEnum == null) {
                msg = be.getMsg();
            } else {
                msg = messageSourceUtils.getByKey(exceptionEnum.getKey());
            }
            response.put("code", be.getCode());
            response.put("msg", msg);
        } else { // 系统异常
            response.put("code", ExceptionEnum.SYS_ERROR.getCode());
            response.put("msg", ExceptionEnum.SYS_ERROR.getMsg());
        }

        String contentType = req.getHeader("Content-Type");
        String accept = req.getHeader("Accept");
        String xRequestedWith = req.getHeader("X-Requested-With");
        if ((contentType != null && contentType.contains("application/json"))
                || (accept != null && accept.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xRequestedWith)) {
            return response;
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("msg", e.getMessage());
            modelAndView.addObject("url", req.getRequestURL());
            modelAndView.addObject("stackTrace", e.getStackTrace());
            modelAndView.setViewName("error");
            return modelAndView;
        }
    }
}
