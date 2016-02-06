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
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends PixicleConfigFragmentBase {

    public ProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProgressFragment.
     */
    public static ProgressFragment newInstance() {
        return new ProgressFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        ColorPicker fromColorPicker = (ColorPicker) view.findViewById(R.id.progress_from_picker);
        ValueBar fromValueBar = (ValueBar) view.findViewById(R.id.progress_from_value_bar);
        fromColorPicker.addValueBar(fromValueBar);
        SaturationBar fromSaturationBar = (SaturationBar) view.findViewById(R.id.progress_from_saturation_bar);
        fromColorPicker.addSaturationBar(fromSaturationBar);
        fromColorPicker.setShowOldCenterColor(false);

        ColorPicker toColorPicker = (ColorPicker) view.findViewById(R.id.progress_to_picker);
        ValueBar toValueBar = (ValueBar) view.findViewById(R.id.progress_to_value_bar);
        toColorPicker.addValueBar(toValueBar);
        SaturationBar toSaturationBar = (SaturationBar) view.findViewById(R.id.progress_to_saturation_bar);
        toColorPicker.addSaturationBar(toSaturationBar);
        toColorPicker.setShowOldCenterColor(false);

        return view;
    }

    @Override
    public String getPixicleConfigArgs() {
        View view = getView();
        EditText percentageEdit = (EditText) view.findViewById(R.id.progress_percentage);
        ColorPicker fromColorPicker = (ColorPicker) view.findViewById(R.id.progress_from_picker);
        ColorPicker toColorPicker = (ColorPicker) view.findViewById(R.id.progress_to_picker);

        int fromColor = fromColorPicker.getColor();
        Integer fromRed = (fromColor & 0x00ff0000)>>16;
        Integer fromGreen = (fromColor & 0x0000ff00)>>8;
        Integer fromBlue = fromColor & 0x000000ff;

        int toColor = toColorPicker.getColor();
        Integer toRed = (toColor & 0x00ff0000)>>16;
        Integer toGreen = (toColor & 0x0000ff00)>>8;
        Integer toBlue = toColor & 0x000000ff;

        Float percentage = Float.parseFloat(percentageEdit.getText().toString());

        percentage *= 100;

        return percentage.intValue() + "," +
               fromRed.toString() + "," +
               fromGreen.toString() + "," +
               fromBlue.toString() + "," +
               toRed.toString() + "," +
               toGreen.toString() + "," +
               toBlue.toString();
    }

    @Override
    public boolean setPixicleConfigArgs(String config) {
        return false;
    }

    @Override
    public String getPluginName() { return "Progress"; }
}
