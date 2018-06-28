package com.wu.augus.zipmanager.utils;

import java.io.File;
import java.io.Serializable;

/**
 * 檔案下載內容
 */
public class Filebody implements Serializable {
    /**
     * 下載狀態
     * true : 成功下載Filebody
     * false : 下載失敗
     */
    private boolean LoadState;
    /**
     * 下載的檔案路徑
     */
    private String LoadFilePath;//下載檔案
    /**
     * 下載訊息
     * 「失敗會附加訊息」
     */
    private String LoadMessage;


    public Filebody(boolean loadState, String loadFilePath, String loadMessage) {
        LoadState = loadState;
        LoadFilePath = loadFilePath;
        LoadMessage = loadMessage;
    }

    public Filebody() {

    }

    public boolean isLoadState() {
        return LoadState;
    }

    public void setLoadState(boolean loadState) {
        LoadState = loadState;
    }

    public String getLoadFilePath() {
        return LoadFilePath;
    }

    public void setLoadFilePath(String loadFilePath) {
        LoadFilePath = loadFilePath;
    }

    public String getLoadMessage() {
        return LoadMessage;
    }

    public void setLoadMessage(String loadMessage) {
        LoadMessage = loadMessage;
    }

    @Override
    public String toString() {
        return "Filebody{" +
                "LoadState=" + LoadState +
                ", LoadFilePath='" + LoadFilePath + '\'' +
                ", LoadMessage='" + LoadMessage + '\'' +
                '}';
    }
}

