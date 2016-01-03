#include "pluginfactory.h"
#include "twinkleplugin.h"

Plugin * PluginFactory::CreatePlugin(String & config, NeoPixelStrip * strip)
{
    if(config.startsWith("Rainbow"))
    {
        String parameters(config.substring(8));
        return new RainbowPlugin(strip, parameters);
    }
    else if(config.startsWith("Solid"))
    {
        String parameters(config.substring(6));
        return new SolidColorPlugin(strip, parameters);
    }
    else if(config.startsWith("Dash"))
    {
        String parameters(config.substring(5));
        return new DashPlugin(strip, parameters);
    }
    else if(config.startsWith("Progress"))
    {
        String parameters(config.substring(9));
        return new ProgressPlugin(strip, parameters);
    }
    else if(config.startsWith("Juggle"))
    {
        String parameters(config.substring(7));
        return new JugglePlugin(strip, parameters);
    }
    else if(config.startsWith("Twinkle"))
    {
        String parameters(config.substring(8));
        return new TwinklePlugin(strip, parameters);
    }
    else if(config.startsWith("Fire"))
    {
        String parameters(config.substring(5));
        return new FirePlugin(strip, parameters);
    }
    else if(config.startsWith("Off"))
    {
        return new OffPlugin(strip);
    }
    else
    {
        return new OffPlugin(strip);
    }
}
