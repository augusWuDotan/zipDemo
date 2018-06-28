package com.wu.augus.zipmanager.utils;

import java.io.File;

public interface ZipCallBack {
    //
    void loadStart();
    void loadStatus(String percent,long now ,long total);
    void loadFileSuccess(String fileName);
    void showLoadFileError(String error);
    //
    void toZipSuccess();
    void toUnZipSuccess();
    void toUnRarSuccess();
    //
    void showZipError(String error);
}
