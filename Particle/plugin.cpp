#include "plugin.h"
#include "math.h"

#define PI      3.1415926535897932384626433832795
#define HALF_PI 1.5707963267948966192313216916398

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
    delay(1000.0);
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
    _currentPosition = fmod(_currentPosition+(deltaTime/_speed), 255);

    int j = round(_currentPosition);

    for(int i=0; i < Strip->Length; i++)
    {
        Strip->SetPixelColor(i, Wheel((i+j) & 255));
    }

    Strip->Show();

    // Don't go faster than the speed
    delay(_speed);
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
    delay(1000.0);
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
        Strip->SetPixelColor(i, i < progress ? _toColor : _fromColor);
    }

    Strip->Show();

    delay(1000.0);
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
    _foregroundColor = Strip->Color(red, green, blue);

    i = parameters.indexOf(',', i+1);
    red = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    green = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    blue = parameters.substring(i+1).toInt();
    _backgroundColor = Strip->Color(red, green, blue);
}

void DashPlugin::Iterate(float deltaTime /* millis */)
{
    _currentPosition = fmod(_currentPosition+(deltaTime/_speed), _width*2);

    int j = round(_currentPosition);

    for(int i=0; i < Strip->Length; i++)
    {
        if(((i+j)%(_width*2)) <= _width)
            Strip->SetPixelColor(i, _foregroundColor);
        else
            Strip->SetPixelColor(i, _backgroundColor);
    }

    Strip->Show();

    // Don't go faster than the speed
    delay(_speed);
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
    float currentTime = millis();

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

/////////////////////////////////////
// FIREPLUGIN
//

FirePlugin::FirePlugin(NeoPixelStrip * strip, String & parameters)
:Plugin(strip)
{
    SetParameters(parameters);
    _flames = new float[Strip->Length];
    for(int i = 0; i < Strip->Length; i++)
      _flames[i] = 0.0;

    for(int i = 0; i < 256; i++)
    {
        float base = (sqrt(pow(256.0f,2.0f)-pow(256.0f-(float)i,2.0f)));
        float accent = (cos(((float)i*PI/255.0f)+PI)+1.0f)/2 * 255.0f;
        float saturation = (tan((float)i*1.53f/255.0f)/25.0f) * 255.0f;

        // These are different colour modes for the flames, to be configurable in a future version
#if 0
        // Blue is the base with purple accent
        _palette[i] = Adafruit_NeoPixel::Color((int)accent, (int)saturation, (int)base);
#else
#if 1
        // Red is the base with yellow accent
        _palette[i] = Adafruit_NeoPixel::Color((int)base, (int)accent, (int)saturation);
#else
        // Green is the base with yellow accent
        _palette[i] = Adafruit_NeoPixel::Color((int)accent, (int)base, (int)saturation);
#endif
#endif
    }
}

FirePlugin::~FirePlugin()
{
  delete _flames;
}

void FirePlugin::SetParameters(String & parameters)
{
    _combustion = parameters.toInt();
    if(_combustion > 255)
      _combustion = 255;

    int i = parameters.indexOf(',', 0);
    _dissipation = parameters.substring(i+1).toInt()%256;
    if(_dissipation > 255)
      _dissipation = 255;
}

void FirePlugin::Iterate(float deltaTime /* millis */)
{
  // Dissipate heat away from the flame
  for(int i = 0; i < Strip->Length; i++)
  {
    float dissipation = ((float)random(0,768)+_dissipation)/(float)Strip->Length; // 768-1024
    float residue = _flames[i]-dissipation;
    _flames[i] = residue < 0.0 ? 0 : residue;
  }

  // Move flame upwards
  for(int i = Strip->Length-1; i > 1; i--)
  {
    _flames[i] = (_flames[i - 1] + (_flames[i - 2]*2)) / 3;
  }
  _flames[1] = _flames[0];

  // Burn more at bottom
  int height = random(0,Strip->Length/8);
  int heat = _flames[height] + random(0,128)+_combustion;
  _flames[height] = (heat > 255.0) ? 255.0 : heat;

  for(int i = 0; i < Strip->Length; i++)
    Strip->SetPixelColor(i, _palette[(int)_flames[i]]);

  Strip->Show();

  delay(10); // should depend on distance between pixels
}

/////////////////////////////////////
// GRADIENTPLUGIN
//

GradientPlugin::GradientPlugin(NeoPixelStrip * strip, String & parameters)
:Plugin(strip)
{
    _pixels = new uint32_t[strip->Length];

    SetParameters(parameters);
}

GradientPlugin::~GradientPlugin()
{
    delete _pixels;
}

void GradientPlugin::SetParameters(String & parameters)
{
    char style = parameters[0];

    float red[3];
    float green[3];
    float blue[3];

    int i = parameters.indexOf(',', 0);
    red[0] = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    green[0] = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    blue[0] = parameters.substring(i+1).toInt();

    i = parameters.indexOf(',', i+1);
    red[1] = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    green[1] = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    blue[1] = parameters.substring(i+1).toInt();

    int length = Strip->Length-1;
    float ratio;
    float intermidiate;
    float redDist = red[0]-red[1];
    float greenDist = green[0]-green[1];
    float blueDist = blue[0]-blue[1];

    // Calculate the values of all the pixels in advance
    for(int i = 0; i <= length; i++)
    {
      ratio = (float)i/((float)length);
      switch(style)
      {
        case 'L': // Linear
          ;
          break;
        case 'A': // ArcSine
          // =(ARCSIN((($A1/37)-0,5)    *2)+  (PI()/2))       /       PI()
          ratio = (asinf((ratio*2)-1)+HALF_PI)/PI;
          break;
        case 'S': // Sine
          // =(SIN((($A1/37)-0,5)*PI())+1)/2
          ratio = (sinf((ratio-0.5)*PI)+1)/2;
          break;
      }

      red[2] = red[1]+(redDist*ratio);
      /*if(red[2] < 0)
        red[2] = 0;
        if(red[2] > 255)
          red[2] = 255;*/
      green[2] = green[1]+(greenDist*ratio);
      /*if(green[2] < 0)
        green[2] = 0;
        if(green[2] > 255)
          green[2] = 255;*/
      blue[2] = blue[1]+(blueDist*ratio);
      /*if(blue[2] < 0)
        blue[2] = 0;
        if(blue[2] > 255)
          blue[2] = 255;*/

      _pixels[i] = Adafruit_NeoPixel::Color(red[2], green[2], blue[2]);
    }
}

void GradientPlugin::Iterate(float deltaTime /* millis */)
{
    for(int i = 0; i < Strip->Length; i++)
    {
        Strip->SetPixelColor(i, _pixels[i]);
    }

    Strip->Show();

    delay(1000.0);
}
