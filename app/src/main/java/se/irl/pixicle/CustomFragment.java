package se.irl.pixicle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomFragment extends PixicleConfigFragmentBase {

    public CustomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CustomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomFragment newInstance() {
        CustomFragment fragment = new CustomFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom, container, false);
    }

    @Override
    public String getPixicleConfigArgs() {
        EditText custom = (EditText) getView().findViewById(R.id.custom_config);
        return custom.getText().toString().split(":")[1];
    }

    @Override
    public boolean setPixicleConfigArgs(String config) {
        return false;
    }

    @Override
    public String getPluginName() {
        EditText custom = (EditText) getView().findViewById(R.id.custom_config);
        return custom.getText().toString().split(":")[0];
    }
}
