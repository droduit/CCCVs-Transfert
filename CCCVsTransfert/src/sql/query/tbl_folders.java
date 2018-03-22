package sql.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import global.Global;
import global.UserSession;
import toolbox.Sql;
import toolbox.Utilities;
/**
 * Contient toutes les requêtes effectuées sur la table <b>trans_folders</b>
 * @author Dominique Roduit
 *
 */
public class tbl_folders {
	/** Nom de la table sur laquelle on effectue des opérations dans cette class **/
	private static final String TABLE_NAME = "trans_folders";
	/**
	 * Requête pour la sélection de la liste des contacts
	 * @return (String) Requête SQL Formatée
	 */
	public static String getAllFolders() {
		return "SELECT * FROM "+TABLE_NAME+" WHERE FKNoUser="+Utilities.formatSQL(UserSession.getID())+" ORDER BY folder_archive, folder_creation_date DESC";
	}
	/**
	 * Requête pour la suppression d'un dossier
	 * @param PK La clé primaire du dossier à supprimer
	 * @return Requête SQL Formatée
	 */
	public static String getDeleteFolder(String PK) {
		return "DELETE FROM "+TABLE_NAME+" WHERE PKNoFolder="+Utilities.formatSQL(PK);
	}
	/**
	 * Requête pour l'insertion d'un dossier
	 * @param name Nom du dossier
	 * @param expiration Nombre de jour ou nous mettons a disposition le dossier
	 * @return Requête SQL
	 */
	public static String getInsertFolder(String name, double expiration, String description) {
		int expirationInt = (int)expiration;
		return "INSERT INTO "+TABLE_NAME+" VALUES(NULL, "+Utilities.formatSQL(UserSession.getID())+", "+Utilities.formatSQL(name)+", "+Utilities.formatSQL(description)+", now(), DATE_ADD(NOW(), INTERVAL "+expirationInt+" DAY), 0)";
	}
	/**
	 * Requête pour la récupération du dernier dossier créé par l'utilisateur
	 * @return La PK du dernier dossier créé par l'utilisateur
	 */
	public static String getSelectMaxPK() {
		return "SELECT MAX(PKNoFolder) AS PKNoFolder FROM "+TABLE_NAME+" WHERE FKNoUser="+Utilities.formatSQL(UserSession.getID());
	}
	/**
	 * Retourne les informations sur un dossier
	 * @param pKNoFolder Clé primaire du dossier
	 * @return Requête SQL
	 */
	public static String getSelectFolderInfos(int pKNoFolder) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE PKNoFolder="+Utilities.formatSQL(pKNoFolder);
	}
	/**
	 * Retourne le nombre de dossiers d'un utilisateur
	 * @return Requête SQL
	 */
	public static int getNumberOfFolders() {
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
	 * Retourne le nombre de jour ou le dossier est disponible (Date d'expiration-Date de création)
	 * @param PKNoFolder Clé primaire du dossier
	 * @return Nombre de jour de disponibilité du dossier
	 */
	public static int getNumberOfDayFolderIsAvailable(int PKNoFolder) {
		ResultSet data = Sql.query("SELECT DATEDIFF(folder_expiration,folder_creation_date) AS NUMBER FROM "+TABLE_NAME+" WHERE PKNoFolder="+Utilities.formatSQL(PKNoFolder));
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
	 * Retourne le nombre de jours restant pour un dossier avant son expiration
	 * @param PKNoFolder Clé primaire du dossier
	 * @return Durée de vie restante du dossier
	 */
	public static int getRemainingDaysFolderIsAvailable(int PKNoFolder) {
		ResultSet data = Sql.query("SELECT DATEDIFF(folder_expiration,NOW()) AS NUMBER FROM "+TABLE_NAME+" WHERE PKNoFolder="+Utilities.formatSQL(PKNoFolder));
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
	 * Retourne le nombde d'heures restantes avant l'expiration d'un dossier
	 * @param PKNoFolder Clé primaire du dossier
	 * @return Heures restantes avant la destruction du dossier
	 */
	public static int getRemainingHoursFolderIsAvailable(int PKNoFolder) {
		ResultSet data = Sql.query("SELECT ABS(TIMESTAMPDIFF( HOUR , folder_expiration, NOW( ) )) AS NUMBER FROM "+TABLE_NAME+" WHERE PKNoFolder="+Utilities.formatSQL(PKNoFolder));
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
	 * Sélection d'un dossier en fonction d'un paramètre crypté passé dans l'URL
	 * @param URLParameter Valeur du paramètre de l'URL
	 * @return Requête SQL
	 */
	public static String getFolderByCryptedURL(String URLParameter) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE SHA2(CONCAT('"+Global.getParm("SECRET_KEY")+"', PKNoFolder), 256)="+Utilities.formatSQL(URLParameter)+"";
	}

}
