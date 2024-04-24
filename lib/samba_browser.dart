import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class SambaBrowser {
  static const MethodChannel _channel = MethodChannel('samba_browser');

  /// List all directories and files under a given URL.
  /// All shares will be returned by their full URL.
  /// The [domain] parameter is only required under Android.
  static Future<List> getShareList(String url, String domain, String username, String password) async {
    Map<String, String> args = {
      'url': url,
      'domain': domain,
      'username': username,
      'password': password,
    };

    final List drives = await _channel.invokeMethod('getShareList', args);
    return drives;
  }

  /// Save a file with a specified name under a given folder.
  /// After the download has finished, the local file URL will be returned.
  /// The [domain] parameter is only required under Android.
  static Future<String> saveFile(String saveFolder, String fileName, String url, String domain, String username, String password) async {
    Map<String, String> args = {
      'saveFolder': saveFolder.endsWith('/') ? saveFolder : '$saveFolder/',
      'fileName': fileName.startsWith('/') ? fileName.replaceFirst('/', '') : fileName,
      'url': url,
      'domain': domain,
      'username': username,
      'password': password,
    };

    final String filePath = await _channel.invokeMethod('saveFile', args);
    return filePath;
  }

  /// Save a file with a specified name under a given folder in a remote location.
  /// After the upload has finished, a confirmation message will be returned.
  /// The [domain] parameter is only required under Android.
  static Future<String> saveFileLocalToRemote(String remoteFilePath, String fileName, String localFilePath, String domain, String username, String password) async {
    Map<String, String> args = {
      'remoteFilePath': remoteFilePath.endsWith('/') ? remoteFilePath : '$remoteFilePath/',
      'fileName': fileName,
      'localFilePath': localFilePath,
      'domain': domain,
      'username': username,
      'password': password,
    };

    final String confirmationMessage = await _channel.invokeMethod('saveFileLocalToRemote', args);
    return confirmationMessage;
  }

  /// After the upload has finished, a confirmation message will be returned.
  /// The [domain] parameter is only required under Android.
  static Future<String> deleteFile(String remoteFolderPath, String domain, String username, String password,
  ) async {

      Map<String, String> args = {
        'remoteFolderPath': remoteFolderPath,
        'domain': domain,
        'username': username,
        'password': password,
      };
      final String confirmationMessage = await _channel.invokeMethod('deleteFile', args);
      return confirmationMessage;

  }
}
