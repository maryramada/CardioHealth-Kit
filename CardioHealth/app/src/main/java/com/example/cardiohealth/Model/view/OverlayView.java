package com.example.cardiohealth.Model.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.cardiohealth.R;
import com.google.mediapipe.tasks.components.containers.Connection;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult;

import java.util.List;

import fragment.CamaraFragment;

public class OverlayView extends View {

    private FaceLandmarkerResult results;

    private CamaraFragment camaraFragment;
    private final Paint paint;
    private Paint linePaint;
    private Paint pointPaint;
    private float scaleFactor = 1f;
    private int imageWidth = 1;
    private int imageHeight = 1;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
        paint = new Paint();
        paint.setColor(android.graphics.Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5f);
    }

    public void clear() {
        results = null;
        linePaint.reset();
        pointPaint.reset();
        invalidate();
        initPaints();
    }

    private void initPaints() {
        linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.mp_color_primary));
        linePaint.setStrokeWidth(LANDMARK_STROKE_WIDTH);
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint = new Paint();
        pointPaint.setColor(Color.YELLOW);
        pointPaint.setStrokeWidth(LANDMARK_STROKE_WIDTH);
        pointPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (results == null || results.faceLandmarks().isEmpty()) {
            clear();
            return;
        }

        for (List<NormalizedLandmark> landmarkList : results.faceLandmarks()) {
            for (NormalizedLandmark normalizedLandmark : landmarkList) {
                float x = normalizedLandmark.x() * imageWidth * scaleFactor;
                float y = normalizedLandmark.y() * imageHeight * scaleFactor;
                canvas.drawPoint(x, y, pointPaint);
            }

            for (Connection connection : FaceLandmarker.FACE_LANDMARKS_CONNECTORS) {
                NormalizedLandmark start = landmarkList.get(connection.start());
                NormalizedLandmark end = landmarkList.get(connection.end());

                float startX = start.x() * imageWidth * scaleFactor;
                float startY = start.y() * imageHeight * scaleFactor;
                float endX = end.x() * imageWidth * scaleFactor;
                float endY = end.y() * imageHeight * scaleFactor;

                canvas.drawLine(startX, startY, endX, endY, linePaint);
            }

            int[] medialIndices = {0, 19, 17, 152, 10, 168, 164};

            for (int i = 0; i < medialIndices.length - 1; i++) {
                int index1 = medialIndices[i];
                int index2 = medialIndices[i + 1];

                if (index1 < landmarkList.size() && index2 < landmarkList.size()) {
                    NormalizedLandmark point1 = landmarkList.get(index1);
                    NormalizedLandmark point2 = landmarkList.get(index2);

                    float x1 = point1.x() * imageWidth * scaleFactor;
                    float y1 = point1.y() * imageHeight * scaleFactor;
                    float x2 = point2.x() * imageWidth * scaleFactor;
                    float y2 = point2.y() * imageHeight * scaleFactor;

                    canvas.drawLine(x1, y1, x2, y2, linePaint);
                }
            }
        }
    }

    public void setResults(FaceLandmarkerResult faceLandmarkerResults, int imageHeight, int imageWidth, RunningMode runningMode) {
        this.results = faceLandmarkerResults;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;

        switch (runningMode) {
            case IMAGE:
            case VIDEO:
                scaleFactor = Math.min((float) getWidth() / imageWidth, (float) getHeight() / imageHeight);
                break;
            case LIVE_STREAM:
                scaleFactor = Math.max((float) getWidth() / imageWidth, (float) getHeight() / imageHeight);
                break;
        }
        invalidate();
    }

    private static final float LANDMARK_STROKE_WIDTH = 8F;
    private static final String TAG = "Face Landmarker Overlay";
}