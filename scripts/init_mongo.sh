#!/usr/bin/env bash

# Password come from an env and that is why this has to be wrapped in bash
# There is no access to the system env from mongo script

echo "Creating users in mongo db"

set -e

mongo <<EOF
use emails
db.createUser({
  user: 'api',
  pwd: '$EMAILS_DB_PASSWORD',
  roles: [{
    role: 'readWrite',
    db: 'emails'
  }]
})
db.createUser({
  user: 'sender',
  pwd: '$EMAILS_DB_PASSWORD',
  roles: [{
    role: 'readWrite',
    db: 'emails'
  }]
})
EOF
