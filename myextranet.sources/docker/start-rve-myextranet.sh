#!/bin/sh

if [ -z "${MYPLACE_VM_JAVA_OPTS}" ]; then
  MYPLACE_VM_JAVA_OPTS=
fi

/usr/lib/jvm/java-11/bin/java -Dloader.path=/opt/myextranet/lib/ $MYPLACE_VM_JAVA_OPTS -jar /opt/myextranet/lib/myextranet.jar --spring.config.location=file:/opt/myextranet/config/