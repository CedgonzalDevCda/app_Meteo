package fr.cedgonzalezdevcda.meteo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import fr.cedgonzalezdevcda.meteo.databinding.FragmentListBinding;

public class FragmentList<P extends ListProvider<E>, E> extends Fragment {
    private VMListProvider<P, E> vm;

    public FragmentList() {
        // Required empty public constructor
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Récupérer l'instance Singleton de VMListProvider.
        vm = new ViewModelProvider(requireActivity()).get(VMListProvider.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Récupérer la classe auto-générée de binding du layout.
        FragmentListBinding binding = FragmentListBinding.inflate(inflater, container, false);
        // Récupérer le ListView depuis le layout via son id.
        ListView listView = binding.list;
        // Créer un ArrayAdapter dédié.
        ArrayAdapter<E> adapter = vm.getProvider().getAdapter(this.getContext());
        // Associer le ListView à l'ArrayAdapter.
        listView.setAdapter(adapter);
        // Définir un écouteur de clicks sur les items de la liste.
        listView.setOnItemClickListener((parent, view, position, id) -> vm.setPosition(position));

        // Observer la liste du provider et peupler l'adapter.
        vm.getLDList(false).observe(getViewLifecycleOwner(), list -> {
            adapter.clear();
            adapter.addAll(list);
        });
        //Retourne le fragment inflaté.
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Récupérer l'Activity associée à ce Fragment.
        MenuHost menuHost = requireActivity();
        // Implémenter l'interface MenuProvider (non fonctionnelle) avec une anonymous-class.
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Inflater le layout du menu.
                menuInflater.inflate(R.menu.menu_list,menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Si clic sur Refresh, actualiser.
                if (menuItem.getItemId() ==  R.id.action_refresh) {
                    // Actualiser.
                    vm.getMldList(true);
                    // Consommer le clic.
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Si clic sur Refresh, actualiser.
        if (item.getItemId() ==  R.id.action_refresh) {
            vm.getLDList(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}