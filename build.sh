#!/bin/bash
# Saiku Docker Build Script
# This script builds Saiku in a Docker container to isolate dependencies

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Saiku Docker Build ===${NC}"
echo ""

# Check for Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed or not in PATH${NC}"
    exit 1
fi

# Parse arguments
BUILD_TYPE="${1:-full}"

case "$BUILD_TYPE" in
    full)
        echo -e "${YELLOW}Running full build (this may take a while)...${NC}"
        docker build -f Dockerfile.build -t saiku-build:latest .

        echo -e "${GREEN}Build complete!${NC}"
        echo ""
        echo "To run Saiku:"
        echo "  docker run -p 8080:8080 saiku-build:latest"
        ;;

    dev)
        echo -e "${YELLOW}Starting interactive build container...${NC}"
        docker run -it --rm \
            -v "$SCRIPT_DIR":/build \
            -v saiku-maven-cache:/root/.m2/repository \
            -w /build \
            maven:3.6.3-jdk-8 \
            bash
        ;;

    deps)
        echo -e "${YELLOW}Attempting to resolve dependencies only...${NC}"
        docker run --rm \
            -v "$SCRIPT_DIR":/build \
            -v saiku-maven-cache:/root/.m2/repository \
            -v "$SCRIPT_DIR/docker-maven-settings.xml":/root/.m2/settings.xml \
            -w /build \
            maven:3.6.3-jdk-8 \
            mvn dependency:resolve -B -e
        ;;

    clean)
        echo -e "${YELLOW}Cleaning Docker artifacts...${NC}"
        docker volume rm saiku-maven-cache 2>/dev/null || true
        docker rmi saiku-build:latest 2>/dev/null || true
        echo -e "${GREEN}Cleaned!${NC}"
        ;;

    *)
        echo "Usage: $0 [full|dev|deps|clean]"
        echo ""
        echo "  full  - Build complete Docker image (default)"
        echo "  dev   - Start interactive container for development"
        echo "  deps  - Resolve Maven dependencies only"
        echo "  clean - Remove Docker volumes and images"
        exit 1
        ;;
esac
