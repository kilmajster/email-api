#!/usr/bin/env bash

git tag --delete latest || true \
  && echo -e "" \
  && git tag latest \
  && echo "git-bot > Latest tag moved to the head " \
  && eval "git --no-pager log --oneline -n 1" \
  && echo -e "" \
  && echo -e "git-bot > Pushing latest tag to the origin..." \
  && git push --delete origin latest || true \
  && git push origin latest
