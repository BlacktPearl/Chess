# Chess Tournament Management System - Installation Guide

## System Requirements

- **Java Runtime Environment (JRE)**: Version 8 or higher
- **Operating System**: Windows, macOS, or Linux
- **Memory**: At least 512MB of RAM
- **Disk Space**: At least 100MB of free disk space
- **Screen Resolution**: Minimum 1024x768 pixels

## Basic Installation

The Chess Tournament Management System requires no complex installation process. Simply follow these steps:

1. Ensure you have Java Runtime Environment (JRE) 8 or higher installed
2. Download the `Chess.jar` file from the release package
3. Place it in any directory of your choice
4. Double-click the JAR file to run the application

## Command Line Execution

If double-clicking doesn't work or you prefer using the command line:

```bash
java -jar Chess.jar
```

## First Run

When you run the application for the first time:

1. A `users.txt` file will be automatically created in the same directory
2. The default administrator account will be set up:
   - Username: `admin`
   - Password: `admin`
3. It's recommended to change the admin password after first login

## Advanced Configuration

### Memory Allocation

If you need to allocate more memory for larger tournaments:

```bash
java -Xmx1024m -jar Chess.jar
```

This allocates 1GB of memory to the application.

### Custom User Database Location

You can specify a custom location for the user database:

```bash
java -Dusers.file=/path/to/custom/users.txt -jar Chess.jar
```

## Updating

To update to a newer version:

1. Download the new `Chess.jar` file
2. Replace the existing JAR file with the new one
3. Ensure your `users.txt` file is preserved

## Troubleshooting Installation Issues

### Java Not Found

If you get an error like "Java not found" or "Unable to access jarfile":

1. Verify Java is installed: 
   ```bash
   java -version
   ```
2. If not installed, download and install Java from [Oracle's website](https://www.java.com/download/)

### Permission Denied

On Linux or macOS, you might need to add execute permission:

```bash
chmod +x Chess.jar
```

### Graphics Issues

If the application doesn't display properly:

1. Ensure your graphics drivers are up to date
2. Try running with hardware acceleration disabled:
   ```bash
   java -Dsun.java2d.opengl=false -jar Chess.jar
   ```

## Uninstallation

To uninstall the application:

1. Delete the `Chess.jar` file
2. Delete the `users.txt` file (note: this will remove all user data)
3. Delete any other files created by the application

## Data Backup

It's recommended to regularly back up your `users.txt` file to prevent data loss. 