#!/bin/sh
# 安装3D模型转换所需的Python依赖
# cadquery需要通过conda安装，因为cadquery-ocp没有为所有平台提供PyPI wheel
cd "$(dirname "$0")"

CONDA_DIR="conda"
CONDA_ENV="cadquery-env"
CONDA_PYTHON="$CONDA_DIR/envs/$CONDA_ENV/bin/python"

# 如果conda环境已经存在，跳过安装
if [ -f "$CONDA_PYTHON" ]; then
    echo "Conda environment already exists, skipping installation."
    exit 0
fi

# 检查conda是否可用
CONDA_CMD=""
if command -v conda >/dev/null 2>&1; then
    CONDA_CMD="conda"
elif [ -f "$CONDA_DIR/bin/conda" ]; then
    CONDA_CMD="$CONDA_DIR/bin/conda"
else
    echo "conda not found. Installing miniconda to $CONDA_DIR..."
    os=$(uname -s | tr '[:upper:]' '[:lower:]')
    arch=$(uname -m)
    case "$os" in
        linux) os="Linux" ;;
        darwin) os="MacOSX" ;;
        *) echo "Unsupported OS: $os"; exit 1 ;;
    esac
    INSTALLER="Miniconda3-latest-${os}-${arch}.sh"
    curl -sSL "https://repo.anaconda.com/miniconda/$INSTALLER" -o /tmp/miniconda.sh
    bash /tmp/miniconda.sh -b -p "$(pwd)/$CONDA_DIR"
    rm /tmp/miniconda.sh
    CONDA_CMD="$(pwd)/$CONDA_DIR/bin/conda"
fi

echo "Creating conda environment '$CONDA_ENV' with cadquery..."
"$CONDA_CMD" create -y -n "$CONDA_ENV" -c conda-forge python=3.11 cadquery trimesh

echo "Installing additional pip dependencies..."
"$CONDA_DIR/envs/$CONDA_ENV/bin/pip" install -r requirements.txt 2>/dev/null || true

echo "Done!"
