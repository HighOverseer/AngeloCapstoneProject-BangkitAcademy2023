FROM python:3.10-slim

ENV PYTHONBUFFERED True
ENV APP_HOME /app

WORKDIR $APP_HOME

# Install libsm6
RUN apt-get update && apt-get install -y libsm6
RUN apt-get update && apt-get install -y python3-opencv
RUN pip install opencv-python
RUN apt-get update && apt-get install libgl1

# Copy requirements file and install dependencies
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt

# Copy the application code
COPY . ./

# Set the command to start the application
CMD exec gunicorn --bind :$PORT --workers 1 --threads 8 --timeout 0 app:app
