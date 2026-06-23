#include "api/EngineFacade.hpp"

#include <cstdlib>
#include <iostream>

int main(int argc, char* argv[]) {
    if (argc < 4) {
        std::cerr << "Usage: bio_os_engine.exe <water> <light> <temperature> [humidity]" << std::endl;
        return 1;
    }

    double water = std::atof(argv[1]);
    double light = std::atof(argv[2]);
    double temperature = std::atof(argv[3]);

    EngineFacade engine;
    engine.initializeDefaultPlant();
    engine.runTick(water, light, temperature);

    std::cout << engine.getSnapshotJson() << std::endl;

    return 0;
}
