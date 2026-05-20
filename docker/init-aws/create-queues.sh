#!/bin/bash
echo "Creating SQS queue: locadora-notifications"
awslocal sqs create-queue --queue-name locadora-notifications --region us-east-1

echo "Verifying queue creation..."
awslocal sqs list-queues --region us-east-1

echo "Queue creation complete!"
