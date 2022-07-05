package fr.cedgonzalezdevcda.meteo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import fr.cedgonzalezdevcda.meteo.databinding.FragmentObservationBinding;

public class FragmentObservation extends Fragment {
    private VMListProvider<OWM, OWM.Observation> vm;

    public FragmentObservation() {
        // Required empty public constructor
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Récupérer l'instance Singleton de VMListProvider.
        vm = new ViewModelProvider(requireActivity()).get(VMListProvider.class);
        // Indiquer que ce fragment à un menu.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Récupérer la classe auto-générée de binding du layout.
        FragmentObservationBinding binding = FragmentObservationBinding.inflate(inflater, container, false);
        // Récupérer l'observation sur laquelle l'utlisateur a cliqué.
        OWM.Observation obs = vm.getItem();
        // Définir les champs du layout.
        assert obs != null;
        binding.obsCity.setText(obs.city);
        binding.obsDescription.setText(obs.description);
        binding.obsTemp.setText(getString(R.string.obs_temp, obs.min,obs.max, obs.feelsLike));
        binding.obsWind.setText(getString(R.string.obs_wind, obs.windDirection, obs.windSpeed));
        binding.obsHumidity.setText(getString(R.string.obs_humidity,obs.humidity));


        //Retourne le fragment inflaté.
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Appeler la méthode parente.
        super.onCreateOptionsMenu(menu, inflater);
        // Inflater le layout du menu.
        inflater.inflate(R.menu.menu_observation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Si clic sur Share, actualiser.
        if (item.getItemId() ==  R.id.action_share) {
            // Récupérer l'observation actuelle.
            OWM.Observation obs = vm.getItem();
            // Créer un Intent.
            assert obs != null;
            Intent intent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_SUBJECT,"Wheater in " + obs.city)
                    .putExtra(Intent.EXTRA_TEXT,obs.toString());
            // Générer la liste des activités correspondant à l'Intent et démarrer celle choisie par l'utilisateur.
            startActivity(Intent.createChooser(intent,null));
            // Consommer le clic.
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}