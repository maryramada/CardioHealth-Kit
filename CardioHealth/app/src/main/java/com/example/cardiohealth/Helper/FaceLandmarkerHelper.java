package com.example.cardiohealth.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.camera.core.ImageProxy;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.core.Delegate;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FaceLandmarkerHelper {
    public static final String TAG = "FaceLandmarkerHelper";
    private static final String MP_FACE_LANDMARKER_TASK = "assets/face_landmarker.task";
    public static final int DELEGATE_CPU = 0;
    public static final int DELEGATE_GPU = 1;
    public static final float DEFAULT_FACE_DETECTION_CONFIDENCE = 0.5F;
    public static final float DEFAULT_FACE_TRACKING_CONFIDENCE = 0.5F;
    public static final float DEFAULT_FACE_PRESENCE_CONFIDENCE = 0.5F;
    public static final int DEFAULT_NUM_FACES = 1;
    public static final int OTHER_ERROR = 0;
    public static final int GPU_ERROR = 1;
    public float minFaceDetectionConfidence = DEFAULT_FACE_DETECTION_CONFIDENCE;
    public float minFaceTrackingConfidence = DEFAULT_FACE_TRACKING_CONFIDENCE;
    public float minFacePresenceConfidence = DEFAULT_FACE_PRESENCE_CONFIDENCE;
    public int maxNumFaces = DEFAULT_NUM_FACES;
    public int currentDelegate = DELEGATE_CPU;
    public RunningMode runningMode = RunningMode.LIVE_STREAM;
    public Context context;
    public LandmarkerListener faceLandmarkerHelperListener;
    public FaceLandmarker faceLandmarker;

    public FaceLandmarkerHelper(
            float minFaceDetectionConfidence,
            float minFaceTrackingConfidence,
            float minFacePresenceConfidence,
            int maxNumFaces,
            int currentDelegate,
            RunningMode runningMode,
            Context context,
            LandmarkerListener faceLandmarkerHelperListener) {
        this.minFaceDetectionConfidence = minFaceDetectionConfidence == 0 ? DEFAULT_FACE_DETECTION_CONFIDENCE : minFaceDetectionConfidence;
        this.minFaceTrackingConfidence = minFaceTrackingConfidence == 0 ? DEFAULT_FACE_TRACKING_CONFIDENCE : minFaceTrackingConfidence;
        this.minFacePresenceConfidence = minFacePresenceConfidence == 0 ? DEFAULT_FACE_PRESENCE_CONFIDENCE : minFacePresenceConfidence;
        this.maxNumFaces = maxNumFaces == 0 ? DEFAULT_NUM_FACES : maxNumFaces;
        this.currentDelegate = currentDelegate;
        this.runningMode = runningMode;
        this.context = context;
        this.faceLandmarkerHelperListener = faceLandmarkerHelperListener;
        setupFaceLandmarker();
    }

    public void clearFaceLandmarker() {
        if (faceLandmarker != null) {
            faceLandmarker.close();
            faceLandmarker = null;
        }
    }

    public boolean isClose() {
        return faceLandmarker == null;
    }

    public void setupFaceLandmarker() {
        BaseOptions.Builder baseOptionBuilder = BaseOptions.builder();

        switch (currentDelegate) {
            case DELEGATE_CPU:
                baseOptionBuilder.setDelegate(Delegate.CPU);
                break;
            case DELEGATE_GPU:
                baseOptionBuilder.setDelegate(Delegate.GPU);
                break;
        }

        baseOptionBuilder.setModelAssetPath(MP_FACE_LANDMARKER_TASK);

        if (runningMode == RunningMode.LIVE_STREAM && faceLandmarkerHelperListener == null) {
            throw new IllegalStateException(
                    "faceLandmarkerHelperListener must be set when runningMode is LIVE_STREAM.");
        }

        try {
            BaseOptions baseOptions = baseOptionBuilder.build();
            FaceLandmarker.FaceLandmarkerOptions.Builder optionsBuilder =
                    FaceLandmarker.FaceLandmarkerOptions.builder()
                            .setBaseOptions(baseOptions)
                            .setMinFaceDetectionConfidence(minFaceDetectionConfidence)
                            .setMinTrackingConfidence(minFaceTrackingConfidence)
                            .setMinFacePresenceConfidence(minFacePresenceConfidence)
                            .setNumFaces(maxNumFaces)
                            .setOutputFaceBlendshapes(true)
                            .setRunningMode(runningMode);

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                        .setResultListener(this::returnLivestreamResult)
                        .setErrorListener(this::returnLivestreamError);
            }

            FaceLandmarker.FaceLandmarkerOptions options = optionsBuilder.build();
            faceLandmarker = FaceLandmarker.createFromOptions(context, options);

            Log.d("FaceLandmarker", "FaceLandmarker criado com sucesso.");
        } catch (IllegalStateException e) {
            Log.e(TAG, "Erro de estado: " + e.getMessage());
            reportErrorToListener("Erro ao inicializar o Face Landmarker.", e);
        } catch (RuntimeException e) {
            Log.e(TAG, "Erro em tempo de execução: " + e.getMessage());
            reportErrorToListener("Erro ao carregar o modelo Face Landmarker.", e);
        }
    }

    private void reportErrorToListener(String message, Exception e) {
        if (faceLandmarkerHelperListener != null) {
            faceLandmarkerHelperListener.onError(message, GPU_ERROR);
        }
    }

    public void detectLiveStream(ImageProxy imageProxy, boolean isFrontCamera) {
        if (runningMode != RunningMode.LIVE_STREAM) {
            throw new IllegalArgumentException("Attempting to call detectLiveStream while not using RunningMode.LIVE_STREAM");
        }

        long frameTime = SystemClock.uptimeMillis();

        Bitmap bitmapBuffer = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
        try {
            bitmapBuffer.copyPixelsFromBuffer(imageProxy.getPlanes()[0].getBuffer());
        } finally {
            imageProxy.close();
        }
        imageProxy.close();

        Matrix matrix = new Matrix();
        matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());
        if (isFrontCamera) {
            matrix.postScale(-1f, 1f, imageProxy.getWidth(), imageProxy.getHeight());
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapBuffer, 0, 0, bitmapBuffer.getWidth(), bitmapBuffer.getHeight(), matrix, true);
        MPImage mpImage = new BitmapImageBuilder(rotatedBitmap).build();

        detectAsync(mpImage, frameTime);
    }

    @VisibleForTesting
    public void detectAsync(MPImage mpImage, long frameTime) {
        if (faceLandmarker != null) {
            faceLandmarker.detectAsync(mpImage, frameTime);
        }
    }

    public VideoResultBundle detectVideoFile(Uri videoUri, long inferenceIntervalMs) throws IOException {
        if (runningMode != RunningMode.VIDEO) {
            throw new IllegalArgumentException(
                    "Attempting to call detectVideoFile while not using RunningMode.VIDEO"
            );
        }

        long startTime = SystemClock.uptimeMillis();

        boolean didErrorOccurred = false;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, videoUri);
        String videoLengthMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Long videoLength = (videoLengthMs != null) ? Long.parseLong(videoLengthMs) : null;

        Bitmap firstFrame = retriever.getFrameAtTime(0);
        Integer width = (firstFrame != null) ? firstFrame.getWidth() : null;
        Integer height = (firstFrame != null) ? firstFrame.getHeight() : null;

        if (videoLength == null || width == null || height == null) {
            return null;
        }

        int numberOfFramesToRead = (int) (videoLength / inferenceIntervalMs);

        List<FaceLandmarkerResult> resultList = new ArrayList<>();

        for (int i = 0; i <= numberOfFramesToRead; i++) {
            long timestampMs = i * inferenceIntervalMs;

            Bitmap frame = retriever.getFrameAtTime(
                    timestampMs * 1000,
                    MediaMetadataRetriever.OPTION_CLOSEST
            );

            if (frame != null) {
                Bitmap argb8888Frame = (frame.getConfig() == Bitmap.Config.ARGB_8888)
                        ? frame
                        : frame.copy(Bitmap.Config.ARGB_8888, false);

                MPImage mpImage = new BitmapImageBuilder(argb8888Frame).build();


                FaceLandmarkerResult detectionResult = faceLandmarker.detectForVideo(mpImage, timestampMs);
                if (detectionResult != null) {
                    resultList.add(detectionResult);
                } else {
                    didErrorOccurred = true;
                    if (faceLandmarkerHelperListener != null) {
                        faceLandmarkerHelperListener.onError("ResultBundle could not be returned in detectVideoFile", -1);
                    }
                }
            } else {
                didErrorOccurred = true;
                if (faceLandmarkerHelperListener != null) {
                    faceLandmarkerHelperListener.onError("Frame at specified time could not be retrieved when detecting in video.", -1);
                }
            }
        }

        retriever.release();

        long inferenceTimePerFrameMs =
                (SystemClock.uptimeMillis() - startTime) / numberOfFramesToRead;

        if (didErrorOccurred) {
            return null;
        } else {
            return new VideoResultBundle(resultList, inferenceTimePerFrameMs, height, width);
        }
    }

    public ResultBundle detectImage(Bitmap image) {
        if (runningMode != RunningMode.IMAGE) {
            throw new IllegalArgumentException(
                    "Attempting to call detectImage while not using RunningMode.IMAGE"
            );
        }

        long startTime = SystemClock.uptimeMillis();

        MPImage mpImage = new BitmapImageBuilder(image).build();

        FaceLandmarkerResult landmarkerResult = faceLandmarker.detect(mpImage);
        if (landmarkerResult != null) {
            long inferenceTimeMs = SystemClock.uptimeMillis() - startTime;
            return new ResultBundle(landmarkerResult, inferenceTimeMs, image.getHeight(), image.getWidth());
        } else {
            if (faceLandmarkerHelperListener != null) {
                faceLandmarkerHelperListener.onError("Face Landmarker failed to detect.", 1);
            }
            return null;
        }
    }

    private void returnLivestreamResult(FaceLandmarkerResult result, MPImage input) {
        if (result.faceLandmarks().size() > 0) {
            long finishTimeMs = SystemClock.uptimeMillis();
            long inferenceTime = finishTimeMs - result.timestampMs();

            if (faceLandmarkerHelperListener != null) {
                faceLandmarkerHelperListener.onResults(new ResultBundle(result, inferenceTime, input.getHeight(), input.getWidth()));
            }
        } else {
            if (faceLandmarkerHelperListener != null) {
                faceLandmarkerHelperListener.onEmpty();
            }
        }
    }

    private void returnLivestreamError(RuntimeException error) {
        if (faceLandmarkerHelperListener != null) {
            faceLandmarkerHelperListener.onError(error.getMessage() != null ? error.getMessage() : "An unknown error has occurred", OTHER_ERROR);
        }
    }

    public interface LandmarkerListener {
        void onError(String error, int errorCode);
        void onResults(ResultBundle resultBundle);
        void onEmpty();
    }

    public static class ResultBundle {
        public final FaceLandmarkerResult result;
        public final long inferenceTime;
        public final int inputImageHeight;
        public final int inputImageWidth;

        public ResultBundle(FaceLandmarkerResult result, long inferenceTime, int inputImageHeight, int inputImageWidth) {
            this.result = result;
            this.inferenceTime = inferenceTime;
            this.inputImageHeight = inputImageHeight;
            this.inputImageWidth = inputImageWidth;
        }
    }

    public static class VideoResultBundle {
        public final List<FaceLandmarkerResult> results;
        public final long inferenceTime;
        public final int inputImageHeight;
        public final int inputImageWidth;

        public VideoResultBundle(List<FaceLandmarkerResult> results, long inferenceTime, int inputImageHeight, int inputImageWidth) {
            this.results = results;
            this.inferenceTime = inferenceTime;
            this.inputImageHeight = inputImageHeight;
            this.inputImageWidth = inputImageWidth;
        }
    }
}