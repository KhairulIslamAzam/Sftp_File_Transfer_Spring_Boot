package com.example.sftp.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Vector;

/**
 * @author Khairul Islam Azam
 * @created 2/04/2021 - 4:40 PM
 * @project SFTP File Transfer in Spring boot
 */

@Service
public class SftpFileTransferService {

    private final Logger logger = LoggerFactory.getLogger(SftpFileTransferService.class);
    /**
     * here class variable are initialize from application.properties
     */
    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private Integer port;

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.privatekey}")
    private String privatekey;

    @Value("${sftp.passphrase}")
    private String passphrase;

    @Value("${sftp.sessionTimeout}")
    private Integer sessionTimeout;

    @Value("${sftp.channelTimeout}")
    private Integer channelTimeout;

    /**
     * this method is used for file upload in client to server
     *
     * @param files here files is the arrays of multipart files which taking from
     *  the rest api from any local host.
     * @param remoteFilePath remote path is the directory of
     * your server where you can store your file
     * @return after storing your file this method
     * return boolean value to ensure that your file is saved or not
     */
    public boolean uploadFile(MultipartFile[] files, String remoteFilePath) {

        ChannelSftp channelSftp = createChannelSftp();

        if (remoteDirectoryCk(remoteFilePath)) {
            try {
                for(MultipartFile file : files) {

                    File convFile = convertMultiPartToFile(file);
                    if (convFile.canRead()) {
                        channelSftp.put(new FileInputStream(convFile),
                                remoteFilePath + "/" + convFile.getName());
                    }
                }
                return true;
            } catch (SftpException | FileNotFoundException ex) {
                logger.error("Error upload file", ex.getMessage());
            } catch (IOException e) {
                logger.error("Error upload file", e.getMessage());
            } finally {
                disconnectChannelSftp(channelSftp);
            }
        }
        return false;
    }

    /**
     * this method is used for downlaod file from server to client
     *
     * @param localFilePath  same as upload directory localFilepath
     * @param remoteFilePath same as upload directory remoteFilePath
     * @return if it fetch data successfully then it sends true otherwise send false
     */
    public boolean downloadFile(String fileName, String localFilePath, String remoteFilePath) {
        ChannelSftp channelSftp = createChannelSftp();
        OutputStream outputStream;

        try {
            File file = new File(localFilePath + fileName);
            outputStream = new FileOutputStream(file);
            channelSftp.get(remoteFilePath + fileName, outputStream);
            file.createNewFile();
            return true;
        } catch (SftpException | IOException ex) {
            logger.error("Error download file", ex.getMessage());
        } finally {
            disconnectChannelSftp(channelSftp);
        }

        return false;
    }

     /**
     * here in this method downloading the list of file from server to host
     * @param localFilePath url of host where file should be stored
     * @param remoteFilePath url of server from where list of file should fetch
     * vector is like The Vector class implements a growable array of objects.Like an array.
     * Vector can grow or shrink as needed to accommodate adding and removing items after the Vector has been created
     * channelSftp.ls(remoteFilePath) = lists the contents of a remote directory
     * ChannelSftp.LsEntry is an Objects implementing this interface can be passed as an argument for ChannelSftp's ChannelSftp.ls(java.lang.String) method.
     * @return if it fetch all data successfully then it sends true otherwise send false
     */
    public boolean downloadListOfFile(String localFilePath, String remoteFilePath) {
        ChannelSftp channelSftp = createChannelSftp();
        try {

            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(remoteFilePath);

            for (ChannelSftp.LsEntry file : fileList) {
                if (!file.getFilename().startsWith(".")) {
                    channelSftp.get(remoteFilePath + file.getFilename(),
                            localFilePath + file.getFilename());
                }
            }

            return true;
        } catch (SftpException ex) {
            logger.error("Error downloading file list", ex.getMessage());
        } finally {
            disconnectChannelSftp(channelSftp);
        }

        return false;
    }

    /**
     * this method is used for deleting file in server directory
     *
     * @param fileName fileName for delete
     * @param remoteFilePath server directory where the fileName stored
     * @return if it deleted successfully then it sends true otherwise send false
     */
    public boolean deleteFile(String fileName, String remoteFilePath) {
        ChannelSftp channelSftp = createChannelSftp();
        try {

            Vector fileList = channelSftp.ls(remoteFilePath);
            for (int i = 0; i < fileList.size(); i++) {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) fileList.get(i);
                String file = lsEntry.getFilename();
                logger.info("access file name: " + file);
                if (file.equalsIgnoreCase(fileName)) {
                    channelSftp.rm(remoteFilePath + fileName);
                    return true;
                } else {
                    logger.info(fileName + " " + "not found");
                }
            }
        } catch (SftpException ex) {
            logger.error("Error deleting file file", ex.getMessage());
        } finally {
            disconnectChannelSftp(channelSftp);
        }
        return false;
    }

    /**
     * this method is used for creating sftpchannel so that user can upload or download or deleting file
     * first we need jsch class after that creating session from jsch object giving the username, host, port
     * after that a Channel connected to an sftp server (which information provided in the application properties).
     * @return after connecting the channel it should return the connect sftpchannel
     */
    private ChannelSftp createChannelSftp() {
        ChannelSftp channelSftp = null;
        try {
            JSch jSch = new JSch();
            jSch.addIdentity(privatekey, passphrase.getBytes());
            Session session = jSch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(sessionTimeout);
            Channel channel = session.openChannel("sftp");
            channel.connect(channelTimeout);
            channelSftp =  (ChannelSftp) channel;
        } catch (JSchException ex) {
            logger.error("Create ChannelSftp error", ex.getMessage());
        }
        return channelSftp;
    }

    /**
     * this method is used for disconnecting channel as well as session
     * @param channelSftp channelSftp is the channel which connect successfully in connect() method
     */
    private void disconnectChannelSftp(ChannelSftp channelSftp) {
        try {
            if (channelSftp == null)
                return;

            if (channelSftp.isConnected())
                channelSftp.disconnect();

            if (channelSftp.getSession() != null)
                channelSftp.getSession().disconnect();

        } catch (Exception ex) {
            logger.error("SFTP disconnect error", ex.getMessage());
        }
    }

    /**
     *
     * @param file it takes multipart file
     * @return convert file from multipart file
     * @throws IOException if there any error in reading then it throws exception
     */
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    /**
     * this method is used for checking server directory.
     * if there is no directory in this server then in catch block we make a directory in server
     * using mkdir()
     * @param path url of the server folder
     * @return it send true if the method got directory other it send false and create directory
     */
    private boolean remoteDirectoryCk(String path) {
        SftpATTRS attrs = null;
        ChannelSftp channelSftp = createChannelSftp();

        try {
            attrs = channelSftp.stat(path);
            if (attrs != null) {
                return true;
            }

        } catch (SftpException e) {
            logger.info("Directory is created. try again");
            if (attrs == null) {
                try {
                    channelSftp.mkdir(path);
                } catch (SftpException ex) {
                    logger.error("directory is not created"+""+ex.getMessage());
                }
            }
        }
        return false;
    }
}

