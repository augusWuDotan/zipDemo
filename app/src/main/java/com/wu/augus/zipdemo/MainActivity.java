package com.wu.augus.zipdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.wu.augus.zipmanager.ZipAccessManager;
import com.wu.augus.zipmanager.utils.ZipCallBack;

import java.io.File;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements ZipCallBack{
    ZipAccessManager zipAccessManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        String GlobalPath = getFilesDir().getAbsolutePath();
        zipAccessManager = ZipAccessManager.instance(GlobalPath);
        zipAccessManager.setZipCallBack(this);
        zipAccessManager.LoadFile("http://mosaandnasa.com/Cocos-hotfix/zip/monster05.zip","monster05",".zip");

        //test toZip
        Log.d("Zip",getFilesDir().getAbsolutePath());


    }

    @Override
    public void toZipSuccess() {

    }

    @Override
    public void toUnZipSuccess() {
        //todo 呼叫前端執行
        try{
            File file = new File(getFilesDir().getAbsolutePath());
            if(file.isDirectory()){
                File[] mfiles =  file.listFiles();
                for(File mfile : mfiles){
                    Log.d("ZipPath",mfile.getAbsolutePath());
                }
            }
        }catch (Exception e){

        }
    }

    @Override
    public void toUnRarSuccess() {

    }

    @Override
    public void showZipError(String error) {
        Log.d("zip","showZipError: "+error);
    }

    @Override
    public void loadStart() {

    }

    @Override
    public void loadStatus(String percent,long nowSize,long totalSize) {

    }

    @Override
    public void loadFileSuccess(String fileName) {
        //todo 此地呼叫解壓縮
        zipAccessManager.unzip(fileName,getFilesDir().getAbsolutePath(),"");
    }

    @Override
    public void showLoadFileError(String error) {

    }
}
