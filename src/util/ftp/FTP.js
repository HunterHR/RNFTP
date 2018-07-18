import ReactNative, {
    NativeModules,
    NativeEventEmitter
  } from 'react-native'
  
  
  const ftpClient = NativeModules.FTP
  const ftpKitEmitter = new NativeEventEmitter(ftpClient);
  
//   ip: '60.191.129.94',
//   port: 21,
//   username: 'ftp1.linuxidc.com',
//   password:'www.linuxidc.com',
//   remotePath:'2018年linuxidc.com/1月/Python网络数据采集 完整PDF高清晰中文版/Python网络数据采集 完整PDF高清晰中文版.7z'

  export class FTPUtil {
    static async download({
      username = 'DocManage',
      password = 'abc123!!',
      host = '172.26.136.167',
      downloadFilePath = '/2017-09-19/656e84b6-0c3a-42b4-8a24-9d41d2b74add.xlsx',
      distPath = '/sdcard',
      downloadProgress = (data) => {
        console.log(data)
      }
    } = {}) {
      if (downloadFilePath.length === 0
        || distPath.length === 0) {
        throw '参数非法'
      }
  
      // 预处理
      if (this.progressSubscription) { this.progressSubscription.remove() }
      this.progressSubscription = ftpKitEmitter.addListener('FTPDownloadProgress',
        (data) => {
          if (downloadProgress) {
            downloadProgress(data)

            if(data==='98'){
              alert("WAHAHA"+data);
            }
          }
        }
      )
  
      // 
      return await ftpClient.downloadProgress({
        username,
        password,
        host,
        downloadFilePath,
        distPath
      })
  
    }
  
    static removeListeners() {
      if (this.progressSubscription) { this.progressSubscription.remove() }
    }
  }
  
  