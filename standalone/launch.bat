@echo off

java -cp "jar/*" -Xmx768m -XX:+HeapDumpOnOutOfMemoryError -Darkham.ged.basedir=. -Darkham.ged.node=standalone -Dlog4j.debug=true -Dged.properties=ged.xml com.arkham.ged.standalone.StandaloneScanner
