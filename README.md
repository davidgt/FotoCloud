FotoCloud
=========

FotoCloud is an Android app for managing your Facebook photos.

Featured services:
------------------

1.-LogIn/LogOut: It is necessary logIn using your Facebook account before accessing to the services. Then you can logOut using the menu option "LogOut" (this option clean your access token too) or pressing "back" to the login panel.

2.-Show your photos: After login, you can see the name of your albums and your photos easily.

3.-Download photo: When you are in the gridView screen, you have to long click on any picture and the photo will appear in your SD/FotoCloud/ (maybe you should refresh the SD card in order to show the picture in your gallery).
         
4.-Upload photo: When you're showing your albums or photos you can select the option  "Upload Photo" in your menu. Then, you should choose between use the camera or select a picture inside your photo gallery (the photo should appear inside FotoCloud Fotos Album).

5.- Languages supported: English and Spanish.

Screenshots
-----------

![Login][1] ![AlbumList][2] ![PhotoGrid][3]

Project Details and Requirements
--------------------------------

Minimun SDK Version: 7 Android 2.1


Target SDK Version: 17 Android 4.2


Tested on HTC Desire S (2.3.5).

Bugs Detected
-------------

-If you press "back" button while the app is "loading", it will crash.


-If you log out in other facebook app and then you try to log out again in fotoCloud, the app will crash.

In other non-tested devices the list will be increased but I'm working on it.

Libraries Used
--------------

ActionBar Sherlock: https://github.com/JakeWharton/ActionBarSherlock

FaceBook SDK for Android: https://developers.facebook.com/android/

Android-Universal-Image-Loader: https://github.com/nostra13/Android-Universal-Image-Loader

How to Build the Project in eclipse
-----------------------------------

1.- Clone the master branch in your computer and then, import it using "Import Android Code Into Workspace".

2.- Download FaceBook SDK and ActionBar Sherlock libraries, import it following the previous step.

3.- Add the libraries, right click on your project->Properties->Android->Add(Library)

Download FotoCloud (APK)
------------------------

Folder named APK contains the last master version of the APP, you can download and run it in your phone.






 [1]: http://img585.imageshack.us/img585/8504/loginvh.jpg
 [2]: http://img248.imageshack.us/img248/9864/albumlist.jpg
 [3]: http://img404.imageshack.us/img404/9634/photogrid.jpg
