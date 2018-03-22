package main;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import toolbox.Sql;

/**
 * Class d'objets Globaux.<br>Elle contient tous les textes importants et souvents répétés dans toute l'application
 * @author Dominique
 *
 */
public class Global {
	// ==================================================================
	// =========================== CONSTANTES ===========================
	// ==================================================================
	public static java.net.URL URL = null;
	/** Indique si l'application est exécutée sur un poste de développement ou de production **/
	public static boolean isDev = true;
	/** 
	 * Paramètre très important qui donne le répertoire dans lequel enregistrer les fichier
	 * Initialisation sur la class principal {@link CccvsTransfert}
	 */
	public static String UPLOAD_DIR = "/opt/tomcat7/webapps/files/";
	
	/** =================== PARAMETRES =============================== **/
	/** HashMap qui contient tous les paramètres **/
	public static final HashMap<String, String> hashParm = new HashMap<String, String>();
	/**
	 * Retourne un paramètre de l'application enregistré dans la base de données.
	 * @param key La clé pour trouver le paramètre
	 * @return (String) Paramètre de la base de données
	 */
	public static String getParm(String key) {
		// La première fois, on rempli le HashMap avec tous les paramètres
		if(hashParm.isEmpty()) {
			ResultSet parm = Sql.query("SELECT * FROM trans_app_param");
			
			try {
				while(parm.next()) {
					hashParm.put(parm.getString("parm_key"), parm.getString("parm_value"));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		// On sélectionne le paramètre
		String param = "";
		if(hashParm.get(key)!=null) {
			param = hashParm.get(key);
			param = param.replace("\"", "'");
		}

		return param;
	}

	
	//=================== TRADUCTIONS ===============================
	/** HashMap qui contient toutes les traductions **/
	public static final HashMap<String, String> hashTranslateFR = new HashMap<String, String>();
	public static final HashMap<String, String> hashTranslateDE = new HashMap<String, String>();
	/**
	 * Fonction "Intertionalize". Elle permet la traduction des textes de l'application
	 * @param key Mot clé du texte à afficher
	 * @return Le mot traduit dans la langue en cours
	 */
	public static String i(String key, String langue) {
		// La première fois, on rempli le HashMap avec toutes les traductions
		if(hashTranslateFR.isEmpty()) {
			loadTranslations();
		}
		
		String trans = "";
		if(langue.equals("fr")) {
			if(hashTranslateFR.get(key)!=null) {
				trans = hashTranslateFR.get(key).trim();
			}
		} else {
			if(hashTranslateDE.get(key)!=null) {
				trans = hashTranslateDE.get(key).trim();
			}
		}

		return trans;
	}
	
	/**
	 * Chargement du tableau contenant les textes en francais et en allemand
	 */
	public static void loadTranslations() {
		// On rempli le HashMap avec toutes les traductions
		hashTranslateFR.clear();
		hashTranslateDE.clear();
		ResultSet translations = Sql.query("SELECT * FROM trans_translate");
		try {
			while(translations.next()) {
				hashTranslateFR.put(translations.getString("trans_label"), translations.getString("trans_fr"));
				hashTranslateDE.put(translations.getString("trans_label"), translations.getString("trans_de"));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
}
