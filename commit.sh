#!/bin/bash

# ------------------------------------------
# Script to Add, Commit, and Push One File at a Time in a Git Repository
# ------------------------------------------

# Directory where the script will operate (default is current directory)
TARGET_DIR=${1:-$(pwd)}

# Git branch to push to (default is 'main')
BRANCH=${2:-main}

# Remote name (default is 'origin')
REMOTE=${3:-origin}

# Change to target directory
cd "$TARGET_DIR" || { echo "❌ Directory not found: $TARGET_DIR"; exit 1; }

# Check if it's a git repository
if [ ! -d ".git" ]; then
    echo "❌ Not a git repository: $TARGET_DIR"
    exit 1
fi

# Iterate over each file in the directory
for file in *; do
    if [ -f "$file" ]; then
        echo "🔄 Processing file: $file"

        # Add the file to git
        git add "$file"

        # Commit the file
        git commit -m "Add file: $file"

        # Push the file
        git push "$REMOTE" "$BRANCH"

        # Check if push was successful
        if [ $? -eq 0 ]; then
            echo "✅ Successfully pushed: $file"
        else
            echo "❌ Failed to push: $file"
            exit 1
        fi
    fi
done

echo "🚀 All files processed and pushed!"
