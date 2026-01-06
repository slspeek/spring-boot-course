#!/usr/bin/env bash
set -e

# create-tag.sh: https://gist.github.com/slspeek/400f682d448e20782d768130f6229f82
NEW_VERSION=$(create-tag.sh -d)
mvn versions:set -DnewVersion="$NEW_VERSION"
git commit -am "Bump version to $NEW_VERSION"
create-tag.sh
