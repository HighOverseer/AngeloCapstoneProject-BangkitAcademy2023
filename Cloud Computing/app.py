import os
import cv2
import numpy as np
from flask import Flask, request, jsonify, render_template, send_file
from werkzeug.utils import secure_filename
from google.cloud import storage
from PIL import Image
from tensorflow.keras.models import load_model

app = Flask(__name__)
app.config['ALLOWED_EXTENSIONS'] = set(['png', 'jpg', 'jpeg', 'mkv', 'mp4', 'm4v', 'mov', 'avi', 'asf', 'webm'])
app.config['MODEL_FILE'] = 'models/fall_detection_fix_model.h5'
app.config['SECRET_KEY'] = 'secret'
app.config['GOOGLE_APPLICATION_CREDENTIALS'] = 'gcloud-credentials.json'

# Load the fall detection model
fall_detect = load_model(app.config['MODEL_FILE'], compile=False)

# Google Cloud Storage Configuration
storage_client = storage.Client.from_service_account_json(app.config['GOOGLE_APPLICATION_CREDENTIALS'])
bucket_name = 'angelo-bucket-storage'
bucket = storage_client.get_bucket(bucket_name)

# Helper function to check allowed file extensions
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in app.config['ALLOWED_EXTENSIONS']

# Function to perform fall detection on each frame of the video
def detect_fall_in_frame(frame):

    frame = cv2.resize(frame, (150, 150))
    frame = np.expand_dims(frame, axis=0)
    frame = frame.astype(np.float32) / 255.0

    # Perform fall detection on the frame using the loaded model
    classes = fall_detect.predict(frame)
    
    # Return True if fall is detected, False otherwise
    if classes[0] < 0.85:  # Adjust threshold as per your model's performance
        print("Jatuh")
        return True
    else:
        print("Tidak Jatuh")
        return False
    
def detect_fall_in_videostream(frame):

    frame = cv2.resize(frame, (150, 150))
    frame = np.expand_dims(frame, axis=0)
    frame = frame.astype(np.float32) / 255.0

    # Perform fall detection on the frame using the loaded model
    classes = fall_detect.predict(frame)
    
    # Return True if fall is detected, False otherwise
    if classes[0] > 0.4:  # Adjust threshold as per your model's performance
        print("Jatuh")
        return True
    else:
        print("Tidak Jatuh")
        return False
    
# Function to perform fall detection on image and videos
def perform_fall_detection(file, file_stream):
    try:
        file_name = secure_filename(file.filename)

        # Upload file directly to Google Cloud Storage
        blob = bucket.blob(file_name)
        blob.upload_from_file(file_stream, content_type=file.content_type)
        file_url = blob.public_url

        if file.filename.lower().endswith(('.jpg', '.jpeg', '.png')):
            img = Image.open(file_stream).convert("RGB")
            img = img.resize((150, 150))
            img_array = np.asarray(img)
            img_array = np.expand_dims(img_array, axis=0)
            normalized_image_array = img_array.astype(np.float32) / 255.0  # Adjust normalization as per your model
            data = np.ndarray(shape=(1, 150, 150, 3), dtype=np.float32)
            data[0] = normalized_image_array

            classes = fall_detect.predict(data)
            if classes[0] < 0.85:  # Adjust threshold as per your model's performance
                print("Jatuh")
                return True, file_url
            else:
                print("Tidak Jatuh")
                return False, file_url
            
        elif file.filename.lower().endswith(('.mp4', '.mkv', '.avi', '.mov')):
            fall_detected = perform_fall_detection_video(file_stream)

            if fall_detected:
                return True, file_url  # If fall detected in the video, return True

            return False, file_url  # If no fall detected in the video, return False
    except Exception as e:
        print(f"Error during fall detection: {str(e)}")
        return False, None

# Function to save image to Google Cloud Storage
def save_image_to_cloud(file_name, file_path):
    blob = bucket.blob(file_name)
    blob.upload_from_filename(file_path)
    file_url = blob.public_url
    return file_url

# Function to save video to Google Cloud Storage
def save_video_to_cloud(file_name, file_path):
    blob = bucket.blob(file_name)
    blob.upload_from_filename(file_path)
    file_url = blob.public_url
    return file_url

# Function to retrieve upload date and time information of a file
def get_file_upload_time(blob):
    timestamp = blob.time_created

    if timestamp is not None:
        return timestamp.strftime('%Y-%m-%d %H:%M:%S')
    else:
        return 'Unknown'

def save_video_frames(frames, file_path):
    out = cv2.VideoWriter(file_path, cv2.VideoWriter_fourcc(*'XVID'), 20.0, (640, 480))
    for frame in frames:
        out.write(frame)
    out.release()

@app.route('/')
def index():
    return render_template('index.html')

