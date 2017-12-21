package fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.modla.andy.processingcardboard.R;

/**
 * Created by sandeep on 1/14/16.
 */
public class InstructionFragment extends Fragment {

    View rootView;
    Typeface face ;
    TextView instruction_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.instruction_fragment, container, false);
        face= Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");

        init();

        return  rootView ;
    }

    private void init() {

        instruction_text = (TextView) rootView.findViewById(R.id.instruction_text);
        instruction_text.setTypeface(face);
    }
}

