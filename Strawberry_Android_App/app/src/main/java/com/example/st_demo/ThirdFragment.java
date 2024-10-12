package com.example.st_demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ThirdFragment extends Fragment {

    // Array of image resources and descriptions
    private int[] imageResources = {R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5, R.drawable.p6, R.drawable.p7};
    private String[] descriptions = {"-Prune affected leaves\n" +
                    "-Maintain good airflow\n" +
                    "-Avoid overhead watering\n" +
                    "-Apply fungicides\n" +
                    "-Rotate crops\n" +
                    "-Keep the area clean\n" +
                    "-Monitor regularly\n" +
                    "-Use disease-resistant varieties",
            "- Prune affected fruit and foliage\n" +
                    "- Apply fungicides as recommended\n" +
                    "- Maintain good air circulation\n" +
                    "- Avoid overhead watering\n" +
                    "- Remove and destroy infected plant debris\n" +
                    "- Practice crop rotation\n" +
                    "- Use disease-resistant strawberry varieties\n" +
                    "- Monitor regularly for signs of infection",
            "- Prune affected plant parts promptly\n" +
                    "- Improve air circulation around plants\n" +
                    "- Avoid overhead watering\n" +
                    "- Apply fungicides as directed\n" +
                    "- Remove and destroy infected plant material\n" +
                    "- Keep the area clean and free of debris\n" +
                    "- Use drip irrigation instead of sprinklers\n" +
                    "- Rotate crops to reduce disease pressure",
            "- Remove and destroy infected leaves\n" +
                    "- Apply fungicides as recommended\n" +
                    "- Avoid overhead watering\n" +
                    "- Ensure good air circulation around plants\n" +
                    "- Mulch around plants to prevent soil splashing\n" +
                    "- Maintain proper spacing between plants\n" +
                    "- Monitor regularly for signs of infection\n" +
                    "- Use disease-resistant strawberry varieties",
            "- Prune affected fruit clusters\n" +
                    "- Apply fungicides labeled for powdery mildew\n" +
                    "- Improve air circulation around plants\n" +
                    "- Avoid overhead watering\n" +
                    "- Remove and destroy infected plant material\n" +
                    "- Maintain proper spacing between plants\n" +
                    "- Monitor regularly for signs of infection\n" +
                    "- Use disease-resistant strawberry varieties",
            "- Prune affected leaves\n" +
                    "- Apply fungicides labeled for powdery mildew\n" +
                    "- Improve air circulation around plants\n" +
                    "- Avoid overhead watering\n" +
                    "- Remove and destroy infected plant material\n" +
                    "- Maintain proper spacing between plants\n" +
                    "- Monitor regularly for signs of infection\n" +
                    "- Use disease-resistant strawberry varieties",
            "- Remove and destroy infected blossoms\n" +
                    "- Apply fungicides labeled for blossom blight\n" +
                    "- Improve air circulation around plants\n" +
                    "- Avoid overhead watering\n" +
                    "- Remove and destroy infected plant debris\n" +
                    "- Maintain proper spacing between plants\n" +
                    "- Monitor regularly for signs of infection\n" +
                    "- Use disease-resistant strawberry varieties"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        LinearLayout containerLayout = view.findViewById(R.id.container);

        for (int i = 0; i < imageResources.length; i++) {
            View itemView = inflater.inflate(R.layout.image_description_item, containerLayout, false);

            ImageView imageView = itemView.findViewById(R.id.imageView);
            TextView descriptionTextView = itemView.findViewById(R.id.descriptionTextView);

            imageView.setImageResource(imageResources[i]);
            descriptionTextView.setText(descriptions[i]);
            descriptionTextView.setGravity(Gravity.CENTER);


            // Set margins for descriptionTextView
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            int margin = 16; // Set the desired margin in pixels
            layoutParams.setMargins(0, margin, 0, margin);
            descriptionTextView.setLayoutParams(layoutParams);

            // Increase text size
            descriptionTextView.setTextSize(16);


            // Add dividing line
            View dividerView = new View(getContext());
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    4 // Set the desired thickness of the divider
            );
            dividerParams.setMargins(0, margin, 0, margin); // Set margin for the divider
            dividerView.setLayoutParams(dividerParams);
            dividerView.setBackgroundColor(Color.GRAY); // Set the color of the divider
            containerLayout.addView(dividerView);




            // Add margin top to imageView
            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            int imageMarginTop = 30; // Set the desired margin top for the image in pixels
            imageLayoutParams.setMargins(0, imageMarginTop, 0, 0);
            imageView.setLayoutParams(imageLayoutParams);

            containerLayout.addView(itemView);
        }

        return view;
    }
}
