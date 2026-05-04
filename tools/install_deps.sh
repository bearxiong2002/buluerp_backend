#!/bin/sh
# 安装3D模型转换所需的Python依赖
cd "$(dirname "$0")"
echo "Creating Python virtual environment..."
python3 -m venv venv
echo "Installing cadquery and trimesh..."
venv/bin/pip install -r requirements.txt
echo "Done!"
