#!/bin/sh

set -e

mkdir -p /opt/myextranet/config/
mkdir -p /opt/myextranet/lib/


mv /opt/application.yml /opt/myextranet/config/
mv /opt/application-svi.yml /opt/myextranet/config/
mv /opt/mybox.properties /opt/myextranet/config/
mv /opt/application-colleng.yml /opt/myextranet/config/

mv /opt/myextranet.jar /opt/myextranet/lib/
