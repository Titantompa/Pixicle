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

        ColorPicker colorPicker = (ColorPicker) view.findViewById(R.id.dash_picker);
        ValueBar valueBar = (ValueBar) view.findViewById(R.id.dash_value_bar);
        colorPicker.addValueBar(valueBar);
        SaturationBar saturationBar = (SaturationBar) view.findViewById(R.id.dash_saturation_bar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.setShowOldCenterColor(false);

        return view;
    }

    @Override
    public String getPixicleConfigArgs() {
        View view = getView();
        EditText lengthEdit = (EditText) view.findViewById(R.id.dash_length);
        EditText speedEdit = (EditText) view.findViewById(R.id.dash_speed);
        ColorPicker colorPicker = (ColorPicker) view.findViewById(R.id.dash_picker);

        int color = colorPicker.getColor();
        Integer red = (color & 0x00ff0000)>>16;
        Integer green = (color & 0x0000ff00)>>8;
        Integer blue = color & 0x000000ff;

        return lengthEdit.getText() + "," +
               speedEdit.getText() + "," +
               red.toString() + "," +
               green.toString() + "," +
               blue.toString();
    }

    @Override
    public boolean setPixicleConfigArgs(String config) {
        return false;
    }

    @Override
    public String getPluginName() { return "Dash"; }
}
