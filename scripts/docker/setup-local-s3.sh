#!/bin/env bash
set -ex
aws --endpoint-url http://localhost:4566 s3api create-bucket \
  --bucket elsevier-technical-exercise \
  --create-bucket-configuration LocationConstraint=eu-west-2
aws --endpoint-url http://localhost:4566 s3 cp /tmp/periodic_table.json s3://elsevier-technical-exercise/periodic_table.json