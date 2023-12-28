import os
import cv2
import numpy as np
from flask import Flask, request, jsonify, render_template, send_file, Response
from werkzeug.utils import secure_filename
from google.cloud import storage
from PIL import Image
#from tensorflow.python.keras.models import load_model
from tensorflow import keras
from keras.models import load_model
from core import CaptureStreamDetect
from threading import Thread
from time import sleep
import random
from datetime import datetime

IP_ADRESS = "192.168.20.247" # might change in different network or device
SEGMENT = "CAM_192_168_20_247"

class Frame_Worker_Thread(Thread):
    def __init__(self) -> None:
        Thread.__init__(self)
     
    def set_arguments(self, cds, frames, file_path):
        self.cds = cds
        self.frames = frames
        self.file_path = file_path

    def run(self):
        check_save_vid(self.cds, self.frames, self.file_path)


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
    if classes[0] > 0.8:  # Adjust threshold as per your model's performance
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
    blob.metadata = {
        'device_ip' : IP_ADRESS
    }

    blob.upload_from_filename(file_path)
    file_url = blob.public_url
    return file_url

# Fungsi untuk mengambil informasi tanggal dan waktu unggah file
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
    
CAPTURE = None

def get_cap_instance(cap):
    if cap == None:
        cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)
    return cap

