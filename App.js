/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View} from 'react-native';
import FTP from './src/util/ftp'
import {FTPUtil} from './src/util/ftp/FTP'


export default class App extends Component{
  state = {
    ip: '172.26.136.167',
    port: 21,
    username: 'DocManage',
    password:'abc123!!',
    remotePath:'2017-09-19/656e84b6-0c3a-42b4-8a24-9d41d2b74add.xlsx'
  }
 

  //public void downloadFile(final String remoteFile1,final String localDestinationDir, final Promise promise){

    // public void downloadFTPFile(final String ipAddress, final String userName, final String password, final String remoteFile1, final String localDestinationDir, final Promise promise) {
    //   this.ip_address = ipAddress;
    //   this.port = 21;

    //download(final String userName, final String password, final String ipAddress, final String remoteFile1, final String localDestinationDir, final Promise promise) {
        
//react-native run-ios –simulator “iPhone X”
    download(){
      //2017-09-19/656e84b6-0c3a-42b4-8a24-9d41d2b74add.xlsx
      // FTP.download(this.state.username,this.state.password,this.state.ip,this.state.remotePath,'/sdcard').then(
      //   (result)=>{
      //     alert('OK'+result);
      //   },
      //   (error)=>{
      //     alert('ERROR'+error);
      //   }
      // )
      const path = FTPUtil.download({});

      alert(path)
    }


  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}  onPress={this.download.bind(this)}>Welcome to React Native!</Text>
        <Text style={styles.instructions}>To get started, edit App.js</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
