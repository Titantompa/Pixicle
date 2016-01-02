#include "math.h"
#include "twinkleplugin.h"

#define PI 3.1415926535897932384626433832795


/////////////////////////////////////
// TWINKLEPLUGIN
//

TwinklePlugin::TwinklePlugin(NeoPixelStrip * strip, String & parameters)
:Plugin(strip)
{
    _twinkles = NULL;
    SetParameters(parameters);
}

void TwinklePlugin::SetParameters(String & parameters)
{
    // parameters: "<count>,<duration>,<r>,<g>,<b>,<sparkle>"

    _count = parameters.toInt();
    if(_count == 0)
        _count = 1;
    if(_count > 50)
        _count = 50;
    if(_count > Strip->Length)
        _count = Strip->Length;

    int i = parameters.indexOf(',', 0);
    _duration = parameters.substring(i+1).toInt();

    i = parameters.indexOf(',', i+1);
    uint8_t red = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    uint8_t green = parameters.substring(i+1).toInt();
    i = parameters.indexOf(',', i+1);
    uint8_t blue = parameters.substring(i+1).toInt();
    _color = Strip->Color(red, green, blue);

    i = parameters.indexOf(',', i+1);
    _sparkle = parameters.substring(i+1).toInt() == 1;

    // Allocate buffer
    if(_twinkles != NULL)
        delete _twinkles;
    _twinkles = new TwinkleEntity[_count];

    uint32_t startMillis = millis();

    for(int i = 0; i < _count; i++)
    {
        _twinkles[i].StartMillis = startMillis;
        _twinkles[i].Duration = i*(_duration/_count);
        _twinkles[i].Position = random(0, Strip->Length-1);
        _twinkles[i].Seed = 1;
    }
}

void TwinklePlugin::Iterate(float deltaTime /* millis */)
{
    float currentTime = (float) millis();

    uint32_t black = Adafruit_NeoPixel::Color(0, 0, 0);

    // Blank the strip
    for(int i = 0; i < Strip->Length; i++)
        Strip->SetPixelColor(i, black);

    // Iterate over all the twinkles and advance them
    for(int i = 0; i < _count; i++)
    {
        float age = currentTime - _twinkles[i].StartMillis;

        if(age > _twinkles[i].Duration)
        {
            // Twinkle is bust, create a new one!
            _twinkles[i].StartMillis = currentTime;
            _twinkles[i].Seed = random(333,6666); // So that they don't all blink synchronized
            _twinkles[i].Duration = (_duration*0.7)+(_duration*0.3*random(0,2))+0.00001; // To give diversity

            // TODO: Find a unique position! :-)
            _twinkles[i].Position = random(0, Strip->Length-1);

            age = 0.0001;
        }

        float red = (_color&0xff0000)>>16;
        float green = (_color&0xff00)>>8;
        float blue = _color&0xff;

        if(_sparkle)
        {
            float seededTime = currentTime + _twinkles[i].Seed;

            float rotor1 = sin(seededTime*0.0077);
            float rotor2 = sin(seededTime*0.017);
            float rotor3 = sin(seededTime*(_twinkles[i].Seed > 499 ? 0.023 : 0.019));

            float factor = rotor1 * rotor2 * rotor3;

            red = (red*0.75)+(red*0.25*factor);
            green = (green*0.75)+(green*0.25*factor);
            blue = (blue*0.75)+(blue*0.25*factor);
        }

        // Scale color to fit age
        int pivotAge = _twinkles[i].Duration*0.2;
        float ageFactor = 0.0;

        if(age < pivotAge)
        {
            // Attack
            ageFactor = age/pivotAge;
        }
        else
        {
            // Decay
            ageFactor = 1-((age-pivotAge)/(_twinkles[i].Duration-pivotAge));
        }

        red *= ageFactor;
        green *= ageFactor;
        blue *= ageFactor;

        uint32_t color = Adafruit_NeoPixel::Color((int)red&0xFF, (int)green&0xFF, (int)blue&0xFF);

        Strip->SetPixelColor(_twinkles[i].Position, color);
    }

    Strip->Show();
}
