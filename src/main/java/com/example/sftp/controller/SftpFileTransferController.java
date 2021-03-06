package com.example.sftp.controller;

import com.example.sftp.service.SftpFileTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Khairul Islam Azam
 * @created 04/04/2021 - 9:27 PM
 * @project SFTP File Transfer in Spring boot
 */
@RestController
public class SftpFileTransferController {

    @Autowired
    private SftpFileTransferService sftpFileTransferService;

    @PostMapping("/upload")
    public boolean uploadFiles(@RequestParam("files") MultipartFile[] files,
                                                       @RequestParam("remotePath") String remotePath) {

        return sftpFileTransferService.uploadFile(files, remotePath);
    }

    @GetMapping("/download")
    public boolean getSingleFile(@RequestParam("localPath") String localPath,
                              @RequestParam("remotePath") String remotePath,
                              @RequestParam("fileName") String fileName) {

        return sftpFileTransferService.downloadFile(fileName, localPath, remotePath);
    }

    @GetMapping("/downloadAll")
    public boolean getAllFiles(@RequestParam("localPath") String localPath,
                            @RequestParam("remotePath") String remotePath) {

        return sftpFileTransferService.downloadListOfFile(localPath, remotePath);
    }

    @DeleteMapping("/delete")
    public boolean deleteFile(@RequestParam("fileName") String fileName,
                           @RequestParam("remotePath") String remotePath) {

        return sftpFileTransferService.deleteFile(fileName, remotePath);
    }

}
