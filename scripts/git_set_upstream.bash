#!/usr/bin/env bash

origin_url="git@github.com:jvmops/email-api.git"

git remote set-url origin "$origin_url" \
  && echo "git-bot > origin_url set to: $origin_url"