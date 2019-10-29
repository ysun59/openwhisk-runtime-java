FROM python:2.7-slim
COPY code/ /app
COPY data/ /tmp/hpybucket/
WORKDIR /app
# RUN pip install -r requirements.txt
RUN /bin/bash -c "apt-get update && apt-get install time -y"
EXPOSE 5000
#ENTRYPOINT ["python","./index.py"]
ENTRYPOINT ["/usr/bin/time","-f", "MMM:%M", "python","./index.py"]
#ENTRYPOINT ["time","-f", ""MMM:%M"", "ls"]
#CMD python ./index.py
