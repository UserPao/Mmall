package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author: huki-konghui
 * @Date: 2019/3/29 10:44
 * @Version 1.0
 */
@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService {

    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //获取扩展名abc.jsp
        String  fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName  = UUID.randomUUID().toString() +"."+ fileExtensionName;
        log.info("开始上传文件，上传文件的文件名：{},上传的路径为:{},新文件名:{}",fileName,path,uploadFileName);
        File fileDir = new File(path);
        if(!fileDir.exists()){
           fileDir.setWritable(true);
           fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //到这里，上传文件已成功
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到ftp服务上
            //上传完成后，删除upload下面的文件
            targetFile.delete();

        } catch (IOException e) {
            log.error("上传文件异常",e);
            return  null;
        }
        return targetFile.getName();
    }

}
