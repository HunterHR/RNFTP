package com.ftp.apache;


import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @A
 */
public class CommonsNetFtpModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static String ip_address;
    private static int port;
    private static FTPClient client;


    @Override
    public String getName() {
        return "FTP";
    }

    public CommonsNetFtpModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        client = new FTPClient();
    }

    @ReactMethod
    public void setup(String ip_address, int port) {
        this.ip_address = ip_address;
        this.port = port;
    }

    @ReactMethod
    public void login(final String username, final String password, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect(CommonsNetFtpModule.this.ip_address, CommonsNetFtpModule.this.port);
                    client.enterLocalPassiveMode();
                    client.login(username, password);
                    promise.resolve(true);
                } catch (Exception e) {
                    promise.reject("ERROR", e.getMessage());
                }
            }
        }).start();
    }

    @ReactMethod
    public void list(final String path, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FTPFile[] files = new FTPFile[0];
                try {
                    files = client.listFiles(path);
                    JSONObject json = new JSONObject();
                    JSONArray arrfiles = new JSONArray();
                    for (FTPFile file : files) {
                        JSONObject tmp = new JSONObject();
                        tmp.put("name", file.getName());
                        tmp.put("size", file.getSize());
                        tmp.put("timestamp", file.getTimestamp());
                        arrfiles.put(tmp);
                    }
                    json.put("results", arrfiles);
                    promise.resolve(json.toString());
                } catch (Exception e) {
                    promise.reject("ERROR", e.getMessage());
                }
            }
        }).start();
    }

    @ReactMethod
    public void makedir(final String path, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.makeDirectory(path);
                    promise.resolve(true);
                } catch (IOException e) {
                    promise.reject("ERROR", e.getMessage());
                }
            }
        }).start();
    }

    @ReactMethod
    public void removedir(final String path, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.removeDirectory(path);
                    promise.resolve(true);
                } catch (IOException e) {
                    promise.reject("ERROR", e.getMessage());
                }
            }
        }).start();
    }

    @ReactMethod
    public void removeFile(final String path, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.deleteFile(path);
                    promise.resolve(true);
                } catch (IOException e) {
                    promise.reject("ERROR", e.getMessage());
                }
            }
        }).start();
    }

    @ReactMethod
    public void changeDirectory(final String path, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.changeWorkingDirectory(path);
                    promise.resolve(true);
                } catch (IOException e) {
                    promise.reject("ERROR", e.getMessage());
                }
            }
        }).start();
    }


    @ReactMethod
    public void logout(final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.logout();
                    client.disconnect();
                    promise.resolve(true);
                } catch (IOException e) {
                    promise.reject("ERROR", e.getMessage());
                }
            }
        }).start();
    }


    @ReactMethod
    public void uploadFile(final String path, final String remoteDestinationDir, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.setFileType(FTP.BINARY_FILE_TYPE);
                    File firstLocalFile = new File(path);

                    String firstRemoteFile = remoteDestinationDir + "/" + firstLocalFile.getName();
                    InputStream inputStream = new FileInputStream(firstLocalFile);

                    System.out.println("Start uploading first file");
                    boolean done = client.storeFile(firstRemoteFile, inputStream);
                    inputStream.close();
                    if (done) {
                        promise.resolve(true);
                    } else {
                        promise.reject("FAILED", firstLocalFile.getName() + " is not uploaded successfully.");
                    }
                } catch (IOException e) {
                    promise.reject("ERROR", e.getMessage());
                }
            }
        }).start();
    }

    @ReactMethod
    public void downloadFile(final String remoteFile1, final String localDestinationDir, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.setFileType(FTP.BINARY_FILE_TYPE);
                    File remoteFile = new File(remoteFile1);
                    File downloadFile1 = new File(localDestinationDir + "/" + remoteFile.getName());
                    OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
                    boolean success = client.retrieveFile(remoteFile1, outputStream1);
                    outputStream1.close();

                    if (success) {
                        promise.resolve(true);
                    } else {
                        promise.reject("FAILED", remoteFile.getName() + " is not downloaded successfully.");
                    }
                } catch (IOException e) {
                    promise.reject("ERROR", e.getMessage());
                }
            }
        }).start();
    }


    @ReactMethod
    public void downloadFileFTP(final String userName, final String password, final String ipAddress, final String remoteFile1, final String localDestinationDir, final Promise promise) {
        this.ip_address = ipAddress;
        this.port = 21;
//        sun.net.ftp.FtpClient client=null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect(CommonsNetFtpModule.this.ip_address, CommonsNetFtpModule.this.port);
                    client.enterLocalPassiveMode();
                    client.login(userName, password);

                    client.setFileType(FTP.BINARY_FILE_TYPE);
                    File remoteFile = new File(remoteFile1);
                    File downloadFile1 = new File(localDestinationDir + "/" + remoteFile.getName());
                    OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
                    boolean success = client.retrieveFile(remoteFile1, outputStream1);
                    outputStream1.close();

//                    if (success) {
//                        promise.resolve(true);
//                    } else {
//                        promise.reject("FAILED", remoteFile.getName() + " is not downloaded successfully.");
//                    }

//
                } catch (IOException e) {
                    promise.reject("ERROR", e.getMessage());
                } finally {
                    if (client.isConnected()) {
                        try {
                            client.logout();

                            client.disconnect();


                            promise.resolve(true);
                        } catch (IOException f) {
                            promise.reject("DISCON", f.getMessage());
                        }
                    }
                }
            }
        }).start();
    }


    //==============================================================================

    //==============================================================================

    File downloadFileResult = null;

    boolean success;
    String errorMsg;

