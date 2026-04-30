package cn.tedu.ivos.user.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private Long id; //用户编号
    private String username; //用户名
    private String password; //密码
    private String email; //邮箱
    private String phone; //手机号
    private Integer age; //年龄
    private String gender; //性别
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime; //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime; //更新时间
    private String status; //用户状态 0-禁用 1-启用
    private String level;//职级 10-员工 20-经理 30-总监 40-总裁
    private Long parentId; //直属领导编号
}
