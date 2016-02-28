package se.irl.pixicle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashFragment extends PixicleConfigFragmentBase {
    public DashFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DashFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashFragment newInstance() {
        DashFragment fragment = new DashFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dash, container, false);

        ColorPicker foregroundColorPicker = (ColorPicker) view.findViewById(R.id.dash_foreground_picker);
        ValueBar foregroundValueBar = (ValueBar) view.findViewById(R.id.dash_foreground_value_bar);
        foregroundColorPicker.addValueBar(foregroundValueBar);
        SaturationBar foregroundSaturationBar = (SaturationBar) view.findViewById(R.id.dash_foreground_saturation_bar);
        foregroundColorPicker.addSaturationBar(foregroundSaturationBar);
        foregroundColorPicker.setShowOldCenterColor(false);

        ColorPicker backgroundColorPicker = (ColorPicker) view.findViewById(R.id.dash_background_picker);
        ValueBar backgroundValueBar = (ValueBar) view.findViewById(R.id.dash_background_value_bar);
        backgroundColorPicker.addValueBar(backgroundValueBar);
        SaturationBar backgroundSaturationBar = (SaturationBar) view.findViewById(R.id.dash_background_saturation_bar);
        backgroundColorPicker.addSaturationBar(backgroundSaturationBar);
        backgroundColorPicker.setShowOldCenterColor(false);

        return view;
    }

    @Override
    public String getPixicleConfigArgs() {
        View view = getView();
        EditText lengthEdit = (EditText) view.findViewById(R.id.dash_length);
        EditText speedEdit = (EditText) view.findViewById(R.id.dash_speed);
        ColorPicker foregroundColorPicker = (ColorPicker) view.findViewById(R.id.dash_foreground_picker);
        ColorPicker backgroundColorPicker = (ColorPicker) view.findViewById(R.id.dash_background_picker);

        int foregroundColor = foregroundColorPicker.getColor();
        Integer foregroundRed = (foregroundColor & 0x00ff0000) >> 16;
        Integer foregroundGreen = (foregroundColor & 0x0000ff00) >> 8;
        Integer foregroundBlue = foregroundColor & 0x000000ff;

        int backgroundColor = backgroundColorPicker.getColor();
        Integer backgroundRed = (backgroundColor & 0x00ff0000) >> 16;
        Integer backgroundGreen = (backgroundColor & 0x0000ff00) >> 8;
        Integer backgroundBlue = backgroundColor & 0x000000ff;

        return lengthEdit.getText() + "," +
                speedEdit.getText() + "," +
                foregroundRed.toString() + "," +
                foregroundGreen.toString() + "," +
                foregroundBlue.toString() + "," +
                backgroundRed.toString() + "," +
                backgroundGreen.toString() + "," +
                backgroundBlue.toString();
    }

    @Override
    public boolean setPixicleConfigArgs(String config) {
        return false;
    }

    @Override
    public String getPluginName() { return "Dash"; }
}
