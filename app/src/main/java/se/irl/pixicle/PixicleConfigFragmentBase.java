package se.irl.pixicle;

import android.support.v4.app.Fragment;

public abstract class PixicleConfigFragmentBase extends Fragment {
    public abstract String getPixicleConfigArgs();
    public abstract boolean setPixicleConfigArgs(String config);
    public abstract String getPluginName();
}
