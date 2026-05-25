cmake_minimum_required(VERSION 3.22.1)

project("bio_os_native")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include_directories(
        bio_os_engine/include
)

file(GLOB_RECURSE BIO_OS_ENGINE_SOURCES
        bio_os_engine/src/*.cpp
)

list(APPEND BIO_OS_ENGINE_SOURCES
        bio_os_engine/src/algorithms/PruningStrategy.cpp
)

add_library(
        bio_os_native
        SHARED
        native-lib.cpp
        ${BIO_OS_ENGINE_SOURCES}
)

find_library(
        log-lib
        log
)

target_link_libraries(
        bio_os_native
        ${log-lib}
)