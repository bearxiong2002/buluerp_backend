@echo off
REM 安装3D模型转换所需的Python依赖
cd /d "%~dp0"
echo Creating Python virtual environment...
python -m venv venv
echo Installing cadquery and trimesh...
call venv\Scripts\pip install -r requirements.txt
echo Done!
