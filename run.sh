#!/bin/sh

mkdir -p target

# cd and build files in src/
javac *.java

# move classes to target/
mv *.class target/

java -cp target/ Main
