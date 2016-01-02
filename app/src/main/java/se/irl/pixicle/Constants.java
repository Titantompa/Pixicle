package se.irl.pixicle;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class Constants {
    public static final String ARG_PIXICLE_IDENTITY = "se.irl.Pixicle.PIXICLE_IDENTITY"; // Internal id, -1 is used for new pixicles
    public static final String ARG_DEVICE_IDENTITY = "se.irl.Pixicle.DEVICE_IDENTITY"; // Global device identifier
    public static final String ARG_ACCESS_TOKEN = "se.irl.Pixicle.ACCESS_TOKEN"; // Access token for cloud operations
    public static final String ARG_CONFIGURATION = "se.irl.Pixicle.CONFIGURATION";

    public static final int ARRAGEMENT_ARRAY = 1;
    public static final int ARRAGEMENT_COIL = 2;
    public static final int ARRAGEMENT_SPHERICAL_COIL = 1;

    public static final SimpleDateFormat StdDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
}
