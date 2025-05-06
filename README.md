# Chess Tournament Management System

A modern and elegant application for managing chess tournaments, players, and matches with a sleek UI design.

## Quick Start

You can run the application directly by double-clicking the `Chess.jar` file or using:

```bash
java -jar Chess.jar
```

## Default Admin Login

Username: `admin`  
Password: `admin`

## Stockfish Integration

This application integrates with the Stockfish chess engine for move validation, position analysis, and training. To use these features, you need to install Stockfish:

### Installing Stockfish

#### Automatic Installation

We provide scripts to automatically download and install Stockfish:

**Linux/Mac:**
```bash
chmod +x download_stockfish.sh
./download_stockfish.sh
```

**Windows:**
```
download_stockfish.bat
```

#### Manual Installation

If the automatic installation doesn't work:

1. Download the appropriate Stockfish version for your platform from [stockfishchess.org](https://stockfishchess.org/download/)
2. Create a directory: `resources/stockfish/`
3. Extract the Stockfish executable to this directory:
   - Linux/Mac: Name the executable `stockfish`
   - Windows: Name the executable `stockfish.exe`
4. Make the file executable (Linux/Mac): `chmod +x resources/stockfish/stockfish`

### Stockfish Features

With Stockfish integrated, you can:

- **Analyze Positions**: Get detailed evaluations of any board position
- **Validate Moves**: Ensure all moves follow chess rules correctly
- **Get Hints**: Receive move suggestions during games or training
- **Training Mode**: Practice against Stockfish at various levels
- **Learn Openings**: Study popular opening lines with engine analysis

## Project Structure

The project is organized into the following directories:

- **Chess.jar** - The executable application (main directory)
- **README.md** - This file (main directory)
- **build.sh** - Build script to compile the application (main directory)
- **users.txt** - User database (main directory)
- **playerStats.txt** - Player statistics database (main directory)
- **src/** - Java source code
- **resources/** - Resource files including manifest, images, and Stockfish
- **docs/** - Documentation including user guide
- **build/** - Compiled class files (created during build)

## Features

- **User Management**: Support for Players, Referees, and Administrators
- **Tournament Management**: Create and manage chess tournaments
- **Match Management**: Track matches, results, and player statistics
- **Modern UI**: Clean, responsive interface with modern design elements
- **Role-Based Access**: Different capabilities for different user roles

## Building from Source

To build the application from source, run:

```bash
./build.sh
```

This will compile all Java files from the `src` directory and create a new JAR file in the main directory.

## Development

- Java source files are located in the `src/` directory
- Resources and configuration files are in the `resources/` directory
- Documentation is kept in the `docs/` directory

## User Guide

For more detailed instructions, please refer to the user guide in the `docs/` directory.

## Default Users

The application comes with a default admin user:
- Username: admin
- Password: admin
- Role: Administrator

## System Requirements
- Java Runtime Environment (JRE) 8 or higher
- Graphical desktop environment

## User Roles

### Player
- Join tournaments
- Play matches
- View tournament details
- Track personal statistics

### Referee
- Manage assigned matches
- Start matches
- Resolve disputes
- Validate moves

### Administrator
- Create and manage tournaments
- Manage players and referees
- Configure system settings

## Getting Started

1. Launch the application by double-clicking `Chess.jar`
2. Log in with your credentials or register a new account
3. Navigate through the dashboard to access different functions based on your role

## User Interface

- **Dashboard**: Main hub showing statistics and quick actions
- **Tournaments**: View and join available tournaments
- **Profile**: Manage your user information
- **Admin Panel**: (Admins only) Manage tournaments, players, and referees
- **Referee Tools**: (Referees only) Manage assigned matches

## Support

If you encounter any issues, please refer to the USER_GUIDE.md file or contact system support.

## Screenshots

![Chess Match](screenshots/match.png)
![Tournament Dashboard](screenshots/dashboard.png)

## Documentation

For more detailed documentation, please see the docs directory or visit our [wiki](https://github.com/yourusername/chess-tournament-manager/wiki).

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Chess piece Unicode symbols
- Java Swing for the UI components 