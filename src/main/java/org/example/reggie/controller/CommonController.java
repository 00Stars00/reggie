package org.example.reggie.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.example.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * 公共接口
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 上传文件
     * @param file 文件
     * @return 成功信息
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        log.info("上传文件:{}", file.getOriginalFilename());

        // 获取文件后缀
        String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));

        // 生成新的文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 创建目录对象
        File dir = new File(basePath);

        // 判断当前目录是否存在
        if(!dir.exists()) {
            // 创建目录
            dir.mkdirs();
        }

        try {
            // 保存文件
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(fileName);
    }

    /**
     * 下载文件
     * @param name 文件名
     * @param response 响应
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        log.info("下载文件:{}", name);

        try {

            // 设置响应头
            response.setContentType("image/jpeg");

            // 读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            // 写入文件
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            // 关闭流
            fileInputStream.close();
            outputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
