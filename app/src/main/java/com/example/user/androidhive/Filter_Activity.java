package com.example.user.androidhive;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.androidhive.utils.BitmapUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.androidhive.MainActivity.receive0;

public class Filter_Activity extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener  {


    private static final String TAG = Filter_Activity.class.getSimpleName();

    public static final String IMAGE_NAME = "dog.jpg";

    public static final int SELECT_GALLERY_IMAGE = 101;

    @BindView(R.id.image_preview)
    ImageView imagePreview; // 상단 이미지 뷰 객체 설정

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    Bitmap finalImage;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }


    /********** 기본이미지 업로드를 위한 변수 *************/
    int serverResponseCode = 0;
    String total_path; // 이미지 파일 path uri
    String upLoadServerUri = "http://49.247.208.191/AndroidHive/Moim_default_img_Upload.php"; // 업로드할 서버 URI

    String name; // 이미지 파일이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_title_main));

//        /**
//         * 이미지 로드 메소드
//         * loadImage() 실행
//         */
//        loadImage();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


        // 인텐트로 저장한 이미지 파일 이름 가져옴
        Intent intent = getIntent();
        name = intent.getStringExtra("BMP"); // 파일 이름을 가져온다.
        Log.e("화면 : "+TAG+", 전달받은 파일이름 = ", name);

        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String foler_name = "/"+"Pictures"+"/";
        String string_path = ex_storage+foler_name;
        Log.e("파일 path = ", string_path);

        File imgFile = new  File(string_path + name + ".jpg");  // 전제주소 + 파일이름.jpg => 파일화

        Log.e("이미지파일 = ", String.valueOf(imgFile));
        total_path = string_path + name + ".jpg";
        Log.e("이미지파일 path = ", total_path);


        if(imgFile.exists()){   // 파일이 존재하면
            Log.e("이미지파일 존재함", "");
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath()); // 파일을 비트맵 이미지화 하여

            originalImage = myBitmap;
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
            imagePreview.setImageBitmap(originalImage); // imagePreview 에 setImageBitmap 설정


        }

    }// onCreate 끝


    /**
     *  뷰 페이저 초기 설정
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());   // 어댑터 객체 선언

        // 필터 리스트 프래그 먼트추가 = adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        // 수정 이미지 프래그먼트 추가 = = adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        // 프래그먼트 추가
        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));

        // 뷰페이저 어댑터 설정
        viewPager.setAdapter(adapter);
    }

    /**
     * 필터 선택 메소드
     */
    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */
    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * 필터 화면 시작과 동시에 이미지 로드
     */
    // load the default image from assets on app launch
    private void loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, IMAGE_NAME, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(originalImage); // imagePreview 에 setImageBitmap 설정
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open) {
            openImageFromGallery();
            return true;
        }

        if (id == R.id.action_save) {
            saveImageToGallery();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 갤러리에서 이미지 가져오면서 받아오는 결과값을
     * 이미지뷰에 setImageBitmap 하여
     * 이미지 표시
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

            // clear bitmap memory
            originalImage.recycle();
            finalImage.recycle();
            finalImage.recycle();

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            imagePreview.setImageBitmap(originalImage); // 갤러리에서 받아온 이미지 설정
            bitmap.recycle();

            // render selected image thumbnails
            filtersListFragment.prepareThumbnail(originalImage);
        }
    }

    /**
     * 갤러리 열고 이미지 가져 오는 메소드
     * 권한 확인이 주 내용
     */
    private void openImageFromGallery() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, SELECT_GALLERY_IMAGE);
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /*
     * saves image to camera gallery
     * */

    /**
     *  갤러리에 이미지 저장 + 서버에 이미지 저장하는 메소드
     */
    private void saveImageToGallery() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                            if (!TextUtils.isEmpty(path)) {
                                // 스낵바 생성
//                                Snackbar snackbar = Snackbar
//                                        .make(coordinatorLayout, "이미지 저장완료!!", Snackbar.LENGTH_LONG)
//                                        .setAction("확인", new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                openImage(path);
//                                            }
//                                        });
//
//                                snackbar.show();

                                // 이미지 서버 저장 코드 작성해야 함.
                                // 이미지 전달을 위한 작업 스레드 생성
                                new Thread(new Runnable() {
                                    public void run() {
                                        uploadFile(total_path); // 이미지 업로드 메소드
//                                        uploadFile(uploadFilePath + "" + uploadFileName);   // 업로드 경로 + 파일 이름. - 이걸 전달함으로써 업로드 된다.
                                    }
                                }).start();

                            //   finish();// 화면 종료
                            }
                            else {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Unable to save image!", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    // opening image in default image viewer app
    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }






    /**
     *  이미지 파일 업로드 위한  uploadFile 클래스
     */
    public int uploadFile(final String sourceFileUri) {   // 이미지 파일을 업로드 하기 위해서는 String sourceFileUri 필요  = uploadFilePath + "" + uploadFileName

        String fileName = sourceFileUri;    // 받아온 sourceFileUri 을 fileName 에 저장 한다.
        Log.e("파일이름 = ",fileName);

        HttpURLConnection conn = null;  // 커넥션 null
        DataOutputStream dos = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;

        byte[] buffer;

        int maxBufferSize = 1 * 1024 * 1024;    // 최대 버퍼 사이즈

        File sourceFile = new File(sourceFileUri);  // sourceFileUri에 해당 되는 File 객체 sourceFile를 생성한다

        if (!sourceFile.isFile()) { // 만약 sourceFile 이 파일이 존재하지 않으면
            Log.e("uploadFile", "Source File not exist :" + sourceFileUri);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Filter_Activity.this, "소스 파일이 존재하지 않습니다 :" +sourceFileUri, Toast.LENGTH_SHORT).show();
//                    messageText.setText("소스 파일이 존재하지 않습니다 :" +uploadFilePath + "" + uploadFileName);   // 파일이 존재하지 않는다고 토스트 띄움
                }
            });
            return 0;
        }
        else {  // 파일이 맞다면
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("fileToUpload", fileName);  // PHP 파일과 동일해야 하는 fileToUpload

                Log.e("업로드할 파일명 = ",""+fileName );

                dos = new DataOutputStream(conn.getOutputStream()); // 오류
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"fileToUpload\";filename=\"" + fileName + "\"" + lineEnd); // PHP 파일과 동일해야 하는 fileToUpload
                dos.writeBytes(lineEnd);


                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];


                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){  // 서버 반응 코드가 200 이면 결과 응답이 온 것.
                    runOnUiThread(new Runnable() { // UI 스레드
                        public void run() {

                            Toast.makeText(Filter_Activity.this, "이미지 업로드 성공", Toast.LENGTH_SHORT).show();
                            Log.e("이미지 업로드 확인 = ", "이미지 업로드 성공");

                            // Profile_IMG_data_uadate.php => 사용하여 이미지 파일이름 업로드 해야함.
                            // 아이디로 찾고, 아이디 이미지이름에 파일이름 넣고...
                            // 아이디 = receive0 , 파일이름 = name
                            // 이미지 이름 삽입을 위한 AsyncTask

                            String user_ID = receive0;  // 아이디 가져오기
                            Log.e("아이디 = ", user_ID);// 아이디 로그 확인

                            // 서버 DB 에 해당 아이디의 프로필 이미지 파일명 삽입
                            insert_IMG_Name_Task IMG_insert_task = new insert_IMG_Name_Task();
                            IMG_insert_task.execute(user_ID,name);  // 아이디 = receive0 , 파일이름 = name


                        }
                    });
                }


                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

                // 에코 데이터 받아 오는 곳 = 결과
                InputStream inputStream =conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader =  new BufferedReader(inputStreamReader);


                final StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) !=null)
                    sb.append(line);

                bufferedReader.close();
                sb.toString().trim();

                Log.e("Upload file to server", "error: " + sb.toString().trim());   // 데이터 결과를 받아와서 로그로 찍어본다.


            } catch (MalformedURLException ex) {
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Filter_Activity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Filter_Activity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload Exception", "Exception : " + e.getMessage(), e);
            }

