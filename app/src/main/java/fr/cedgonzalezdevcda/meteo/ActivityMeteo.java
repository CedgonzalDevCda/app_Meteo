package fr.cedgonzalezdevcda.meteo;

import static fr.cedgonzalezdevcda.meteo.R.*;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import fr.cedgonzalezdevcda.meteo.databinding.ActivityMeteoBinding;

public class ActivityMeteo extends AppCompatActivity {

//    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Appel de la méthode parente.
        super.onCreate(savedInstanceState);
        // Récupérer la classe auto-générée de binding du layout.
        fr.cedgonzalezdevcda.meteo.databinding.ActivityMeteoBinding binding = ActivityMeteoBinding.inflate(this.getLayoutInflater());
        // Afficher le layout.
        this.setContentView(binding.getRoot());
        // Supporter l'ActionBar via une Toolbar
        this.setSupportActionBar(binding.toolbar);
        // Récupérer l'instance Singleton de VMListProvider.
        VMListProvider<OWM, OWM.Observation> vm = new ViewModelProvider(this).get(VMListProvider.class);
        // Définir le provider dans le VM.
        vm.setProvider(new OWM());
        // Préparer la snackbar du chargement.
            Snackbar loadingBar = Snackbar.make(binding.container, R.string.info_loading, Snackbar.LENGTH_INDEFINITE);
        // Observer l'état du ViewModel pour réagir aux chargements.
        vm.getMldState().observe(this, state -> {
            switch (state) {
                case VMListProvider.STATE_NO_INTERNET:
                    // Afficher un Snackbar.
                    Snackbar.make(binding.container, R.string.info_no_internet, Snackbar.LENGTH_LONG).show();
                    break;
                case VMListProvider.STATE_LOADING_START:
                    // Afficher le Snackbar du chargement.
                    loadingBar.show();
                    break;
                case VMListProvider.STATE_LOADING_END:
                    // Masquer le Snackbar du chargement.
                    loadingBar.dismiss();
                    break;
                case VMListProvider.STATE_CLICK_ON_ITEM:
                    this
                    // Remplacer le FragmentList par le FragmentObservation en ajoutant la transaction à l'historique.
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new FragmentObservation())
                            .addToBackStack(null)
                            .commit();
                    break;
                case VMListProvider.STATE_DONE:
                    // Retourner pour éviter une boucle sans fin.
                    return;
            }
            vm.setStateDone();
        });
        // La première fois, charger une instance de FragmentList.
        if (savedInstanceState == null) {
            this
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(id.container, new FragmentList<OWM, OWM.Observation>())
                    .commit();
        }
    }
}