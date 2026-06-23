#!/usr/bin/env bash
set -e

rm -f bio_os_engine.exe

CPP_FILES=$(find bio_os_engine/src -name "*.cpp" ! -name "EngineNativeBridge.cpp" -print)

echo "Compiling files:"
echo "$CPP_FILES"

g++ -std=c++17 bio_os_engine.cpp $CPP_FILES \
-Ibio_os_engine/include \
-o bio_os_engine.exe \
-static -static-libgcc -static-libstdc++

echo "Build success."
