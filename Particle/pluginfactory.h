#ifndef _PLUGINFACTORY_H
#define _PLUGINFACTORY_H

#include "plugin.h"

class PluginFactory
{
    public:
    static Plugin * CreatePlugin(String & config, NeoPixelStrip * strip);
};

#endif // _PLUGINFACTORY_H
