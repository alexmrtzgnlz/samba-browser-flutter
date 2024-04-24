package dev.schlosser.samba_browser;

import androidx.annotation.RequiresApi;
import android.os.Build;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SambaDeleteRemoteFile {
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    static void deleteFile(MethodCall call, MethodChannel.Result result) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                String remoteFolderPath = call.argument("remoteFolderPath");

                SmbFile smbFile = new SmbFile(remoteFolderPath, new NtlmPasswordAuthentication(call.argument("domain"), call.argument("username"), call.argument("password")));
                
                if (smbFile.exists()) {
                    smbFile.delete();
                    result.success("File deleted successfully from server.");
                } else {
                    result.error("File does not exist on server.", null, null);
                }
            } catch (IOException e) {
                result.error("An IO error occurred.", e.getMessage(), null);
            }
        });
    }
}
