#ifndef _PLUGIN_H
#define _PLUGIN_H

#include "neopixelstrip.h"

class Plugin
{
    protected:
        NeoPixelStrip * Strip;

    public:
        Plugin(NeoPixelStrip * strip);
        virtual ~Plugin();

        virtual void Iterate(float deltaTime /* millis */) = 0;

        virtual void SetParameters(String & parameters);
};

/////////////////////////////////////
// SPECIALIZATIONS
//

class FirePlugin : public Plugin
{
    private:
        float _dissipation;
        float _combustion;

        float * _flames;

        uint32_t _palette[256];

    public:
        FirePlugin(NeoPixelStrip * strip, String & parameters);
        ~FirePlugin();

        virtual void Iterate(float deltaTime /* millis */);

        // parameters: "<combustion>,<dissipation>"
        // combustion = 0-255 amount of new sparks
        // dissipation = 0-255 amount of dissipation
        virtual void SetParameters(String & parameters);
};

class DashPlugin : public Plugin
{
    private:
        float _currentPosition;
        float _speed;
        uint32_t _color;
        int _width;

    public:
        DashPlugin(NeoPixelStrip * strip, String & parameters);

        virtual void Iterate(float deltaTime /* millis */);

        // parameters: "<w>,<s>,<r>,<g>,<b>"
        // w = width of each bar (in neopixels)
        // s = int millis per shift
        // r = uint8_t red component
        // g = uint8_t green component
        // b = uint8_t blue component
        virtual void SetParameters(String & parameters);
};

class OffPlugin : public Plugin
{
    public:
        OffPlugin(NeoPixelStrip * strip);

        virtual void Iterate(float deltaTime /* millis */);
};

class RainbowPlugin : public Plugin
{
    private:
        float _currentPosition;
        float _speed;

    public:
        RainbowPlugin(NeoPixelStrip * strip, String & parameters);

        virtual void Iterate(float deltaTime /* millis */);

        // parameters: "<s>"
        // s = int millis per shift
        virtual void SetParameters(String & parameters);

    private:
        static uint32_t Wheel(byte WheelPos);
};

class SolidColorPlugin : public Plugin
{
    private:
        uint32_t _color;

    public:
        SolidColorPlugin(NeoPixelStrip * strip, String & parameters);

        virtual void Iterate(float deltaTime /* millis */);

        // parameters: "<r>,<g>,<b>"
        // r = uint8_t red component
        // g = uint8_t green component
        // b = uint8_t blue component
        virtual void SetParameters(String & parameters);
};

class JugglePlugin : public Plugin
{
    private:
        uint32_t _color;
        float _interval;

        float _intervalStart;

        float _currentStartMillis;
        float _currentDuration;

        bool _sparkle = false;

    public:
        JugglePlugin(NeoPixelStrip * strip, String & parameters);

        virtual void Iterate(float deltaTime /* millis */);

        // parameters: "<interval>,<r>,<g>,<b>,<sparkle>"
        // interval = interval between projectiles in ms
        // r = uint8_t red component
        // g = uint8_t green component
        // b = uint8_t blue component
        // sparkle = bit (0/1) if projectile should sparkle
        virtual void SetParameters(String & parameters);
};

class ProgressPlugin : public Plugin
{
    private:
        uint32_t _fromColor;
        uint32_t _toColor;
        float _percentage;

    public:
        ProgressPlugin(NeoPixelStrip * strip, String & parameters);

        virtual void Iterate(float deltaTime /* millis */);

        // parameters: "<percentage>,<fr>,<fg>,<fb>,<tr>,<tg>,<tb>"
        // percentage = percentage as 100 * percentage e g 50% = 5000
        // fr = uint8_t red component background
        // fg = uint8_t green component background
        // fb = uint8_t blue component background
        // tr = uint8_t red component foreground
        // tg = uint8_t green component foreground
        // tb = uint8_t blue component foreground
        virtual void SetParameters(String & parameters);
};

class GradientPlugin : public Plugin
{
    private:
        uint32_t * _pixels;

    public:
        GradientPlugin(NeoPixelStrip * strip, String & parameters);
        ~GradientPlugin();

        virtual void Iterate(float deltaTime /* millis */);

        // parameters: "<style>,<fr>,<fg>,<fb>,<tr>,<tg>,<tb>"
        // style = 'A' - Arcsin, 'S' - Sin or 'L' - Linear
        // fr = uint8_t red component background
        // fg = uint8_t green component background
        // fb = uint8_t blue component background
        // tr = uint8_t red component foreground
        // tg = uint8_t green component foreground
        // tb = uint8_t blue component foreground
        virtual void SetParameters(String & parameters);
};

#endif // #ifndef _PLUGIN_H
