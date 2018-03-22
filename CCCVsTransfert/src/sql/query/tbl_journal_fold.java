package sql.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import global.Global;
import toolbox.Sql;
import toolbox.Utilities;

/**
 * JOURNALISATION<br>
 * Contient toutes les requêtes effectuées sur la table <b>trans_journal_download</b>
 * @author Dominique Roduit
 *
 */
public class tbl_journal_fold {
	/** Nom de la table sur laquelle on effectue des opérations dans cette class **/
	private static final String TABLE_NAME = "trans_journal_folders";
	/**
	 * Insertion d'une entrée dans le journal des téléchargements
	 * @param FKNoContact Clé étrangère vers le contact
	 * @param FKNoFolder Clé étrangère vers le dossier
	 * @param FKNoFile Clé étrangère vers le fichier
	 * @return Requête SQL formatée
	 */
	public static String getInsertQuery(String action, int FKNoContact, int FKNoFolder, int FKNoFile) {
		String file = (FKNoFile==0) ? "NULL" : Utilities.formatSQL(FKNoFile);
		return "INSERT INTO "+TABLE_NAME+" VALUES(NULL, "+Utilities.formatSQL(FKNoContact)+", "+Utilities.formatSQL(FKNoFolder)+", "+file+", "+Utilities.formatSQL(action)+", now())";
	}
	/**
	 * Retourne la requête de sélection des informations journalisées pour un contact précisé
	 * @param pKNoContact Clé primaire du contact
	 * @return Requête SQL
	 */
	public static String getSelectAllForContact(int pKNoContact) {
		return "SELECT * FROM "+TABLE_NAME+" " +
				"LEFT JOIN trans_files ON PKNoFile=FKNoFile " +
				"WHERE FKNoContact="+Utilities.formatSQL(pKNoContact);
	}
	/**
	 * Retourne la requête de sélection des informations journalisées sur un dossier, pour un contact précisé
	 * @param pKNoContact Clé primaire du contact
	 * @param PKNoFolder Clé primaire du dossier
	 * @return Requête SQL
	 */
	public static String getSelectAllForContactAndFolder(int pKNoContact, int PKNoFolder) {
		return "SELECT * FROM "+TABLE_NAME+" " +
				"LEFT JOIN trans_files ON PKNoFile=FKNoFile " +
				"WHERE FKNoContact="+Utilities.formatSQL(pKNoContact)+" AND "+TABLE_NAME+".FKNoFolder="+Utilities.formatSQL(PKNoFolder);
	}
	/**
	 * Requête de sélection de toutes les informations journalisées sur les dossiers
	 * @param where Clause WHERE de la requête
	 * @return Requête SQL
	 */
	public static String getSelectAll(String where) {
		String Where = (!where.isEmpty()) ? " WHERE "+where+ " " : "";
		return "SELECT * FROM "+TABLE_NAME+" "+
			   "LEFT JOIN trans_contacts ON PKNoContact=FKNoContact "+
			   "LEFT JOIN trans_files ON PKNoFile=FKNoFile "+
			   "LEFT JOIN trans_folders ON PKNoFolder="+TABLE_NAME+".FKNoFolder "+
			   "LEFT JOIN trans_users ON trans_folders.FKNoUser = PKNoUser "+Where+
			   "LIMIT 100";
	}
	
}
