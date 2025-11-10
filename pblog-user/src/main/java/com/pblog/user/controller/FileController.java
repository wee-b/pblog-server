package com.pblog.user.controller;


import com.pblog.common.result.ResponseResult;
import com.pblog.user.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

   @Autowired
   private FileService fileService;


   @PostMapping("/uploadAvatar")
   public ResponseResult<String> uploadAvatar(@RequestParam("file") MultipartFile file){
      String url = fileService.uploadAvatar(file);
      return ResponseResult.success(url);
   }
}
