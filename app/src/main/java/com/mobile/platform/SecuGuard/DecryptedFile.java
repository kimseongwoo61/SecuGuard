package com.mobile.platform.SecuGuard;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;

public class DecryptedFile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdecrypted);

        // 복호화된 파일 목록을 받아옴
        File[] decryptedFiles = (File[]) getIntent().getSerializableExtra("decryptedFiles");

        // 부모 레이아웃 가져오기
        LinearLayout parentLayout = findViewById(R.id.image_blank);

        // ScrollView 생성
        ScrollView scrollView = createScrollView();

        // LinearLayout 생성
        LinearLayout linearLayout = createLinearLayout();

        // decryptedFiles 배열을 이용하여 동적으로 CardView 및 ImageView를 생성하고 추가
        for (File file : decryptedFiles) {
            // CardView 생성
            CardView cardView = createCardView(this, file.getName(), "Your Create Time", file);
            // LinearLayout에 CardView 추가
            linearLayout.addView(cardView);
        }

        // ScrollView에 LinearLayout 추가
        scrollView.addView(linearLayout);

        // 부모 레이아웃에 ScrollView 추가
        parentLayout.addView(scrollView);
    }

    // ScrollView 생성 메서드
    private ScrollView createScrollView() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        return scrollView;
    }

    // LinearLayout 생성 메서드
    private LinearLayout createLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

    // CardView 생성 메서드
    public static CardView createCardView(Context context, String imageName, String createTime, File decryptedFiles) {
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(4);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(16, 16, 16, 16);

        // 내부 LinearLayout 생성
        LinearLayout internalLayout = createInternalLayout(context, decryptedFiles);

        // CardView에 내부 LinearLayout 추가
        cardView.addView(internalLayout);

        return cardView;
    }

    // 내부 LinearLayout 생성 메서드
    private static LinearLayout createInternalLayout(Context context, File decryptedFiles) {
        LinearLayout internalLayout = new LinearLayout(context);
        LinearLayout.LayoutParams internalParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        internalLayout.setLayoutParams(internalParams);
        internalLayout.setOrientation(LinearLayout.HORIZONTAL);
        internalLayout.setWeightSum(100);

        // ImageView 생성
        ImageView imageView = createImageView(context, decryptedFiles);

        // 두 번째 LinearLayout 생성
        LinearLayout textLayout = createTextLayout(context, decryptedFiles);

        // 내부 LinearLayout에 추가
        internalLayout.addView(imageView);
        internalLayout.addView(textLayout);

        return internalLayout;
    }

    // ImageView 생성 메서드
    private static ImageView createImageView(Context context, File decryptedFiles) {
        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                0,
                180,
                25
        );
        imageParams.rightMargin = 32;
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Uri imageUri = Uri.fromFile(decryptedFiles);
        imageView.setImageURI(imageUri);

        return imageView;
    }

    // 두 번째 LinearLayout 생성 메서드
    private static LinearLayout createTextLayout(Context context, File decryptedFiles) {
        LinearLayout textLayout = new LinearLayout(context);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                75
        );
        textLayout.setLayoutParams(textLayoutParams);
        textLayout.setOrientation(LinearLayout.VERTICAL);

        // 첫 번째 TextView 생성
        TextView nameTextView = createTextView(context, decryptedFiles.getName());
        // 두 번째 TextView 생성
        TextView createTimeTextView = createTextView(context, "Create Time : Your Create Time");

        // TextView를 두 번째 LinearLayout에 추가
        textLayout.addView(nameTextView);
        textLayout.addView(createTimeTextView);

        return textLayout;
    }

    // TextView 생성 메서드
    private static TextView createTextView(Context context, String text) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = 4;
        textView.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setTypeface(context.getResources().getFont(R.font.calibri));
        }
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setTextSize(16);
        textView.setText(text);

        return textView;
    }
}
