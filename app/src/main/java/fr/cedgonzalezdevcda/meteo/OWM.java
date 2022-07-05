package fr.cedgonzalezdevcda.meteo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class OWM implements ListProvider<OWM.Observation> {
    public static final class Observation {
        String city;
        int min;
        int max;
        int feelsLike;
        int humidity;
        int windSpeed;
        int windDirection;
        String description;
        String iconURL;
        Bitmap icon;

        @NonNull
        @Override
        public String toString() {
            return city + " : " + min + "°C / " + max + "°C";
        }
    }

    public static final class ArrayAdapterObservation extends ArrayAdapter<Observation> {
        private final int resource;

        public ArrayAdapterObservation(@NonNull Context context, int resource) {
            super(context, resource);
            // Sauvegarder la référence au layout.
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            if (convertView == null) {
                // Cet item n'est pas recyclé, inflater le layout.
                Log.d("Adapter", "inflate");
                view = LayoutInflater.from(getContext()).inflate(resource, parent,false);
            } else {
                // Cet item est recyclé, l'utiliser tel quel.
                Log.d("Adapter", "recycle");
                view = convertView;
            }
            // Utiliser l'Observation pour définir le texte du layout.
            ((TextView) view.findViewById(R.id.item_text)).setText(getItem(position).toString());
            ((ImageView) view.findViewById(R.id.item_icon)).setImageBitmap(getItem(position).icon);
            // Si champ description disponible, le définir.
            TextView tvDescription = view.findViewById(R.id.item_description);
           if (tvDescription != null) {
               tvDescription.setText(getItem(position).description);
           }
            // Retourner le layout inflaté et renseigné.
            return view;
        }
    }

    private static final String URL = "https://api.openweathermap.org/data/2.5/group?id=264371,2950159,3060972,2800866,3054643,2618425,2964574,658225,2267057,3196359,3117735,2988507,3067696,456172,3169070,2673730,588409,756135,2761369,593116&units=metric&lang=fr&mode=json";
    private static final String KEY = "d204160d78d72ae92d79bb1eeb44055d";
    private static final String ICON_BASE_URL = "https://api.openweathermap.org/img/w/";
    private static final String ICON_EXTENSION = "png";
    private static final String ERR_ICON_NOT_FOUND = "Error icon not found";

    @Override
    public String getURL() {
        return URL + "&appid=" + KEY;
    }

    @Override
    public ArrayList<Observation> toList(String strJSON)  throws JSONException {
        // Créer une ArrayList vide.
        ArrayList<Observation> observations = new ArrayList<>();
        // Récupérer la racine JSON.
        JSONObject root = new JSONObject(strJSON);
        // Récupérer le tableau des villes.
        JSONArray cities = root.getJSONArray("list");
        // Pour chaque ville, récupérer les données.
        for (int i = 0; i < cities.length(); i++ ) {
            // Créer une nouvelle observation.
            Observation obs = new Observation();
            // L'ajouter à la liste.
            observations.add(obs);
            // Récupérer les données
            JSONObject city = cities.getJSONObject(i);
            obs.city = city.getString("name");
            //
            JSONObject weather = city.getJSONArray("weather").getJSONObject(0);
            obs.description = weather.getString("description");
            obs.iconURL = ICON_BASE_URL + weather.getString("icon") + '.' + ICON_EXTENSION;
            JSONObject main = city.getJSONObject("main");
            obs.min = (int) Math.round(main.getDouble("temp_min"));
            obs.max = (int) Math.round(main.getDouble("temp_max"));
            obs.feelsLike = (int) Math.round(main.getDouble("feels_like"));
            obs.humidity = (int) Math.round(main.getDouble("humidity"));
            JSONObject wind = city.getJSONObject("wind");
            obs.windDirection = wind.getInt("deg");
            obs.windSpeed = (int) Math.round(wind.getDouble("speed") * 3.6) ;
            // Downloader les icônes.
            try {
                obs.icon = BitmapFactory.decodeStream(new URL(obs.iconURL).openStream());
            } catch (IOException e) {
                Log.e("OWM.toList",ERR_ICON_NOT_FOUND);
            }
        }
        return observations;
    }

    @Override
    public ArrayAdapter<Observation> getAdapter(Context context) {
        return new ArrayAdapterObservation(context,R.layout.observations);
    }

}
