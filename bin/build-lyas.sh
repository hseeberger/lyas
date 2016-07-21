#!/usr/bin/env sh

sbt \
  'set mainClass.in(Compile) := Some("de.heikoseeberger.lyas.LyasApp")' \
  docker:publishLocal
