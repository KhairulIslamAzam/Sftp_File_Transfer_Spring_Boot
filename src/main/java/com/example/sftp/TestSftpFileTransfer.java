//package com.example.sftp;
//
//import com.example.sftp.service.SftpFileTransferService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestSftpFileTransfer implements CommandLineRunner {
//
//	private final SftpFileTransferService sftpFileTransferService;
//	private Logger logger = LoggerFactory.getLogger(TestSftpFileTransfer.class);
//
//	public TestSftpFileTransfer(SftpFileTransferService sftpFileTransferService) {
//		this.sftpFileTransferService = sftpFileTransferService;
//	}
//
//	@Override
//	public void run(String... args) throws Exception {
//
////		logger.info("Start upload file");
////		boolean isUploaded = sftpFileTransferService.uploadFile("C:\\Users\\BS585\\Desktop\\test.txt",
////				"/home/azam/Desktop/abc/test.txt");
////		logger.info("Upload result: " + String.valueOf(isUploaded));
////
////		logger.info("Start download file");
////		boolean isDownloaded = sftpFileTransferService.downloadFile("inb.txt","C:\\Users\\BS585\\Desktop\\Newfolder\\",
////				"/home/azam/Desktop/inbound/");
////		logger.info("Download result: " + String.valueOf(isDownloaded));
////
//		logger.info("Start downloadList file");
//		boolean isDownloadedList = sftpFileTransferService.downloadListOfFile();
//		logger.info("Download result: " + String.valueOf(isDownloadedList));
//
////		logger.info("Start deleting file");
////		boolean isDelete = sftpFileTransferService.deleteFile("inb.txt",
////				"/home/azam/Desktop/inbound/");
////		logger.info("delete result: " + String.valueOf(isDelete));
//
//	}
//
//}
