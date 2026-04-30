package cn.tedu.ivos.base.exception;

import cn.tedu.ivos.base.response.StatusCode;
import lombok.Getter;

//自定义业务异常类
public class ServiceException extends RuntimeException{
    @Getter
    private StatusCode statusCode;

    //Alt+Insert 一直回车
    public ServiceException(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
