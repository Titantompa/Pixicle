package se.irl.pixicle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GradientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GradientFragment extends PixicleConfigFragmentBase {

    public GradientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GradientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GradientFragment newInstance() {
        return new GradientFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_gradient, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.transition_style);
        addItemsToSpinner(spinner);

        ColorPicker fromColorPicker = (ColorPicker) view.findViewById(R.id.gradient_from_picker);
        ValueBar fromValueBar = (ValueBar) view.findViewById(R.id.gradient_from_value_bar);
        fromColorPicker.addValueBar(fromValueBar);
        SaturationBar fromSaturationBar = (SaturationBar) view.findViewById(R.id.gradient_from_saturation_bar);
        fromColorPicker.addSaturationBar(fromSaturationBar);
        fromColorPicker.setShowOldCenterColor(false);

        ColorPicker toColorPicker = (ColorPicker) view.findViewById(R.id.gradient_to_picker);
        ValueBar toValueBar = (ValueBar) view.findViewById(R.id.gradient_to_value_bar);
        toColorPicker.addValueBar(toValueBar);
        SaturationBar toSaturationBar = (SaturationBar) view.findViewById(R.id.gradient_to_saturation_bar);
        toColorPicker.addSaturationBar(toSaturationBar);
        toColorPicker.setShowOldCenterColor(false);

        return view;
    }

    private void addItemsToSpinner(Spinner spinner) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.props_transition_styles_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    public String getPixicleConfigArgs() {
        View view = getView();
        Spinner spinner = (Spinner) view.findViewById(R.id.transition_style);
        ColorPicker fromColorPicker = (ColorPicker) view.findViewById(R.id.gradient_from_picker);
        ColorPicker toColorPicker = (ColorPicker) view.findViewById(R.id.gradient_to_picker);

        int fromColor = fromColorPicker.getColor();
        Integer fromRed = (fromColor & 0x00ff0000)>>16;
        Integer fromGreen = (fromColor & 0x0000ff00)>>8;
        Integer fromBlue = fromColor & 0x000000ff;

        int toColor = toColorPicker.getColor();
        Integer toRed = (toColor & 0x00ff0000)>>16;
        Integer toGreen = (toColor & 0x0000ff00)>>8;
        Integer toBlue = toColor & 0x000000ff;

        return spinner.getSelectedItem().toString().charAt(0) + "," +
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
    public String getPluginName() { return "Gradient"; }
}