@app.route('/detect_video_stream', methods=['GET'])
def detect_fall_on_video_stream():
    try:
        
        cap = get_cap_instance(CAPTURE)  # Open default camera

        fourcc = cv2.VideoWriter_fourcc(*'XVID')
        out = cv2.VideoWriter('output.avi', fourcc, 20.0, (640, 480))

        start_time = None
        elapsed_time = 0.0
        fall_detected = False
        frames = []
        fall_start_time = None

        while True:
            ret, frame = cap.read()

            if ret == False:
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

            return jsonify({
                'status': 'Fall Detected',
                'file_url': file_url,
                'file_name': file_name,
                #'upload_time': get_file_upload_time(blob)
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


def custom(cds):
    while True:
        ret, frame = cds.cap.read()
        if ret == False:
            break

        if cds.start_time is None:
            cds.start_time = cv2.getTickCount()

        frame_result = detect_fall_in_videostream(frame)
        if frame_result and not cds.fall_detected:
            cds.fall_start_time = cv2.getTickCount()
            cds.fall_detected = True

        if cds.fall_detected:
            cds.elapsed_time = (cv2.getTickCount() - cds.fall_start_time) / cv2.getTickFrequency()
            cds.add_frame(frame)
        
            if cds.elapsed_time >= 10.0 and cds.is_trying_to_save_video == False: 
                cds.is_trying_to_save_video = True
                cds.fall_detected = False

                old_frames, old_file_path = cds.return_and_reset_frames() # Capture 10 seconds  

                worker_thread = Frame_Worker_Thread()
                worker_thread.set_arguments(cds, old_frames, old_file_path)     
                worker_thread.start()
                #asyncio.run(check_save_vid(cds, old_frames, old_file_path))

        cds.out.write(frame)

        ret, buffer = cv2.imencode('.jpg', frame)
        frame = buffer.tobytes()
        yield (b'--frame\r\n'
                b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')

@app.route('/video_feed')
def video_feed():
    #asyncio.run(send_push_notification())
    client_device_local_name = request.args.get("local_name")
    capture_detect_stream = CaptureStreamDetect(client_device_local_name)
    return Response(custom(capture_detect_stream), mimetype='multipart/x-mixed-replace; boundary=frame')

def check_save_vid(cds, old_frames, old_file_path):
    try:
        if len(old_frames) > 0:
            file_name = f'fall_video_stream_{random.randint(0, 1000)}.avi'
            file_path = old_file_path
            save_video_frames(old_frames, file_path)
            file_url = save_video_to_cloud(file_name, file_path)
            print("fall detected")
            print(file_url)
            os.remove(file_path)
            date = datetime.today().strftime('%d/%m/%Y')
            send_push_notification(cds.client_device_local_name, file_url, IP_ADRESS, date)
            cds.is_trying_to_save_video = False
        else:
            os.remove(old_file_path)
            print("fall not detected")
            cds.is_trying_to_save_video = False
    except Exception as e:
        print(str(e))
    
        cds.is_trying_to_save_video = False


def gen_frames():  
    cap = get_cap_instance(CAPTURE)
    while True:
        success, frame = cap.read()  # read the camera frame
        if not success:
            break
        else:
            ret, buffer = cv2.imencode('.jpg', frame)
            frame = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')  # concat frame one by one and show result



@app.route('/process_video_stream', methods=['POST'])
def process_video_stream():
    data = request.get_json()
    label = data['label']
    bbox = data['bbox']

    # Process label and bbox data as needed
    # Perform fall detection on video stream frame
    # Example:
    frame = None  # Replace None with the frame data received from the front-end

    if frame is not None:
        fall_detected = detect_fall_in_videostream(frame)
        
        if fall_detected:
            # Your response if fall is detected
            return jsonify({'result': 'Fall Detected'}), 200
        else:
            # Your response if no fall is detected
            return jsonify({'result': 'No Fall Detected'}), 200
    else:
        return jsonify({'error': 'Frame data not received or processed'}), 400
     
# Route untuk menampilkan daftar file yang diunggah beserta informasi tanggal unggahnya
@app.route('/files', methods=['GET'])
def list_files():
    blobs = storage_client.list_blobs(bucket_name)
    files = []

    filter_date = request.args.get("filter_date")
    
    if filter_date == None:
        return jsonify({'files': files})


    for blob in blobs:
        time_created = blob.time_created.strftime("%d/%m/%Y")
        
        print(blob.name + " : " + time_created)
        if not isinstance(blob.metadata, dict):
            continue
    
        is_blob_uploaded_here = IP_ADRESS in blob.metadata.values()
        
        if is_blob_uploaded_here and time_created == filter_date:
            file_info = {
                'file_name': blob.name,
                'file_url': blob.public_url,
                'upload_time': get_file_upload_time(blob)
            }
            files.append(file_info)

    return jsonify({'files': files})

# Route untuk menampilkan informasi spesifik mengenai satu file
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
    

import onesignal
from onesignal.api import default_api
from onesignal.model.notification import Notification
from onesignal.model.button import Button
from onesignal.model.create_notification_success_response import CreateNotificationSuccessResponse
from pprint import pprint

configuration = onesignal.Configuration(
    user_key= "ZDhmYmMyMGUtNDRiYi00NWFiLWEyYzUtMjQ1MDljMDdhMmQ2",
    app_key="NGJkYTI3YjktY2I3ZC00ODFhLTg4ZjQtZmQ3NzNjYTU0YTFi"
)


def send_push_notification(client_device_local_name, playback_url, device_ip, date):
    with onesignal.ApiClient(configuration) as api_client:
        api_instance = default_api.DefaultApi(api_client)
        notification = Notification(
            app_id = "476ef1a2-2641-4451-a445-998599b2251d",
            included_segments = [ SEGMENT ],
            contents = {
                'en' : 'A fall accident detected in ' + client_device_local_name
            },
            headings = {
                'en' : 'There\'s a fall accident!!'
            },
            small_icon = "https://storage.googleapis.com/angelo-bucket-storage/logo.png",
            large_icon = "https://storage.googleapis.com/angelo-bucket-storage/alert.png",
            priority = 10,
            buttons = [
                Button(
                    id="btn_call",
                    text = "Call Emergency"
                )
            ],
            data = {
                'playback_url' : playback_url,
                'device_ip' : device_ip,
                'date' : date
            }
        )

        try:
            api_response = api_instance.create_notification(notification)
            pprint(api_response)
            return True
        except Exception as e:
            print(str(e))
            print("fail")
            return False
            


# Run the Flask app
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
