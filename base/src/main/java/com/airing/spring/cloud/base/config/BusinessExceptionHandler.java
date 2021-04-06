package com.airing.spring.cloud.base.config;

import com.airing.spring.cloud.base.entity.ResponseData;
import com.airing.spring.cloud.base.enums.ExceptionEnum;
import com.airing.spring.cloud.base.exception.BusinessException;
import com.airing.spring.cloud.base.utils.MessageSourceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 *
 * @author GEYI
 * @date 2021年03月31日 10:03
 */
@RestControllerAdvice
public class BusinessExceptionHandler {

    @Autowired
    private MessageSourceUtils messageSourceUtils;

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest req) {
        ResponseData response;
        // 业务异常
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            ExceptionEnum exceptionEnum = be.getExceptionEnum();
            String msg;
            if (exceptionEnum == null) {
                msg = be.getMsg();
            } else {
                msg = messageSourceUtils.getByKey(exceptionEnum.getKey(), be.getArgs(), be.getMessage());
            }
            response = new ResponseData<>(be.getCode(), msg);
        } else { // 系统异常
            response = new ResponseData<>(ExceptionEnum.SYS_ERROR.getCode(), ExceptionEnum.SYS_ERROR.getMsg());
        }
        return response;

        /* 判断返回json格式数据还是返回页面
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
            modelAndView.addObject("timestamp", System.currentTimeMillis());
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("status", e.getMessage());
            modelAndView.addObject("message", e.getMessage());
            modelAndView.setViewName("error");
            return modelAndView;
        }*/
    }
}
