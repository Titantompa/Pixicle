package se.irl.pixicle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SolidFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SolidFragment extends PixicleConfigFragmentBase {

    public SolidFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SolidFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SolidFragment newInstance() {
        return new SolidFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solid, container, false);

        ColorPicker colorPicker = (ColorPicker) view.findViewById(R.id.solid_picker);
        ValueBar valueBar = (ValueBar) view.findViewById(R.id.solid_value_bar);
        colorPicker.addValueBar(valueBar);
        SaturationBar saturationBar = (SaturationBar) view.findViewById(R.id.solid_saturation_bar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.setShowOldCenterColor(false);

        return view;
    }

    @Override
    public String getPixicleConfigArgs() {
        View view = getView();
        ColorPicker colorPicker = (ColorPicker) view.findViewById(R.id.solid_picker);

        int color = colorPicker.getColor();
        Integer red = (color & 0x00ff0000)>>16;
        Integer green = (color & 0x0000ff00)>>8;
        Integer blue = color & 0x000000ff;

        return "Solid:" +
                red.toString() + "," +
                green.toString() + "," +
                blue.toString();
    }
}
