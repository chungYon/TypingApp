package com.example.sunrin.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OptionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "category";
    private static final String ARG_PARAM2 = "param2";

    private TextView cpmText;
    private TextView accuracyText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnglishFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OptionFragment newInstance(String param1, String param2) {
        OptionFragment fragment = new OptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_option, container, false);

        Button btn_start = rootView.findViewById(R.id.btn_start);
        TextView title = rootView.findViewById(R.id.title);

        cpmText = rootView.findViewById(R.id.cpm);
        accuracyText = rootView.findViewById(R.id.accuracy);

        //title.setText("ENGLISH TYPING");
        title.setText(mParam1.toUpperCase() + " TYPING");

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 액티비티 호출]
                Intent intent = null;
                if(mParam1.equals("english"))
                    intent = new Intent(getActivity(), EnglishTypingActivity.class);
                else if(mParam1.equals("korean"))
                    intent = new Intent(getActivity(), KoreanTypingActivity.class);
                else if(mParam1.equals("game"))
                    intent = new Intent(getActivity(), EnglishTypingActivity.class);

                if(intent != null)
                    startActivityForResult(intent, 1);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1){
            cpmText.setText("cpm: " + data.getStringExtra("cpm"));
            accuracyText.setText("accuracy: " + data.getStringExtra("accuracy"));
        }else{
            cpmText.setText("");
            accuracyText.setText("");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}