package sql.query;

import global.Global;
import global.UserSession;
import toolbox.Utilities;

/**
 * Contient toutes les requ�tes effectu�es sur la table <b>trans_users</b>
 * @author Dominique Roduit
 *
 */
public class tbl_users {
	/** Nom de la table sur laquelle on effectue des op�rations dans cette class **/
	private static final String TABLE_NAME = "trans_users";
	/**
	 * S�lection d'un utilisateur par son adresse e-mail
	 * @param email Adresse de l'utilisateur
	 * @return Requ�te SQL
	 */
	public static String getUserByEmail(String email) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE user_mail="+Utilities.formatSQL(email);
	}
	/**
	 * Mise � jour du champs de validation du compte utilisateur
	 * @param email Adresse de l'utilisateur qui valide son compte
	 * @return Requ�te SQL
	 */
	public static String getUpdByMailValidation(String email) {
		return "UPDATE "+TABLE_NAME+" SET user_validation=1, user_validation_date=now() WHERE user_mail="+Utilities.formatSQL(email);
	}
	/**
	 * S�lection d'un utilisateur en fonction d'un param�tre crypt� pass� dans l'URL
	 * @param URLParameter Valeur du param�tre de l'URL
	 * @return Requ�te SQL
	 */
	public static String getUserByCryptedURL(String URLParameter) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE SHA2(CONCAT('"+Global.getParm("SECRET_KEY")+"', user_mail), 256)="+Utilities.formatSQL(URLParameter)+"";
	}
	/**
	 * Retourne la requ�te d'insertion d'un utilisateur
	 * @param email Adresse e-mail
	 * @param pass Mot de passe
	 * @param internal L'utilisateur est un interne ou non
	 * @return Requ�te SQL
	 */
	public static String getInsertQuery(String email, String pass, boolean isInternal) {
		// Par d�faut, le compte n'est pas valid�
		boolean account_valid = false;
		
		return 
		"INSERT INTO "+TABLE_NAME+" VALUES(NULL, "+Utilities.formatSQL(email)+", "+
		Utilities.formatSQL(pass)+"," +
		Utilities.formatSQL(isInternal)+", "+Utilities.formatSQL(account_valid)+", now(), "+
		Utilities.formatSQL(Global.browser.getLocale().toString().substring(0,2))+", 0)";
	}
	/**
	 * Requ�te pour la mise � jour de la langue de l'utilisateur
	 * @param newLanguage Langue choisie par l'utilisateur
	 * @return Requ�te SQL
	 */
	public static String getUpdLanguage(String newLanguage) {
		return "UPDATE "+TABLE_NAME+" SET user_langue="+Utilities.formatSQL(newLanguage)+" WHERE PKNoUser="+UserSession.getID();
	}
	/**
	 * Requ�te pour la s�lection des informations sur un utilisateur
	 * @param PKNoUser Cl� primaire de l'utilisateur
	 * @return Requ�te SQL
	 */
	public static String getSelectUserInfos(int PKNoUser) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE PKNoUser="+Utilities.formatSQL(PKNoUser);
	}
	/**
	 * S�lection de tous les contacts de l'interface utilisateur
	 * @return Requ�te SQL
	 */
	public static String getSelectAll() {
		return "SELECT * FROM "+TABLE_NAME+" ORDER BY user_internal ASC, PKNoUser";
	}
	/**
	 * Requ�te de mise � jour d'un utilisateur (Admin ou pas)
	 * @param newValue true si admin
	 * @return Requ�te SQL
	 */
	public static String getUpdForAdminAssign(int PKNoUser, boolean newValue) {
		return "UPDATE "+TABLE_NAME+" SET user_admin ="+Utilities.formatSQL(newValue)+" WHERE PKNoUser="+Utilities.formatSQL(PKNoUser);
	}
	/**
	 * Requ�te de validation d'un compte utilisateur
	 * @param newValue true pour valider
	 * @return Requ�te SQL
	 */
	public static String getUpdForValidation(int PKNoUser, boolean newValue) {
		return "UPDATE "+TABLE_NAME+" SET user_validation ="+Utilities.formatSQL(newValue)+" WHERE PKNoUser="+Utilities.formatSQL(PKNoUser);
	}
	/**
	 * Requ�te de suppression d'un utilisateur
	 * @param pkNoUser Cl� primaire de l'utilisateur
	 * @return Requ�te SQL
	 */
	public static String getDeleteUser(int pkNoUser) {
		return "DELETE FROM "+TABLE_NAME+" WHERE PKNoUser="+Utilities.formatSQL(pkNoUser);
	}
}
