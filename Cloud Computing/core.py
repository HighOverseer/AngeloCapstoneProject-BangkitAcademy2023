import cv2
import random

class CaptureStreamDetect:
    
    def __generate_file_name(self):
        rand = random.randint(0, 1000)
        filename = f'output_{str(rand)}.avi'
        return filename

    def __init__(self, client_device_local_name):
        self.client_device_local_name = client_device_local_name
        self.cap = cv2.VideoCapture(0, cv2.CAP_DSHOW);
        self.start_time = None
        self.elapsed_time = 0.0
        self.fall_detected = False
        self.frames = []
        self.fall_start_time = None
        self.fourcc = cv2.VideoWriter_fourcc(*'XVID')
        
        self.is_trying_to_save_video = False
    
        filename = self.__generate_file_name()
        self.current_file_name = filename
        self.out = cv2.VideoWriter(filename, self.fourcc, 20.0, (640, 480))
        

    def return_and_reset_frames(self):
        self.out.release()
        frames = self.frames.copy()
        old_file_name = self.current_file_name
    
        self.frames.clear()
        print(f"frames size: {len(frames)}")
        filename = self.__generate_file_name()
        self.current_file_name = filename
        self.out = cv2.VideoWriter(filename, self.fourcc, 20.0, (640, 480))
        return frames, old_file_name

    def add_frame(self, frame):
        if(self.is_trying_to_save_video == False):
            self.frames.append(frame)
    
from threading import Thread
from app import check_save_vid




