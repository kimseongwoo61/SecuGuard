package com.mobile.platform.SecuGuard;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    LinearLayout profile;
    final int PICTURE_REQUEST_CODE = 100;
    final String InternalSaveEncrypt = "SecuGuard";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        // 선택된 사진에 대한 암호화되는 동안 로딩창을 띄우기 위한 팝업 설정
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("파일 암호화 중..."); // 팝업에 표시될 메시지
        progressDialog.setCancelable(false); // 팝업을 취소할 수 없도록 설정

        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        profile = header.findViewById(R.id.profile);
        profile.setOnClickListener(this);


        // 암호화, 복호화된 데이터 조회 및
        // 파일 외부 반출, 비밀번호 변경, 저장소 비우기에 대한
        // 각각의 리스너 설치
        TextView viewAllGallery = findViewById(R.id.view_all_gallery);
        TextView viewAllEncryptData = findViewById(R.id.view_all_encryptdata);

        ImageView exportFile = findViewById(R.id.exportFile);
        ImageView clearStorage = findViewById(R.id.clearData);

        viewAllGallery.setOnClickListener(this);
        viewAllEncryptData.setOnClickListener(this);

        exportFile.setOnClickListener(this);
        clearStorage.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 앱 종료 시 내부 decrypt 폴더와 파일을 삭제
        String internalDecryptFolder = getStoragePathWithSubdirectory(this, "Decrypt", 0);
        deleteDirectory(new File(internalDecryptFolder));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // home에서 로그아웃 버튼을 누르면 단순히 인텐드를 종료한다.
        if (id == R.id.nav_logout) {
            finish();
        }

        // 평가하기 버튼을 누른 경우 플레이 스토어를 연다.
        else if (id == R.id.nav_rate) {
            openGooglePlay();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        // 권한 요청을 위한 코드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 파일 쓰기 권한 확인
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 파일 쓰기 권한 요청
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }

            // 갤러리 읽기 권한 확인
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 갤러리 읽기 권한 요청
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
            }
        }

        // 암호화시킬 이미지 파일을 갤러리에서 가져온다.
        // 참고 : https://ghj1001020.tistory.com/368
        if (view.getId() == R.id.view_all_gallery) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

            // 사진을 여러개 선택할 수 있도록 한다
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");

            // 암호화 및 이미지 처리 진행.
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICTURE_REQUEST_CODE);
        }

        // 암호화된 데이터를 복호화한 다음, 해당 정보를 화면에 띄운다.
        if (view.getId() == R.id.view_all_encryptdata) {

            // 우선 내부 저장소에 복호화 파일을 저장할 수 있는 폴더 영역의 유무를 확인하고
            // 없으면 해당 폴더를 생성한다.
            String InternalDecryptFolder = getStoragePathWithSubdirectory(this, "Decrypt", 0);
            File folder = new File(InternalDecryptFolder);
            if (!folder.exists() || !folder.isDirectory()) {
                folder.mkdirs();
            }

            // 고정 저장경로(앱 내부 저장소의 SecuGuard 폴더)에 존재하는 암호화된 이미지 파일들의 리스트를 모두 받아온다.
            String secuGuardFolderPath = getStoragePathWithSubdirectory(this, "SecuGuard", 0);

            // 암호화된 파일이 없어서 복호화할 파일이 없는 경우
            File[] files = getFilesInFolder(secuGuardFolderPath);

            if (files == null || files.length == 0) {
                Toast.makeText(getApplicationContext(),
                                "암호화된 파일이 존재하지 않습니다.",
                                Toast.LENGTH_SHORT)
                        .show();
                return;
            } else {
                // 사용자가 사전에 입력한 복호화키를 이용하여 고정경로 하위 폴더인 dcrypt에 모두 복호화한 뒤 저장한다.
                // 복호화된 파일들을 화면에 뿌려준다.
                for (int i = 0; i < files.length; i++) {
                    try {
                        System.out.println(String.valueOf(files[i]));
                        Image_aes.decryptFile(this, Image_aes.getPassword(this), String.valueOf(files[i]), InternalDecryptFolder);
                    } catch (Exception e) {
                        System.out.println("파일 복호화 중 에러가 발생하였습니다.");
                        e.printStackTrace();
                    }
                }

                // 파일 복호화가 완료되면 해당 파일을 출력할 액티비티를 호출한다.
                File[] decryptedFiles = getFilesInFolder(InternalDecryptFolder);

                // Intent를 사용하여 새로운 액티비티로 전환
                Intent intent = new Intent(this, DecryptedFile.class);

                // 복호화된 파일 목록을 새로운 액티비티로 전달 (선택적)
                intent.putExtra("decryptedFiles", decryptedFiles);

                startActivity(intent);
            }
        }

        // 파일을 복호화한 뒤, 아래 경로에 파일을 저장한다.
        // 저장 경로 : 외부 저장소/download/Decrypt/(복호화된 파일들)
        if (view.getId() == R.id.exportFile){

            // 우선 외부 저장소에 복호화 파일을 저장할 수 있는 폴더 영역의 유무를 확인하고
            // 없으면 반출 폴더를 생성하도록 한다.
            String ExternalDecryptFolder = getStoragePathWithSubdirectory(this, "Decrypt", 1);
            File folder = new File(ExternalDecryptFolder);
            if (!folder.exists() || !folder.isDirectory()) {
                folder.mkdirs();
            }

            // 고정 저장경로(앱 내부 저장소의 SecuGuard 폴더)에 존재하는
            // 암호화된 이미지 파일들의 리스트를 모두 받아온다.
            String secuGuardFolderPath = getStoragePathWithSubdirectory(this,
                    "SecuGuard",
                    0);

            File[] files = getFilesInFolder(secuGuardFolderPath);

            // 암호화된 파일이 없어서 복호화할 파일이 없는 경우
            if (files == null) {
                Toast.makeText(getApplicationContext(),
                        "암호화된 파일이 존재하지 않습니다.",
                        Toast.LENGTH_SHORT)
                        .show();
            }

            else {
                // 사용자가 사전에 입력한 복호화키를 이용하여 외부 반출경로에 복호화하도록 한다.
                for (int i = 0; i < files.length; i++) {
                    try {
                        System.out.println(String.valueOf(files[i]));
                        Image_aes.decryptFile(this,
                                Image_aes.getPassword(this),
                                String.valueOf(files[i]),
                                ExternalDecryptFolder);

                    } catch (Exception e) {
                        System.out.println("파일 복호화 중 에러가 발생하였습니다.");
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getApplicationContext(), "파일 반출이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }


        // 내부 저장소에 존재하는 Secuguard, Decrypt 폴더와 내부 파일을 
        // 모두 삭제한다.(재궈적 탐색 필요.)
        // 삭제 전, 경고문 출력 후 사용자의 동의에 따라 해당 작업 시행.
        if (view.getId() == R.id.clearData){
            showAlert();
        }

    }

    private CardView createCardView(Context context, String filename) {
        // CardView 생성
        CardView cardView = new CardView(context);
        cardView.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        ));
        cardView.setCardElevation(4); // 원하는 값을 설정하세요
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(16, 16, 16, 16);

        // LinearLayout 생성
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setWeightSum(100);

        // ImageView 생성 및 설정
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                60, // 원하는 높이를 설정하세요
                25
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // 이미지 설정 코드 (이미지 파일 경로를 이용하여 이미지 설정)

        // TextView 생성 및 설정 (파일명 표시)
        TextView nameTextView = new TextView(context);
        nameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                75
        ));
        nameTextView.setText("파일 명: " + filename);
        // 나머지 TextView 설정 코드

        // LinearLayout에 ImageView와 TextView 추가
        linearLayout.addView(imageView);
        linearLayout.addView(nameTextView);

        // CardView에 LinearLayout 추가
        cardView.addView(linearLayout);

        return cardView;
    }

    private void openGooglePlay() {
        String appPackageName = getPackageName();

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    String[] imagePaths = new String[clipData.getItemCount()];

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri selectedImageUri = clipData.getItemAt(i).getUri();
                        imagePaths[i] = getPathFromUri(selectedImageUri);
                    }

                    showProgressDialog(); // 팝업 표시

                    // imagePaths : 암호화할 이미지들의 갤러리 상 경로
                    // 우선, 암호화된 데이터를 저장할 폴더가 존재하는지 확인한다.
                    String internalDir = getStoragePathWithSubdirectory(this, InternalSaveEncrypt, 0);
                    File folder = new File(internalDir);

                    if (!folder.exists() && !folder.isDirectory()) {
                        // 만약 해당 경로의 폴더가 없으면 폴더를 생성한다.
                        boolean success = folder.mkdirs();

                        // 만약 파일 생성 과정에서 문제가 발생하면 그냥 종료한다.
                        if (!success)
                            finish();
                    }

                    // 파일 암호화를 위한 객체 생성
                    Image_aes crypt = new Image_aes();

                    // AES 키를 생성하기 위한 로그인 사용자의 password를 불러옴.
                    String password = crypt.getPassword(this);

                    // 해당 파일을 사전에 정의된 경로(sdcard/secuGuard)로 모두 복사한 뒤,
                    for (int i = 0; i < imagePaths.length; i++) {

                        // AES 키를 생성하고, 각각의 이미지 파일을 암호화해서 저장한다.
                        // 단, 저장되는 암호화된 파일의 이름은 원본 파일의 이름이다.
                        File imageFile = new File(imagePaths[i]);

                        // 중복 방지를 위한 파일 이름 처리
                        crypt.setFileName(imageFile.getName());
                        // 파일 암호화를 진행 후, 내부 저장소의 SecuGuard 폴더에 원본 파일 명으로 저장한다.
                        try {
                            crypt.encryptFile(this, password, imageFile, internalDir);
                        } catch (Exception e) {
                            System.out.println("-----------------------------------------에러로그");
                            e.printStackTrace();
                        }

                    }
                    // 팝업을 통해 원본 파일 삭제 여부를 확인하고, 사용자의 동의에 따라 원본 파일을 삭제한다.
                    hideProgressDialog();
                }
            }
        }
    }

    // URI를 파일 경로로 변환하는 메서드
    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imagePath = cursor.getString(column_index);
            cursor.close();
            return imagePath;
        }

        return null;
    }

    // 팝업창을 출력한다.
    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    // 팝업창을 제거한다.
    private void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    // sourcePath -> destinationPath 으로 파일을 복사한다.
    // 복사하려는 경로에 동일한 파일이 존재하는 경우 (file name)_1,(file name)_2 이런 식으로
    // 인덱스 번호를 붙여서 복사한다.
    public static void copyFile(String sourcePath, String destinationFolder) {
        File sourceFile = new File(sourcePath);
        File destinationFolderFile = new File(destinationFolder);

        if (!destinationFolderFile.exists()) {
            boolean success = destinationFolderFile.mkdirs();
            if (!success) {
                System.err.println("폴더를 생성하는 데 문제가 발생했습니다.");
                return;
            }
        }

        String fileName = sourceFile.getName();
        File destinationFile = new File(destinationFolderFile, fileName);

        int fileIndex = 1;
        while (destinationFile.exists()) {
            // 이미 파일이 존재하면 인덱스를 늘려서 다시 시도
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            fileName = baseName + "_" + fileIndex + extension;
            destinationFile = new File(destinationFolderFile, fileName);
            fileIndex++;
        }

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            System.out.println("파일이 복사되었습니다. 새로운 파일명: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("파일 복사 중 오류가 발생했습니다.");
        }
    }

    // 메소드를 통해 내부 저장소 경로를 가져온다.
    // 단순 데이터 저장 목적 : "내부 저장소" + "/SecuGuard" -> flag 0
    // 외부 파일 반출할 경우 : "외부 저장소" + "/SecuGuard" -> flag 1
    public static String getStoragePathWithSubdirectory(Context context, String subdirectory, int flag) {
        // 앱의 내부 저장소 디렉토리 가져오기
        String internalStoragePath = context.getFilesDir().toString();
        String externalStoragePath = Environment.getExternalStorageDirectory().toString();
        String fullPath;

        if (flag == 0) {
            // "SecuGuard"라는 내부저장소 디렉토리 경로 생성
            fullPath = internalStoragePath + "/" + subdirectory;
        } else if (flag == 1) {
            // "SecuGuard"라는 외부저장소 디렉토리 경로 생성
            fullPath = externalStoragePath + "/" + subdirectory;
        } else {
            fullPath = null;
        }

        return fullPath;
    }

    private String generateUniqueFileName(String directory, String originalFileName) {
        File file = new File(directory, originalFileName);
        int index = 1;

        // 동일한 파일 이름이 이미 존재하는지 확인
        while (file.exists()) {
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            originalFileName = baseName + "_" + index + extension;
            file = new File(directory, originalFileName);
            index++;
        }

        return originalFileName;
    }

    public static File[] getFilesInFolder(String folderPath) {
        File folder = new File(folderPath);

        // 폴더가 존재하는지 확인
        if (folder.exists() && folder.isDirectory()) {
            // 폴더 안에 있는 파일 목록을 가져옴
            return folder.listFiles();
        } else {
            // 폴더가 존재하지 않거나 디렉터리가 아닌 경우 또는 에러가 발생한 경우 null 반환
            return null;
        }
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                File childFile = new File(dir, child);
                deleteDirectory(childFile);
            }
        }
        dir.delete();
    }

    public void showAlert(){
        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(Home.this);
        // alert의 title과 Messege 세팅
        myAlertBuilder.setTitle("Warning!!!");
        myAlertBuilder.setMessage("내부 암 * 복호화 파일을 모두 삭제할까요?\n작업 진행 시, 복구는 불가능 합니다.");

        // 버튼 추가 (Ok 버튼과 Cancle 버튼 )
        myAlertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // OK 버튼을 눌렸을 경우
                Toast.makeText(getApplicationContext(), "내부 저장소를 정리합니다.", Toast.LENGTH_SHORT).show();

                // 내부 저장소의 SecuGuard, Decrypt 폴더를
                // 완전히 삭제한다.(재귀적인 삭제 진행 -> 내부 파일도 완전 삭제)
                deleteDirectory(new File(getFilesDir(), "SecuGuard"));
                deleteDirectory(new File(getFilesDir(), "Decrypt"));
                
                // 작업 완료 여부를 화면에 표시한다.
                Toast.makeText(getApplicationContext(),"정리 완료", Toast.LENGTH_SHORT).show();
            }
        });

        myAlertBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancle 버튼을 눌렸을 경우
                Toast.makeText(getApplicationContext(),"작업을 중지합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // Alert를 생성해주고 보여주는 메소드(show를 선언해야 Alert가 생성됨)
        myAlertBuilder.show();
    }
}