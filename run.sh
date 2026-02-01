#!/bin/bash

# URL Shortener - Setup and Run Script
# This script helps with setting up and running the application

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

function print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

function print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

function print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

function check_prerequisites() {
    print_info "Checking prerequisites..."

    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi

    print_info "All prerequisites are met!"
}

function setup_backend() {
    print_info "Setting up backend..."
    cd "$SCRIPT_DIR/app/backend"

    if command -v mvn &> /dev/null; then
        print_info "Maven found. Running tests..."
        mvn clean test || print_warning "Tests failed. Continuing anyway..."
    else
        print_warning "Maven not found. Skipping backend tests."
    fi

    cd "$SCRIPT_DIR"
    print_info "Backend setup complete!"
}

function setup_frontend() {
    print_info "Setting up frontend..."
    cd "$SCRIPT_DIR/app/frontend"

    if [ ! -d "node_modules" ]; then
        if command -v npm &> /dev/null; then
            print_info "Installing frontend dependencies..."
            npm install
        else
            print_warning "npm not found. Will use Docker to build."
        fi
    fi

    cd "$SCRIPT_DIR"
    print_info "Frontend setup complete!"
}

function run_docker() {
    print_info "Starting application with Docker Compose..."

    # Check if containers are already running
    if docker-compose ps | grep -q "Up"; then
        print_warning "Containers are already running. Stopping them first..."
        docker-compose down
    fi

    # Build and start in detached mode
    print_info "Building and starting containers..."
    docker-compose up -d --build

    # Wait a moment for containers to start
    sleep 3

    # Show status
    print_info "Container status:"
    docker-compose ps

    echo ""
    print_info "Application started successfully!"
    print_info "Frontend: http://localhost:3000"
    print_info "Backend:  http://localhost:8080"
    print_info "API Docs: http://localhost:8080/urls"
    echo ""
    print_info "To view logs, run: docker-compose logs -f"
    print_info "To stop, run: $0 stop"
}

function run_local_backend() {
    print_info "Starting backend locally..."
    cd "$SCRIPT_DIR/app/backend"

    if ! command -v mvn &> /dev/null; then
        print_error "Maven is required to run backend locally."
        exit 1
    fi

    mvn spring-boot:run
}

function run_local_frontend() {
    print_info "Starting frontend locally..."
    cd "$SCRIPT_DIR/app/frontend"

    if ! command -v npm &> /dev/null; then
        print_error "npm is required to run frontend locally."
        exit 1
    fi

    npm start
}

function stop_docker() {
    print_info "Stopping Docker containers..."
    docker-compose down
    print_info "Containers stopped successfully!"
}

function logs_docker() {
    print_info "Showing Docker container logs..."
    print_info "Press Ctrl+C to exit logs view"
    sleep 2
    docker-compose logs -f
}

function clean() {
    print_info "Cleaning build artifacts..."

    # Clean backend
    if [ -d "$SCRIPT_DIR/app/backend/target" ]; then
        rm -rf "$SCRIPT_DIR/app/backend/target"
        print_info "Cleaned backend build artifacts"
    fi

    # Clean frontend
    if [ -d "$SCRIPT_DIR/app/frontend/build" ]; then
        rm -rf "$SCRIPT_DIR/app/frontend/build"
        print_info "Cleaned frontend build artifacts"
    fi

    if [ -d "$SCRIPT_DIR/app/frontend/node_modules" ]; then
        read -p "Remove frontend node_modules? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            rm -rf "$SCRIPT_DIR/app/frontend/node_modules"
            print_info "Cleaned frontend node_modules"
        fi
    fi

    # Clean database
    if [ -d "$SCRIPT_DIR/app/backend/data" ]; then
        read -p "Remove database files? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            rm -rf "$SCRIPT_DIR/app/backend/data"
            print_info "Cleaned database files"
        fi
    fi

    print_info "Clean complete!"
}

function show_help() {
    cat << EOF
URL Shortener - Setup and Run Script

Usage: $0 [COMMAND]

Commands:
    setup       Set up the application (check prerequisites, install dependencies)
    docker      Run the application with Docker Compose (recommended)
    logs        View Docker container logs (requires containers to be running)
    backend     Run backend locally (requires Java 21 and Maven)
    frontend    Run frontend locally (requires Node.js and npm)
    stop        Stop Docker containers
    clean       Clean build artifacts and optionally database
    help        Show this help message

Examples:
    $0 setup        # First time setup
    $0 docker       # Run with Docker (easiest)
    $0 logs         # View logs from running containers
    $0 backend      # Run backend locally in this terminal
    $0 frontend     # Run frontend locally (open new terminal for this)
    $0 stop         # Stop all Docker containers
    $0 clean        # Clean build artifacts

After starting with Docker:
    Frontend:   http://localhost:3000
    Backend:    http://localhost:8080
    API Docs:   http://localhost:8080/urls
    H2 Console: http://localhost:8080/h2-console

After starting locally:
    Backend starts on port 8080
    Frontend starts on port 3000
    (You'll need two separate terminals)

EOF
}

# Main script
case "${1:-}" in
    setup)
        check_prerequisites
        setup_backend
        setup_frontend
        print_info "Setup complete! Run '$0 docker' to start the application."
        ;;
    docker)
        check_prerequisites
        run_docker
        ;;
    logs)
        check_prerequisites
        logs_docker
        ;;
    backend)
        run_local_backend
        ;;
    frontend)
        run_local_frontend
        ;;
    stop)
        stop_docker
        ;;
    clean)
        clean
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        show_help
        exit 1
        ;;
esac

