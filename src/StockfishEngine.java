import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class StockfishEngine {
    private Process engineProcess;
    private BufferedReader processReader;
    private BufferedWriter processWriter;
    private int engineStrength = 20; // Default strength out of 20
    private boolean isReady = false;
    private String enginePath;
    
    /**
     * Creates a new StockfishEngine instance
     * 
     * @param enginePath the path to the Stockfish engine executable
     */
    public StockfishEngine(String enginePath) {
        this.enginePath = enginePath;
        initializeEngine();
    }
    
    /**
     * Initializes the Stockfish engine process
     */
    private void initializeEngine() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(enginePath);
            processBuilder.redirectErrorStream(true);
            engineProcess = processBuilder.start();
            
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));
            
            // Initialize the engine
            sendCommand("uci");
            boolean uciOk = waitForResponse("uciok", 5000);
            if (!uciOk) {
                System.err.println("Stockfish engine did not respond to UCI command");
                close();
                return;
            }
            
            sendCommand("isready");
            boolean readyOk = waitForResponse("readyok", 5000);
            if (!readyOk) {
                System.err.println("Stockfish engine is not ready");
                close();
                return;
            }
            
            // Set default engine strength
            setEngineStrength(engineStrength);
            
            isReady = true;
        } catch (IOException e) {
            System.err.println("Error initializing Stockfish engine: " + e.getMessage());
            isReady = false;
            close();
        }
    }
    
    /**
     * Send a command to the Stockfish engine
     * 
     * @param command the UCI command to send
     */
    public synchronized void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            System.err.println("Error sending command to Stockfish: " + e.getMessage());
        }
    }
    
    /**
     * Wait for a specific response from the engine with timeout
     * 
     * @param expected the expected response string
     * @param timeoutMs timeout in milliseconds
     * @return true if the response was received, false otherwise
     */
    private boolean waitForResponse(String expected, int timeoutMs) {
        try {
            String line;
            long startTime = System.currentTimeMillis();
            
            while ((System.currentTimeMillis() - startTime) < timeoutMs) {
                if (processReader.ready()) {
                    line = processReader.readLine();
                    if (line != null && line.contains(expected)) {
                        return true;
                    }
                } else {
                    // Give the process some time to produce output
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from Stockfish: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Set the Stockfish engine strength (1-20)
     * 
     * @param strength the strength level (1-20)
     */
    public void setEngineStrength(int strength) {
        if (strength < 1) strength = 1;
        if (strength > 20) strength = 20;
        
        this.engineStrength = strength;
        
        // Convert strength to skill level and depth
        int skillLevel = strength;
        int depth = 1 + strength;
        
        // Apply settings
        sendCommand("setoption name Skill Level value " + skillLevel);
        sendCommand("setoption name Threads value 1");
        sendCommand("setoption name Hash value 128");
        sendCommand("setoption name MultiPV value 1");
    }
    
    /**
     * Analyze the current position and get the best move
     * 
     * @param fen the FEN notation of the current position
     * @param timeInMs the time to think in milliseconds
     * @return the best move in UCI notation (e.g., "e2e4")
     */
    public String getBestMove(String fen, int timeInMs) {
        if (!isReady) return null;
        
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + timeInMs);
        
        try {
            String line;
            String bestMove = null;
            
            // Set a timeout to prevent infinite waiting
            long startTime = System.currentTimeMillis();
            long timeout = timeInMs + 1000; // Add a buffer of 1 second
            
            while ((line = processReader.readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    bestMove = line.split("\\s+")[1];
                    break;
                }
                
                // Check for timeout
                if (System.currentTimeMillis() - startTime > timeout) {
                    sendCommand("stop");
                    break;
                }
            }
            
            return bestMove;
        } catch (IOException e) {
            System.err.println("Error getting best move from Stockfish: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Analyze a position and return detailed evaluation
     * 
     * @param fen the FEN notation of the position
     * @param depth the depth to analyze to
     * @return a map containing analysis information
     */
    public Map<String, Object> analyzePosition(String fen, int depth) {
        if (!isReady) return null;
        
        Map<String, Object> analysis = new HashMap<>();
        List<String> pvMoves = new ArrayList<>();
        
        sendCommand("position fen " + fen);
        sendCommand("setoption name MultiPV value 3");
        sendCommand("go depth " + depth);
        
        try {
            String line;
            boolean collecting = true;
            int score = 0;
            
            while (collecting && (line = processReader.readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    String bestMove = line.split("\\s+")[1];
                    analysis.put("bestMove", bestMove);
                    collecting = false;
                } else if (line.contains(" pv ")) {
                    // Parse info line
                    String[] parts = line.split("\\s+");
                    
                    // Find the score
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].equals("cp") && i + 1 < parts.length) {
                            score = Integer.parseInt(parts[i + 1]);
                            analysis.put("score", score / 100.0); // Convert to pawns
                        }
                        
                        if (parts[i].equals("mate") && i + 1 < parts.length) {
                            int mateIn = Integer.parseInt(parts[i + 1]);
                            analysis.put("mateIn", mateIn);
                        }
                        
                        // Find the PV (principal variation)
                        if (parts[i].equals("pv")) {
                            StringBuilder pv = new StringBuilder();
                            for (int j = i + 1; j < parts.length; j++) {
                                pv.append(parts[j]).append(" ");
                            }
                            pvMoves.add(pv.toString().trim());
                            break;
                        }
                    }
                }
            }
            
            analysis.put("pvMoves", pvMoves);
            
            // Reset MultiPV
            sendCommand("setoption name MultiPV value 1");
            return analysis;
            
        } catch (IOException e) {
            System.err.println("Error analyzing position with Stockfish: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Convert chess board to FEN notation
     * 
     * @param board the chess board represented as a Map<String, String>
     * @param isWhiteTurn if it's white's turn
     * @return FEN string
     */
    public static String boardToFen(Map<String, String> board, boolean isWhiteTurn) {
        StringBuilder fen = new StringBuilder();
        
        // Piece placement
        for (int rank = 7; rank >= 0; rank--) {
            int emptyCount = 0;
            for (int file = 0; file < 8; file++) {
                String square = "" + (char)('a' + file) + (rank + 1);
                String piece = board.get(square);
                
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    
                    char pieceChar = piece.charAt(1);
                    fen.append(piece.startsWith("w") ? Character.toUpperCase(pieceChar) : Character.toLowerCase(pieceChar));
                }
            }
            
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            
            if (rank > 0) {
                fen.append('/');
            }
        }
        
        // Active color
        fen.append(' ').append(isWhiteTurn ? 'w' : 'b');
        
        // Castling (simplified - assume all castling rights)
        fen.append(" KQkq");
        
        // En passant target (simplified - we don't track en passant)
        fen.append(" -");
        
        // Halfmove clock and fullmove number (simplified)
        fen.append(" 0 1");
        
        return fen.toString();
    }
    
    /**
     * Checks if a move is valid according to Stockfish
     * 
     * @param fen the current position in FEN notation
     * @param move the move to check in UCI notation (e.g., "e2e4")
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(String fen, String move) {
        if (!isReady) return false;
        
        sendCommand("position fen " + fen);
        sendCommand("go perft 1");
        
        try {
            String line;
            while ((line = processReader.readLine()) != null) {
                if (line.contains(move + ":")) {
                    return true;
                }
                if (line.startsWith("Nodes searched")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking move validity: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get a hint for the current position
     * 
     * @param fen the FEN notation of the current position
     * @return a hint move in UCI notation
     */
    public String getHint(String fen) {
        return getBestMove(fen, 1000); // Think for 1 second
    }
    
    /**
     * Close the engine process
     */
    public void close() {
        try {
            if (processWriter != null) {
                sendCommand("quit");
                processWriter.close();
            }
            if (processReader != null) {
                processReader.close();
            }
            if (engineProcess != null) {
                engineProcess.destroy();
            }
        } catch (IOException e) {
            System.err.println("Error closing Stockfish engine: " + e.getMessage());
        }
    }
    
    /**
     * Check if the engine is ready
     * 
     * @return true if the engine is ready, false otherwise
     */
    public boolean isReady() {
        if (engineProcess == null) {
            return false;
        }
        
        try {
            sendCommand("isready");
            return waitForResponse("readyok", 5000);
        } catch (Exception e) {
            System.err.println("Error checking if Stockfish is ready: " + e.getMessage());
            return false;
        }
    }
} 