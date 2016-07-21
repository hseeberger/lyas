#!/usr/bin/env sh

sbt \
  'set name := "lyas-server"' \
  'set mainClass.in(Compile) := Some("de.heikoseeberger.lyas.LyasServerApp")' \
  docker:publishLocal
