package fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cardiohealth.databinding.FaceBlendshapesResultBinding;
import com.google.mediapipe.tasks.components.containers.Category;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult;

import java.util.ArrayList;
import java.util.List;

public class FaceBlendshapesResultAdapter extends RecyclerView.Adapter<FaceBlendshapesResultAdapter.ViewHolder> {

    private static final String NO_VALUE = "--";
    private List<Category> categories;

    public FaceBlendshapesResultAdapter() {
        this.categories = new ArrayList<>(52);
    }

    public void updateResults(FaceLandmarkerResult faceLandmarkerResult) {
        categories.clear();
        if (faceLandmarkerResult != null && faceLandmarkerResult.faceBlendshapes().isPresent()) {

            List<Category> faceBlendshapes = faceLandmarkerResult.faceBlendshapes().get().get(0);

            List<Category> modifiableFaceBlendshapes = new ArrayList<>(faceBlendshapes);

            modifiableFaceBlendshapes.sort((category1, category2) -> Float.compare(category2.score(), category1.score()));

            int min = Math.min(modifiableFaceBlendshapes.size(), categories.size());

            for (int i = 0; i < min; i++) {
                categories.add(modifiableFaceBlendshapes.get(i));
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FaceBlendshapesResultBinding binding = FaceBlendshapesResultBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = categories.get(position);
        if (category != null) {
            holder.bind(category.categoryName(), category.score());
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final FaceBlendshapesResultBinding binding;

        public ViewHolder(FaceBlendshapesResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String label, Float score) {

            if (label != null) {
                binding.tvLabel.setText(label);
            } else {
                binding.tvLabel.setText(NO_VALUE);
            }

            if (score != null) {
                binding.tvScore.setText(String.format("%.2f", score));
            } else {
                binding.tvScore.setText(NO_VALUE);
            }
        }
    }
}
