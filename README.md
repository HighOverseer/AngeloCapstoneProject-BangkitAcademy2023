# Angelo: Emergency Response Application

This repository documents the process and components of the Angelo Emergency Response Application for falling people detection based on body posture recognition.

<p align="center">
  <img src="Angelo.png" alt="Angelo logo" height="180" />
</p>

<h1 align="center">Angelo</h1>

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



# Angelo Fall Detection API

This repository contains the Flask API for Angelo Emergency Response Application, enabling fall detection based on body posture recognition.

## Usage

1. Clone the repository:
    ```bash
    git clone https://github.com/HighOverseer/AngeloCapstoneProject-BangkitAcademy2023
    ```

2. Install virtual environment:
    ```bash
    python -m venv venv
    ```

3. Install necessary requirements:
    ```bash
    pip install -r requirements.txt
    ```

4. Run the Flask API:
    ```bash
    flask run
    ```
    or
    ```bash
    python app.py
    ```
Angelo Flask API Repository : [Flask API] (https://github.com/lintangtrisnadi/Angelo-Flask-API)

## API Endpoints

The API endpoints can be accessed at [https://angelo-fall-detection-y7rr5xpfna-uc.a.run.app/](https://angelo-fall-detection-y7rr5xpfna-uc.a.run.app/).

## API Routes

The Flask API includes the following routes:

- **POST /predict**: 
    - Endpoint for detecting falls in uploaded images.
- **POST /predict_video**: 
    - Endpoint for detecting falls in uploaded videos.
- **GET /detect_video_stream**: 
    - Endpoint for streaming video from the camera and detecting falls in real-time.
- **GET /files**: 
    - Endpoint to display a list of uploaded files along with their upload dates information.
- **GET /files/<filename>**: 
    - Endpoint to display specific information about a single file.

## Code Overview

The API integrates fall detection algorithms using a pre-trained model for detecting falls in images and videos. It includes functions for performing fall detection on frames, image streams, and video streams.

### Team Collaboration

All teams collaborated to integrate their components into the Angelo application, creating a holistic solution for fall detection and emergency response.

|            Member           | Student ID |        Path        |                    University                    |                                                       Contacts                                                      |
| :-------------------------: | :--------: | :----------------: | :----------------------------------------: | :-----------------------------------------------------------------------------------------------------------------: |
|        Samuel Theodore, Lie        | M200BSY1389 |  Machine Learning  | Universitas Diponegoro |           [LinkedIn](https://www.linkedin.com/in/samueltheodore/)          |
|      Giselle Ameris Wibowo, Bok     | M180BSX1090 |  Machine Learning  |          Universitas Airlangga         |   [LinkedIn](https://www.linkedin.com/in/giselle-ameris-wibowo-bok-7b8b17217/)   |
|     Niken Kurniasari     | M180BSX1172 | Machine Learning |          Universitas Airlangga          |             [LinkedIn](https://www.linkedin.com/in/niken-kurniasari-18034b247/)             |
|      Yuma Zahran Ewaldo     | C180BSY4169 | Cloud Computing |          Universitas Airlangga          |    [LinkedIn](https://www.linkedin.com/in/yuma-zahran-ewaldo/)     |
|     Lintang Trisnadi     | C006BSY3469 |   Cloud Computing  |               Universitas Brawijaya              |            [LinkedIn](https://www.linkedin.com/in/lintang-trisnadi/)             |
| Fajar Alif Riyandi | A184BSY2030 |   Mobile Development  |         Universitas Andalas         | [LinkedIn](https://www.linkedin.com/in/fajar-alif-riyandi-b257512a1/)  |

---
