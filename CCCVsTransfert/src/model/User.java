package model;

/**
 * Modèle d'un utilisateur (Table trans_users).<br>
 * Permet le stockage des informations sur un utilisateur dans un objet.
 * @author Dominique Roduit
 *
 */
public class User {
	/** Clé primaire de l'utilisateur **/
	private int PKNoUser = 0;
	/** Adresse e-mail de l'utilisateur **/
	private String user_mail = "";
	/** Mot de passe de l'utilisateur (crypté) **/
	private String user_pass = "";
	/** Indique si l'utilisateur est interne ou non **/
	private boolean user_internal = false;
	/** Indique si l'utilisateur à validé son compte ou non **/
	private boolean user_validation = false;
	/** Contient la date de validation du compte **/
	private String user_validation_date = "";
	/** Contient la langue préférée de l'utilisateur **/
	private String user_langue = "";
	/** Indique si l'utilisateur est administrateur ou non **/
	private boolean user_admin = false;
	
	
	public User() {	
	}
	/**
	 * Création d'un objet, model d'utilisateur
	 * @param PKNoUser Clé primaire de l'utilisateur
	 * @param user_mail Adresse e-mail de l'utilisateur
	 * @param user_pass Mot de passe de l'utilisateur
	 * @param user_internal Indique si l'utilisateur est interne ou pas
	 * @param user_validation Indique si l'utilisateur a validé son compte
	 * @param user_validation_date Date de validation du compte utilisateur
	 * @param user_langue Langue de l'utilisateur
	 * @param user_admin Indique si l'utilisateur est un administrateur
	 */
	public User(int PKNoUser, String user_mail, String user_pass, boolean user_internal, boolean user_validation, String user_validation_date, String user_langue, boolean user_admin) {
		setPKNoUser(PKNoUser);
		setUser_mail(user_mail);
		setUser_pass(user_pass);
		setInternal(user_internal);
		setUser_validation(user_validation);
		setUser_validation_date(user_validation_date);
		setUser_langue(user_langue);
		setUser_admin(user_admin);
	}
	/**
	 * Obtention de la clé primaire
	 * @return Clé primaire
	 */
	public int getPKNoUser() {
		return PKNoUser;
	}
	/**
	 * Enregistrement de la clé primaire de l'utilisateur
	 * @param PKNoUser Clé primaire de l'utilisateur
	 */
	public void setPKNoUser(int PKNoUser) {
		this.PKNoUser = PKNoUser;
	}
	/**
	 * Obtention de l'adresse e-mail de l'utilisateur
	 * @return Adresse e-mail de l'utilisateur
	 */
	public String getUser_mail() {
		return user_mail;
	}
	/**
	 * Réglage de l'adresse e-mail de l'utilisateur
	 * @param user_mail
	 */
	public void setUser_mail(String user_mail) {
		this.user_mail = user_mail;
	}
	/**
	 * Obtention du mot de passe crypté de l'utilisateur 
	 * @return Mot de passe crypté
	 */
	public String getUser_pass() {
		return user_pass;
	}
	/**
	 * Définition du mot de passe de l'utilisateur
	 * @param pass Mot de passe de l'utilisateur (crypté)
	 */
	public void setUser_pass(String pass) {
		this.user_pass = pass;
	}
	/**
	 * Obtention de l'état interne ou non de l'utilisateur
	 * @return 1 : Utilisateur interne<br>0 : Utilisateur externe
	 */
	public boolean isInternal() {
		return user_internal;
	}
	/**
	 * Sauvegarde le fait que l'utilisateur soit un interne
	 * @param user_internal 1 : Utilisateur interne<br>0: Utilisateur externe
	 */
	public void setInternal(boolean user_internal) {
		this.user_internal = user_internal;
	}
	/**
	 * Retourne l'état du compte de l'utilisateur
	 * @return 1 : Compte validé<br>0 : Compte non-validé
	 */
	public boolean getUser_validation() {
		return user_validation;
	}
	/**
	 * Sauvegarde le fait que l'utilisateur ait validé son compte ou non
	 * @param user_validation 1 : Compte validé<br>0 : Compte non-validé
	 */
	public void setUser_validation(boolean user_validation) {
		this.user_validation = user_validation;
	}
	/**
	 * Retourne la date de validation du compte utilisateur
	 * @return Date de validation
	 */
	public String getUser_validation_date() {
		return user_validation_date;
	}
	/**
	 * Stocke la date de validation du compte utilisateur
	 * @param user_validation_date Date de validation
	 */
	public void setUser_validation_date(String user_validation_date) {
		this.user_validation_date = user_validation_date;
	}
	/**
	 * Retourne la langue préférée par l'utilisateur<br>
	 * Par défaut, la langue préférée est celle que l'utilisateur utilise dans son navigateur.
	 * @return Langue de l'utilisateur
	 */
	public String getUser_langue() {
		return user_langue;
	}
	/**
	 * Stocke la langue préférée de l'utilisateur
	 * @param user_langue Langue de l'utilisateur
	 */
	public void setUser_langue(String user_langue) {
		this.user_langue = user_langue;
	}
	/**
	 * @return Indique si l'utilisateur est un administrateur
	 */
	public boolean isUser_admin() {
		return user_admin;
	}
	/**
	 * @param Définit l'utilisateur comme administrateur ou non
	 */
	public void setUser_admin(boolean user_admin) {
		this.user_admin = user_admin;
	}
	
	
}
