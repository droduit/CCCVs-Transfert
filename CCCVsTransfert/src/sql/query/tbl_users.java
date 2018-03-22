package sql.query;

import global.Global;
import global.UserSession;
import toolbox.Utilities;

/**
 * Contient toutes les requêtes effectuées sur la table <b>trans_users</b>
 * @author Dominique Roduit
 *
 */
public class tbl_users {
	/** Nom de la table sur laquelle on effectue des opérations dans cette class **/
	private static final String TABLE_NAME = "trans_users";
	/**
	 * Sélection d'un utilisateur par son adresse e-mail
	 * @param email Adresse de l'utilisateur
	 * @return Requête SQL
	 */
	public static String getUserByEmail(String email) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE user_mail="+Utilities.formatSQL(email);
	}
	/**
	 * Mise à jour du champs de validation du compte utilisateur
	 * @param email Adresse de l'utilisateur qui valide son compte
	 * @return Requête SQL
	 */
	public static String getUpdByMailValidation(String email) {
		return "UPDATE "+TABLE_NAME+" SET user_validation=1, user_validation_date=now() WHERE user_mail="+Utilities.formatSQL(email);
	}
	/**
	 * Sélection d'un utilisateur en fonction d'un paramètre crypté passé dans l'URL
	 * @param URLParameter Valeur du paramètre de l'URL
	 * @return Requête SQL
	 */
	public static String getUserByCryptedURL(String URLParameter) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE SHA2(CONCAT('"+Global.getParm("SECRET_KEY")+"', user_mail), 256)="+Utilities.formatSQL(URLParameter)+"";
	}
	/**
	 * Retourne la requête d'insertion d'un utilisateur
	 * @param email Adresse e-mail
	 * @param pass Mot de passe
	 * @param internal L'utilisateur est un interne ou non
	 * @return Requête SQL
	 */
	public static String getInsertQuery(String email, String pass, boolean isInternal) {
		// Par défaut, le compte n'est pas validé
		boolean account_valid = false;
		
		return 
		"INSERT INTO "+TABLE_NAME+" VALUES(NULL, "+Utilities.formatSQL(email)+", "+
		Utilities.formatSQL(pass)+"," +
		Utilities.formatSQL(isInternal)+", "+Utilities.formatSQL(account_valid)+", now(), "+
		Utilities.formatSQL(Global.browser.getLocale().toString().substring(0,2))+", 0)";
	}
	/**
	 * Requête pour la mise à jour de la langue de l'utilisateur
	 * @param newLanguage Langue choisie par l'utilisateur
	 * @return Requête SQL
	 */
	public static String getUpdLanguage(String newLanguage) {
		return "UPDATE "+TABLE_NAME+" SET user_langue="+Utilities.formatSQL(newLanguage)+" WHERE PKNoUser="+UserSession.getID();
	}
	/**
	 * Requête pour la sélection des informations sur un utilisateur
	 * @param PKNoUser Clé primaire de l'utilisateur
	 * @return Requête SQL
	 */
	public static String getSelectUserInfos(int PKNoUser) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE PKNoUser="+Utilities.formatSQL(PKNoUser);
	}
	/**
	 * Sélection de tous les contacts de l'interface utilisateur
	 * @return Requête SQL
	 */
	public static String getSelectAll() {
		return "SELECT * FROM "+TABLE_NAME+" ORDER BY user_internal ASC, PKNoUser";
	}
	/**
	 * Requête de mise à jour d'un utilisateur (Admin ou pas)
	 * @param newValue true si admin
	 * @return Requête SQL
	 */
	public static String getUpdForAdminAssign(int PKNoUser, boolean newValue) {
		return "UPDATE "+TABLE_NAME+" SET user_admin ="+Utilities.formatSQL(newValue)+" WHERE PKNoUser="+Utilities.formatSQL(PKNoUser);
	}
	/**
	 * Requête de validation d'un compte utilisateur
	 * @param newValue true pour valider
	 * @return Requête SQL
	 */
	public static String getUpdForValidation(int PKNoUser, boolean newValue) {
		return "UPDATE "+TABLE_NAME+" SET user_validation ="+Utilities.formatSQL(newValue)+" WHERE PKNoUser="+Utilities.formatSQL(PKNoUser);
	}
	/**
	 * Requête de suppression d'un utilisateur
	 * @param pkNoUser Clé primaire de l'utilisateur
	 * @return Requête SQL
	 */
	public static String getDeleteUser(int pkNoUser) {
		return "DELETE FROM "+TABLE_NAME+" WHERE PKNoUser="+Utilities.formatSQL(pkNoUser);
	}
}
