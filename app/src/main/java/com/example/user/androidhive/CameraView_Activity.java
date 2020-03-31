package com.example.user.androidhive;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class CameraView_Activity extends AppCompatActivity  implements CameraBridgeViewBase.CvCameraViewListener2  {


//    String uploadFilePath = "/storage/emulated/0/NOSTest/";
    String filename;
    Button camera_button;

    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView; // 카메라 담당
    private Mat matInput;
    private Mat matResult;
    Bitmap bmp = null;



    //    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public static native long loadCascade(String cascadeFileName );
    //    public static native void detect(long cascadeClassifier_face, long cascadeClassifier_eye, long matAddrInput, long matAddrResult);
    public static native int detect(long cascadeClassifier_face, long cascadeClassifier_eye, long matAddrInput, long matAddrResult);

    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

//    Imgcodecs.imwrite

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    FileOutputStream filestream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);    // 풀스크린 고정
        setContentView(R.layout.activity_camera_view_);

        // 카메라 버튼 클릭 리스너
        camera_button = (Button)findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"버튼 눌려짐",Toast.LENGTH_LONG).show();

                Bitmap bmp = null;
                try {
                    bmp = Bitmap.createBitmap(matInput.cols(), matInput.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(matInput, bmp);
                }
                catch (CvException e){
                    Log.d("Exception",e.getMessage());
                }
                Log.e("Bitmap = ", String.valueOf(bmp));

                // 파일 생성
                Random rnd = new Random();
                int num = rnd.nextInt(1000000);
                filename = "ex_save_"+String.valueOf(num);
                Log.e("파일이름 = ", filename);
                saveBitmaptoJpeg(bmp,"Pictures",filename);   // Bitmap bitmap,String folder, String name

                // 화면이동
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Filter_Activity.class); // 다음 넘어갈 클래스 지정  Test_Activity  Filter_Activity

                intent.putExtra("BMP",filename); // 파일 이름 넘긴다.

                startActivity(intent); // 다음 화면으로 넘어간다
                finish();// 액티비티 종료

            }
        });// 카메라 버튼 클릭 리스너 끝



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {
                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            else  read_cascade_file(); //추가
        }
        else  read_cascade_file(); //추가


        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

//        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0) 카메라 화면

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }// onClick 끝


    /**
     *  저장장치에 jpeg 파일로 이미지 저장.
     */
    public static void saveBitmaptoJpeg(Bitmap bitmap,String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();    // 외부 저장소 주소
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder+"/"; // 폴더 이름
        String file_name = name+".jpg"; // 저장 할 이름
        String string_path = ex_storage+foler_name; // 최종 저장소 주소

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }



    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        matInput = inputFrame.rgba();

        if ( matResult != null ) matResult.release();
        matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());

//        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        Core.flip(matInput, matInput, 1); // 카메라로부터 영상을 읽어올 때  전면 카메라의 경우 영상이 뒤집혀서 읽히기 때문에  180도 회전 시켜줘야 합니다.

//                           detect(cascadeClassifier_face,cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        final int detectValue = detect(cascadeClassifier_face,cascadeClassifier_eye, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());

//        Log.e("int data =  ", String.valueOf(detectValue)); // 얼굴 검출 수
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 현재 UI 스레드가 아니기 때문에 메시지 큐에 Runnable을 등록 함
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(detectValue == 0 ) {  // detectValue = 0 이면
                            camera_button.setVisibility(View.GONE);
                        }
                        else {
                            camera_button.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();

//        Log.e("matResult = ", String.valueOf(matResult)); // mat 결고ㅓㅏ

        return matResult; // 최종 결과를 안드로이드폰의 화면에 보여지도록 결과 Mat 객체를 리턴합니다.
    }// onCameraFrame


    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    // String[] PERMISSIONS  = {"android.permission.CAMERA"};
    String[] PERMISSIONS  = {"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE:  // 카메라 퍼미션 요청 코드
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    }else
                    {
                        read_cascade_file();
                    }
                }
                break;
        }
    }


    /**
     * 퍼미션 확인하는 다이얼 로그
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( CameraView_Activity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }



    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d( TAG, "copyFile :: 다음 경로로 파일복사 "+ pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }

    }// copyFile 끝

    private void read_cascade_file(){
        //copyFile 메소드는 Assets에서 해당 파일을 가져와
        //외부 저장소 특정위치에 저장하도록 구현된 메소드입니다.
        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        Log.d(TAG, "read_cascade_file:");

        //loadCascade 메소드는 외부 저장소의 특정 위치에서 해당 파일을 읽어와서
        //CascadeClassifier 객체로 로드합니다.
        cascadeClassifier_face = loadCascade( "haarcascade_frontalface_alt.xml");
        Log.d(TAG, "read_cascade_file:");

        cascadeClassifier_eye = loadCascade( "haarcascade_eye_tree_eyeglasses.xml");
    }// read_cascade_file 끝




}// 코드 끝
