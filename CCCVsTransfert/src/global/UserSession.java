package global;

/**
 * Stockage des informations d'un utilisateur lors de sa connexion.<br>
 * Cette class est comparable � une variable de session en PHP.<br>
 * Lors du rechargement de la page, les donn�es sont conserv�es gr�ce � un ThreadLocal.<br>
 * Si l�application est red�marr�e, la session est d�truite.
 * @author Dominique Roduit
 *
 */
public class UserSession {
	/** Cl� primaire de l'utilisateur **/
	private static int ID = 0;
	/** Adresse e-mail de l'utilisateur **/
	private static String mail = "";
	/** Indique si l'utilisateur est un interne ou non **/
	private static boolean internal = false;
	/** Stocke la langue pr�f�r�e de l'utilisateur **/
	private static String langue = "fr";
	/** Indique si l'utilisateur est un administrateur ou non **/
	private static boolean admin = false;
	
	/**
	 * Retourne la cl� primaire de l'utilisateur
	 * @return Cl� primaire de l'utilisateur
	 */
	public static int getID() {
		return ID;
	}
	/**
	 * Enregistre la cl� primaire de l'utilisateur
	 * @param iD Cl� primaire de l'utilisateur
	 */
	public static void setID(int iD) {
		ID = iD;
	}
	/**
	 * Retourne l'adresse e-mail de l'utilisateur connect�
	 * @return Adresse e-mail
	 */
	public static String getMail() {
		return mail;
	}
	/**
	 * Enregistre l'adresse e-mail de l'utilisateur connect�
	 * @param mail Adresse e-mail
	 */
	public static void setMail(String mail) {
		UserSession.mail = mail;
	}
	/**
	 * Indique si l'utilisateur est un interne ou non
	 * @return true si l'utilisateur est un interne
	 */
	public static boolean isInternal() {
		return internal;
	}
	/**
	 * Enregistre si l'utilisateur est un interne ou non
	 * @param internal true si l'utilisateur est un interne
	 */
	public static void setInternal(boolean internal) {
		UserSession.internal = internal;
	}
	/**
	 * Retourne la langue d�finit par l'utilisateur ou par son navigateur si celui-ci ne l'a jamais modifi�e
	 * @return Langue de l'utilisateur
	 */
	public static String getLangue() {
		return langue.substring(0,2);
	}
	/**
	 * Enregistre la langue choisie par l'utilisateur
	 * @param langue Langue choisie par l'utilisateur
	 */
	public static void setLangue(String langue) {
		UserSession.langue = langue;
		Global.APP_LANGUAGE = langue;
		Global.reloadTranslations();
		Global.IMG_FLAG_COUNTRY = Global.PATH_THEME_RESSOURCES+Global.APP_LANGUAGE+".png";
		System.out.println("Modification de la langue de l'interface : "+langue.toUpperCase()+"; Utilisateur : "+ID);
	}
	/**
	 * @return Indique si l'utilisateur est un administrateur
	 */
	public static boolean isAdmin() {
		return admin;
	}
	/**
	 * @param D�finit l'utilisateur comme administrateur ou non
	 */
	public static void setAdmin(boolean admin) {
		UserSession.admin = admin;
	}	
}