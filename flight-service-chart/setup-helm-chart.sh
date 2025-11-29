#!/bin/bash
echo "=== Deploying Flight Service with Helm ==="

# Clean up any existing installation
echo "1. Cleaning up existing installation..."
helm uninstall flight-service -n flight-service 2>/dev/null || true
kubectl delete namespace flight-service 2>/dev/null || true

# Create namespace
echo "2. Creating namespace..."
kubectl create namespace flight-service

# Install Helm chart
echo "3. Installing Helm chart..."
helm install flight-service ./flight-service-chart -n flight-service

# Wait for deployment
echo "4. Waiting for deployment to complete..."
sleep 30

# Check status
echo "5. Checking deployment status..."
helm list -n flight-service
kubectl get all -n flight-service

# Get access information
echo "6. Getting access information..."
NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}')

echo -e "\n✅ DEPLOYMENT COMPLETE!"
echo "🎯 Access your Flight Service at:"
echo "   http://$NODE_IP:30080/api/v1/health"
echo "   http://$NODE_IP:30080/api/v1/flights"
echo "   http://$NODE_IP:30080/api/v1/flights/stats"

echo -e "\n🔧 Management Commands:"
echo "   Upgrade:    helm upgrade flight-service ./flight-service-chart -n flight-service"
echo "   Uninstall:  helm uninstall flight-service -n flight-service"
echo "   Status:     helm status flight-service -n flight-service"
