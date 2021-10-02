/*
 * @Author: zemin zheng
 * @Date: 2021-09-20 16:03:42
 * @LastEditTime: 2021-10-01 20:14:38
 * @LastEditors: Please set LastEditors
 * @Description: minio工具类
 * @FilePath: \blogkoa\utils\minioUtils.js
 */
const fs = require("fs");
const moment = require("moment");
const minioClient = require("./minio.js");


/**
 * 可获取到对象属性（若属性为空，也不会报错）
 * @param obj
 * @param args
 */
const getVal = (obj, ...args) => {
  let out = null;
  if (obj || obj === 0) {
    out = obj;
    if (args && args.length > 0) {
      for (let index = 0; index < args.length; index++) {
        const key = args[index];
        out = out[key];
        if (out === undefined || out === null || out === "") {
          return null;
        }
      }
    } else {
      if (out === undefined || out === null || out === "") {
        return null;
      }
    }
  }
  return out;
};

class MinioUtils {
  constructor() {}

  /**
   * @description 列出全部存储桶
   *
   * @returns {Array}
   * @memberof MinioUtils
   * @example
   * minioUtils.getAllBuckets().then((res) => {  console.log(res);  });
   */
  getAllBuckets() {
    return new Promise((resolve, reject) => {
      minioClient
        .listBuckets()
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  }

  /**
   * @description 列出一个存储桶下的所有文件
   *
   * @return {Array}
   * @memberof MinioUtils
   * @example
   * minioUtils.getOneBuckets('music').then((res) => {  console.log(res);  });
   */
  getOneBuckets(bucketsName) {
    return new Promise((resolve, reject) => {
      if (!bucketsName) {
        resolve([]);
      }
      var stream = minioClient.listObjectsV2(bucketsName, "", true);
      let arr = [];

      stream.on("data", (obj) => {
        arr.push(obj);
      });

      stream.on("end", function () {
        resolve(arr);
      });

      stream.on("error", function (err) {
        console.log(err);
        reject(err);
      });
    });
  }

  /**
   * @description 判断是否有这个存储桶
   *
   * @param {String} bucketsName 存储桶的名字
   * @return {Boolean}
   * @memberof MinioUtils
   * @example
   * minioUtils.bucketExists('music').then((res) => {  console.log(res);  });
   */
  bucketExists(bucketsName) {
    return new Promise((resolve, reject) => {
      if (!bucketsName) {
        resolve();
      }

      minioClient.bucketExists(bucketsName, function (err, exists) {
        if (err) {
          console.log(err);
          resolve(false);
        }
        if (exists) {
          resolve(true);
        } else {
          resolve(false);
        }
      });
    });
  }

  /**
   * @description 创建一个新的存储桶。
   *
   * @param {*} bucketsName 存储桶的名字
   * @return {Boolean}
   * @memberof MinioUtils
   * @example
   * minioUtils.createBucket('test').then((res) => {  console.log(res);  });
   */
  createBucket(bucketsName) {
    return new Promise((resolve, reject) => {
      if (!bucketsName) {
        resolve();
      }

      minioClient.makeBucket(bucketsName, "us-east-1", function (err) {
        if (err) {
          console.log("Error creating bucket.", err);
          resolve(false);
        }

        resolve(true);
      });
    });
  }

  /**
   * @description 下载对象并将其保存为本地文件系统中的文件。
   *
   * @param {String} bucketsName 存储桶的名字
   * @param {String} file 文件名字
   * @param {String} filePath 写入的本地文件系统上的路径
   * @return {Boolean}
   * @memberof MinioUtils
   * @example
   * minioUtils.downFile(
   *   "music", "周杰伦 - Mojito202006152354441192.flac", "H:/85/周杰伦 - Mojito202006152354441192.flac"
   * ).then((res) => {  console.log(res);  });
   */
  downFile(bucketsName, file, filePath) {
    return new Promise((resolve, reject) => {
      if (!bucketsName || !file || !filePath) {
        resolve();
      }

      minioClient.fGetObject(bucketsName, file, filePath, function (err) {
        if (err) {
          console.log(err);
          resolve(false);
        }
        resolve(true);
      });
    });
  }

  /**
   * @description 下载的临时url
   *
   * @param {String} bucketsName 存储桶的名字
   * @param {String} fileName 存储桶中的文件名字
   * @param {number} [expiry=24*60*60] 以秒为单位的到期时间。默认值为 7 天。（可选的）
   * @return {String} URL
   * @memberof MinioUtils
   * @example
   * minioUtils.FileTmpUrl("music", "周.flac").then((res) => {  console.log(res);  });
   */
  FileTmpUrl(bucketsName, fileName, expiry = 24 * 60 * 60) {
    return new Promise((resolve, reject) => {
      if (!bucketsName || !fileName) {
        resolve();
      }

      minioClient.presignedUrl("GET", bucketsName, fileName, expiry, function (
        err,
        presignedUrl
      ) {
        if (err) {
          console.log(err);
          resolve(err);
        }

        resolve(presignedUrl);
      });
    });
  }

  /**
   * @description 往存储桶上传一个文件
   * @DO 参考upload文件
   *
   * @param {String} bucketsName 存储桶的名字
   * @param {File} file 文件对象
   * @param {String} fileType 文件类型（会在文件名前面拼上这个字符串，作为标识）
   * @return {String}
   * @memberof MinioUtils
   * @example
   * let fileArr = Object.entries(params.files);
   *
   * minioUtils.putFile("test", fileArr).then((res) => {  console.log(res);  });
   */
  putFile(bucketsName, file, fileType) {
    return new Promise((resolve, reject) => {
      if (
        !bucketsName ||
        !getVal(file, "0", "1", "path") ||
        !getVal(file, "0", "1", "name")
      ) {
        resolve();
      }

      const path = file[0][1].path;
      const fileName = file[0][1].name;
      const type = file[0][1].type;
      const nowDate = moment(new Date()).format("YYYYMMDDHHmmss");
      const random = Math.round((Math.random() * 9 + 1) * 1000);
      const fileNameRandom = nowDate + random;

      const fileSuffix = fileName.substring(fileName.lastIndexOf(".")); // 文件后缀名字
      const filePrefix = fileName.substring(0, fileName.lastIndexOf(".")); // 文件名字前面那段

      let objectName =
        filePrefix.length > 20
          ? filePrefix.substring(0, 20) + fileNameRandom + fileSuffix
          : filePrefix + fileNameRandom + fileSuffix;

      // console.log( fileName, type, nowDate, random, fileNameRandom);
      // console.log(filePrefix, fileSuffix);
      // console.log(objectName);

      var fileStream = fs.createReadStream(path);
      fs.stat(path, function (err, stats) {
        if (err) {
          console.log(err);
          resolve(err);
        }

        objectName = fileType + "_" + objectName;

        minioClient.putObject(
          bucketsName,
          objectName,
          fileStream,
          stats.size,
          { "Content-Type": type },
          function (err, etag) {
            if (err) {
              resolve(err);
            }

            resolve({
              fileName,
              bucketsName,
              fileContentType: type,
              size: stats.size,
              fileNameRandom,
              url: objectName,
            });
          }
        );
      });
    });
  }

  /**
   * @description 获取文件信息
   *
   * @param {String} bucketsName 存储桶的名字
   * @param {String} fileName 存储桶中需要获取文件信息的名字
   * @return {Object}
   * @memberof MinioUtils
   * @example
   * minioUtils.getFileInfo("test", '9.png').then((res) => {  console.log(res);  });
   */
  getFileInfo(bucketsName, fileName) {
    return new Promise((resolve, reject) => {
      if (!bucketsName || !fileName) {
        resolve();
      }

      minioClient.statObject(bucketsName, fileName, function (err, stat) {
        if (err) {
          console.log(err);
          resolve(err);
        }

        resolve(stat);
      });
    });
  }

  /**
   * @description 移除一个对象
   *
   * @param {String} bucketsName 存储桶的名字
   * @param {String} fileName 移出文件名字
   * @return {Boolean} 
   * @memberof MinioUtils
   * @example
   * minioUtils.removeObject("test", '9.png' ).then((res) => {  console.log(res);  });
   */
  removeObject(bucketsName, fileName) {
    return new Promise((resolve, reject) => {
      if (!bucketsName || !fileName) {
        resolve();
      }

      minioClient.removeObject(bucketsName, fileName, function (e) {
        if (e) {
          console.log("Unable to remove Objects ", e);
          resolve(false);
        }

        resolve(true);
      });
    });
  }

  /**
   * @description 删除objectsList 中的所有对象
   *
   * @param {String} bucketsName 存储桶的名字
   * @param {Array} objectsList 存储桶中要删除的对象列表
   * @return {Boolean}
   * @memberof MinioUtils
   * @example
   * minioUtils.removeObjects("test", ['9.png'] ).then((res) => {  console.log(res);  });
   */
  removeObjects(bucketsName, objectsList) {
    return new Promise((resolve, reject) => {
      if (!bucketsName || !objectsList) {
        resolve();
      }

      minioClient.removeObjects(bucketsName, objectsList, function (e) {
        if (e) {
          console.log("Unable to remove Objects ", e);
          resolve(false);
        }

        resolve(true);
      });
    });
  }
}

module.exports = MinioUtils;
