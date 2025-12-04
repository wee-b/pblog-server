package com.pblog.user.controller;


import com.pblog.common.result.ResponseResult;
import com.pblog.user.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

   @Autowired
   private FileService fileService;


   @PostMapping("/uploadAvatar")
   public ResponseResult<String> uploadAvatar(@RequestParam("file") MultipartFile file){
      String url = fileService.uploadAvatar(file);
      log.info("/file/uploadAvatar url={}", url);
      return ResponseResult.successData(url);
   }

   @PostMapping("/uploadImage")
   public ResponseResult<String> uploadImage(@RequestParam("file") MultipartFile file){
      String url = fileService.uploadImage(file);
      log.info("/file/uploadImage url={}", url);
      return ResponseResult.successData(url);
   }
}
