#include "neopixelstrip.h"

NeoPixelStrip::NeoPixelStrip(Adafruit_NeoPixel * strip)
{
    _strip = strip;
    Length = strip->numPixels();
}

void NeoPixelStrip::Clear()
{
    Fill(0);
}

void NeoPixelStrip::Fill(uint32_t color)
{
    for(int i = 0; i < _strip->numPixels(); i++)
        _strip->setPixelColor(i, color);

    _strip->show();
}
