#!/bin/sh

set -e


cd `dirname $0`

ROOT_PATH=`pwd`


echo "\n################################################################################"
echo "building image ..."
echo "################################################################################\n"

. ../version.sh


docker build --build-arg http_proxy="${http_proxy}" --build-arg https_proxy="${https_proxy}" --build-arg MYPLACE_VERSION="${MYPLACE_VERSION}" .
