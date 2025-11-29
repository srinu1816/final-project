#!/bin/bash

set -e  # Exit on any error

echo "Applying namespace..."
kubectl apply -f namespace.yml

echo "Applying ConfigMap..."
kubectl apply -f configmap.yml

echo "Applying Secret..."
kubectl apply -f secret.yml

echo "Applying MySQL PersistentVolume..."
kubectl apply -f mysql-pv.yml

echo "Applying MySQL Deployment..."
kubectl apply -f mysql-deployment.yml

echo "Waiting for MySQL pod to be ready..."
kubectl wait --for=condition=ready pod -l app=mysql -n flight-service --timeout=300s

echo "Applying Flight App Deployment..."
kubectl apply -f flight-app-deployment.yml

echo "Deployment complete."

