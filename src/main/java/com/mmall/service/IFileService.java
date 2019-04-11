package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: huki-konghui
 * @Date: 2019/3/29 10:43
 * @Version 1.0
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
