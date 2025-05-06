#!/bin/bash

echo "Chess Tournament System - Stockfish Setup Tool"
echo "---------------------------------------------"

# Ensure resources directory exists
mkdir -p resources/stockfish

# Detect OS
OS="unknown"
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    OS="linux"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    OS="mac"
elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    OS="windows"
fi

echo "Detected OS: $OS"

# Detect architecture
ARCH=$(uname -m)
echo "Detected architecture: $ARCH"

STOCKFISH_URL=""
STOCKFISH_FILENAME=""

if [[ "$OS" == "mac" ]]; then
    if [[ "$ARCH" == "arm64" ]]; then
        echo "Downloading Stockfish for macOS (Apple Silicon)..."
        STOCKFISH_URL="https://github.com/official-stockfish/Stockfish/releases/download/sf_16/stockfish-macos-arm64"
        STOCKFISH_FILENAME="stockfish-mac"
    else
        echo "Downloading Stockfish for macOS (Intel)..."
        STOCKFISH_URL="https://github.com/official-stockfish/Stockfish/releases/download/sf_16/stockfish-macos-x86-64-avx2"
        STOCKFISH_FILENAME="stockfish-mac"
    fi
elif [[ "$OS" == "linux" ]]; then
    echo "Downloading Stockfish for Linux..."
    STOCKFISH_URL="https://github.com/official-stockfish/Stockfish/releases/download/sf_16/stockfish-ubuntu-x86-64-avx2"
    STOCKFISH_FILENAME="stockfish"
elif [[ "$OS" == "windows" ]]; then
    echo "Downloading Stockfish for Windows..."
    STOCKFISH_URL="https://github.com/official-stockfish/Stockfish/releases/download/sf_16/stockfish-windows-x86-64-avx2.exe"
    STOCKFISH_FILENAME="stockfish.exe"
else
    echo "Unsupported OS: $OS"
    echo "Creating a mock Stockfish engine file..."
    
    cat > resources/stockfish/stockfish-mac << 'EOF'
#!/bin/bash

# This is a mock Stockfish engine for testing
# It responds to common UCI commands

while read line; do
  if [[ "$line" == "uci" ]]; then
    echo "id name Stockfish 16 (mock)"
    echo "id author Mock Author"
    echo "option name Threads type spin default 1 min 1 max 512"
    echo "option name Hash type spin default 16 min 1 max 33554432"
    echo "uciok"
  elif [[ "$line" == "isready" ]]; then
    echo "readyok"
  elif [[ "$line" == "quit" ]]; then
    exit 0
  elif [[ "$line" == "ucinewgame" ]]; then
    echo "info string Starting new game"
  elif [[ "$line" =~ ^position.* ]]; then
    echo "info string Position set"
  elif [[ "$line" =~ ^go.* ]]; then
    echo "info depth 1 score cp 20 time 10 nodes 10 nps 1 pv e2e4"
    echo "info depth 2 score cp 30 time 20 nodes 100 nps 5 pv e2e4 e7e5"
    echo "bestmove e2e4 ponder e7e5"
  fi
done
EOF
    chmod +x resources/stockfish/stockfish-mac
    echo "Mock Stockfish engine has been created at resources/stockfish/stockfish-mac"
    exit 0
fi

# Download Stockfish
if curl -L -o resources/stockfish/$STOCKFISH_FILENAME "$STOCKFISH_URL"; then
    # Make it executable
    chmod +x resources/stockfish/$STOCKFISH_FILENAME
    echo "Stockfish has been downloaded to resources/stockfish/$STOCKFISH_FILENAME"
    echo "You can now run the chess application."
else
    echo "Failed to download Stockfish. Creating a mock engine instead..."
    cat > resources/stockfish/stockfish-mac << 'EOF'
#!/bin/bash

# This is a mock Stockfish engine for testing
# It responds to common UCI commands

while read line; do
  if [[ "$line" == "uci" ]]; then
    echo "id name Stockfish 16 (mock)"
    echo "id author Mock Author"
    echo "option name Threads type spin default 1 min 1 max 512"
    echo "option name Hash type spin default 16 min 1 max 33554432"
    echo "uciok"
  elif [[ "$line" == "isready" ]]; then
    echo "readyok"
  elif [[ "$line" == "quit" ]]; then
    exit 0
  elif [[ "$line" == "ucinewgame" ]]; then
    echo "info string Starting new game"
  elif [[ "$line" =~ ^position.* ]]; then
    echo "info string Position set"
  elif [[ "$line" =~ ^go.* ]]; then
    echo "info depth 1 score cp 20 time 10 nodes 10 nps 1 pv e2e4"
    echo "info depth 2 score cp 30 time 20 nodes 100 nps 5 pv e2e4 e7e5"
    echo "bestmove e2e4 ponder e7e5"
  fi
done
EOF
    chmod +x resources/stockfish/stockfish-mac
    echo "Mock Stockfish engine has been created at resources/stockfish/stockfish-mac"
fi 