# Route to handle the uploaded image for fall detection
@app.route('/predict', methods=['POST'])
def predict_fall():
    if 'file' not in request.files:
        return jsonify({'error': 'No file uploaded'})

    file = request.files['file']

    if file and allowed_file(file.filename):
        fall_detected, file_url = perform_fall_detection(file, file.stream)

        if fall_detected:
            filename = secure_filename(file.filename)  # Process the filename
            return jsonify({
                'status': 'Prediction successful',
                'result': 'Fall Detected',
                'message': 'Emergency assistance required!',
                'file_url': file_url,
                'file_name': filename
            }), 200
        else:
            filename = secure_filename(file.filename)  # Process the filename
            return jsonify({
                'status': 'Prediction successful',
                'result': 'No Fall Detected',
                'file_url': file_url,
                'file_name': filename
            }), 200
    else:
        return jsonify({
            'error': 'Invalid file format'
        }), 400
    

# Route to handle the uploaded image for fall detection
@app.route('/predict_video', methods=['POST'])
def predict_fall_video():
    if 'file' not in request.files:
        return jsonify({'error': 'No file uploaded'})

    file = request.files['file']

    if file and allowed_file(file.filename):
        file_name = secure_filename(file.filename)
        file_path = f'static/uploads/{file_name}'  # Relative path to save the video
        
        # Create the directory if it doesn't exist
        os.makedirs(os.path.dirname(file_path), exist_ok=True)

        file.save(file_path)  # Save the uploaded file
        
        cap = cv2.VideoCapture(file_path)  # Open the temporarily saved video
        fall_detected = False

        # Perform fall detection on each frame of the video
        while True:
            ret, frame = cap.read()
            if not ret:
                break

            frame_result = detect_fall_in_frame(frame)
            if frame_result:
                fall_detected = True
                break  # If fall detected, stop processing frames
            
        cap.release()  # Release video capture
        
        if fall_detected:
            file_url = save_video_to_cloud(file_name, file_path)  # Save video to Google Cloud Storage
            uploaded_blob = bucket.blob(file_name)  # Get the blob reference of the uploaded video
            os.remove(file_path)  # Remove temporary file

            return jsonify({
                'status': 'Fall Detected',
                'file_url': file_url,
                'file_name': file_name,
                'upload_time': get_file_upload_time(uploaded_blob)  # Use the uploaded_blob reference here
            }), 200
        else:
            os.remove(file_path)  # Remove temporary file if no fall detected
            return jsonify({
                'status': 'No Fall Detected'
            }), 200
    else:
        return jsonify({
            'error': 'Invalid file format'
        }), 400


# Route to stream video from camera, detect fall, and save to Google Cloud Storage
@app.route('/detect_video_stream', methods=['GET'])
def detect_fall_on_video_stream():
    try:
        cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)  # Open default camera

        fourcc = cv2.VideoWriter_fourcc(*'XVID')
        out = cv2.VideoWriter('output.avi', fourcc, 20.0, (640, 480))

        start_time = None
        elapsed_time = 0.0
        fall_detected = False
        frames = []
        fall_start_time = None

        while True:
            ret, frame = cap.read()
            if not ret:
                break

            if start_time is None:
                start_time = cv2.getTickCount()

            frame_result = detect_fall_in_videostream(frame)
            if frame_result and not fall_detected:
                fall_start_time = cv2.getTickCount()
                fall_detected = True

            if fall_detected:
                elapsed_time = (cv2.getTickCount() - fall_start_time) / cv2.getTickFrequency()
                frames.append(frame)

                if elapsed_time >= 10.0:  # Capture 10 seconds
                    break

            out.write(frame)

        out.release()
        cap.release()

        if fall_detected and len(frames) > 0:
            file_name = 'fall_video_stream.avi'
            file_path = 'output.avi'
            save_video_frames(frames, file_path)
            file_url = save_video_to_cloud(file_name, file_path)
            os.remove(file_path)

            # Get the blob reference of the uploaded video
            uploaded_blob = bucket.blob(file_name)

            # Return the uploaded time of the blob
            return jsonify({
                'status': 'Fall Detected',
                'file_url': file_url,
                'file_name': file_name,
                'upload_time': get_file_upload_time(uploaded_blob)  # Use the uploaded_blob reference here
            }), 200
        else:
            os.remove('output.avi')
            return jsonify({
                'status': 'No Fall Detected'
            }), 200
    except Exception as e:
        print(f"Error during video stream fall detection: {str(e)}")
        return jsonify({
            'error': 'Error during video stream fall detection'
        }), 500

   
# Route to display a list of uploaded files along with their upload dates information
@app.route('/files', methods=['GET'])
def list_files():
    blobs = storage_client.list_blobs(bucket_name)
    files = []

    for blob in blobs:
        file_info = {
            'file_name': blob.name,
            'file_url': blob.public_url,
            'upload_time': get_file_upload_time(blob)
        }
        files.append(file_info)

    return jsonify({'files': files})


# Route to display specific information about a single file
@app.route('/files/<filename>', methods=['GET'])
def get_file_info(filename):
    blob = bucket.blob(filename)

    if blob.exists():
        file_info = {
            'file_name': blob.name,
            'file_url': blob.public_url,
            'upload_time': get_file_upload_time(blob)
        }
        return jsonify(file_info)
    else:
        return jsonify({'error': 'File not found'}), 404
    
# Run the Flask app
if __name__ == '__main__':
    app.run(debug=True)