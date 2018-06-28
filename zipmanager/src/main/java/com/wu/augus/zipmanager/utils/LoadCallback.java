package com.wu.augus.zipmanager.utils;

public interface LoadCallback {
    void CallBackLoadData(Filebody filebody);
    void loadStatus(float percent, long now , long total,boolean isDone);
    void writeStatus(float percent, long now , long total);
}
