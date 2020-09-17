package com.example.velveteyebrows.activities.abstracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;

public abstract class NavigatableActivity extends AppCompatActivity {

    private final HashMap<String, Fragment.SavedState> _savedStateHashMap
            = new HashMap<>();


    protected void navigateToFragment(int idContainer, Fragment fragment, boolean addToBackStack){

        FragmentManager fragmentManager = getSupportFragmentManager();

       /* String className = fragment.getClass().getName();

        if(_savedStateHashMap.containsKey(className)){
            fragment.setInitialSavedState(_savedStateHashMap.get(className));
        }*/

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(idContainer, fragment);

        if(addToBackStack){
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}
