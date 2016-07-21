#!/usr/bin/env sh

docker run \
  --detach \
  --name lyas-sse-server \
  --publish 8000:8000 \
  hseeberger/lyas-sse-server
