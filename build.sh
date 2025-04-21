#!/bin/bash

# Clean up previous build if it exists
rm -rf build
mkdir -p build

# Compile all Java files from src directory
echo "Compiling Java source files..."
javac -d build src/*.java

# Create JAR file
echo "Creating JAR file..."
cd build
jar cvfm ../Chess.jar ../resources/Manifest.txt *.class
cd ..

echo "Build completed successfully"
echo "The Chess.jar file is now available in the main directory" 