//    @ReactMethod
//    public void download(
//            ReadableMap params,
//            final Promise promise) {
//
//        ReadableNativeMap map = (ReadableNativeMap) params;
//        final String userName = map.getString("username");
//        final String password = map.getString("password");
//
//
//
//        this.ip_address = map.getString("host");
//        this.port = 21;
//
//        final String remoteFile1 = map.getString("downloadFilePath");
//        final String localDestinationDir = map.getString("distPath");
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    client.connect(CommonsNetFtpModule.this.ip_address, CommonsNetFtpModule.this.port);
//                    client.enterLocalPassiveMode();
//                    client.login(userName, password);
//
//                    client.setFileType(FTP.BINARY_FILE_TYPE);
//                    File remoteFile = new File(remoteFile1);
//                    downloadFileResult = new File(localDestinationDir + "/" + remoteFile.getName());
//                    OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFileResult));
//
//                    success = client.retrieveFile(remoteFile1, outputStream1);
//
//                    outputStream1.close();
//                } catch (IOException e) {
//                    promise.reject("ERROR", e.getMessage());
//                } finally {
//                    if (client.isConnected()) {
//                        try {
//                            client.logout();
//
//                            client.disconnect();
//
//
//                            if (success) {
//                                promise.resolve(downloadFileResult.getAbsolutePath());
//                            } else {
//                                promise.reject("FAILED", downloadFileResult.getName() + " is not downloaded successfully.");
//                            }
//                        } catch (IOException f) {
//                            promise.reject("DISCON", f.getMessage());
//                        }
//                    }
//                }
//            }
//        }).start();
//    }

    @ReactMethod
    public void download(ReadableMap params,
                         final Promise promise) {
        //来自RN的字典参数
        ReadableNativeMap map = (ReadableNativeMap) params;
        final String userName = map.getString("username");
        final String password = map.getString("password");

        this.ip_address = map.getString("host");
        this.port = 21;

        final String remoteFile1 = map.getString("downloadFilePath");
        final String localDestinationDir = map.getString("distPath");

        //开始处理
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //链接FTP
                    client.connect(CommonsNetFtpModule.this.ip_address, CommonsNetFtpModule.this.port);
                    client.enterLocalPassiveMode();
                    client.login(userName, password);

                    client.setFileType(FTP.BINARY_FILE_TYPE);

                    //FTP远程文件
                    File remoteFile = new File(remoteFile1);
                    //本地存放文件
                    downloadFileResult = new File(localDestinationDir + "/" + remoteFile.getName());

                    //检查远程文件是否存在
                    FTPFile[] files = client.listFiles(remoteFile1/*new String(remoteFile1.getBytes("GBK"), "iso-8859-1")*/);
                    if (files.length != 1) {
                        // promise.reject("ERROR", "远程文件不存在");
                        success = false;
                        errorMsg = "远程文件不存在";
                    }


                    long lRemoteSize = files[0].getSize();

                    //本地存在文件，进行断点下载
                    if (downloadFileResult.exists()) {
                        long localSize = downloadFileResult.length();
                        //判断本地文件大小是否大于远程文件大小
                        if (localSize >= lRemoteSize) {
                            // promise.reject("ERROR", "本地文件大于远程文件，下载中止");
                            success = true;
                            errorMsg = "本地文件大于远程文件，下载中止";
                        }

                        //进行断点续传，并记录状态
                        FileOutputStream out = new FileOutputStream(downloadFileResult, true);
                        client.setRestartOffset(localSize);
                        InputStream in = client.retrieveFileStream(remoteFile1/*new String(remoteFile1.getBytes("GBK"), "iso-8859-1")*/);
                        byte[] bytes = new byte[1024];
                        long step = lRemoteSize / 100;
                        long process = localSize / step;
                        int c;


                        while ((c = in.read(bytes)) != -1) {
                            out.write(bytes, 0, c);
                            localSize += c;
                            long nowProcess = localSize / step;
                            if (nowProcess > process) {

                                process = nowProcess;
//                            if (process % 10 == 0) {//每10%发一次
//                                System.out.println("下载进度：" + process);
//
                                Log.e("progress", "====" + process);

                                //FTPDownloadProgress
                                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                        .emit("FTPDownloadProgress", String.valueOf(process));
//                            }
                                //TODO 更新文件下载进度,值存放在process变量中
                            }
                        }
                        in.close();
                        out.close();


                        success = client.completePendingCommand();
                    } else {
                        OutputStream out = new FileOutputStream(downloadFileResult);
                        InputStream in = client.retrieveFileStream(remoteFile1/*new String(remoteFile1.getBytes("GBK"), "iso-8859-1")*/);

                        byte[] bytes = new byte[1024];

                        long step = lRemoteSize / 100;//将分为100份
                        long process = 0;
                        long localSize = 0L;
                        int c;
                        while ((c = in.read(bytes)) != -1) {
                            out.write(bytes, 0, c);
                            localSize += c;

                            //当前进度
                            long nowProcess = localSize / step;

                            if (nowProcess > process) {

                                process = nowProcess;
//                            if (process % 10 == 0) {//每10%发一次
//                                System.out.println("下载进度：" + process);
//
                                Log.e("progress", "====" + process);

                                //FTPDownloadProgress
                                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                        .emit("FTPDownloadProgress", String.valueOf(process));
//                            }
                                //TODO 更新文件下载进度,值存放在process变量中
                            }
                        }
                        in.close();
                        out.close();

                        success = client.completePendingCommand();
                    }


//
//                    //FTP服务器文件大小
//                    long lRemoteSize = files[0].getSize();
//                    //Log
////                    Log.d("progress","lRemoteSize==="+lRemoteSize);
//
//                    OutputStream out = new FileOutputStream(downloadFileResult);
//                    InputStream in = client.retrieveFileStream(remoteFile1/*new String(remoteFile1.getBytes("GBK"), "iso-8859-1")*/);
//
//                    byte[] bytes = new byte[1024];
//
//                    long step = lRemoteSize / 100;//将分为100份
//                    long process = 0;
//                    long localSize = 0L;
//                    int c;
//                    while ((c = in.read(bytes)) != -1) {
//                        out.write(bytes, 0, c);
//                        localSize += c;
//
//                        //当前进度
//                        long nowProcess = localSize / step;
//
//                        if (nowProcess > process) {
//
//                            process = nowProcess;
////                            if (process % 10 == 0) {//每10%发一次
////                                System.out.println("下载进度：" + process);
////
//                                Log.e("progress", "====" + process);
//
//                                //FTPDownloadProgress
//                                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
//                                        .emit("FTPDownloadProgress", String.valueOf(process));
////                            }
//                            //TODO 更新文件下载进度,值存放在process变量中
//                        }
//                    }
//                    in.close();
//                    out.close();
//
//                   success= client.completePendingCommand();
                } catch (Exception e) {
                    promise.reject("ERROR", e.getMessage());
                } finally {
                    if (client.isConnected()) {
                        try {
                            client.logout();
                            client.disconnect();

                            if (success) {
                                promise.resolve(downloadFileResult.getAbsolutePath());
                            } else {
                                promise.reject("FAILED", downloadFileResult.getName() + " is not downloaded successfully."+errorMsg);
                            }
                        } catch (Exception f) {
                            promise.reject("DISCON", f.getMessage());
                        }
                    }
                }
            }
        }).start();
    }

}




