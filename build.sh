#!/bin/bash

# Clean up previous build if it exists
rm -rf build
mkdir -p build

# Check if Stockfish is available
if [ ! -d "resources/stockfish" ]; then
    echo "Warning: Stockfish directory not found. Some features may be unavailable."
    echo "Consider running ./download_stockfish.sh to install Stockfish."
    
    # Create the directory structure for stockfish
    mkdir -p resources/stockfish
fi

# Compile all Java files from src directory
echo "Compiling Java source files..."
javac -d build src/*.java

# Create JAR file
echo "Creating JAR file..."
cd build
jar cvfm ../Chess.jar ../resources/Manifest.txt *.class
cd ..

# Make the download script executable
chmod +x download_stockfish.sh

echo "Build completed successfully"
echo "The Chess.jar file is now available in the main directory" 