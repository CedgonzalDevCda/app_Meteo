package fr.cedgonzalezdevcda.meteo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class Util {
    public static boolean isConnected(final Context context) {
        // Récupérer un Connectivity Manager.
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Si null, aucun réseau disponible sur l'appareil.
        if (cm == null)
            return false;
        // Sinon, récupérer les connexions réseaux disponibles.
        Network[] networks = cm.getAllNetworks();
        // Parmi ces connexions, rechercher une connexion Internet active.
        for (Network network : networks) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(network);
            // Si connexion internet, retourner true.
            if (nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
                return true;
        }
        // Aucune connexion internet active, retourner false.
        return false;
    }
}
