#!/bin/bash

set -ex

./mvnw checkstyle:check

docker compose -f docker-compose.deps.yml -f docker-compose.test.yml up --build \
 --exit-code-from app \
 --abort-on-container-exit