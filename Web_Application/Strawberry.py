# import streamlit as st
# from PIL import Image
# from io import BytesIO
# from ultralytics import YOLO
# import matplotlib.pyplot as plt

# def load_image(image_file):
#     img = Image.open(image_file)
#     return img

# def main():
#     st.title("YOLO Object Detection App")
#     st.write("Upload an image and click 'Detect Objects' to perform object detection.")

#     uploaded_image = st.file_uploader("Choose an image...", type=["jpg", "png", "jpeg"])
#     if uploaded_image is not None:
#         image = load_image(uploaded_image)

#         # Convert PIL image to BytesIO
#         img_byte_arr = BytesIO()
#         image.save(img_byte_arr, format='JPEG')
#         image_bytes = img_byte_arr.getvalue()

#         st.image(image, caption="Uploaded Image", use_column_width=True)

#     if st.button("Detect Objects"):
#         # if uploaded_i?mage is not None:
#             st.write("Performing object detection...")
#             # image_bytes="/home/anand/Desktop/yolov8 (copy)/A.jpg"
#             # Initialize YOLO model
#             my_new_model = YOLO("/home/anand/Desktop/yolov8 (copy)/best.pt")  # You may need to pass specific arguments, such as model weights or configuration

#             # Perform object detection
#             results = my_new_model.predict(image_bytes, conf=0.5)

#             # Assuming results is a list of detection results
#             # Access the annotated image from the list
#             annotated_image = results[0].plot()

#             # Convert the annotated image back to PIL format for display
#             annotated_image_pil = Image.fromarray(annotated_image)

#             # Display output image
#             st.image(annotated_image_pil, caption="Objects Detected", use_column_width=True)

# if __name__ == "__main__":
#     main()


#<-------------correct code--------------->

# import streamlit as st
# from PIL import Image
# from io import BytesIO
# from ultralytics import YOLO
# import matplotlib.pyplot as plt

# def main():
#     st.title("StrawberryCare App")
#     st.write("Upload an image and click 'Disease Detection' to perform disease detection.")

#     uploaded_image = st.file_uploader("Choose an image...", type=["jpg", "png", "jpeg"])
#     if uploaded_image is not None:
#         # Read uploaded image data as bytes
#         image_bytes = uploaded_image.read()

#         # Create a PIL image object from the byte data
#         image = Image.open(BytesIO(image_bytes))
#         st.image(image, caption="", use_column_width=True)

#     if st.button("Disease Detection"):
#         if uploaded_image is not None:
#             st.write("Performing disease detection...")

#             # Initialize YOLO model
#             my_new_model = YOLO("/home/anand/Desktop/UI/best.pt")  # You may need to pass specific arguments, such as model weights or configuration

#             # Perform object detection
#             results = my_new_model.predict(image, conf=0.3)

#             # Assuming results is a list of detection results
#             # Access the annotated image from the list
#             annotated_image = results[0].plot()

#             # Convert the annotated image array back to PIL format for display
#             annotated_image_pil = Image.fromarray(annotated_image)

#             # Display output image
#             st.image(annotated_image_pil, caption="", use_column_width=True)

# if __name__ == "__main__":
#     main()


#<----------------------------------------------------------------------------------------------------->


# import streamlit as st
# from PIL import Image
# from io import BytesIO
# from ultralytics import YOLO
# import matplotlib.pyplot as plt

# def main():
#     st.title("StrawberryCare App")
#     st.write("Upload an image and click 'Disease Detection' to perform disease detection.")

#     uploaded_image = st.file_uploader("Choose an image...", type=["jpg", "png", "jpeg"])
#     if uploaded_image is not None:
#         # Read uploaded image data as bytes
#         image_bytes = uploaded_image.read()

#         # Create a PIL image object from the byte data
#         image = Image.open(BytesIO(image_bytes))
#         st.image(image, caption="", use_column_width=True)

#     if st.button("Disease Detection"):
#         if uploaded_image is not None:
#             st.write("Performing disease detection...")

#             # Initialize YOLO model
#             my_new_model = YOLO("/home/anand/Desktop/UI/best.pt")  # You may need to pass specific arguments, such as model weights or configuration

#             # Perform object detection
#             results = my_new_model.predict(image, conf=0.3)

#             # Assuming results is a list of detection results
#             # Access the annotated image from the list
#             annotated_image = results[0].plot()

#             # Convert the annotated image array back to PIL format for display
#             annotated_image_pil = Image.fromarray(annotated_image)

#             # Display output image
#             st.image(annotated_image_pil, caption="", use_column_width=True)
            
#             # Add the provided code here
#             new_result = results[0]

#             # Extract masks
#             extracted_masks = new_result.masks.data
#             masks_array = extracted_masks.cpu().numpy()


#             detected_boxes = new_result.boxes.data

#             # Initialize class_labels 
#             class_labels = detected_boxes[:, -1].int().tolist()
#             # Initialize a dictionary to hold masks by class
#             masks_by_class = {name: [] for name in new_result.names.values()}

#             # Iterate through the masks and class labels
#             for mask, class_id in zip(masks_array, class_labels):
#                 class_name = new_result.names[class_id]  # Map class ID to class name
#                 masks_by_class[class_name].append(mask)

#             # Create a set to store unique labels
#             encountered_labels = set(class_labels)

