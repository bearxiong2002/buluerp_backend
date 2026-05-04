#!/bin/sh
# 安装3D模型转换所需的Python依赖
cd "$(dirname "$0")"
echo "Creating Python virtual environment..."
python3 -m venv venv 2>/dev/null || {
    echo "python3-venv not found, attempting to install..."
    apt-get update -qq && apt-get install -y -qq python3-venv 2>/dev/null || {
        echo "Failed to install python3-venv, trying pip3 directly..."
        pip3 install --break-system-packages -r requirements.txt && echo "Done!" && exit 0
        pip3 install -r requirements.txt && echo "Done!" && exit 0
        echo "Failed to install dependencies. Please install python3-venv manually."
        exit 1
    }
    python3 -m venv venv
}
echo "Installing cadquery and trimesh..."
venv/bin/pip install -r requirements.txt
echo "Done!"
