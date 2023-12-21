# Angelo: Emergency Response Application

This repository documents the process and components of the Angelo Emergency Response Application for falling people detection based on body posture recognition.

## Documentation Structure

### 1. Model Documentation

#### Objective
Building a model for fall detection based on body posture recognition.

#### Steps Taken

1. **Data Collection:**
   - Sourced relevant datasets for model training.
   - Compiled a dataset with 485 images for training and validation.

2. **Model Training:**
   - Utilized Keras and TensorFlow for training the fall detection model.
   - Employed the YOLOv4 deep learning architecture.
   - Achieved a model accuracy of 99% with a split of 55% fall and 45% non-fall instances.

3. **Model Creation:**
   - Saved the trained model as `fall_detection_model.h5`.
   - Integrated the model with Flask to create an API.

4. **Model Utilization:**
   - Adjusted thresholds for fall detection based on model performance.
   - Fine-tuned normalization techniques for image preprocessing.

### 2. API Documentation

#### Objective
Creating an API to connect the model with the Angelo application.

#### Steps Taken

1. **Cloud Storage Configuration:**
   - Set up a Google Cloud Storage bucket (`angelo-bucket-storage`) to store fall detection videos.

2. **API Development:**
   - Developed an API using Flask to connect the machine learning model with the application.

3. **Cloud Storage Integration:**
   - Integrated Cloud Storage for storing videos generated from fall detection.

4. **API Deployment:**
   - Deployed the API to Google Cloud Run for scalability and serverless architecture.

### 3. Mobile App Documentation

#### Objective
Developing the Angelo mobile application for Android devices.

#### Steps Taken

1. **App Development:**
   - Utilized Kotlin with the Android Native Framework for coding functionalities.
   - Integrated data retrieval from the team-made API for fall detection.
   - Implemented functionality to detect falls in a live video stream.

2. **UI Design:**
   - Designed the primary layout of the application's user interface.
   - Focused on emergency call functionalities and push notifications for fall detection alerts.

3. **Testing and Optimization:**
   - Conducted thorough testing to ensure seamless functionality.
   - Optimized the application's performance for user responsiveness.

### Team Collaboration

All teams collaborated to integrate their components into the Angelo application, creating a holistic solution for fall detection and emergency response.

---
