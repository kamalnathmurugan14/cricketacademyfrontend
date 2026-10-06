#!/usr/bin/env bash
# Starts the API once the database accepts connections.
set -e
cd "$(dirname "$0")/../."
for i in $(seq 1 60); do
  (echo > /dev/tcp/db/3306) 2>/dev/null && break
  sleep 2
done
exec mvn -q spring-boot:run
