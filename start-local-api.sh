#!/bin/bash

docker-compose up -d
aws dynamodb create-table \
    --attribute-definitions \
      AttributeName=niceName,AttributeType=S \
    --table-name \
      surl_links \
    --key-schema \
      AttributeName=niceName,KeyType=HASH \
    --provisioned-throughput \
      ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --endpoint-url \
      http://localhost:8000
sam local start-api --docker-network surl-sam-backend --region us-east-1