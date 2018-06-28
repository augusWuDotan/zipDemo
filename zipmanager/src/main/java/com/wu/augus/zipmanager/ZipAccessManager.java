package com.wu.augus.zipmanager;

import android.util.Log;

import com.wu.augus.zipmanager.rar.RarArchive;
import com.wu.augus.zipmanager.utils.Filebody;
import com.wu.augus.zipmanager.utils.LoadCallback;
import com.wu.augus.zipmanager.utils.ZipCallBack;
import com.wu.augus.zipmanager.zip.core.ZipFile;
import com.wu.augus.zipmanager.zip.model.ZipParameters;
import com.wu.augus.zipmanager.zip.util.Zip4jConstants;

import java.io.File;
import java.text.NumberFormat;

/**
 * augus
 * 2018.06.27
 * 單例
 * 可以解壓縮 .zip .tar .tar.gz & .tgz .tar.bz2 & .tbz2 .tar.xz & .txz .tar.lz4 & .tlz4 .tar.sz & .tsz
 * .rar (extract only ,may you can find a method to create look ir.mahdi.mzip.rar classes)
 */
public class ZipAccessManager implements LoadCallback {
    private static String TAG = "ZipAccessManager";
    //1.必須可以依據檔名設定fileName
    //2.解壓縮的層級
    //3.因為是單例所以需要下載清單

    public volatile static ZipAccessManager zipAccessManager;//
    public ZipCallBack zipCallBack;//
    public ZipDownLoadManager downLoadManager;

    public ZipAccessManager(String GlobalFilePath) {
        if (downLoadManager == null) downLoadManager = new ZipDownLoadManager(this, GlobalFilePath);
    }

    public static ZipAccessManager instance(String GlobalFilePath) {
        if (zipAccessManager == null) {
            synchronized (ZipAccessManager.class) {
                if (zipAccessManager == null) {
                    zipAccessManager = new ZipAccessManager(GlobalFilePath);
                }
            }
        }
        return zipAccessManager;
    }

    //
    public void setZipCallBack(ZipCallBack zipCallBack) {
        this.zipCallBack = zipCallBack;
    }

    //下載結束callback
    @Override
    public void CallBackLoadData(Filebody filebody) {
        if (filebody.isLoadState()) {
            //成功下載
            Log.d(TAG,"loadFileSuccess data: "+filebody.toString());
            this.zipCallBack.loadFileSuccess(filebody.getLoadFilePath());
        } else {
            //下載失敗
            Log.d(TAG,"showLoadFileError data: "+filebody.toString());
            this.zipCallBack.showLoadFileError(filebody.getLoadMessage());
        }
    }

    //下載中callback
    @Override
    public void loadStatus(float percent, long now , long total) {
        NumberFormat nf   =   NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(1);//第三位
        Log.d(TAG,"percent : "+nf.format(percent)+" ,nowSize : "+now+" ,totalSize : "+total);
        this.zipCallBack.loadStatus(nf.format(percent),now,total);
    }

    /**
     * 更新下載主要路徑
     *
     * @param baseURL
     */
    public void updateBaseUrl(String baseURL) {
        this.downLoadManager.changeBaseUrl(baseURL);
    }

    /**
     * 下載 [全路徑]
     */
    public void LoadFile(String URL, String FileName, String FileExtension) {
        Log.d(TAG,"LoadFile:loadStart");
        this.zipCallBack.loadStart();
        this.downLoadManager.LoadFile(URL, FileName, FileExtension);
    }

    /**
     * 下載 [主路徑]+/+[指定檔名]
     */
    public void LoadFile(String fileName, String FileExtension) {
        Log.d(TAG,"LoadFile:loadStart");
        this.zipCallBack.loadStart();
        this.downLoadManager.LoadFileName(fileName, FileExtension);
    }

    /**
     * 壓縮 zip
     *
     * @param targetFilePath      需要壓縮的路徑
     * @param destinationFilePath 目的地路徑
     * @param password            密碼「如果沒有就給空字串」
     */
    public void zip(String targetFilePath, String destinationFilePath, String password) {
        try {

            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            if (password.length() > 0) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                parameters.setPassword(password);
            }

            ZipFile zipFile = new ZipFile(destinationFilePath);

            File targetFile = new File(targetFilePath);
            if (targetFile.isFile()) {
                //是檔案 .isFile()
                zipFile.addFile(targetFile, parameters);
            } else if (targetFile.isDirectory()) {
                //是資料夾 .isDirectory()
                zipFile.addFolder(targetFile, parameters);
            }
            if (this.zipCallBack != null) {
                Log.d(TAG,"toZipSuccess");
                this.zipCallBack.toZipSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (this.zipCallBack != null) this.zipCallBack.showZipError(e.getMessage());
        }
    }


    /**
     * 解壓縮 zip
     *
     * @param targetZipFilePath
     * @param destinationFolderPath
     * @param password
     */
    public void unzip(String targetZipFilePath, String destinationFolderPath, String password) {
        try {
            //
            ZipFile zipFile = new ZipFile(targetZipFilePath);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(destinationFolderPath);
            //
            if (this.zipCallBack != null) {
                Log.d(TAG,"toUnZipSuccess");
                this.zipCallBack.toUnZipSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (this.zipCallBack != null) this.zipCallBack.showZipError(e.getMessage());
        }
    }

    /**
     * 解壓縮 rar
     *
     * @param targetZipFilePath
     * @param destinationFolderPath
     */
    public void unrar(String targetZipFilePath, String destinationFolderPath) {
        try {
            RarArchive rarArchive = new RarArchive();
            rarArchive.extractArchive(targetZipFilePath, destinationFolderPath);

            if (this.zipCallBack != null) {
                Log.d(TAG,"toUnRarSuccess");
                this.zipCallBack.toUnRarSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (this.zipCallBack != null) this.zipCallBack.showZipError(e.getMessage());
        }
    }

    /**
     * 解壓縮 rar
     *
     * @param targetZipFile
     * @param destinationFolder
     */
    public void unrar(File targetZipFile, File destinationFolder) {
        try {
            RarArchive rarArchive = new RarArchive();
            rarArchive.extractArchive(targetZipFile, destinationFolder);

            if (this.zipCallBack != null) {
                Log.d(TAG,"toUnRarSuccess");
                this.zipCallBack.toUnRarSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (this.zipCallBack != null) this.zipCallBack.showZipError(e.getMessage());
        }
    }

}
