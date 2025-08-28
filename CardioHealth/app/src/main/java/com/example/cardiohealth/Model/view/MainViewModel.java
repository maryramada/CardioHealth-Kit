package com.example.cardiohealth.Model.view;
import androidx.lifecycle.ViewModel;
import com.example.cardiohealth.Helper.FaceLandmarkerHelper;

public class MainViewModel extends ViewModel {
    private int delegate = FaceLandmarkerHelper.DELEGATE_CPU;
    private float minFaceDetectionConfidence = FaceLandmarkerHelper.DEFAULT_FACE_DETECTION_CONFIDENCE;
    private float minFaceTrackingConfidence = FaceLandmarkerHelper.DEFAULT_FACE_TRACKING_CONFIDENCE;
    private float minFacePresenceConfidence = FaceLandmarkerHelper.DEFAULT_FACE_PRESENCE_CONFIDENCE;
    private int maxFaces = FaceLandmarkerHelper.DEFAULT_NUM_FACES;
    public int getCurrentDelegate() {
        return delegate;
    }
    public float getCurrentMinFaceDetectionConfidence() {
        return minFaceDetectionConfidence;
    }
    public float getCurrentMinFaceTrackingConfidence() {
        return minFaceTrackingConfidence;
    }
    public float getCurrentMinFacePresenceConfidence() {
        return minFacePresenceConfidence;
    }
    public int getCurrentMaxFaces() {
        return maxFaces;
    }
    public void setDelegate(int delegate) {
        this.delegate = delegate;
    }
    public void setMinFaceDetectionConfidence(float confidence) {
        this.minFaceDetectionConfidence = confidence;
    }
    public void setMinFaceTrackingConfidence(float confidence) {
        this.minFaceTrackingConfidence = confidence;
    }
    public void setMinFacePresenceConfidence(float confidence) {
        this.minFacePresenceConfidence = confidence;
    }
    public void setMaxFaces(int maxFaces) {
        this.maxFaces = maxFaces;
    }
}
