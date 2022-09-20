/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.objectdetection

import android.R.id
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.examples.objectdetection.databinding.ActivityMainBinding


/**
 * Main entry point into our app. This app follows the single-activity pattern, and all
 * functionality is implemented in the form of fragments.
 */


abstract class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onClick(v: View) {
        if (v.id == R.id.capture_btn) {
            try {
                //use standard intent to capture an image
                val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //we will handle the returned data in onActivityResult
                startActivityForResult(captureIntent, cameracapture)
            } catch (ante: ActivityNotFoundException) {
                //display an error message
                val errorMessage = "Whoops - your device doesn't support capturing images!"
                val toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }
    //keep track of camera capture intent
    private val cameracapture = 1

    //captured picture uri
    private var picUri: Uri? = null

    //keep track of cropping intent
    private val piccrop = 2

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if(requestCode == cameracapture){
                picUri = data!!.data
                performCrop()
            }
            else if(requestCode == piccrop){
                //get the returned data
                val extras = data!!.extras
                //get the cropped bitmap
                val thePic = extras!!.getParcelable<Bitmap>("data")
                //retrieve a reference to the ImageView
                val picView: ImageView = findViewById<View>(R.id.picture) as ImageView
                //display the returned cropped image
                picView.setImageBitmap(thePic)
            }
        }
    }

    private fun performCrop() {
        try {
            //call the standard crop action intent (the user device may not support it)

            //call the standard crop action intent (the user device may not support it)
            val cropIntent = Intent("com.android.camera.action.CROP")
            //indicate image type and Uri
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*")
            //set crop properties
            //set crop properties
            cropIntent.putExtra("crop", "true")
            //indicate aspect of desired crop
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            //indicate output X and Y
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256)
            cropIntent.putExtra("outputY", 256)
            //retrieve data on return
            //retrieve data on return
            cropIntent.putExtra("return-data", true)
            //start the activity - we handle returning in onActivityResult
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, piccrop)
        } catch (ante: ActivityNotFoundException) {
            //display an error message
            val errorMessage = "Whoops - your device doesn't support the crop action!"
            val toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
            toast.show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        // class ShootAndCropActivity extends Activity implements OnClickListener {
        //retrieve a reference to the UI button
        val captureBtn = findViewById<View>(R.id.capture_btn) as Button
        //handle button clicks
        captureBtn.setOnClickListener(this)
   }

     override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }
}
