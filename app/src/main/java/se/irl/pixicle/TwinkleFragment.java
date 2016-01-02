package se.irl.pixicle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TwinkleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TwinkleFragment extends PixicleConfigFragmentBase {

    public TwinkleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TwinkleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TwinkleFragment newInstance() {
        TwinkleFragment fragment = new TwinkleFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twinkle, container, false);

        ColorPicker colorPicker = (ColorPicker) view.findViewById(R.id.twinkle_picker);
        ValueBar valueBar = (ValueBar) view.findViewById(R.id.twinkle_value_bar);
        colorPicker.addValueBar(valueBar);
        SaturationBar saturationBar = (SaturationBar) view.findViewById(R.id.twinkle_saturation_bar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.setShowOldCenterColor(false);

        return view;
    }

    @Override
    public String getPixicleConfigArgs() {
        View view = getView();
        EditText countEdit = (EditText) view.findViewById(R.id.twinkle_count);
        EditText durationEdit = (EditText) view.findViewById(R.id.twinkle_duration);
        ColorPicker colorPicker = (ColorPicker) view.findViewById(R.id.twinkle_picker);
        CheckBox flickering = (CheckBox) view.findViewById(R.id.twinkle_flickering);

        int color = colorPicker.getColor();
        Integer red = (color & 0x00ff0000)>>16;
        Integer green = (color & 0x0000ff00)>>8;
        Integer blue = color & 0x000000ff;

        return "Twinkle:" +
                countEdit.getText().toString() + "," +
                durationEdit.getText().toString() + "," +
                red.toString() + "," +
                green.toString() + "," +
                blue.toString() +"," +
                (flickering.isChecked() ? "1" : "0");
    }
}
