cmake_minimum_required(VERSION 3.4.1)

include_directories(src/main/cpp/inline_hook
                    src/main/cpp/dex_dump
                    src/main/cpp/)

aux_source_directory(src/main/cpp/inline_hook SRC_PATH)
aux_source_directory(src/main/cpp/dex_dump SRC_PATH)
aux_source_directory(src/main/cpp SRC_PATH)

add_library(dump-dex SHARED ${SRC_PATH})

find_library(log-lib log)

target_link_libraries(dump-dex ${log-lib})