#!/bin/bash

# Clean
rm -rf ./bin
mkdir -p ./bin/META-INF
rm simulator.jar
rm *.class

# Build the project
cp ./manifest.txt ./bin/META-INF/MANIFEST.MF
find . -name "*.java" -exec javac -d ./bin/ {} \;

# Package the jar
jar cfe simulator.jar Simulator -C ./bin/ .
zip -ur simulator.jar
chmod +x simulator.jar

# Build the docs
# pandoc README.md -o Report.pdf
docker run --rm --volume "$(pwd):/data" pandoc/latex README.md -o IN5020-Group7-Report.pdf
rm group7-artifacts.zip
zip -r group7-artifacts.zip *.java *.pdf simulator.jar ./outputs/*.out

# Clean
rm -rf ./bin