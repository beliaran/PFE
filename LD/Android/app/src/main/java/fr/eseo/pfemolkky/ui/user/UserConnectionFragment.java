package fr.eseo.pfemolkky.ui.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import fr.eseo.pfemolkky.MainActivity;
import fr.eseo.pfemolkky.R;

public class UserConnectionFragment extends Fragment {
    /**
     * Function called when fragment is created <br>
     * Inflate the fragment
     *
     * @param inflater           the layout xml containing the page
     * @param container          a group of view containing the page
     * @param savedInstanceState the saved instante state between the pages
     * @return the inflated fragment with all elements
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        View root = inflater.inflate(R.layout.fragment_user_connection, container, false);
        TextView textViewRegister = root.findViewById(R.id.registerText);
        EditText textEmail = root.findViewById(R.id.textEmail);
        EditText textPassword = root.findViewById(R.id.textPassword);
        Button buttonSignIn = root.findViewById(R.id.buttonSignIn);

        if (getActivity() != null) {
            SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
            textEmail.setText(settings.getString("Username", "").toString());
            textPassword.setText(settings.getString("Password", "").toString());
            ((MainActivity) getActivity()).setAllowBack(true);
            buttonSignIn.setOnClickListener(view -> {
                SharedPreferences.Editor editor = settings.edit();
                if ((!textEmail.getText().toString().equals("")) && (!textPassword.getText().toString().equals(""))) {
                    editor.putString("Username", textEmail.getText().toString());
                    editor.putString("Password", textPassword.getText().toString());
                    editor.apply();
                } else {
                    Context context = getContext();
                    CharSequence text = getResources().getString(R.string.namePasswordNotNull);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
            textViewRegister.setOnClickListener(view -> {
                navController.navigate(R.id.nav_user_register);
            });
        }
        return root;
    }
}
