package sql.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import global.Global;
import global.UserSession;
import toolbox.Sql;
import toolbox.Utilities;
/**
 * Cette class contient toutes les requêtes SQL utiles à la gestion de la table des contacts.
 * @author Dominique Roduit
 *
 */
public class tbl_contacts {
	/** Nom de la table sur laquelle on effectue des opérations dans cette class **/
	private static final String TABLE_NAME = "trans_contacts";
	/**
	 * Requête pour la sélection de la liste des contacts
	 * @return (String) Requête SQL Formatée
	 */
	public static String getSelectAllContactQuery() {
		return "SELECT * FROM "+TABLE_NAME+" WHERE FKNoUser="+Utilities.formatSQL(UserSession.getID());
	}
	/**
	 * Requête pour la sélection d'un contact par sa clé primaire
	 * @param PK (String) Clé primaire du contact
	 * @return (String) Requête SQL Formatée
	 */
	public static String getSelectByPKContactQuery(String PK) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE PKNoContact="+PK;
	}
	/**
	 * Requête pour la suppression d'un contact par sa clé primaire
	 * @param PK (String) Clé primaire du contact
	 * @return (String) Requête SQL Formatée
	 */
	public static String getDeleteContactQuery(String PK) {
		return "DELETE FROM "+TABLE_NAME+" WHERE PKNoContact="+Utilities.formatSQL(PK);
	}
	/**
	 * Requête pour l'insertion d'un contact
	 * @param name (String) Nom du contact
	 * @param email (String) E-mail du contact
	 * @return (String) Requête SQL Formatée
	 */
	public static String getInsertContactQuery(String name, String email) {
		return "INSERT INTO "+TABLE_NAME+" VALUES " +
			   "(NULL, "+Utilities.formatSQL(UserSession.getID())+", "+Utilities.formatSQL(name)+", "+Utilities.formatSQL(email)+", now(), "+Utilities.formatSQL(Utilities.isInternal(email))+")";
	}
	/**
	 * Requête pour la mise à jour d'un contact
	 * @param PK (String) Clé primaire du contact
	 * @param name (String) Nom du contact
	 * @param email (String) E-mail du contact
	 * @return (String) Requête SQL Formatée
	 */
	public String getUpdateContactQuery(String PK, String name, String email) {
		return "UPDATE "+TABLE_NAME+" SET " +
		"contact_name="+Utilities.formatSQL(name)+", " +
		"contact_mail="+Utilities.formatSQL(email)+", " +
		"contact_internal="+Utilities.formatSQL(Utilities.isInternal(email))+", "+
		"contact_creation_date=now() " +
		"WHERE FKNoUser="+Utilities.formatSQL(UserSession.getID())+" AND PKNoContact="+Utilities.formatSQL(PK);
	}
	/**
	 * Retourne l'ID du dernier contact
	 * @return ID du dernier contact
	 */
	public static String getLastContact() {
		return "SELECT MAX(PKNoContact) AS LAST FROM "+TABLE_NAME+" WHERE FKNoUser="+Utilities.formatSQL(UserSession.getID());
	}
	/**
	 * Retourne le nombre de contact dans la liste d'un utilisateur
	 * @return Requête SQL
	 */
	public static int getNumberOfContact() {
		ResultSet data = Sql.query("SELECT COUNT(*) AS NUMBER FROM "+TABLE_NAME+" WHERE FKNoUser="+Utilities.formatSQL(UserSession.getID()));
		int number = 0;
		try {
			if(data.first()) {
				number = data.getInt("NUMBER");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return number;
	}
	/**
	 * Sélection d'un contact en fonction d'un paramètre crypté passé dans l'URL
	 * @param URLParameter Valeur du paramètre de l'URL
	 * @return Requête SQL
	 */
	public static String getContactByCryptedURL(String URLParameter) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE SHA2(CONCAT('"+Global.getParm("SECRET_KEY")+"', PKNoContact), 256)="+Utilities.formatSQL(URLParameter)+"";
	}
	/**
	 * Sélection des contacts attribués comme destinataires d'un dossier
	 * @param PKNoFolder Clé primaire du dossier
	 * @return Requête SQL
	 */
	public static String getSelectContactsFromFolder(int PKNoFolder) {
		return "SELECT * FROM  trans_recipients LEFT JOIN "+TABLE_NAME+" ON PKNoContact=FKNoContact WHERE FKNoFolder="+Utilities.formatSQL(PKNoFolder)+" ORDER BY contact_name ASC, contact_mail ASC";
	}
}