#             # Define a dictionary mapping class names to remedies
#             class_remedies = {
#                 0: 'Remedy for Angular Leafspot',
#                 1: 'Remedy for Anthracnose Fruit Rot',
#                 2: 'Remedy for Blossom Blight',
#                 3: 'Remedy for Gray Mold',
#                 4: 'Remedy for Leaf Spot',
#                 5: 'Remedy for Powdery Mildew Fruit',
#                 6: 'Remedy for Powdery Mildew Leaf'
#             }

#             # Print the class remedies for the values in encountered_labels
#             for label in encountered_labels:
#                 if label in class_remedies:
#                     st.write(f"{label}: {class_remedies[label]}")

# if __name__ == "__main__":
#     main()


import streamlit as st
from PIL import Image
from io import BytesIO
from ultralytics import YOLO
import matplotlib.pyplot as plt

def main():
    st.title("StrawberryCare")
    st.write("Upload an image and click 'Disease Detection' to perform disease detection.")

    uploaded_image = st.file_uploader("Choose an image...", type=["jpg", "png", "jpeg"])
    if uploaded_image is not None:
        # Read uploaded image data as bytes
        image_bytes = uploaded_image.read()

        # Create a PIL image object from the byte data
        image = Image.open(BytesIO(image_bytes))
        st.image(image, caption="", use_column_width=True)

    if st.button("Disease Detection"):
        if uploaded_image is not None:
            st.write("Performing disease detection...")

            # Initialize YOLO model
            my_new_model = YOLO("/home/anand/Desktop/UI/best.pt")  # You may need to pass specific arguments, such as model weights or configuration

            # Perform object detection
            results = my_new_model.predict(image, conf=0.55)

            # Assuming results is a list of detection results
            # Access the annotated image from the list
            annotated_image = results[0].plot()

            # Convert the annotated image array back to PIL format for display
            annotated_image_pil = Image.fromarray(annotated_image)

            # Display output image
            st.image(annotated_image_pil, caption="", use_column_width=True)
            
            # Add the provided code here
            new_result = results[0]

            # Extract masks
            detected_boxes = new_result.boxes.data
            extracted_masks = new_result.masks.data
            masks_array = extracted_masks.cpu().numpy()
            

            # Initialize class_labels 
            class_labels = detected_boxes[:, -1].int().tolist()
            
            # Initialize a dictionary to hold masks by class
            masks_by_class = {name: [] for name in new_result.names.values()}

            # Iterate through the masks and class labels
            for mask, class_id in zip(masks_array, class_labels):
                class_name = new_result.names[class_id]  # Map class ID to class name
                masks_by_class[class_name].append(mask)

            # Create a set to store unique labels
            encountered_labels = set(class_labels)

            disease_head = {
                0: 'Angular Leafspot',
                1: 'Anthracnose Fruit Rot',
                2: 'Blossom Blight',
                3: 'Gray Mold',
                4: 'Leaf Spot',
                5: 'Powdery Mildew Fruit',
                6: 'Powdery Mildew Leaf'
            }

            # Define a dictionary mapping class names to remedies
            class_remedies = {
                0: 'Prune affected leaves, maintain good airflow, avoid overhead watering, apply fungicides, rotate crops, keep the area clean, monitor regularly, and use disease-resistant varieties to manage the issue effectively.',
                1: 'Prune affected fruit and foliage, apply fungicides as recommended, maintain good air circulation, avoid overhead watering, remove and destroy infected plant debris, practice crop rotation, use disease-resistant strawberry varieties, and monitor regularly for signs of infection to effectively manage the issue.',
                2: 'Remove and destroy infected blossoms, apply fungicides labeled for blossom blight, improve air circulation around plants, avoid overhead watering, remove and destroy infected plant debris, maintain proper spacing between plants, monitor regularly for signs of infection, and use disease-resistant strawberry varieties to effectively manage the issue.',
                3: 'Prune affected plant parts promptly, improve air circulation around plants, avoid overhead watering, apply fungicides as directed, remove and destroy infected plant material, keep the area clean and free of debris, use drip irrigation instead of sprinklers, and rotate crops to reduce disease pressure for effective management.',
                4: 'Remove and destroy infected leaves, apply fungicides as recommended, avoid overhead watering, ensure good air circulation around plants, mulch around plants to prevent soil splashing, maintain proper spacing between plants, monitor regularly for signs of infection, and use disease-resistant strawberry varieties to effectively manage the issue.',
                5: 'Prune affected fruit clusters, apply fungicides labeled for powdery mildew, improve air circulation around plants, avoid overhead watering, remove and destroy infected plant material, maintain proper spacing between plants, monitor regularly for signs of infection, and use disease-resistant strawberry varieties to effectively manage the issue.',
                6: 'Prune affected leaves, apply fungicides labeled for powdery mildew, improve air circulation around plants, avoid overhead watering, remove and destroy infected plant material, maintain proper spacing between plants, monitor regularly for signs of infection, and use disease-resistant strawberry varieties to effectively manage the issue.'
            }

            # Print headings and remedies
            # st.subheader("Remedies")
            # for label in encountered_labels:
            #     disease_name = disease_head[label]
            #     remedy = class_remedies[label]
            #     st.write(f"- <b>{disease_name}</b>: {remedy}", unsafe_allow_html=True)
            if not encountered_labels:
                st.write("Healthy")
            else:
                st.subheader("Remedies")
                for label in encountered_labels:
                    disease_name = disease_head[label]
                    remedy = class_remedies[label]
                    st.write(f"- <b>{disease_name}</b>: {remedy}", unsafe_allow_html=True)



if __name__ == "__main__":
    main()

