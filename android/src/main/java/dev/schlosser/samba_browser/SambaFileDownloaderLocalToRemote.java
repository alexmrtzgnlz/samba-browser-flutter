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


public class SambaFileDownloaderLocalToRemote {

    private static final int FILE_CACHE_SIZE = 8192;

    @RequiresApi(api = Build.VERSION_CODES.N)
    static void saveFileLocalToRemote(MethodCall call, MethodChannel.Result result)  {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            String localFilePath = call.argument("localFilePath");
            String remoteFilePath = call.argument("remoteFilePath");
            String fileName = call.argument("fileName"); // Obtener el nombre del archivo

            try {
                File localFile = new File(localFilePath);
                FileInputStream in = new FileInputStream(localFile);

                // Construir la ruta completa del archivo remoto
                String fullRemoteFilePath = remoteFilePath + fileName;

                // Crear un objeto SmbFile para representar el directorio remoto
                SmbFile remoteDirectory = new SmbFile(remoteFilePath, new NtlmPasswordAuthentication(call.argument("domain"), call.argument("username"), call.argument("password")));
                
                // Verificar si el directorio remoto existe, si no, crearlo
                if (!remoteDirectory.exists()) {
                    remoteDirectory.mkdirs();
                }

                // Crear el objeto SmbFile para el archivo remoto
                SmbFile smbFile = new SmbFile(fullRemoteFilePath, new NtlmPasswordAuthentication(call.argument("domain"), call.argument("username"), call.argument("password")));
                SmbFileOutputStream out = new SmbFileOutputStream(smbFile);

                byte[] buffer = new byte[FILE_CACHE_SIZE];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                out.close();
                in.close();

                result.success("File uploaded successfully to server.");
            } catch (IOException e) {
                result.error("An IO error occurred.", e.getMessage(), null);
            }
        });
    }

}


// package dev.schlosser.samba_browser;

// import androidx.annotation.RequiresApi;
// import android.os.Build;
// import io.flutter.plugin.common.MethodCall;
// import io.flutter.plugin.common.MethodChannel;
// import java.io.IOException;
// import jcifs.smb.NtlmPasswordAuthentication;
// import jcifs.smb.SmbFile;
// import jcifs.smb.SmbFileOutputStream;
// import java.io.InputStream;

// public class SambaFileDownloaderLocalToRemote {

//     private static final int FILE_CACHE_SIZE = 8192;

//     @RequiresApi(api = Build.VERSION_CODES.N)
//     static void saveFileLocalToRemote(MethodCall call, MethodChannel.Result result)  {
//         String url = call.argument("url");

//         try {
//             SmbFile smbFile = new SmbFile(url, new NtlmPasswordAuthentication(call.argument("domain"), call.argument("username"), call.argument("password")));
//             InputStream in = smbFile.getInputStream();
            
//             SmbFileOutputStream out = new SmbFileOutputStream(smbFile);
            
//             byte[] buffer = new byte[FILE_CACHE_SIZE];
//             int bytesRead;
            
//             while ((bytesRead = in.read(buffer)) != -1) {
//                 out.write(buffer, 0, bytesRead);
//             }
            
//             out.close();
//             in.close();
            
//             result.success("File downloaded successfully to server.");
//         } catch (IOException e) {
//             result.error("An IO error occurred.", e.getMessage(), null);
//         }
//     }
// }