//            progress_dialog.dismiss();
            return serverResponseCode;
        } // End else block
    }// uploadFile 끝




    /**
     * 이미지 파일 이름 데이터 입력 AsyncTask
     */
    private class insert_IMG_Name_Task extends AsyncTask<String, Void, String> {
        String errorString = null;
        @Override
        protected void onPreExecute() {     // 시작전
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {   // 완료 후
            super.onPostExecute(result);
            Log.e("", "response - " + result);

            String error_sql = result.substring(0,3);

            if (result.equals("데이터를 입력하세요.")){    // 값을 받아 오지 못했다면
                Log.e("", "데이터 제대로 전달이 안됨.");
            }
            else if(error_sql.equals("SQL문")){  // result에서 잘라낸 데이터가 'SQL문' 이라면 에러가 발생햇다는 의미
                Log.e("SQL문 에러 = ",result);
            }
            else {  // 데이터 제대로 입력되었다는 의미
                Log.e("이미지파일 이름 입력 성공 = ",result);

                finish(); // 화면 종료
            }
        }

        @Override
        protected String doInBackground(String... params) {

            // 아이디 , 이미지파일 이름
            String user_id = params[0];   // 채팅방 키 전달
            String IMG_Name  = params[1]; // 해당 모임명 전달

            Log.e("아이디 =",user_id);
            Log.e("이미지파일 =",IMG_Name);

            String serverURL = "http://49.247.208.191/AndroidHive/Profile/Profile_IMG_data_uadate.php";   // AndroidHive/Profile/Profile_IMG_data_uadate.php 의 파일로 이동
            String postParameters = "user_id=" + user_id + "&IMG_Name=" + IMG_Name ;   // 데이터 전달

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("word_List_Adapter", "response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream(); } else{
                    inputStream = httpURLConnection.getErrorStream(); }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d("word_List_Adapter", "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }// Create_Room_Task 끝





    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first

//        // render selected image thumbnails
//        // 썸네일 이미지 랜더링
//        filtersListFragment.prepareThumbnail(originalImage);
    }




}// 코드 끝


//        ImageView imageView2 = (ImageView)findViewById(R.id.imageView2);
//
//        Intent intent = getIntent();
//        String name = intent.getStringExtra("BMP"); // 파일 이름을 가져온다.
//        Log.e("전달받은 파일이름 = ", name);
//
//        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String foler_name = "/"+"Pictures"+"/";
//        String string_path = ex_storage+foler_name;
//
//        File imgFile = new  File(string_path + name + ".jpg");  // 전제주소 + 파일이름.jpg => 파일화
//
//        if(imgFile.exists()){   // 파일이 존재하면
//            Log.e("이미지파일 존재함", "");
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath()); // 파일을 비트맵 이미지화 하여
//            imageView2.setImageBitmap(myBitmap);    // 이미지 뷰에 표시한다.
//        }

