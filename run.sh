#!/bin/sh

# cd and build files in src/
cd src/
javac *.java

# move classes to target/
cd ../
mv src/*.class target/

java -cp target/ Main
