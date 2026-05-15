FROM docker.io/library/python:3.12-alpine

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Create data directory for SQLite
RUN mkdir -p data

EXPOSE 5000

# Initialize DB and start Gunicorn
CMD ["python", "app.py"]
