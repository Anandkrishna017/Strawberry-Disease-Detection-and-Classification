package com.example.st_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.CastOp;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InstanceSegmentation {
    private static final String MODEL_PATH = "best_float32.tflite";
    private static final int TENSOR_WIDTH = 448;
    private static final int TENSOR_HEIGHT = 448;

    private static final float TENSOR_WIDTH_FLOAT = (float) TENSOR_WIDTH;
    private static final float TENSOR_HEIGHT_FLOAT = (float) TENSOR_HEIGHT;
    private static final float INPUT_MEAN = 0f;
    private static final float INPUT_STANDARD_DEVIATION = 255f;
    private static final DataType INPUT_IMAGE_TYPE = DataType.FLOAT32;
    private static final DataType OUTPUT_IMAGE_TYPE = DataType.FLOAT32;
    private static final int NUM_ELEMENTS = 4116;
    private static final int NUM_CHANNELS = 43;

    private static final int LABEL_SIZE = 7;
    private static final int BATCH_SIZE = 1;
    private static final int X_POINTS = 112;
    private static final int Y_POINTS = 112;
    private static final int MASKS_NUMBERS = 32;
    private static final float CONFIDENCE_THRESHOLD = 0.50f;
    //    private static final float IOU_THRESHOLD = 0.60f;
    private static final float IOU_THRESHOLD = 0.20f;
    ArrayList<String> labelsList = new ArrayList<>();
    private static final String[] CLASS_LABELS = {
            "Angular Leafspot",
            "Anthracnose Fruit Rot",
            "Blossom Blight",
            "Gray Mold",
            "Leaf Spot",
            "Powdery Mildew Fruit",
            "Powdery Mildew Leaf"
    };
    private final ImageProcessor imageProcessor = new ImageProcessor.Builder()
            .add(new NormalizeOp(INPUT_MEAN, INPUT_STANDARD_DEVIATION))
            .add(new CastOp(INPUT_IMAGE_TYPE))
            .build();
    public Output invoke(Context context, Bitmap bitmap) throws IOException {
        labelsList.clear();

        Interpreter interpreter = new Interpreter(FileUtil.loadMappedFile(context, MODEL_PATH));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, TENSOR_WIDTH, TENSOR_HEIGHT, false);

        TensorImage tensor = new TensorImage(DataType.FLOAT32);
        tensor.load(resizedBitmap);
        TensorImage processedImage = imageProcessor.process(tensor);
        Object[] imageBuffer = {processedImage.getBuffer()};
        //FloatBuffer imageBuffer = processedImage.getBuffer().asFloatBuffer();

        TensorBuffer coordinatesBuffer = TensorBuffer.createFixedSize(
                new int[]{BATCH_SIZE,NUM_CHANNELS, NUM_ELEMENTS},
                OUTPUT_IMAGE_TYPE
        );

        TensorBuffer maskProtoBuffer = TensorBuffer.createFixedSize(
                new int[]{BATCH_SIZE,MASKS_NUMBERS, Y_POINTS, X_POINTS},
                OUTPUT_IMAGE_TYPE
        );

        Map<Integer, Object> outputBuffer = new HashMap<>();
        outputBuffer.put(0, coordinatesBuffer.getBuffer().rewind());
        outputBuffer.put(1, maskProtoBuffer.getBuffer().rewind());

        interpreter.runForMultipleInputsOutputs(imageBuffer, outputBuffer);


        float[] coordinates = coordinatesBuffer.getFloatArray();
        float[] masks = maskProtoBuffer.getFloatArray();

        List<Output0> filterOutput0 = bestBox(coordinates);
        boolean objectsDetected = filterOutput0 != null && !filterOutput0.isEmpty();

        if (!objectsDetected) {
            return new Output(bitmap, false, null, null, 0, 0);
        }

        Bitmap resultBitmap = Bitmap.createBitmap(419, 419, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);

        // Draw the original bitmap on the new bitmap
        canvas.drawBitmap(bitmap, 0, 0, null);

        Paint paint = new Paint();
        // Use the canvas for drawing bounding boxes and labels on the new bitmap
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20); // Adjust text size as needed

        //for segmentation Masks
        List<Bitmap> output1 = reshapeOutput1(masks);

        Log.d("Size", "Size: " + output1.size());

        List<String> labels = new ArrayList<>();
        List<Float> confidenceScores = new ArrayList<>();
        int healthyCount = 0;
        int unhealthyCount = 0;

        for (Output0 box : filterOutput0) {

            // Draw filled rectangle
            float x1 = box.cx - (box.w / 2F);
            float y1 = box.cy - (box.h / 2F);
            float x2 = box.cx + (box.w / 2F);
            float y2 = box.cy + (box.h / 2F);

            Log.d("Coordinates", "Coordinates: " + "x1: " + x1 + " , " + "x2: " + x2 + " , " + "y1: " + y1 + " , " + "y2: " + y2);

            float left = (x1 * resultBitmap.getWidth());
            float top = (y1 * resultBitmap.getHeight());
            float right = (x2 * resultBitmap.getWidth());
            float bottom = (y2 * resultBitmap.getHeight());

            // Get the corresponding class mask for the current bounding box
            int classId = box.classId;
            String classLabel = getLabelForClassId(classId);


            // Ensure that the class ID is within a valid range
            if (classId >= 0 && classId < MASKS_NUMBERS && classId < output1.size()) {
                Bitmap classMask = output1.get(classId);

                // Calculate the size of the bounding box in the original image
                float bboxWidth = box.w * resultBitmap.getWidth();
                float bboxHeight = box.h * resultBitmap.getHeight();

                Log.d("Bounding Box Width", "Bounding Box Width: " + bboxWidth);
                Log.d("Bounding Box Height", "Bounding Box Height: " + bboxHeight);

                classMask = Bitmap.createScaledBitmap(classMask, (int) bboxWidth, (int) bboxHeight, true);

                // Padding values (adjust as needed)
                int paddingX = 0;
                int paddingY = 0;

                // Ensure the bounding box coordinates are within valid range
            /*float x1Clamped = Math.max(0, Math.min(1, x1));
            float y1Clamped = Math.max(0, Math.min(1, y1));
            float x2Clamped = Math.max(0, Math.min(1, x2));
            float y2Clamped = Math.max(0, Math.min(1, y2));*/

                // Calculate pixel coordinates with padding
                int roiLeft = (int) ((x1 * classMask.getWidth()) - paddingX);
                int roiTop = (int) ((y1 * classMask.getHeight()) - paddingY);
                int roiRight = (int) ((x2 * classMask.getWidth()) + paddingX);
                int roiBottom = (int) ((y2 * classMask.getHeight()) + paddingY);

                // Clamp pixel coordinates to ensure they are within the mask dimensions
                roiLeft = Math.max(0, Math.min(classMask.getWidth(), roiLeft));
                roiTop = Math.max(0, Math.min(classMask.getHeight(), roiTop));
                roiRight = Math.max(0, Math.min(classMask.getWidth(), roiRight));
                roiBottom = Math.max(0, Math.min(classMask.getHeight(), roiBottom));

                Log.d("ROI Coordinates", "Coordinates: " + "roiLeft: " + roiLeft + " , " + "roiTop: " + roiTop + " , " + "roiRight: " + roiRight + " , " + "roiBottom: " + roiBottom);

                Rect srcRect = new Rect(roiLeft, roiTop, roiRight, roiBottom);
                RectF destRect = new RectF(left, top, right, bottom);

                // Update threshold values based on the classId
                int thresholdValue = getThresholdValue(classId); // Update thresholds as needed

                // Inside your main invoke method, after getting the classMask
                classMask = applyThreshold(classMask, thresholdValue, classId);

                // Set the paint color for the outline
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3); // Adjust outline width as needed
                paint.setColor(Color.RED); // Adjust outline color as needed
                paint.setFilterBitmap(true);
                paint.setDither(true);

                // Draw the bounding box with outline
                canvas.drawRect(destRect, paint);

                // Draw the bounding box with outline
                //canvas.drawRect(left, top, right, bottom, paint);

                // Draw the masked region directly onto the resultBitmap
                canvas.drawBitmap(classMask, srcRect, destRect, paint);
            }

            // Display label and confidence score together
            String confidenceText = classLabel + " (" + String.format("%.2f", box.cnf) + ")";

            // Calculate the position for displaying label and confidence score
            float textX = left;
            float textY = top - 10;

            //labels.add(label);
            confidenceScores.add(box.cnf);

            // Draw label and confidence score together
            canvas.drawText(confidenceText, textX, textY, textPaint);

        }
        interpreter.close();
        return new Output (resultBitmap, true, labels, confidenceScores, healthyCount, unhealthyCount);
    }

    private int getThresholdValue(int classId) {
        // Define threshold values for different classes
        switch (classId) {
            case 0: // Class 0
//                return 250; // Adjust threshold value as needed
                return 50;
            case 1: // Class 1
//                return 250; // Adjust threshold value as needed
                return 50;
            case 2: // Class 2
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
//                return 180; // Adjust threshold value as needed
            default:
                return 128; // Default threshold value
        }
    }

    private Bitmap applyThreshold(Bitmap inputBitmap, int threshold, int classId) {
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        // Define colors for each class
        int[] classColors = {
                Color.rgb(255, 128, 128),  // Class 0 (Light Red)
                Color.rgb(128, 255, 128),  // Class 1 (Light Green)
                Color.rgb(128, 128, 255),  // Class 2 (Light Blue)
                Color.rgb(255, 255, 128),  // Class 3 (Light Yellow)
                Color.rgb(255, 128, 255),  // Class 4 (Light Magenta)
                Color.rgb(128, 255, 255),  // Class 5 (Light Cyan)
                Color.rgb(255, 192, 128),  // Class 6 (Orange)
                Color.rgb(192, 255, 128),  // Class 7 (Lime)
                Color.rgb(128, 255, 192),  // Class 8 (Aquamarine)
                Color.rgb(128, 192, 255),  // Class 9 (Sky Blue)
                Color.rgb(255, 128, 192),  // Class 10 (Rose)
                Color.rgb(192, 128, 255),  // Class 11 (Lavender)
                Color.rgb(255, 224, 128),  // Class 12 (Apricot)
                Color.rgb(255, 128, 224),  // Class 13 (Fuchsia)
                Color.rgb(128, 255, 224),  // Class 14 (Light Teal)
                Color.rgb(224, 128, 255),  // Class 15 (Violet)
                Color.rgb(128, 224, 255),  // Class 16 (Light Sky Blue)
                Color.rgb(224, 255, 128),  // Class 17 (Light Lime)
                Color.rgb(192, 128, 128),  // Class 18 (Maroon)
                Color.rgb(128, 192, 128),  // Class 19 (Olive)
                Color.rgb(128, 128, 192)   // Class 20 (Navy)
        };

        for (int x = 0; x < inputBitmap.getWidth(); x++) {
            for (int y = 0; y < inputBitmap.getHeight(); y++) {
                int pixel = inputBitmap.getPixel(x, y);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Apply threshold to convert to binary
                int gray = (red + green + blue) / 3;
                int thresholdedValue = (gray > threshold) ? 255 : 0;

                int opacity = (thresholdedValue > 0) ? 160 : 0; // Use original alpha as opacity

                // Get the color for the current class
                int classColor;
                if (classId >= 0 && classId < classColors.length) {
                    classColor = classColors[classId];
                    //Log.d("Class Color", "Class Color" + classColor);
                } else {
                    classColor = Color.TRANSPARENT; // Default to transparent for unknown class
                    //Log.d("classColor", "classColor" + classColor);
                }

                // Apply opacity to the class color
                int thresholdedPixel = Color.argb(opacity, Color.red(classColor), Color.green(classColor), Color.blue(classColor));

                // Set the pixel color in the outputBitmap
                outputBitmap.setPixel(x, y, thresholdedPixel);
            }
        }

        return outputBitmap;
    }
    private List<Bitmap> reshapeOutput1(float[] masks) {
        List<Bitmap> all = new ArrayList<>();

        for (int maskIndex = 0; maskIndex < MASKS_NUMBERS; maskIndex++) {
            Bitmap bitmap = Bitmap.createBitmap(X_POINTS, Y_POINTS, Bitmap.Config.ARGB_8888);

            // Find the maximum pixel value for normalization
            float maxPixelValue = Float.MIN_VALUE;
            for (int i = maskIndex; i < masks.length; i += MASKS_NUMBERS) {
                maxPixelValue = Math.max(maxPixelValue, masks[i]);
            }

            // Apply the mask to the bitmap
            for (int x = 0; x < X_POINTS; x++) {
                for (int y = 0; y < Y_POINTS; y++) {
                    int pixelIndex = maskIndex + MASKS_NUMBERS * (x + X_POINTS * y);
                    float pixelValue = masks[pixelIndex];

                    // Normalize pixel value to cover the entire color range
                    int colorValue = (int) (pixelValue / maxPixelValue * 255);

                    // Set the pixel color
                    int color = Color.argb(255, colorValue, colorValue, colorValue);
                    bitmap.setPixel(x, y, color);
                }
            }

            all.add(bitmap);
        }

        return all;
    }


    /*private List<Bitmap> reshapeOutput1(float[] masks, List<Output0> boundingBoxes) {
        List<Bitmap> all = new ArrayList<>();

        for (int maskIndex = 0; maskIndex < MASKS_NUMBERS; maskIndex++) {
            Output0 box = boundingBoxes.get(maskIndex);
            float[] mask = new float[X_POINTS * Y_POINTS];

            // Copy mask data for the current mask
            System.arraycopy(masks, maskIndex * X_POINTS * Y_POINTS, mask, 0, X_POINTS * Y_POINTS);

            // Normalize the mask
            float maxPixelValue = Float.MIN_VALUE;
            float minPixelValue = Float.MAX_VALUE;
            for (float pixelValue : mask) {
                maxPixelValue = Math.max(maxPixelValue, pixelValue);
                minPixelValue = Math.min(minPixelValue, pixelValue);
            }
            float range = maxPixelValue - minPixelValue;
            for (int i = 0; i < mask.length; i++) {
                mask[i] = (mask[i] - minPixelValue) / range;
            }

            // Ensure the width and height are integers
            int maskWidth = (int) box.w;
            int maskHeight = (int) box.h;

            Bitmap bitmap = Bitmap.createBitmap(maskWidth, maskHeight, Bitmap.Config.ARGB_8888);


            // Scale and interpolate the mask to match the bounding box size
            Matrix matrix = new Matrix();
            matrix.postScale(box.w / (float) X_POINTS, box.h / (float) Y_POINTS);
            // Convert float mask values to integers (0-255 range)
            int[] intPixels = new int[mask.length];
            for (int i = 0; i < mask.length; i++) {
                intPixels[i] = (int) (mask[i] * 255);
            }
    // Create a bitmap for the mask
    Bitmap scaledMask = Bitmap.createBitmap(intPixels, X_POINTS, Y_POINTS, Bitmap.Config.ARGB_8888);

            Bitmap scaledAndInterpolatedMask = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(scaledAndInterpolatedMask);
            canvas.drawBitmap(scaledMask, matrix, null);

            all.add(scaledAndInterpolatedMask);
        }

        return all;
    }*/
    private List<Output0> applyNMS(List<Output0> boxes) {
        List<Output0> sortedBoxes = new ArrayList<>(boxes);
        Collections.sort(sortedBoxes, (box1, box2) -> Float.compare(box2.w * box2.h, box1.w * box1.h));

        List<Output0> selectedBoxes = new ArrayList<>();

        while (!sortedBoxes.isEmpty()) {
            Output0 first = sortedBoxes.get(0);
            selectedBoxes.add(first);
            sortedBoxes.remove(0);

            Iterator<Output0> iterator = sortedBoxes.iterator();
            while (iterator.hasNext()) {
                Output0 nextBox = iterator.next();
                float iou = calculateIoU(first, nextBox);
                if (iou >= IOU_THRESHOLD) {
                    iterator.remove();
                }
            }
        }

        return selectedBoxes;
    }
    private float calculateIoU(Output0 box1, Output0 box2) {
        float x1 = Math.max(box1.x1, box2.x1);
        float y1 = Math.max(box1.y1, box2.y1);
        float x2 = Math.min(box1.x2, box2.x2);
        float y2 = Math.min(box1.y2, box2.y2);

        float intersectionArea = Math.max(0F, x2 - x1) * Math.max(0F, y2 - y1);
        float box1Area = box1.w * box1.h;
        float box2Area = box2.w * box2.h;

        return intersectionArea / (box1Area + box2Area - intersectionArea);
    }

