#!/usr/bin/env sh

docker run \
  --detach \
  --name lyas-server \
  --publish 8000:8000 \
  hseeberger/lyas-server
