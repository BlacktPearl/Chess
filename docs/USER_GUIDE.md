# Chess Tournament Management System - User Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
3. [User Interface](#user-interface)
4. [Player Guide](#player-guide)
5. [Referee Guide](#referee-guide)
6. [Administrator Guide](#administrator-guide)
7. [Troubleshooting](#troubleshooting)

## Introduction

The Chess Tournament Management System is a comprehensive application designed to manage chess tournaments, players, matches, and results. It features a modern, elegant user interface and supports multiple user roles.

## Getting Started

### Installation
No installation is required. Simply run the `Chess.jar` file from the main directory.

### Login
When you start the application, you'll be presented with a login screen:
1. Enter your username and password
2. Click "Sign In"
3. If you don't have an account, click "Register New Account"
4. You can also continue as a guest with limited functionality

### First-time Login
For the first time, you can use the default administrator account:
- Username: `admin`
- Password: `admin`

## User Interface

The interface is divided into several sections:

### Main Dashboard
- **Header**: Displays your profile information and logout button
- **Sidebar**: Navigation menu with options based on your role
- **Content Area**: Displays the selected panel's content

### Common Features
- **Profile Panel**: View and edit your profile information
- **Tournaments Panel**: View available tournaments
- **Dashboard Panel**: Quick overview and statistics

## Player Guide

### Registration
1. Click "Register New Account" on the login screen
2. Fill in your details
3. Select "Player" as your role
4. Click "Create Account"

### Joining Tournaments
1. Navigate to the Tournaments panel
2. Browse available tournaments
3. Click "Join" on a tournament you wish to participate in

### Playing Matches
1. When a match is scheduled, it will appear in your dashboard
2. Click on the match to open the chess board
3. Play your moves according to standard chess rules
4. The result will be automatically recorded when the match ends

### Viewing Statistics
1. Go to your Profile panel
2. View your rating, match history, and tournament participation

## Referee Guide

### Registration
1. Click "Register New Account" on the login screen
2. Fill in your details
3. Select "Referee" as your role
4. Click "Create Account"

### Managing Matches
1. Navigate to the Referee Tools panel
2. View assigned matches
3. Click "Start Match" to begin a match between players
4. Monitor the game for rule violations

### Resolving Disputes
1. If players have a dispute, click "Resolve Dispute"
2. Review the position and history
3. Make a ruling according to chess regulations
4. Enter the result manually if necessary

## Administrator Guide

### Creating Tournaments
1. Navigate to the Admin panel
2. Click "Create Tournament"
3. Fill in tournament details:
   - Name
   - Start and end dates
   - Format (Swiss, Round Robin, etc.)
   - Number of rounds
4. Click "Create"

### Managing Users
1. Navigate to the Admin panel
2. Click "Manage Users"
3. View all registered users
4. Actions available:
   - Edit user details
   - Reset password
   - Change user role
   - Deactivate account

### Managing Tournaments
1. Navigate to the Admin panel
2. Click "Manage Tournaments"
3. View all tournaments
4. Actions available:
   - Edit tournament details
   - Add/remove participants
   - Generate pairings
   - View results and standings

## Troubleshooting

### Login Issues
- **Forgotten Password**: Contact an administrator to reset your password
- **Account Locked**: After multiple failed attempts, accounts may be temporarily locked

### Application Errors
- If the application crashes, restart it
- Ensure you have the latest Java version installed
- Check the user database file (`users.txt`) exists and is not corrupted

### Data Recovery
- User data is stored in the `users.txt` file
- Tournament data is saved after each action
- Make regular backups of these files to prevent data loss

## Support

For additional support, please contact the system administrator or refer to the documentation in the `docs` directory. 