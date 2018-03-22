package global;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.vaadin.terminal.gwt.server.WebBrowser;
import main.CccvsTransfert;
import modules.ContactModule;
import modules.TransfertModule;

import toolbox.Sql;

/**
 * Class de stockage des champs Globaux (qui doivent �tre accessibles pour toutes les class de l�application).<br>Elle contient les m�thodes de r�cup�ration des param�tres et des traductions de l'application. Toutes les m�thodes et champs de la class sont statiques.
 * @author Dominique Roduit
 */
public class Global {
	// ==================================================================
	// =========================== CONSTANTES ===========================
	// ==================================================================
	/** Langue par d�faut de l'interface **/
	public static String APP_LANGUAGE = "fr";
	/** URL de l'application. Seulement la partie http://domaine.ch:port/monApplication. Sans les param�tres de l'URL. **/
	public static java.net.URL URL = null;
	/** Indique si l'application est ex�cut�e sur un poste de d�veloppement ou de production **/
	public static boolean isDev = true;
	/** Contient les param�tres du navigateur tel que le nom, la version et la langue **/
	public static WebBrowser browser = null;
	/** Contient les param�tres de l'URL s'il y en a **/
	public static String URLParameters = null;
	/** 
	 * Param�tre tr�s important qui donne le r�pertoire dans lequel enregistrer les fichier<br>
	 * Initialisation dans la class principal {@link CccvsTransfert}
	 */
	public static String UPLOAD_DIR = new File(System.getProperty("java.io.tmpdir")).getPath()+"/";
	
	// ==================================================================
	// ====================== IMAGES & RESSOURCES =======================
	// ==================================================================
	/** R�pertoire contenant les ressources (images) du th�me **/
	public static final String PATH_THEME_RESSOURCES = "../img/";
	/**
	 * R�pertoire contenant les ressources du th�me.<br>
	 * A utiliser lorsqu'on �cris l'image en HTML<br>ex. :
	 */
	public static final String PATH_THEME_RESSOURCES_HTML = "/CCCVsTransfert/VAADIN/themes/CccvsDesign/"+PATH_THEME_RESSOURCES;
	/** Logo affich� dans le header du layout principal **/
	public static final String IMG_MAIN_LOGO = PATH_THEME_RESSOURCES+"logoMainHeader.png";
	/** Image contact **/
	public static final String IMG_CONTACT = PATH_THEME_RESSOURCES+"contact.png";
	/** Image affich�e dans le footer � c�t� de l'adresse de l'utilisateur connect� **/
	public static final String IMG_APPROVED = PATH_THEME_RESSOURCES+"tick-shield.png";
	/** Image d'ajout d'un dossier **/
	public static final String IMG_FOLDER_ADD = PATH_THEME_RESSOURCES+"newfolder.png";
	/** Image illustrant un t�l�chargement **/
	public static final String IMG_DOWNLOAD = PATH_THEME_RESSOURCES+"up.png";
	/** Image illustrant une consultation **/
	public static final String IMG_SEARCH = PATH_THEME_RESSOURCES+"search.png";
	/** Image illustrant une archive t�l�charg�e **/
	public static final String IMG_ARCHIVE_DOWNLOAD = PATH_THEME_RESSOURCES+"archive_down.png";
	
	public static String IMG_FLAG_COUNTRY = PATH_THEME_RESSOURCES+UserSession.getLangue()+".png";
	
	
	// =================== PARAMETRES ===============================
	/** HashMap qui contient tous les param�tres **/
	public static final HashMap<String, String> hashParm = new HashMap<String, String>();
	/**
	 * Retourne un param�tre de l'application enregistr� dans la base de donn�es.
	 * @param key La cl� pour trouver le param�tre
	 * @return (String) Param�tre de la base de donn�es
	 */
	public static String getParm(String key) {
		// La premi�re fois, on rempli le HashMap avec tous les param�tres
		if(hashParm.isEmpty()) {
			reloadParm();
		}
		
		// On s�lectionne le param�tre
		String param = "";
		if(hashParm.get(key)!=null) {
			param = hashParm.get(key);
			param = param.replace("\"", "'");
		}

		return param;
	}
	
	//=================== TRADUCTIONS ===============================
	/** HashMap qui contient toutes les traductions **/
	public static final HashMap<String, String> hashTranslate = new HashMap<String, String>();
	/**
	 * Fonction "Intertionalize". Elle permet la traduction des textes de l'application
	 * @param key Mot cl� du texte � afficher
	 * @return Le mot traduit dans la langue en cours
	 */
	public static String i(String key) {
		// La premi�re fois, on rempli le HashMap avec toutes les traductions
		if(hashTranslate.isEmpty()) {
			reloadTranslations();
		}
		
		String trans = "";
		if(hashTranslate.get(key)!=null) {
			trans = hashTranslate.get(key).trim();
		}

		return trans;
	}
	/**
	 * Rechargement du tableau contenant les traductions
	 */
	public static void reloadTranslations() {
		// On rempli le HashMap avec toutes les traductions
		hashTranslate.clear();
		ResultSet translations = Sql.query("SELECT * FROM trans_translate");
		try {
			while(translations.next()) {
				//System.out.println("remplissage : "+APP_LANGUAGE);
				hashTranslate.put(translations.getString("trans_label"), translations.getString("trans_"+APP_LANGUAGE.toLowerCase()));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	/**
	 * Rechargement du tableau des param�tres
	 */
	public static void reloadParm() {
		hashParm.clear();
		ResultSet parm = Sql.query("SELECT * FROM trans_app_param");
		try {
			while(parm.next()) {
				hashParm.put(parm.getString("parm_key"), parm.getString("parm_value"));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
}
