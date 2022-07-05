package fr.cedgonzalezdevcda.meteo;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * L'API level 30 a marqué la classe AsyncTask comme dépréciée en recommandant d'utiliser les classes du package java.util.concurrent.
 * Suivant cette recommandation, cette classe propose les mêmes fonctionnalités qu'AsyncTask (le suivi de progression en moins).
 */
public abstract class AsyncFunction<Param, Result> {
	//Executor associé à un thread parallèle.
	private final Executor executor = Executors.newSingleThreadExecutor();
	// Handler référençant le thread principal.
	private final Handler handler = new Handler(Looper.getMainLooper());

	// Méthode exécutée avant doInBackground().
	protected void onPreExecute() {
	}

	// Seule méthode exécutée sur le thread parallèle.
	protected abstract Result doInBackground(Param... params);

	// Méthode exécutée sur le thread principal APRES doInBackground().
	protected void onPostExecute(Result result) {
	}

	protected void onError(Exception e) {
		e.printStackTrace();
	}

	// Exécute la série de méthodes ci-dessus.
	public void execute(Param... params) {
		// Exécuter onPreExecute() sur le thread principal.
		onPreExecute();
		executor.execute(() -> {
			final Result result;
			try {
				// Exécuter doInBackground() sur le thread parallèle.
				result = doInBackground(params);
				// Au retour, passer le résultat à onPostExecute() sur le thread principal.
				handler.post(() -> onPostExecute(result));
			} catch (Exception e) {
				// En cas d'erreur, exécuter onError() sur le thread principal.
				handler.post(() -> onError(e));
			}
		});
	}
}
