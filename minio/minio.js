/*
 * @Author: zemin zheng
 * @Date: 2021-09-01 22:35:07
 * @LastEditTime: 2021-09-30 11:43:41
 * @LastEditors: Please set LastEditors
 * @Description: minio文件服务器配置文件
 * @FilePath: \blogkoa\config\minio.js
 */

var Minio = require("minio");

let endPoint = "127.0.0.1";

var minioClient = new Minio.Client({
  endPoint,
  port: 9000,
  useSSL: false,
  accessKey: "admin",
  secretKey: "123456",
});

module.exports = minioClient;