//private float calculateIoU(Output0 b1, Output0 b2) {
//    float x1 = Math.max(b1.cx - (b1.w / 2F), b2.cx - (b2.w / 2F));
//    float y1 = Math.max(b1.cy - (b1.h / 2F), b2.cy - (b2.h / 2F));
//    float x2 = Math.min(b1.cx + (b1.w / 2F), b2.cx + (b2.w / 2F));
//    float y2 = Math.min(b1.cy + (b1.h / 2F), b2.cy + (b2.h / 2F));
//
//
//    float intersectionArea = Math.max(0F, x2 - x1) * Math.max(0F, y2 - y1);
//    float box1Area = b1.w * b1.h;
//    float box2Area = b2.w * b2.h;
//
//    return intersectionArea / (box1Area + box2Area - intersectionArea);
//}

    private String getLabelForClassId(int classId) {
        if (classId >= 0 && classId < CLASS_LABELS.length) {
            Log.d("Class ID", "Class ID: " + classId);
            labelsList.add(CLASS_LABELS[classId]);
            return CLASS_LABELS[classId];
        } else {
            return "Unknown";
        }
    }

    public class Output {
        private final Bitmap bitmap;
        private final boolean objectsDetected;
        private final List<String> labels;
        private final List<Float> confidenceScores;
        private final int healthyCount;
        private final int unhealthyCount;

        public Output(Bitmap bitmap, boolean objectsDetected, List<String> labels,
                      List<Float> confidenceScores, int healthyCount, int unhealthyCount) {
            this.bitmap = bitmap;
            this.objectsDetected = objectsDetected;
            this.labels = labels;
            this.confidenceScores = confidenceScores;
            this.healthyCount = healthyCount;
            this.unhealthyCount = unhealthyCount;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public boolean isObjectsDetected() {
            return objectsDetected;
        }

        public List<String> getLabels() {
            return labelsList;
        }

        public List<Float> getConfidenceScores() {
            return confidenceScores;
        }

        public int getHealthyCount() {
            return healthyCount;
        }

        public int getUnhealthyCount() {
            return unhealthyCount;
        }
    }

    private static class Output0 {
        float x1;
        float y1;
        float x2;
        float y2;
        float cx;
        float cy;
        float w;
        float h;
        float cnf;
        List<Float> maskWeight;

        int classId;

        Output0(float x1, float y1, float x2, float y2, float cx, float cy, float w, float h, float cnf, int classId, List<Float> maskWeight) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.cx = cx;
            this.cy = cy;
            this.w = w;
            this.h = h;
            this.cnf = cnf;
            this.classId = classId;
            this.maskWeight = maskWeight;
        }
    }

    private List<Output0> bestBox(float[] array) {
        List<Output0> boundingBoxes = new ArrayList<>();
        for (int c = 0; c < NUM_ELEMENTS; c++) {
            List<Float> confidences = new ArrayList<>();
            int start = 4 + LABEL_SIZE;
            for (int i = 4; i < start; i++) {
                confidences.add(array[c + NUM_ELEMENTS * i]);
            }
            float maxConfidence = -Float.MAX_VALUE;
            int classId = -1;
            for (int i = 0; i < confidences.size(); i++) {
                if (confidences.get(i) > maxConfidence) {
                    maxConfidence = confidences.get(i);
                    classId = i;
                }
            }

            if (maxConfidence > CONFIDENCE_THRESHOLD) {
                float cx = array[c];
                float cy = array[c + NUM_ELEMENTS];
                float w = array[c + NUM_ELEMENTS * 2];
                float h = array[c + NUM_ELEMENTS * 3];
                float x1 = cx - (w / 2F);
                float y1 = cy - (h / 2F);
                float x2 = cx + (w / 2F);
                float y2 = cy + (h / 2F);
                if (x1 <= 0F || x1 >= TENSOR_WIDTH_FLOAT) continue;
                if (y1 <= 0F || y1 >= TENSOR_HEIGHT_FLOAT) continue;
                if (x2 <= 0F || x2 >= TENSOR_WIDTH_FLOAT) continue;
                if (y2 <= 0F || y2 >= TENSOR_HEIGHT_FLOAT) continue;

                List<Float> maskWeight = new ArrayList<>();
                for (int index = 0; index < MASKS_NUMBERS; index++) {
                    maskWeight.add(array[c + NUM_ELEMENTS * (index + 5)]);
                }

                boundingBoxes.add(
                        new Output0(
                                x1, y1, x2, y2, cx, cy, w, h, maxConfidence, classId, maskWeight
                        )
                );
            }
        }

        if (boundingBoxes.isEmpty()) return null;

        return applyNMS(boundingBoxes);
    }


    private float sigmoid(float x) {
        return (float) (1 / (1 + Math.exp(-x)));
    }
}