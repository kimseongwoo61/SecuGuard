package com.mobile.platform.SecuGuard;

import android.content.Context;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.spec.KeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;


public class Image_aes {
    private static String FILE_NAME = "encrypted_file.txt";
    private static SecretKey secretKey;

    public static void setFileName(String fileName) {
        FILE_NAME = fileName;
    }

    public static void savePassword(Context context, String password) {
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("user_password", password)
                .apply();
    }

    public static String getPassword(Context context) {
        return context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                .getString("user_password", "test111");
    }

    // 변경된 부분: 고정된 솔트 값 사용
    private static final byte[] FIXED_SALT = new byte[]{
            (byte) 0x5a, (byte) 0xb2, (byte) 0x18, (byte) 0xf1,
            (byte) 0xd2, (byte) 0xa3, (byte) 0xe3, (byte) 0x45,
            (byte) 0x81, (byte) 0x2f, (byte) 0x09, (byte) 0xc7,
            (byte) 0x5d, (byte) 0xe8, (byte) 0x29, (byte) 0x0c
    };

    // 변경된 부분: 솔트를 매번 생성하는 대신 고정된 솔트 사용
    public static SecretKey generateAESKey(String password) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), FIXED_SALT, 65536, 256);
        SecretKey tmp = factory.generateSecret(keySpec);
        SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        return secretKey;
    }

    public static void encryptFile(Context context, String password, File inputFilePath, String outputPath) throws GeneralSecurityException, Exception {
        SecretKey secretKey = generateAESKey(password);
        System.out.println("암호화");
        System.out.println(secretKeyToString(secretKey));
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // 패딩을 추가하여 Cipher 인스턴스 생성
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(readFileToByteArray(inputFilePath));


        try (FileOutputStream outputStream = new FileOutputStream(new File(outputPath, FILE_NAME))) {
            outputStream.write(encryptedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String secretKeyToString(SecretKey secretKey) {
        byte[] encodedKey = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }
    public static void decryptFile(Context context, String password, String inputPath, String outputPath) throws Exception {
        SecretKey secretKey = generateAESKey(password);
        File inputFile = new File(inputPath);
        System.out.println("복호화");
        System.out.println(secretKeyToString(secretKey));
        
        // 파일 이름 추출
        String originalFileName = inputFile.getName();
        byte[] encryptedBytes = readFileToByteArray(inputFile);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // 추출한 파일 이름으로 복호화된 파일 저장
        String decryptedFilePath = outputPath + File.separator + originalFileName;

        try (FileOutputStream outputStream = new FileOutputStream(new File(decryptedFilePath))) {
            outputStream.write(decryptedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static byte[] readFileToByteArray(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }
}
