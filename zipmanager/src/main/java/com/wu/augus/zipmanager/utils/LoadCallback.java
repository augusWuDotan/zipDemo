package com.wu.augus.zipmanager.utils;

public interface LoadCallback {
    void CallBackLoadData(Filebody filebody);
    void loadStatus(float percent, long now , long total);
}
