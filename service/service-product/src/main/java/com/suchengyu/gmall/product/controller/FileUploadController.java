package com.suchengyu.gmall.product.controller;

import com.suchengyu.gmall.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * FileUploadController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-15
 * @Description:
 */
@Api(description = "文件上传接口")
@CrossOrigin    //跨域
@RestController
@RequestMapping("admin/product/")
public class FileUploadController {

    @Value("${fileServer.url}")
    private String imgUrl;

    @ApiOperation(value = "添加spu时上传文件接口")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) throws IOException, MyException {
        String imgUrls = "http://" + imgUrl;
        // 1.读取配置文件
        String path = FileUploadController.class.getClassLoader().getResource("tracker.conf").getPath();
        // 2.创建连接
        ClientGlobal.init(path);
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = trackerClient.getConnection();
        // 3.获取存储客户端
        StorageClient storageClient = new StorageClient(connection, null);
        // 4.通过storage上传文件
        // 获取文件名
        String filename = file.getOriginalFilename();
        String suffix = FilenameUtils.getExtension(filename);
        String[] jpgs = storageClient.upload_file(file.getBytes(), suffix, null);
        for (String jpg : jpgs) {
            imgUrls = imgUrls + "/" + jpg;
        }
        System.out.println(imgUrls);
        return Result.ok(imgUrls);
    }

}
