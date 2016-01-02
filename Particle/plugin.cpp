#include "plugin.h"
#include "math.h"

#define PI 3.1415926535897932384626433832795

/////////////////////////////////////
// PLUGIN
//

Plugin::Plugin(NeoPixelStrip * strip)
{
    Strip = strip;
}

Plugin::~Plugin()
{
}

void Plugin::SetParameters(String & parameters)
{
}

/////////////////////////////////////
// OFFPLUGIN
//

OffPlugin::OffPlugin(NeoPixelStrip * strip)
:Plugin(strip)
{
}

void OffPlugin::Iterate(float deltaTime /* millis */)
{
    // Blank out the neopixels
    Strip->Clear();
    Strip->Show();

    // This only surfaces for air every second
    if(deltaTime < 1000.0)
    {
        delay(1000.0-deltaTime);
    }
}

/////////////////////////////////////
// RAINBOWPLUGIN
//

RainbowPlugin::RainbowPlugin(NeoPixelStrip * strip, String & parameters)
:Plugin(strip)
{
    _currentPosition = 0.0;
    SetParameters(parameters);
}

void RainbowPlugin::SetParameters(String & parameters)
{
    _speed = parameters.toInt();
}

void RainbowPlugin::Iterate(float deltaTime /* millis */)
{
    // Don't go faster than the speed
    if(deltaTime < _speed)
    {
        delay(_speed-deltaTime);
        deltaTime += _speed-deltaTime;
    }

    _currentPosition = fmod(_currentPosition+(deltaTime/_speed), 255);

    int j = round(_currentPosition);

    for(int i=0; i < Strip->Length; i++)
    {
        Strip->SetPixelColor(i, Wheel((i+j) & 255));
    }

    Strip->Show();
}

 // Input a value 0 to 255 to get a color value.
// The colours are a transition r - g - b - back to r.
uint32_t RainbowPlugin::Wheel(byte WheelPos)
{
    if(WheelPos < 85)
    {
        return Adafruit_NeoPixel::Color(WheelPos * 3, 255 - WheelPos * 3, 0);
    }
    else if(WheelPos < 170)
    {
        WheelPos -= 85;
        return Adafruit_NeoPixel::Color(255 - WheelPos * 3, 0, WheelPos * 3);
    }
    else
    {
        WheelPos -= 170;
        return Adafruit_NeoPixel::Color(0, WheelPos * 3, 255 - WheelPos * 3);
    }
}

/////////////////////////////////////
// SOLIDCOLORPLUGIN
//

SolidColorPlugin::SolidColorPlugin(NeoPixelStrip * strip, String & parameters)
:Plugin(strip)
{
    SetParameters(parameters);
}

void SolidColorPlugin::SetParameters(String & parameters)
{
    uint8_t red = parameters.toInt();
    int i = parameters.indexOf(',', 0);
    uint8_t green = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    uint8_t blue = parameters.substring(i+1).toInt();

    _color = Strip->Color(red, green, blue);
}

void SolidColorPlugin::Iterate(float deltaTime /* millis */)
{
    // Blank out the neopixels
    Strip->Fill(_color);
    Strip->Show();

    // This only surfaces for air every second
    if(deltaTime < 1000.0)
    {
        delay(1000.0-deltaTime);
    }
}


/////////////////////////////////////
// PROGRESSPLUGIN
//

ProgressPlugin::ProgressPlugin(NeoPixelStrip * strip, String & parameters)
:Plugin(strip)
{
    SetParameters(parameters);
}

void ProgressPlugin::SetParameters(String & parameters)
{

    int percentage = parameters.toInt();

    _percentage = (float) percentage / 10000.0;

    int i = parameters.indexOf(',', 0);
    uint8_t red = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    uint8_t green = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    uint8_t blue = parameters.substring(i+1).toInt();
    _fromColor = Strip->Color(red, green, blue);

    i = parameters.indexOf(',', i+1);
    red = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    green = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    blue = parameters.substring(i+1).toInt();
    _toColor = Strip->Color(red, green, blue);
}

void ProgressPlugin::Iterate(float deltaTime /* millis */)
{
    int progress = ((float) Strip->Length * _percentage);

    for(int i = 0; i < Strip->Length; i++)
    {
        Strip->SetPixelColor(i, i <= progress ? _toColor : _fromColor);
    }

    Strip->Show();

    if(deltaTime < 1000.0)
    {
        delay(1000.0-deltaTime);
    }
}

