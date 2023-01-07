package fr.eseo.pfemolkky.ui.user;

import android.content.Context;
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

public class UserRegisterFragment extends Fragment {
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
        View root = inflater.inflate(R.layout.fragment_user_register, container, false);
        EditText userName = root.findViewById(R.id.textUserName);
        EditText firstName = root.findViewById(R.id.textFirstName);
        EditText lastName = root.findViewById(R.id.textLastName);
        EditText email = root.findViewById(R.id.textEmailRegister);
        EditText password = root.findViewById(R.id.textPasswordRegister);
        EditText passwordVerification = root.findViewById(R.id.textPasswordRegisterVerification);
        Button buttonRegister = root.findViewById(R.id.buttonRegister);
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setAllowBack(true);
            buttonRegister.setOnClickListener(view -> {
                if ((!firstName.getText().toString().equals(""))
                        && (!userName.getText().toString().equals(""))
                        && (!lastName.getText().toString().equals(""))
                        && (!email.getText().toString().equals(""))
                        && (!password.getText().toString().equals(""))
                        && (!passwordVerification.getText().toString().equals(""))) {
                    System.out.println("pass");
                    System.out.println(firstName.getText().toString() + userName.getText().toString() + lastName.getText().toString() + email.getText().toString() + password.getText().toString() + passwordVerification.getText().toString());
                    if (password.getText().toString().equals(passwordVerification.getText().toString())) {

                    } else {
                        Context context = getContext();
                        CharSequence text = getResources().getString(R.string.passwordNotEgal);
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                } else {
                    Context context = getContext();
                    CharSequence text = getResources().getString(R.string.anyFieldEmpty);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
        }
        return root;
    }
}
