package com.example.st_demo;
//
//import androidx.fragment.app.Fragment;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.io.IOException;
//
//public class SecondFragment extends Fragment {
//    private static final int REQUEST_CODE = 100;
//    private ImageView imageView;
//    private Bitmap selectedImage;
//    private InstanceSegmentation instanceSegmentation;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_second, container, false);
//
//        imageView = view.findViewById(R.id.imageView);
//        Button uploadButton = view.findViewById(R.id.uploadButton);
//        Button predictButton = view.findViewById(R.id.predictButton);
//
//        instanceSegmentation = new InstanceSegmentation();
//
//        uploadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGallery();
//            }
//        });
//
//        predictButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (selectedImage != null) {
//                    try {
//                        InstanceSegmentation.Output output = instanceSegmentation.invoke(getActivity(), selectedImage);
//                        showOutput(output);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Toast.makeText(getActivity(), "Error processing image", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(getActivity(), "Please upload an image first", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        return view;
//    }
//
//    private void openGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, REQUEST_CODE);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
//            try {
//                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
//                selectedImage = BitmapFactory.decodeStream(inputStream);
//                imageView.setImageBitmap(selectedImage);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void showOutput(InstanceSegmentation.Output output) {
//        // Display the output in the ImageView or any other desired UI element
//        // For example, you can display the masks and bounding boxes
//        // You can access masks using output.getMasks() and bounding boxes using output.getBoundingBoxes()
//        // Here, I'll simply display the original image with bounding boxes overlaid
//        imageView.setImageBitmap(output.getBitmap());
//        Toast.makeText(getActivity(), "Disease Detected", Toast.LENGTH_SHORT).show();
//    }
//}





import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class SecondFragment extends Fragment {
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private ImageView imageView;
    private Bitmap selectedImage;
    private InstanceSegmentation instanceSegmentation;
    TextView textViewLabels;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);


        textViewLabels = view.findViewById(R.id.textView);
        imageView = view.findViewById(R.id.imageView);
        Button uploadButton = view.findViewById(R.id.uploadButton);
        Button predictButton = view.findViewById(R.id.predictButton);
        Button captureButton = view.findViewById(R.id.captureButton);

        instanceSegmentation = new InstanceSegmentation();

        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textViewLabels.setText("");
                openGallery();
            }
        });

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewLabels.setText("");
                processSelectedImage();
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewLabels.setText("");
                dispatchTakePictureIntent();
            }
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                selectedImage = BitmapFactory.decodeStream(inputStream);
                Bitmap resizedImage = resizeImage(selectedImage, 419, 419);
                imageView.setImageBitmap(resizedImage);
//                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            selectedImage = (Bitmap) extras.get("data");
            Bitmap resizedImage = resizeImage(selectedImage, 419, 419);
            imageView.setImageBitmap(resizedImage);
//            imageView.setImageBitmap(selectedImage);
        }
    }

    private void processSelectedImage() {
        if (selectedImage != null) {
            Bitmap resizedImage = resizeImage(selectedImage, 419, 419);
            try {
                InstanceSegmentation.Output output = instanceSegmentation.invoke(getActivity(), resizedImage);
                showOutput(output);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error processing image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Please upload an image first", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeImage(Bitmap image, int targetWidth, int targetHeight) {
        return Bitmap.createScaledBitmap(image, targetWidth, targetHeight, true);
    }

    private void showOutput(InstanceSegmentation.Output output) {
        imageView.setImageBitmap(output.getBitmap());


        List<String> labels = output.getLabels();

        if (labels.isEmpty()) {
//            TextView textViewLabels = view.findViewById(R.id.textView);
            textViewLabels.setText("Healthy");
        } else {

            // Remove duplicates from the labels list
            Set<String> uniqueLabels = new LinkedHashSet<>(labels);

            // Convert the set of unique labels back to a list
            List<String> uniqueLabelsList = new ArrayList<>(uniqueLabels);

            // Display the unique labels in the TextView
//            TextView textViewLabels = view.findViewById(R.id.textView);
            StringBuilder labelsStringBuilder = new StringBuilder();
            for (String label : uniqueLabelsList) {
                labelsStringBuilder.append(label).append("\n"); // Append each label followed by a newline
            }
            textViewLabels.setText(labelsStringBuilder.toString());
        }




//        Toast.makeText(getActivity(), "Diagnosis", Toast.LENGTH_SHORT).show();
    }
}

