#!/bin/bash

 javac -sourcepath src -d build/classes -cp /minecraft/server/src/*.java -cp /minecraft/server/bin/minecraft_server.jar src/mapper.java

jar cvf build/jar/mapper.jar MANIFEST.MF -C build/classes mapper.class -C build/classes/ Marker.class -C build/classes WorldMap.class


cp build/jar/mapper.jar export/

cd export

tar zcvf mapper_plugin.1.0.tgz *
