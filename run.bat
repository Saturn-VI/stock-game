@echo off

mkdir target 2>NUL

REM cd and build files in src/
javac *.java

REM move classes to target/
move *.class target\

REM Run the main class
java -cp target Main
