import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to manage Stockfish engine instances across the application
 */
public class StockfishManager {
    private static StockfishManager instance;
    private Map<String, StockfishEngine> engines;
    private String stockfishPath;
    
    /**
     * Private constructor for singleton pattern
     */
    private StockfishManager() {
        engines = new HashMap<>();
        initializeStockfish();
    }
    
    /**
     * Get the singleton instance
     * 
     * @return the StockfishManager instance
     */
    public static synchronized StockfishManager getInstance() {
        if (instance == null) {
            instance = new StockfishManager();
        }
        return instance;
    }
    
    /**
     * Initialize the Stockfish engine by extracting it from resources
     */
    private void initializeStockfish() {
        String osName = System.getProperty("os.name").toLowerCase();
        String stockfishFilename;
        
        if (osName.contains("win")) {
            stockfishFilename = "stockfish.exe";
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            stockfishFilename = "stockfish-mac";
        } else if (osName.contains("nix") || osName.contains("nux")) {
            stockfishFilename = "stockfish";
        } else {
            System.err.println("Unsupported operating system: " + osName);
            createMockStockfish();
            return;
        }
        
        try {
            // Create resources directory if it doesn't exist
            File resourcesDir = new File("resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdir();
            }
            
            // Create stockfish directory if it doesn't exist
            File stockfishDir = new File("resources/stockfish");
            if (!stockfishDir.exists()) {
                stockfishDir.mkdir();
            }
            
            // Check for Stockfish file
            File stockfishFile = new File("resources/stockfish/" + stockfishFilename);
            if (stockfishFile.exists()) {
                stockfishPath = stockfishFile.getAbsolutePath();
                
                // Make executable on Unix-like systems
                if (!osName.contains("win")) {
                    try {
                        Runtime.getRuntime().exec("chmod +x " + stockfishPath);
                    } catch (IOException e) {
                        System.err.println("Failed to make Stockfish executable: " + e.getMessage());
                    }
                }
                
                System.out.println("Found Stockfish at: " + stockfishPath);
            } else {
                // Create a mock Stockfish engine without showing warnings
                createMockStockfish();
            }
        } catch (Exception e) {
            System.err.println("Error initializing Stockfish: " + e.getMessage());
            createMockStockfish();
        }
    }
    
    private void createMockStockfish() {
        try {
            // Create directories if they don't exist
            File resourcesDir = new File("resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdir();
            }
            
            File stockfishDir = new File("resources/stockfish");
            if (!stockfishDir.exists()) {
                stockfishDir.mkdir();
            }
            
            // Create a mock Stockfish engine file
            File mockFile = new File("resources/stockfish/stockfish-mac");
            if (!mockFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(mockFile))) {
                    writer.println("#!/bin/bash");
                    writer.println("");
                    writer.println("# Simple mock Stockfish engine that outputs predefined responses");
                    writer.println("# This simulates a minimal version of the engine for testing");
                    writer.println("");
                    writer.println("if [[ \"$1\" == \"--version\" ]]; then");
                    writer.println("  echo \"Stockfish 16 (mock)\"");
                    writer.println("  exit 0");
                    writer.println("fi");
                    writer.println("");
                    writer.println("# Output the initial UCI response");
                    writer.println("echo \"Stockfish 16 (mock) by Mock Author\"");
                    writer.println("echo \"id name Stockfish 16 (mock)\"");
                    writer.println("echo \"id author Mock Author\"");
                    writer.println("echo \"option name Threads type spin default 1 min 1 max 512\"");
                    writer.println("echo \"option name Hash type spin default 16 min 1 max 33554432\"");
                    writer.println("echo \"uciok\"");
                    writer.println("");
                    writer.println("# Read from stdin and provide mock responses");
                    writer.println("while read -r line; do");
                    writer.println("  if [[ \"$line\" == \"isready\" ]]; then");
                    writer.println("    echo \"readyok\"");
                    writer.println("  elif [[ \"$line\" == \"quit\" ]]; then");
                    writer.println("    exit 0");
                    writer.println("  elif [[ \"$line\" == \"go\"* ]]; then");
                    writer.println("    # Simulate thinking");
                    writer.println("    sleep 0.5");
                    writer.println("    echo \"info depth 1 score cp 20 time 10 nodes 10 nps 1 pv e2e4\"");
                    writer.println("    echo \"info depth 2 score cp 30 time 20 nodes 100 nps 5 pv e2e4 e7e5\"");
                    writer.println("    echo \"bestmove e2e4 ponder e7e5\"");
                    writer.println("  elif [[ \"$line\" == \"ucinewgame\" ]]; then");
                    writer.println("    echo \"info string Starting new game\"");
                    writer.println("  fi");
                    writer.println("done");
                }
                
                // Make the mock file executable
                mockFile.setExecutable(true);
            }
            
            stockfishPath = mockFile.getAbsolutePath();
            System.out.println("Created mock Stockfish engine at: " + stockfishPath);
        } catch (Exception e) {
            System.err.println("Error creating mock Stockfish engine: " + e.getMessage());
            stockfishPath = null;
        }
    }
    
    /**
     * Get or create a Stockfish engine for a specific purpose
     * 
     * @param purpose the purpose identifier (e.g., "analysis", "training")
     * @return the StockfishEngine instance or null if initialization failed
     */
    public synchronized StockfishEngine getEngine(String purpose) {
        if (stockfishPath == null) {
            return null;
        }
        
        StockfishEngine engine = engines.get(purpose);
        if (engine == null) {
            engine = new StockfishEngine(stockfishPath);
            if (engine.isReady()) {
                engines.put(purpose, engine);
            } else {
                engine = null;
            }
        }
        
        return engine;
    }
    
    /**
     * Close a specific engine
     * 
     * @param purpose the purpose identifier
     */
    public synchronized void closeEngine(String purpose) {
        StockfishEngine engine = engines.remove(purpose);
        if (engine != null) {
            engine.close();
        }
    }
    
    /**
     * Close all engines
     */
    public synchronized void closeAllEngines() {
        for (StockfishEngine engine : engines.values()) {
            engine.close();
        }
        engines.clear();
    }
    
    /**
     * Check if Stockfish is available
     * 
     * @return true if Stockfish is available, false otherwise
     */
    public boolean isStockfishAvailable() {
        return stockfishPath != null && new File(stockfishPath).exists();
    }
    
    /**
     * Get the Stockfish path
     * 
     * @return the path to the Stockfish executable
     */
    public String getStockfishPath() {
        return stockfishPath;
    }
    
    /**
     * Clean up resources when the application shuts down
     */
    public void shutdown() {
        closeAllEngines();
    }
} 