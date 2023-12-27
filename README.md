# Gallery Encryption App

![001.jpg](https://github.com/kimseongwoo61/SecuGuard/blob/main/screen_shot/introduction.jpg)

## **Project Background**

- **Multimedia Encryption:** The app encrypts multimedia data using AES to prevent unauthorized access to users' private information.
- **File Access Restriction:** To prevent unauthorized file access, the app exclusively handles data within the internal storage.
- **Restricted Access to Authorized Users:** The app incorporates a login (password) logic for access control when users initially connect.



## **Key Features**

![003.png](https://github.com/kimseongwoo61/SecuGuard/blob/main/screen_shot/introduction2.png)

- **Login and Encryption Key Generation:** As a fundamental means of access control, the app utilizes SharedPreferences from the internal storage to perform information matching. It generates AES key data based on user-inputted data, which is subsequently used for file encryption.
- **Dashboard:** In addition to data encryption and decryption, the main activity includes a dashboard feature that provides convenience functions such as data reset and developer contact.
- **Encryption:** When a user loads data from the gallery, the app encrypts the data based on the AES key in the internal SharedPreferences.
- **File Decryption and External Export:** After decrypting data based on the internal storage key, users can intuitively view decrypted data through dynamic CardView. Clicking the export button saves the decrypted data to sdcard/Decrypt in external storage.
- **Additional Features:** Logout, Clear Internal Storage, Link to Google Play Store for Reviews.



## **Design Reference**

The app's design is inspired by [ClassroomUI-Android](https://github.com/Shashank02051997/ClassroomUI-Android).


## **Getting Started**

Follow these steps to get started with the Gallery Encryption App:

1. Clone the repository:
    
    ```bash
    git clone https://github.com/kimseongwoo61/SecuGuard.git
    ```
    
2. Open the project in your preferred Android development environment.
3. Build and run the app on your Android device or emulator.



## **Acknowledgments**

Special thanks to [Shashank02051997](https://github.com/Shashank02051997) for the inspiring design from [ClassroomUI-Android](https://github.com/Shashank02051997/ClassroomUI-Android).

Feel free to reach out for any questions or improvements!

