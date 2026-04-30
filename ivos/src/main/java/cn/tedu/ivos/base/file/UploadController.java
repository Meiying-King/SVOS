package cn.tedu.ivos.base.file;

import cn.tedu.ivos.base.response.JsonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Tag(name = "图片上传模块")
@Slf4j
@RestController
@RequestMapping("/v1/file")
public class UploadController {

    @Operation(summary = "上传图片")
    @ApiOperationSupport(order = 10)
    @PostMapping("upload")
    public JsonResult upload(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        fileName = UUID.randomUUID()+suffix;
        String dirPath = "d:/files";
        SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
        String datePath = sdf.format(new Date());
        File dirFile = new File(dirPath+datePath);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        String filePath = dirPath+datePath+fileName;
        file.transferTo(new File(filePath));
        return JsonResult.ok(datePath+fileName);
    }

    @Operation(summary = "删除图片")
    @ApiOperationSupport(order = 20)
    @PostMapping("remove")
    public JsonResult remove(String imgUrl){
        log.debug("删除图片,参数:{}",imgUrl);
        new File("d:/files"+imgUrl).delete();
        return JsonResult.ok();
    }










}
