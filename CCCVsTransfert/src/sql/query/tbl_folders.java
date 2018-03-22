package sql.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import global.Global;
import global.UserSession;
import toolbox.Sql;
import toolbox.Utilities;
/**
 * Contient toutes les requ�tes effectu�es sur la table <b>trans_folders</b>
 * @author Dominique Roduit
 *
 */
public class tbl_folders {
	/** Nom de la table sur laquelle on effectue des op�rations dans cette class **/
	private static final String TABLE_NAME = "trans_folders";
	/**
	 * Requ�te pour la s�lection de la liste des contacts
	 * @return (String) Requ�te SQL Format�e
	 */
	public static String getAllFolders() {
		return "SELECT * FROM "+TABLE_NAME+" WHERE FKNoUser="+Utilities.formatSQL(UserSession.getID())+" ORDER BY folder_archive, folder_creation_date DESC";
	}
	/**
	 * Requ�te pour la suppression d'un dossier
	 * @param PK La cl� primaire du dossier � supprimer
	 * @return Requ�te SQL Format�e
	 */
	public static String getDeleteFolder(String PK) {
		return "DELETE FROM "+TABLE_NAME+" WHERE PKNoFolder="+Utilities.formatSQL(PK);
	}
	/**
	 * Requ�te pour l'insertion d'un dossier
	 * @param name Nom du dossier
	 * @param expiration Nombre de jour ou nous mettons a disposition le dossier
	 * @return Requ�te SQL
	 */
	public static String getInsertFolder(String name, double expiration, String description) {
		int expirationInt = (int)expiration;
		return "INSERT INTO "+TABLE_NAME+" VALUES(NULL, "+Utilities.formatSQL(UserSession.getID())+", "+Utilities.formatSQL(name)+", "+Utilities.formatSQL(description)+", now(), DATE_ADD(NOW(), INTERVAL "+expirationInt+" DAY), 0)";
	}
	/**
	 * Requ�te pour la r�cup�ration du dernier dossier cr�� par l'utilisateur
	 * @return La PK du dernier dossier cr�� par l'utilisateur
	 */
	public static String getSelectMaxPK() {
		return "SELECT MAX(PKNoFolder) AS PKNoFolder FROM "+TABLE_NAME+" WHERE FKNoUser="+Utilities.formatSQL(UserSession.getID());
	}
	/**
	 * Retourne les informations sur un dossier
	 * @param pKNoFolder Cl� primaire du dossier
	 * @return Requ�te SQL
	 */
	public static String getSelectFolderInfos(int pKNoFolder) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE PKNoFolder="+Utilities.formatSQL(pKNoFolder);
	}
	/**
	 * Retourne le nombre de dossiers d'un utilisateur
	 * @return Requ�te SQL
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
	 * Retourne le nombre de jour ou le dossier est disponible (Date d'expiration-Date de cr�ation)
	 * @param PKNoFolder Cl� primaire du dossier
	 * @return Nombre de jour de disponibilit� du dossier
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
	 * @param PKNoFolder Cl� primaire du dossier
	 * @return Dur�e de vie restante du dossier
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
	 * @param PKNoFolder Cl� primaire du dossier
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
	 * S�lection d'un dossier en fonction d'un param�tre crypt� pass� dans l'URL
	 * @param URLParameter Valeur du param�tre de l'URL
	 * @return Requ�te SQL
	 */
	public static String getFolderByCryptedURL(String URLParameter) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE SHA2(CONCAT('"+Global.getParm("SECRET_KEY")+"', PKNoFolder), 256)="+Utilities.formatSQL(URLParameter)+"";
	}

}
