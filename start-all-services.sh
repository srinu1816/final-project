#!/bin/bash
echo "=== Starting All Services ==="

# 1. Start Docker
echo "1. Starting Docker..."
sudo chmod 666 /var/run/docker.sock
sudo systemctl start docker
sleep 5

# 2. Start Application
echo "2. Starting Flight Service Application..."
cd ~/assignment2
docker-compose up -d
sleep 10

# 3. Start Elasticsearch
echo "3. Starting Elasticsearch..."
sudo systemctl start elasticsearch
sleep 15

# 4. Start Kibana
echo "4. Starting Kibana..."
sudo systemctl start kibana
sleep 10

# 5. Start Logstash
echo "5. Starting Logstash..."
sudo systemctl start logstash
sleep 10

# 6. Start Filebeat
echo "6. Starting Filebeat..."
sudo systemctl start filebeat
sleep 5

echo "=== All Services Started ==="
echo "Checking service status..."

# Check status of all services
sudo systemctl status docker elasticsearch kibana logstash filebeat --no-pager -l
docker-compose ps
