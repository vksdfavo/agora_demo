package com.example.newdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.newdemo.databinding.ActivityOpenCameraBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenCameraActivity extends AppCompatActivity {
    ActivityOpenCameraBinding binding;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private CameraManager cameraManager;
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private ImageReader imageReader;
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean isCameraRotated = false;
    private float initialZoomLevel = 1.0f; // Initial zoom level
    private float maxZoomLevel = 2.0f; // Maximum zoom level
    private float zoomLevel = initialZoomLevel;
    static int PICK_IMAGE_PHOTO = 1;

    private Rect zoomRect;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpenCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
                focusCamera();

            }
        });

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        binding.textureView.setSurfaceTextureListener(textureListener);

        ViewGroup.LayoutParams layoutParams = binding.textureView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.textureView.setLayoutParams(layoutParams);

        binding.flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCameraRotated = !isCameraRotated;
            }
        });

        binding.galleryImage.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_PHOTO);
        });

    }




    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupCamera(width, height);
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Handle surface texture size change, if needed
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // Handle surface texture updates, if needed
        }
    };


    private void setupCamera(int width, int height) {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    this.cameraId = cameraId;
                    break;
                }
            }

            StreamConfigurationMap map = cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = map.getOutputSizes(SurfaceTexture.class);

            //       Size chosenSize = chooseOptimalSize(sizes, width, height);

            getOptimalPreviewSize(sizes, width, height);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private Size chooseOptimalSize(Size[] sizes, int width, int height) {
        List<Size> bigEnough = new ArrayList<>();
        for (Size size : sizes) {
            if (size.getWidth() >= width && size.getHeight() >= height) {
                bigEnough.add(size);
            }
        }
        if (bigEnough.size() > 0) {
            return bigEnough.get(0);
        } else {
            return sizes[0];
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            try {
                cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        cameraDevice = camera;

                        createCaptureSession();
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        cameraDevice.close();
                        cameraDevice = null;
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        cameraDevice.close();
                        cameraDevice = null;
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Size getOptimalPreviewSize(Size[] sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) width / height;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Size size : sizes) {
            double ratio = (double) size.getWidth() / size.getHeight();
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.getHeight() - height) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.getHeight() - height);
            }
        }

        return optimalSize;
    }

    private void createCaptureSession() {
        try {
            SurfaceTexture surfaceTexture = binding.textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(binding.textureView.getWidth(), binding.textureView.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);

            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            Size[] outputSizes = map.getOutputSizes(SurfaceTexture.class);
            Size chosenSize = chooseOptimalSize(outputSizes, binding.textureView.getWidth(), binding.textureView.getHeight());

            imageReader = ImageReader.newInstance(chosenSize.getWidth(), chosenSize.getHeight(), ImageFormat.JPEG, 1);
            Surface imageSurface = imageReader.getSurface();

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, imageSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraCaptureSession = session;
                    startPreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    // Handle session configuration failure
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureImage() {
        try {
            if (imageReader == null) {
                // ImageReader is not initialized, return or handle the error
                return;
            }

            CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            cameraCaptureSession.stopRepeating();
            cameraCaptureSession.abortCaptures();
            cameraCaptureSession.capture(captureBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    imageReader.setOnImageAvailableListener(onImageAvailableListener, null);


                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private ImageReader.OnImageAvailableListener onImageAvailableListener = reader -> {
        Image image = reader.acquireLatestImage();
        if (image != null) {
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = 0;
            try {
                orientation = getImageOrientation(image, rotation);
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }

            // Convert the Image to Bitmap and apply rotation
            Bitmap bitmap = imageToBitmap(image);
            Bitmap rotatedBitmap = rotateBitmap(bitmap, orientation);


            String filePath = saveBitmapToFile(rotatedBitmap);
            Intent intent = new Intent(OpenCameraActivity.this, ShowImageActivity.class);
            intent.putExtra("imageFilePath", filePath);
            startActivity(intent);
            image.close();
        }
    };

    private int getImageOrientation(Image image, int rotation) throws CameraAccessException {
        CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

        int deviceOrientation;
        switch (rotation) {
            case Surface.ROTATION_0:
                deviceOrientation = 0;
                break;
            case Surface.ROTATION_90:
                deviceOrientation = 90;
                break;
            case Surface.ROTATION_180:
                deviceOrientation = 180;
                break;
            case Surface.ROTATION_270:
                deviceOrientation = 270;
                break;
            default:
                deviceOrientation = 0;
        }

        int totalRotation = (sensorOrientation + deviceOrientation + 360) % 360;
        return totalRotation;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int rotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String saveBitmapToFile(Bitmap bitmap) {
        String filePath = getExternalCacheDir() + File.separator + "image.png";
        try {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    private Bitmap imageToBitmap(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private void focusCamera() {
        try {
            // Check if the camera device supports autofocus
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            int[] afModes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
            if (afModes == null || afModes.length == 0 || (afModes.length == 1 && afModes[0] == CameraMetadata.CONTROL_AF_MODE_OFF)) {
                // Autofocus is not supported, return or handle the error
                return;
            }

            // Set the desired autofocus mode
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);

            // Capture a still image with autofocus
            cameraCaptureSession.capture(captureRequestBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

        }
    };

    private void startPreview() {
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                // Permission denied, handle accordingly
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (binding.textureView.isAvailable()) {
            setupCamera(binding.textureView.getWidth(), binding.textureView.getHeight());
            openCamera();
        } else {
            binding.textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    protected void onPause() {
        closeCamera();
        super.onPause();
    }
    private void closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_PHOTO && resultCode == -1) {
            Uri imageUriPhotos = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUriPhotos);
                Bitmap rotatedBitmap = rotateBitmap(bitmap, 1);
                String filePath = saveBitmapToFile(rotatedBitmap);
                Intent intent = new Intent(OpenCameraActivity.this, ShowImageActivity.class);
                intent.putExtra("imageFilePath", filePath);
                startActivity(intent);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }
}