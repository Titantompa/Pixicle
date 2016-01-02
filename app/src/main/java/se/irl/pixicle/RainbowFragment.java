package se.irl.pixicle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RainbowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RainbowFragment extends PixicleConfigFragmentBase {

    public RainbowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RainbowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RainbowFragment newInstance() {
        RainbowFragment fragment = new RainbowFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rainbow, container, false);
        return view;
    }

    @Override
    public String getPixicleConfigArgs() {
        View view = getView();
        EditText speedEdit = (EditText) view.findViewById(R.id.rainbow_speed);
        return "Rainbow:" + speedEdit.getText();
    }
}