/////////////////////////////////////
// DASHPLUGIN
//

DashPlugin::DashPlugin(NeoPixelStrip * strip, String & parameters)
:Plugin(strip)
{
    _currentPosition = 0.0;
    SetParameters(parameters);
}

void DashPlugin::SetParameters(String & parameters)
{
    _width = parameters.toInt();

    int i = parameters.indexOf(',', 0);
    _speed = parameters.substring(i+1).toInt();

    i = parameters.indexOf(',', i+1);
    uint8_t red = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    uint8_t green = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    uint8_t blue = parameters.substring(i+1).toInt();
    _color = Strip->Color(red, green, blue);
}

void DashPlugin::Iterate(float deltaTime /* millis */)
{
    // Don't go faster than the speed
    if(deltaTime < _speed)
    {
        delay(_speed-deltaTime);
        deltaTime += _speed-deltaTime;
    }

    _currentPosition = fmod(_currentPosition+(deltaTime/_speed), _width*2);

    int j = round(_currentPosition);

    uint32_t white = Adafruit_NeoPixel::Color(255, 255, 255);

    for(int i=0; i < Strip->Length; i++)
    {
        if(((i+j)%(_width*2)) <= _width)
            Strip->SetPixelColor(i, _color);
        else
            Strip->SetPixelColor(i, white);
    }

    Strip->Show();
}


/////////////////////////////////////
// JUGGLEPLUGIN
//

#define MAX_DURATION 2222

JugglePlugin::JugglePlugin(NeoPixelStrip * strip, String & parameters)
:Plugin(strip)
{
    _currentStartMillis = -1.0;
    _intervalStart = 1.0;
    SetParameters(parameters);
}

void JugglePlugin::SetParameters(String & parameters)
{
    _interval = parameters.toInt();

    int i = parameters.indexOf(',', 0);
    uint8_t red = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    uint8_t green = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    uint8_t blue = parameters.substring(i+1).toInt();
    _color = Strip->Color(red, green, blue);
    i = parameters.indexOf(',', i+1);
    _sparkle = parameters.substring(i+1).toInt() == 1;
}

void JugglePlugin::Iterate(float deltaTime /* millis */)
{
    float currentTime = ((float)micros())/1000;

    if(_intervalStart > 0.0)
    {
        if(currentTime - _intervalStart > _interval)
        {
            // Create a new projectile!
            _currentDuration = (float) random(MAX_DURATION/3, MAX_DURATION);
            _currentStartMillis = currentTime-1; // To prevent div by zero
            _intervalStart = -1.0;
        }
        else
        {
            return;
        }
    }

    float elapsed = currentTime - _currentStartMillis;

    // Calculate height of projectile
    float radius = _currentDuration/2;
    float adjacent = radius-elapsed;

    int height = -1.0;
    if(elapsed <= _currentDuration)
    {
      float elevation = sqrt((radius*radius)-(adjacent*adjacent))/radius;
      height = (elevation*(_currentDuration/MAX_DURATION)*Strip->Length);
    }

    uint32_t black = Adafruit_NeoPixel::Color(0, 0, 0);
    uint32_t color = _color;

    if(_sparkle)
    {
        float rotor1 = sin(currentTime*0.007);
        float rotor2 = sin(currentTime*0.0077);
        float rotor3 = sin(currentTime*0.017);
        float rotor4 = sin(currentTime*0.023);

        float factor = /* rotor1 * */rotor2 * rotor3 * rotor4;

        float red = (color&0xff0000)>>16;
        float green = (color&0xff00)>>8;
        float blue = color&0xff;

        red = (red*0.5)+(red*0.5*factor);
        green = (green*0.5)+(green*0.5*factor);
        blue = (blue*0.5)+(blue*0.5*factor);

        color = ((int)red<<16)|((int)green<<8)|((int)blue);
    }

    if(height < 0.0)
    {
        _intervalStart = currentTime;
        Strip->Fill(black);
    }
    else
    {
        for(int i=0; i < Strip->Length; i++)
        {
            Strip->SetPixelColor(i, i == height ? color : black);
            if(i+1 == height)
                Strip->SetPixelColor(i, color);
            if(i+2 == height)
                Strip->SetPixelColor(i, color);
        }
    }

    Strip->Show();
}
