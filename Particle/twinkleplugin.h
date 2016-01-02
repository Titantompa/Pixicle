#ifndef _TWINKLEPLUGIN_H
#define _TWINKLEPLUGIN_H

#include "plugin.h"
#include "neopixelstrip.h"

struct TwinkleEntity
{
    public:
        int Position;
        float StartMillis;
        float Seed;
        float Duration;
};

class TwinklePlugin : public Plugin
{
    private:
        uint32_t _color;
        uint32_t _count;

        float _duration;

        bool _sparkle = false;

        TwinkleEntity * _twinkles;

    public:
        TwinklePlugin(NeoPixelStrip * strip, String & parameters);

        virtual void Iterate(float deltaTime /* millis */);

        // parameters: "<count>,<duration>,<r>,<g>,<b>,<sparkle>"
        // count = the number of twinkles
        // duration = the duration in ms of a twinkle
        // r = uint8_t red component
        // g = uint8_t green component
        // b = uint8_t blue component
        // sparkle = bit (0/1) if projectile should sparkle
        virtual void SetParameters(String & parameters);
};

#endif // _TWINKLEPLUGIN_H
