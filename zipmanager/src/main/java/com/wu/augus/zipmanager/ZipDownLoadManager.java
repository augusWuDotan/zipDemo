package com.wu.augus.zipmanager;

import android.util.Log;

import com.wu.augus.zipmanager.utils.Filebody;
import com.wu.augus.zipmanager.utils.LoadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 下載檔案管理器
 */
public class ZipDownLoadManager {
    public String TAG = "ZipDownLoadManager";
    private String API_BASE_URL = "http://mosaandnasa.com/Cocos-hotfix/zip/";
    private static final int CONNECTTIMEOUT = 5 * 5;
    private static final int WRITETIMEOUT = 5 * 5;
    private static final int READTIMEOUT = 10 * 5;

    private OkHttpClient.Builder httpClient;
    private Retrofit.Builder builder;
    private Retrofit ServiceGenerator;

    public LoadCallback loadCallback = null;
    private String GloblePath = "";//file 路徑

    //
    class writeBody {
        Boolean isSuccess;
        String PathName;
        String ErrorMessage;

        public writeBody() {
        }

        public String getPathName() {
            return PathName;
        }

        public void setPathName(String pathName) {
            PathName = pathName;
        }

        public Boolean getSuccess() {
            return isSuccess;
        }

        public void setSuccess(Boolean success) {
            isSuccess = success;
        }

        public String getErrorMessage() {
            return ErrorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            ErrorMessage = errorMessage;
        }
    }

    public ZipDownLoadManager(LoadCallback loadCallback, String GloblePath) {
        this.loadCallback = loadCallback;
        this.GloblePath = GloblePath;
        //初始化
        init();
    }

    final ProgressListener progressListener = new ProgressListener() {
        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            float percent = new BigDecimal((float) bytesRead / (float) contentLength).setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
            loadCallback.loadStatus(percent, bytesRead, contentLength, done);//todo 回傳下載訊息
        }
    };


    public void init() {

        httpClient = new OkHttpClient.Builder()
                //超時
                .connectTimeout(CONNECTTIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITETIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READTIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                                .build();
                    }
                })
                //錯誤重新連接
                .retryOnConnectionFailure(true);

        builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        ServiceGenerator = builder.client(httpClient.build()).build();
    }

    /**
     * 更新 baseurl
     *
     * @param newBaseUrl 新的主要路徑
     */
    public void changeBaseUrl(String newBaseUrl) {
        builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()
                );
        ServiceGenerator = builder.client(httpClient.build()).build();
    }


    public void LoadFile(String URL, final String FileName, final String FileExtension) {
        //zip/Mantichore.zip
        //check string
        if (URL.isEmpty()) return;
        if (URL.equals("")) return;
        if (FileName.isEmpty()) return;
        if (FileName.equals("")) return;

        //load start
        FileDownloadService downloadService = ServiceGenerator.create(FileDownloadService.class);
        //
        Call<ResponseBody> call = downloadService.LoadFile(URL);
        //
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //
                writeBody body = null;
                Filebody filebody = null;
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");
                    //
                    body = writeResponseBodyToDisk(response.body(), FileName, FileExtension);
                    //
                    Log.d(TAG, "file download was a success? " + body.isSuccess);
                    filebody = new Filebody();
                    filebody.setLoadState(body.isSuccess);
                    filebody.setLoadFilePath(body.isSuccess ? body.PathName : null);
                    filebody.setLoadMessage(body.isSuccess ? "success" : body.ErrorMessage);
                } else {
                    filebody = new Filebody();
                    filebody.setLoadState(false);
                    filebody.setLoadFilePath(null);
                    filebody.setLoadMessage("server contact failed");
                    Log.d(TAG, "server contact failed");
                }
                loadCallback.CallBackLoadData(filebody);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error");
                Filebody filebody = new Filebody();
                filebody.setLoadState(false);
                filebody.setLoadFilePath(null);
                filebody.setLoadMessage(t.getMessage());
                loadCallback.CallBackLoadData(filebody);
                call.cancel();
            }
        });

    }

    public void LoadFileName(final String FileName, final String FileExtension) {
        //check string
        if (FileName.isEmpty()) return;
        if (FileName.equals("")) return;

        //load start
        FileDownloadService downloadService = ServiceGenerator.create(FileDownloadService.class);
        //
        Call<ResponseBody> call = downloadService.LoadFileName(FileName + FileExtension);
        //
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //
                writeBody body = null;
                Filebody filebody = null;
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");
                    //
                    body = writeResponseBodyToDisk(response.body(), FileName, FileExtension);
                    //
                    Log.d(TAG, "file download was a success? " + body.isSuccess);
                    filebody = new Filebody();
                    filebody.setLoadState(body.isSuccess);
                    filebody.setLoadFilePath(body.isSuccess ? body.PathName : null);
                    filebody.setLoadMessage(body.isSuccess ? "success" : body.ErrorMessage);
                } else {
                    filebody = new Filebody();
                    filebody.setLoadState(false);
                    filebody.setLoadFilePath(null);
                    filebody.setLoadMessage("server contact failed");
                    Log.d(TAG, "server contact failed");
                }
                loadCallback.CallBackLoadData(filebody);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error");
                Filebody filebody = new Filebody();
                filebody.setLoadState(false);
                filebody.setLoadFilePath(null);
                filebody.setLoadMessage(t.getMessage());
            }
        });

    }


    private writeBody writeResponseBodyToDisk(ResponseBody body, String FileName, String FileExtension) {
        writeBody writebody = new writeBody();
        String FilePath = GloblePath + File.separator + FileName + FileExtension;
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(FilePath);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;/**/

                    //
                    float percent = new BigDecimal(((double) fileSizeDownloaded / (double) fileSize)).setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
                    loadCallback.writeStatus(percent, fileSizeDownloaded, fileSize);//todo 回傳下載訊息
                }

                outputStream.flush();

                writebody.setPathName(FilePath);
                writebody.setSuccess(true);
                writebody.setErrorMessage(null);
                return writebody;
            } catch (IOException e) {
                writebody.setPathName(FilePath);
                writebody.setSuccess(false);
                writebody.setErrorMessage(e.getMessage());
                return writebody;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            writebody.setPathName(FilePath);
            writebody.setSuccess(false);
            writebody.setErrorMessage(e.getMessage());
            return writebody;
        }

    }


    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }
}

