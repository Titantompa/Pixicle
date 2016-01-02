#ifndef _NEOPIXELSTRIP_H
#define _NEOPIXELSTRIP_H

#include "neopixel.h"

class NeoPixelStrip
{
    private:
        Adafruit_NeoPixel * _strip;

    public:
        unsigned short Length;

        NeoPixelStrip(Adafruit_NeoPixel * strip);

        // Blank out all the pixels
        void Clear();
        void Fill(uint32_t color);

        inline uint32_t Color(uint8_t r, uint8_t g, uint8_t b) { return _strip->Color(r,g,b); }
        inline void SetPixelColor(uint16_t n, uint8_t r, uint8_t g, uint8_t b) { _strip->setPixelColor(n, r, g, b); }
        inline void SetPixelColor(uint16_t n, uint32_t c) { _strip->setPixelColor(n, c); }
        inline void SetBrightness(uint8_t l)  { _strip->setBrightness(l); }
        inline void Begin() { _strip->begin(); }
        inline void Show() { _strip->show(); }
};

#endif // #ifndef _NEOPIXELSTRIP_H
