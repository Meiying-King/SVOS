package cn.tedu.ivos.user.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Data
public class UserSaveParam {
    @Schema(description = "用户编号")
    private Long id;
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;
    @Schema(description = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    private String email;
    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号格式错误")
    private String phone;
    @Schema(description = "年龄")
    @NotNull(message = "年龄不能为空")
    @Range(min = 0,max = 200,message = "年龄范围0-200之间")
    private Integer age;
    @Schema(description = "性别")
    @NotBlank(message = "性别不能为空")
    private String gender;
    @Schema(description = "用户状态")
    @NotBlank(message = "用户状态不能为空")
    private String status;
    @Schema(description = "职级")
    @NotBlank(message = "职级不能为空")
    private String level;
    @Schema(description = "直属领导编号")
    //@NotNull(message = "直属领导编号不能为空")
    private Long parentId;
}
