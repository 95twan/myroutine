#!/bin/bash

set -a
source ./docs/env_templates/.env.shop
set +a

echo "🚀 Starting Shop Service with Local Config..."

java -jar shop-service/build/libs/shop-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local
read
