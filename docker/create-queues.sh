#!/bin/bash

echo "Waiting for LocalStack to be ready..."
sleep 5

echo "Creating SQS queue: locadora-notifications"
docker exec localstack awslocal sqs create-queue --queue-name locadora-notifications --region us-east-1

echo "Verifying queue creation..."
docker exec localstack awslocal sqs list-queues --region us-east-1

echo "Queue creation complete!"
