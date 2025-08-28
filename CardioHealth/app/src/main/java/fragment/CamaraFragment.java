package fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.res.Configuration;
import android.content.Context;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.NotificationChannel;

import androidx.core.app.NotificationCompat;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.cardiohealth.Helper.FaceLandmarkerHelper;
import com.example.cardiohealth.Model.view.MainViewModel;
import com.example.cardiohealth.R;
import com.example.cardiohealth.databinding.FragmentCamaraBinding;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.core.RunningMode;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CamaraFragment extends Fragment implements FaceLandmarkerHelper.LandmarkerListener {
    private static final String TAG = "Face Landmarker";
    private FragmentCamaraBinding fragmentCamaraBinding;
    private FaceLandmarkerHelper faceLandmarkerHelper;
    private final MainViewModel viewModel = new MainViewModel();
    private final FaceBlendshapesResultAdapter faceBlendshapesResultAdapter = new FaceBlendshapesResultAdapter();
    private Preview preview;
    private ImageAnalysis imageAnalyzer;
    private Camera camera;
    private ProcessCameraProvider cameraProvider;
    private int cameraFacing = CameraSelector.LENS_FACING_FRONT;
    private ExecutorService backgroundExecutor;
    private static final List<Integer> MEDIAL_LANDMARKS = Arrays.asList(0, 19, 17, 152, 10, 168);
    private static final int[] RIGHT_SIDE_POINTS = {75, 240, 165, 186, 57, 61, 185, 146, 76, 62, 77};
    private static final int[] LEFT_SIDE_POINTS = {305, 460, 391, 410, 287, 291, 409, 375, 206, 292, 307};

    @Override
    public void onResume() {
        super.onResume();

        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                    .navigate(R.id.action_camera_to_permissions);
            return;
        }

        if (faceLandmarkerHelper == null) {
            faceLandmarkerHelper = new FaceLandmarkerHelper(
                    0.5f,
                    0.5f,
                    0.5f,
                    1,
                    0,
                    RunningMode.LIVE_STREAM,
                    getContext(),
                    this
            );
        }

        backgroundExecutor.execute(() -> {
            if (faceLandmarkerHelper != null && faceLandmarkerHelper.isClose()) {
                faceLandmarkerHelper.setupFaceLandmarker();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (faceLandmarkerHelper != null) {
            viewModel.setMaxFaces(faceLandmarkerHelper.maxNumFaces);
            viewModel.setMinFaceDetectionConfidence(faceLandmarkerHelper.minFaceDetectionConfidence);
            viewModel.setMinFaceTrackingConfidence(faceLandmarkerHelper.minFaceTrackingConfidence);
            viewModel.setMinFacePresenceConfidence(faceLandmarkerHelper.minFacePresenceConfidence);
            viewModel.setDelegate(faceLandmarkerHelper.currentDelegate);

            backgroundExecutor.execute(faceLandmarkerHelper::clearFaceLandmarker);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentCamaraBinding = null;

        backgroundExecutor.shutdown();
        try {
            backgroundExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        fragmentCamaraBinding = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentCamaraBinding = FragmentCamaraBinding.inflate(inflater, container, false);
        return fragmentCamaraBinding.getRoot();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backgroundExecutor = Executors.newSingleThreadExecutor();

        fragmentCamaraBinding.viewFinder.post(() -> setUpCamera());

        backgroundExecutor.execute(() -> {
            faceLandmarkerHelper = new FaceLandmarkerHelper(
                    viewModel.getCurrentMinFaceDetectionConfidence(),
                    viewModel.getCurrentMinFaceTrackingConfidence(),
                    viewModel.getCurrentMinFacePresenceConfidence(),
                    viewModel.getCurrentMaxFaces(),
                    viewModel.getCurrentDelegate(),
                    RunningMode.LIVE_STREAM,
                    view.getContext(),
                    this
            );
        });
    }

    private void setUpCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraProvider = cameraProviderFuture.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                bindCameraUseCases();

                Toast toast = Toast.makeText(getContext(), "Sorria!", Toast.LENGTH_SHORT);
                View view = toast.getView();
                toast.show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }


    @SuppressLint("UnsafeOptInUsageError")
    private void bindCameraUseCases() {
        if (cameraProvider == null) {
            throw new IllegalStateException("Camera initialization failed.");
        }

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraFacing)
                .build();

        preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCamaraBinding.viewFinder.getDisplay().getRotation())
                .build();

        imageAnalyzer = new ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCamaraBinding.viewFinder.getDisplay().getRotation())
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build();

        imageAnalyzer.setAnalyzer(backgroundExecutor, image -> detectFace(image));

        cameraProvider.unbindAll();

        try {
            camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
            );
            preview.setSurfaceProvider(fragmentCamaraBinding.viewFinder.getSurfaceProvider());
        } catch (Exception exc) {
            Log.e(TAG, "Use case binding failed", exc);
        }
    }

    private void detectFace(ImageProxy imageProxy) {
        faceLandmarkerHelper.detectLiveStream(
                imageProxy,
                cameraFacing == CameraSelector.LENS_FACING_FRONT
        );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (imageAnalyzer != null) {
            imageAnalyzer.setTargetRotation(fragmentCamaraBinding.viewFinder.getDisplay().getRotation());
        }
    }

    public void analyze(List<NormalizedLandmark> landmarks) {
        if (landmarks == null || landmarks.isEmpty()) return;

        NormalizedLandmark leftMouth = landmarks.get(61);
        NormalizedLandmark rightMouth = landmarks.get(291);

        float leftMouthY = leftMouth.y();
        float rightMouthY = rightMouth.y();

        float mouthInclination = Math.abs(rightMouthY - leftMouthY);

        List<Integer> leftSidePointsList = new ArrayList<>();
        for (int point : LEFT_SIDE_POINTS) {
            leftSidePointsList.add(point);
        }

        List<Integer> rightSidePointsList = new ArrayList<>();
        for (int point : RIGHT_SIDE_POINTS) {
            rightSidePointsList.add(point);
        }

        float rmsLeft = calculateRMS(landmarks, leftSidePointsList);
        float rmsRight = calculateRMS(landmarks, rightSidePointsList);

        float rmsDifference = Math.abs(rmsLeft - rmsRight);

        Log.d("Assimetria", "Inclinação da boca: " + mouthInclination + " | Diferença RMS: " + rmsDifference);

        if (mouthInclination > 0.03 && rmsDifference > 0.18) {
            Log.d("Assimetria", "Inclinação da boca: " + mouthInclination + " | Diferença RMS: " + rmsDifference);
            sendAlert("Assimetria Facial Detetada. \n Realize a Análise Vocal.");
        }
    }

    private float calculateRMS(List<NormalizedLandmark> landmarks, List<Integer> sidePoints) {
        float totalSquaredDifference = 0;
        int count = 0;

        for (int index : sidePoints) {
            NormalizedLandmark point = landmarks.get(index);

            NormalizedLandmark closestMedial = landmarks.get(MEDIAL_LANDMARKS.get(0));
            float minDistance = Math.abs(point.x() - closestMedial.x());

            for (int medialIndex : MEDIAL_LANDMARKS) {
                NormalizedLandmark medialPoint = landmarks.get(medialIndex);
                float distance = Math.abs(point.x() - medialPoint.x());
                if (distance < minDistance) {
                    minDistance = distance;
                    closestMedial = medialPoint;
                }
            }

            float diffX = point.x() - closestMedial.x();
            float diffY = point.y() - closestMedial.y();
            float squaredDistance = diffX * diffX + diffY * diffY;

            totalSquaredDifference += squaredDistance;
            count++;
        }

        return (float) Math.sqrt(totalSquaredDifference / count);
    }

    public void sendAlert(String message) {

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "stroke_alert_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "AVC Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getContext(), channelId)
                .setContentTitle("Alerta de Análise Facial")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_health_alert)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(1, notification);
    }

    @Override
    public void onResults(FaceLandmarkerHelper.ResultBundle resultBundle) {
        getActivity().runOnUiThread(() -> {
            if (fragmentCamaraBinding != null) {

                if (resultBundle.result != null && !resultBundle.result.faceLandmarks().isEmpty()) {
                    analyze(resultBundle.result.faceLandmarks().get(0));
                }

                fragmentCamaraBinding.overlay.setResults(resultBundle.result, resultBundle.inputImageHeight,
                        resultBundle.inputImageWidth, RunningMode.LIVE_STREAM);

                fragmentCamaraBinding.overlay.invalidate();
            }
        });
    }

    @Override
    public void onEmpty() {
        fragmentCamaraBinding.overlay.clear();
        getActivity().runOnUiThread(() -> {
            faceBlendshapesResultAdapter.updateResults(null);
            faceBlendshapesResultAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onError(String error, int errorCode) {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            faceBlendshapesResultAdapter.updateResults(null);
            faceBlendshapesResultAdapter.notifyDataSetChanged();
        });
    }
}