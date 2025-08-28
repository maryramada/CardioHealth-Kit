package fragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cardiohealth.Helper.FaceLandmarkerHelper;
import com.example.cardiohealth.Model.view.MainViewModel;
import com.example.cardiohealth.R;
import com.example.cardiohealth.databinding.FragmentGalleryBinding;

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.core.RunningMode;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GalleryFragment extends Fragment implements FaceLandmarkerHelper.LandmarkerListener {

    public enum MediaType {
        IMAGE,
        VIDEO,
        UNKNOWN
    }
    private FragmentGalleryBinding fragmentGalleryBinding;
    private FaceLandmarkerHelper faceLandmarkerHelper;
    private final MainViewModel viewModel = new MainViewModel();
    private final FaceBlendshapesResultAdapter faceBlendshapesResultAdapter = new FaceBlendshapesResultAdapter();
    private ScheduledExecutorService backgroundExecutor;
    private static final String TAG = "GalleryFragment";
    private static final List<Integer> MEDIAL_LANDMARKS = Arrays.asList(0, 19, 17, 152, 10, 168);
    private static final int[] RIGHT_SIDE_POINTS = {75, 240, 165, 186, 57, 61, 185, 146, 76, 62, 77};
    private static final int[] LEFT_SIDE_POINTS = {305, 460, 391, 410, 287, 291, 409, 375, 206, 292, 307};

    private final ActivityResultLauncher<String[]> getContent =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    Uri mediaUri = uri;
                    MediaType mediaType = loadMediaType(mediaUri);
                    switch (mediaType) {
                        case IMAGE:
                            runDetectionOnImage(mediaUri);
                            break;
                        case VIDEO:
                            runDetectionOnVideo(mediaUri);
                            break;
                        case UNKNOWN:
                            updateDisplayView(mediaType);
                            Toast.makeText(
                                    requireContext(),
                                    "Unsupported data type.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentGalleryBinding = FragmentGalleryBinding.inflate(inflater, container, false);
        return fragmentGalleryBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentGalleryBinding.fabGetContent.setOnClickListener(v -> getContent.launch(new String[] {"image/*", "video/*"}));
        fragmentGalleryBinding.recyclerviewResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        fragmentGalleryBinding.recyclerviewResults.setAdapter(faceBlendshapesResultAdapter);
    }

    @Override
    public void onPause() {
        fragmentGalleryBinding.overlay.clear();
        if (fragmentGalleryBinding.videoView.isPlaying()) {
            fragmentGalleryBinding.videoView.stopPlayback();
        }
        fragmentGalleryBinding.videoView.setVisibility(View.GONE);
        fragmentGalleryBinding.imageResult.setVisibility(View.GONE);
        fragmentGalleryBinding.tvPlaceholder.setVisibility(View.VISIBLE);

        requireActivity().runOnUiThread(() -> {
            faceBlendshapesResultAdapter.updateResults(null);
            faceBlendshapesResultAdapter.notifyDataSetChanged();
        });
        super.onPause();
    }

    private void runDetectionOnImage(Uri uri) {
        backgroundExecutor = Executors.newSingleThreadScheduledExecutor();
        updateDisplayView(MediaType.IMAGE);

        Bitmap bitmap = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(
                        requireActivity().getContentResolver(),
                        uri
                );
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        uri
                );
            }

            if (bitmap != null) {

                Bitmap finalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                fragmentGalleryBinding.imageResult.setImageBitmap(finalBitmap);

                backgroundExecutor.execute(() -> {
                    faceLandmarkerHelper = new FaceLandmarkerHelper(
                            viewModel.getCurrentMinFaceDetectionConfidence(),
                            viewModel.getCurrentMinFaceTrackingConfidence(),
                            viewModel.getCurrentMinFacePresenceConfidence(),
                            viewModel.getCurrentMaxFaces(),
                            viewModel.getCurrentDelegate(),
                            RunningMode.IMAGE,
                            requireContext(),
                            this
                    );

                    FaceLandmarkerHelper.ResultBundle result =
                            faceLandmarkerHelper.detectImage(finalBitmap);

                    if (result != null) {
                        List<NormalizedLandmark> landmarks = result.result.faceLandmarks().get(0);
                        requireActivity().runOnUiThread(() -> {
                            if (fragmentGalleryBinding.recyclerviewResults.getScrollState()
                                    != ViewPager2.SCROLL_STATE_DRAGGING) {
                                faceBlendshapesResultAdapter.updateResults(result.result);
                                faceBlendshapesResultAdapter.notifyDataSetChanged();
                            }

                            fragmentGalleryBinding.overlay.setResults(
                                    result.result,
                                    finalBitmap.getHeight(),
                                    finalBitmap.getWidth(),
                                    RunningMode.IMAGE
                            );
                            analyze(landmarks);
                        });
                    } else {
                        Log.e(TAG, "Error running face landmarker.");
                    }
                    faceLandmarkerHelper.clearFaceLandmarker();
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load and process the image: " + e.getMessage());
        }
    }

    private void runDetectionOnVideo(Uri uri) {
        updateDisplayView(MediaType.VIDEO);

        fragmentGalleryBinding.videoView.setVideoURI(uri);
        fragmentGalleryBinding.videoView.setOnPreparedListener(mp -> mp.setVolume(0f, 0f));
        fragmentGalleryBinding.videoView.requestFocus();

        backgroundExecutor = Executors.newSingleThreadScheduledExecutor();
        backgroundExecutor.execute(() -> {
            faceLandmarkerHelper = new FaceLandmarkerHelper(
                    viewModel.getCurrentMinFaceDetectionConfidence(),
                    viewModel.getCurrentMinFaceTrackingConfidence(),
                    viewModel.getCurrentMinFacePresenceConfidence(),
                    viewModel.getCurrentMaxFaces(),
                    viewModel.getCurrentDelegate(),
                    RunningMode.VIDEO,
                    requireContext(),
                    this
            );

            requireActivity().runOnUiThread(() -> {
                fragmentGalleryBinding.videoView.setVisibility(View.GONE);
                fragmentGalleryBinding.progress.setVisibility(View.VISIBLE);
            });

            try {
                FaceLandmarkerHelper.VideoResultBundle resultBundle = faceLandmarkerHelper.detectVideoFile(uri, VIDEO_INTERVAL_MS);
                if (resultBundle != null) {
                    requireActivity().runOnUiThread(() -> displayVideoResult(resultBundle));
                } else {
                    Log.e(TAG, "Error running face landmarker.");
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException occurred while running face landmarker", e);
            }

            faceLandmarkerHelper.clearFaceLandmarker();
        });
    }

    private void displayVideoResult(FaceLandmarkerHelper.VideoResultBundle result) {
        fragmentGalleryBinding.videoView.setVisibility(View.VISIBLE);
        fragmentGalleryBinding.progress.setVisibility(View.GONE);
        fragmentGalleryBinding.videoView.start();
        long videoStartTimeMs = SystemClock.uptimeMillis();

        backgroundExecutor.scheduleAtFixedRate(() -> {
            requireActivity().runOnUiThread(() -> {
                long videoElapsedTimeMs = SystemClock.uptimeMillis() - videoStartTimeMs;
                int resultIndex = (int) (videoElapsedTimeMs / VIDEO_INTERVAL_MS);

                if (resultIndex >= result.results.size() || fragmentGalleryBinding.videoView.getVisibility() == View.GONE) {
                    backgroundExecutor.shutdown();
                } else {
                    fragmentGalleryBinding.overlay.setResults(result.results.get(resultIndex), result.inputImageHeight, result.inputImageWidth, RunningMode.VIDEO);

                    if (fragmentGalleryBinding.recyclerviewResults.getScrollState() != ViewPager2.SCROLL_STATE_DRAGGING) {
                        faceBlendshapesResultAdapter.updateResults(result.results.get(resultIndex));
                        faceBlendshapesResultAdapter.notifyDataSetChanged();
                    }
                }
            });
        }, 0, VIDEO_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private void updateDisplayView(MediaType mediaType) {
        fragmentGalleryBinding.imageResult.setVisibility(mediaType == MediaType.IMAGE ? View.VISIBLE : View.GONE);
        fragmentGalleryBinding.videoView.setVisibility(mediaType == MediaType.VIDEO ? View.VISIBLE : View.GONE);
        fragmentGalleryBinding.tvPlaceholder.setVisibility(mediaType == MediaType.UNKNOWN ? View.VISIBLE : View.GONE);
    }

    private MediaType loadMediaType(Uri uri) {
        String mimeType = requireContext().getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.startsWith("image")) return MediaType.IMAGE;
            if (mimeType.startsWith("video")) return MediaType.VIDEO;
        }
        return MediaType.UNKNOWN;
    }

    private void classifyingError() {
        requireActivity().runOnUiThread(() -> {
            fragmentGalleryBinding.progress.setVisibility(View.GONE);
            updateDisplayView(MediaType.UNKNOWN);
        });
    }

    @Override
    public void onEmpty() {
        fragmentGalleryBinding.overlay.clear();
        getActivity().runOnUiThread(() -> {
            faceBlendshapesResultAdapter.updateResults(null);
            faceBlendshapesResultAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onError(String error, int errorCode) {
        classifyingError();
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });
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
            Log.d("Assimetria", "Linha no OnResults");
            if (fragmentGalleryBinding != null) {
                if (resultBundle.result != null && !resultBundle.result.faceLandmarks().isEmpty()) {
                    analyze(resultBundle.result.faceLandmarks().get(0));
                }
            }
        });
    }
    private static final long VIDEO_INTERVAL_MS = 300L;
}
