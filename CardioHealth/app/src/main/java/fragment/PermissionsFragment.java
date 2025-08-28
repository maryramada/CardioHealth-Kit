package fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.cardiohealth.R;

public class PermissionsFragment extends Fragment {

    private static final String[] PERMISSIONS_REQUIRED = {Manifest.permission.CAMERA};

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            Toast.makeText(getContext(), "Permission request granted", Toast.LENGTH_LONG).show();
                            navigateToCamera();
                        } else {
                            Toast.makeText(getContext(), "Permission request denied", Toast.LENGTH_LONG).show();
                        }
                    }
            );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            navigateToCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }


    private void navigateToCamera() {
        if (getView() != null) {
            Navigation.findNavController(
                    requireActivity(),
                    R.id.fragment_container
            ).navigate(R.id.action_permissions_to_camera);
        }
    }

    public static boolean hasPermissions(Context context) {
        for (String permission : PERMISSIONS_REQUIRED) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}

