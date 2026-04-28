"""STP/STEP to GLTF converter using cadquery + trimesh.

Usage:
    python convert_stp_to_gltf.py <input.stp> <output.gltf>
"""

import sys
import os
import tempfile
import traceback


def convert_stp_to_gltf(stp_path: str, gltf_path: str) -> bool:
    """Convert a STEP file to GLTF format."""
    temp_stl = None
    try:
        import cadquery as cq
        from cadquery.occ_impl import exporters, importers

        # 1. Load STEP file
        result = importers.importStep(stp_path)

        # 2. Export to temporary STL
        with tempfile.NamedTemporaryFile(suffix=".stl", delete=False) as f:
            temp_stl = f.name
        exporters.export(result, temp_stl, exportType="STL")

        # 3. Convert STL to GLTF via trimesh
        import trimesh
        mesh = trimesh.load_mesh(temp_stl)
        mesh.export(gltf_path)

        print(f"Conversion successful: {stp_path} -> {gltf_path}")
        return True

    except Exception as e:
        print(f"Conversion failed: {e}", file=sys.stderr)
        traceback.print_exc(file=sys.stderr)
        return False

    finally:
        if temp_stl and os.path.exists(temp_stl):
            os.remove(temp_stl)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print(f"Usage: {sys.argv[0]} <input.stp> <output.gltf>", file=sys.stderr)
        sys.exit(1)

    input_path = sys.argv[1]
    output_path = sys.argv[2]

    if not os.path.exists(input_path):
        print(f"Input file not found: {input_path}", file=sys.stderr)
        sys.exit(1)

    success = convert_stp_to_gltf(input_path, output_path)
    sys.exit(0 if success else 1)
