#include "twinkleplugin.h"
#include "pluginfactory.h"
#include "neopixelstrip.h"
#include "application.h"
#include "neopixel.h"
#include "plugin.h"

// EEPROM token "PXCL" to verify that what is in there is written by us
#define PIXICLE_TOKEN 0x5058434C

#define EEPROM_TOKEN_INDEX 0
#define EEPROM_PIXELS_INDEX 4
#define EEPROM_HWFLAGS_INDEX 6
#define EEPROM_CONFIG_INDEX 8

// IMPORTANT: Set pixel COUNT, PIN and TYPE
#define PIXEL_PIN D0
#define PIXEL_COUNT 66 // LYSRÖRET ÄR 66 PIXELS, DEN LÖSA RADDAN ÄR 199 PIXELS LÅNG, VARDAGSRUMSGARDINEN 338, LYNXRÖRET ÄR 84
#define PIXEL_TYPE WS2812B

NeoPixelStrip * strip = NULL;
Plugin * CurrentPlugin = NULL;
String * CurrentConfig = NULL;
char ConfigBuffer[200];
char ScheduleDebug[48];
unsigned long Configuration_TimeStamp = millis();
unsigned long ActiveConfig_TimeStamp = ~0;
int ChatBack = 0;
int AverageMicros = 0;
int LoopCnt = 0;

unsigned long LastMicros = 0;

// TODO: This is different between the CORE and PHOTON
#if PLATFORM_ID == 0 // Core (0)
#define MICROS_ROLLOVER ((unsigned long)59652323)
#elif (PLATFORM_ID == 6) // Photon (6)
#define MICROS_ROLLOVER ((unsigned long)35791394)
#else
#error "*** Unknown PLATFORM_ID, only Core (0) or Photon (6) is supported ***"
#endif

static unsigned long us_delta(unsigned long old_us, unsigned long new_us)
{
    new_us -= old_us;
    if((long)new_us < 0)
        new_us += MICROS_ROLLOVER;
    return new_us;
}

class CmdParms
{
    public:
        uint32_t State;
        uint8_t Priority;
        uint32_t Duration;
        uint8_t Command;
        String CommandParameters;
};

int ApplyConfig(String config)
{
    Configuration_TimeStamp = millis();

    UpdateConfigInfo(config);

    // TODO: Store in EEPROM
    char buffer[90];
    CurrentConfig->toCharArray(buffer, 90);
    EEPROM.put(EEPROM_CONFIG_INDEX, buffer);

    return(0);
}

long ScheduledMillis;
String * ScheduledConfiguration = NULL;

int ScheduleCfg(String parameters)
{
  memcpy(ScheduleDebug, "hejsan", 7);

    if(ScheduledConfiguration != NULL)
    {
      delete ScheduledConfiguration;
      ScheduledConfiguration = NULL;
    }

    memcpy(ScheduleDebug, "svejsan", 8);

    ScheduledMillis = parameters.toInt();

    int i = parameters.indexOf(':', 0);
    ScheduledConfiguration = new String(parameters.substring(i+1));

    String debug = String::format("Schedule at %d, time now at %d", ScheduledMillis, Time.now());

    debug.toCharArray(ScheduleDebug, 96);
}

int SetPixelCnt(String param)
{
    uint16_t pixelCnt = param.toInt();

    EEPROM.put(EEPROM_PIXELS_INDEX, pixelCnt);

    System.reset();

    return(0);
}

void UpdateConfigInfo(String & config)
{
    if(CurrentConfig != NULL)
    {
        delete CurrentConfig;
    }

    CurrentConfig = new String(config);
    CurrentConfig->trim();
    CurrentConfig->toCharArray(ConfigBuffer, 200);
}

int Ping(String parm)
{
    ChatBack++;

    return(1);
}

NeoPixelStrip* bootstrap()
{
    uint32_t token;
    uint16_t pixels = PIXEL_COUNT;
    char buffer[90];

    EEPROM.get(EEPROM_TOKEN_INDEX, token);

    if(token != PIXICLE_TOKEN)
    {
        token = PIXICLE_TOKEN;
        EEPROM.put(EEPROM_TOKEN_INDEX, token);
        EEPROM.put(EEPROM_PIXELS_INDEX, pixels);
        EEPROM.put(EEPROM_CONFIG_INDEX, "Progress:3.5,255,0,0,0,255,0");
    }

    //
    // Load the stored config
    //
    EEPROM.get(EEPROM_CONFIG_INDEX, buffer);
    ApplyConfig(String(buffer));
    EEPROM.get(EEPROM_PIXELS_INDEX, pixels);

    return new NeoPixelStrip(new Adafruit_NeoPixel(pixels, PIXEL_PIN, PIXEL_TYPE));
}

void setup()
{
    //
    // set up the functions and variables
    //
    Particle.function("ApplyConfig", ApplyConfig);
    Particle.function("ScheduleCfg", ScheduleCfg);
    Particle.function("SetPixelCnt", SetPixelCnt);
    Particle.function("Ping", Ping);
    Particle.variable("CurrentCfg", ConfigBuffer, STRING);
    Particle.variable("AvgMicros", &AverageMicros, INT);
    Particle.variable("ChatBack", &ChatBack, INT);
    Particle.variable("LoopCnt", &LoopCnt, INT);
    Particle.variable("Schedule", ScheduleDebug, STRING);

    //
    // Start up the NeoPixel strip and turn everything off
    //
    strip = bootstrap();

    strip->Begin();
    strip->Show();

    //
    // Initialize the timer
    //
    LastMicros = micros();
}

void loop()
{
    unsigned long microsSinceLastCall = us_delta(LastMicros, micros());
    LastMicros = micros();

    if(AverageMicros == 0)
        AverageMicros = microsSinceLastCall;

    AverageMicros = ((AverageMicros * 10)+microsSinceLastCall)/11;

    if(ScheduledConfiguration != NULL)
    {
      if(ScheduledMillis < Time.now())
      {
        String * configuration = ScheduledConfiguration;
        ScheduledConfiguration = NULL;
        ApplyConfig(*configuration);
        delete configuration;
      }
    }

    if(ActiveConfig_TimeStamp != Configuration_TimeStamp)
    {
        // Reload config
        if(CurrentPlugin != NULL)
        {
            delete CurrentPlugin;
            CurrentPlugin = NULL;
        }

        // Parse function from config
        CurrentPlugin = PluginFactory::CreatePlugin(*CurrentConfig, strip);

        ActiveConfig_TimeStamp = Configuration_TimeStamp;
    }

    LoopCnt++;

    if(CurrentPlugin == NULL)
        CurrentPlugin = new OffPlugin(strip);

    CurrentPlugin->Iterate(((float)microsSinceLastCall)/1000.0);
}
