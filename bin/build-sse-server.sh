#!/usr/bin/env sh

sbt \
  'set name := "lyas-sse-server"' \
  'set mainClass.in(Compile) := Some("de.heikoseeberger.lyas.SseServerApp")' \
  docker:publishLocal
