package se.irl.pixicle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FireFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FireFragment extends PixicleConfigFragmentBase {

    public FireFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FireFragment.
     */
    public static FireFragment newInstance() {
        return new FireFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fire, container, false);
    }

    @Override
    public String getPixicleConfigArgs() {

        View view = getView();

        EditText dissipationEdit = (EditText) view.findViewById(R.id.fire_dissipation);
        EditText combustionEdit = (EditText) view.findViewById(R.id.fire_combustion);

        return combustionEdit.getText()+"," + dissipationEdit.getText();
    }

    @Override
    public boolean setPixicleConfigArgs(String config) {
        return false;
    }

    @Override
    public String getPluginName() { return "Fire"; }
}
