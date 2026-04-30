package cn.tedu.ivos.user.controller;

import cn.tedu.ivos.base.response.JsonResult;
import cn.tedu.ivos.user.pojo.dto.UserLoginParam;
import cn.tedu.ivos.user.pojo.dto.UserQuery;
import cn.tedu.ivos.user.pojo.dto.UserSaveParam;
import cn.tedu.ivos.user.pojo.vo.UserVO;
import cn.tedu.ivos.user.service.UserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户模块")
@Slf4j
@RestController
@RequestMapping("/v1/user")
public class UserController {
    @Autowired
    UserService userService;

    @Operation(summary = "用户登录")
    @ApiOperationSupport(order = 10)
    @PostMapping("login")
    public JsonResult login(@Validated @RequestBody UserLoginParam userLoginParam){
        log.debug("用户登录,参数:{}",userLoginParam);
        //按Ctrl点方法名去接口
        //按Ctrl+Alt点方法名去接口实现类
        UserVO userVO = userService.login(userLoginParam);
        return JsonResult.ok(userVO);
    }

    @Operation(summary = "查询用户")
    @ApiOperationSupport(order = 20)
    @GetMapping("select")
    public JsonResult selectUser(UserQuery userQuery){
        log.debug("查询用户,参数:{}",userQuery);
        List<UserVO> list = userService.selectUser(userQuery);
        return JsonResult.ok(list);
    }

    @Operation(summary = "保存用户")
    @ApiOperationSupport(order = 30)
    @PostMapping("save")
    public JsonResult save(@Validated UserSaveParam userSaveParam){
        log.debug("保存用户,参数:{}",userSaveParam);
        userService.save(userSaveParam);
        return JsonResult.ok();
    }

    @Operation(summary = "修改用户状态")
    @ApiOperationSupport(order = 40)
    @PostMapping("/update/status/{userId}/{status}")
    public JsonResult updateStatus(
            @PathVariable Long userId,@PathVariable String status){
        log.debug("修改用户状态,参数:{},{}",userId,status);
        userService.updateStatus(userId,status);
        return JsonResult.ok();
    }

    @Operation(summary = "删除用户")
    @ApiOperationSupport(order = 60)
    @PostMapping("delete/{userId}")
    public JsonResult deleteUser(@PathVariable Long userId){
        log.debug("删除用户:userId={}",userId);
        userService.deleteUser(userId);
        return JsonResult.ok();
    }

    @Operation(summary = "查询审批人列表")
    @ApiOperationSupport(order = 70)
    @GetMapping("/select/audit/{parentId}")
    public JsonResult selectAuditList(@PathVariable Long parentId){
        log.debug("查询审批人列表,参数:{}",parentId);
        List<UserVO> list = userService.selectAudit(parentId);
        return JsonResult.ok(list);
    }

    @Operation(summary = "重置密码")
    @ApiOperationSupport(order = 80)
    @PostMapping("update/password/{userId}")
    public JsonResult updatePassword(@PathVariable Long userId){
        log.debug("重置密码,参数:{}",userId);
        userService.updatePassword(userId);
        return JsonResult.ok();
    }





}
