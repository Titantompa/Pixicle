package se.irl.pixicle;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class Constants {
    public static final String ARG_DEVICE_IDENTITY = "se.irl.Pixicle.DEVICE_IDENTITY"; // -1 means new pixicle

    public static final int ARRAGEMENT_ARRAY = 1;
    public static final int ARRAGEMENT_COIL = 2;
    public static final int ARRAGEMENT_SPHERICAL_COIL = 1;

    public static final SimpleDateFormat StdDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
}
