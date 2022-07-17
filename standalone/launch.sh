#!/bin/bash

java -cp "jar/*" -Xmx768m -XX:+HeapDumpOnOutOfMemoryError \
-Darkham.ged.basedir=. -Darkham.ged.node=standalone -Dged.properties=ged.xml \
com.arkham.ged.standalone.StandaloneScanner
