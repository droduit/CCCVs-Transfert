package sql.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import global.Global;
import toolbox.Sql;
import toolbox.Utilities;

/**
 * JOURNALISATION<br>
 * Contient toutes les requ�tes effectu�es sur la table <b>trans_journal_download</b>
 * @author Dominique Roduit
 *
 */
public class tbl_journal_fold {
	/** Nom de la table sur laquelle on effectue des op�rations dans cette class **/
	private static final String TABLE_NAME = "trans_journal_folders";
	/**
	 * Insertion d'une entr�e dans le journal des t�l�chargements
	 * @param FKNoContact Cl� �trang�re vers le contact
	 * @param FKNoFolder Cl� �trang�re vers le dossier
	 * @param FKNoFile Cl� �trang�re vers le fichier
	 * @return Requ�te SQL format�e
	 */
	public static String getInsertQuery(String action, int FKNoContact, int FKNoFolder, int FKNoFile) {
		String file = (FKNoFile==0) ? "NULL" : Utilities.formatSQL(FKNoFile);
		return "INSERT INTO "+TABLE_NAME+" VALUES(NULL, "+Utilities.formatSQL(FKNoContact)+", "+Utilities.formatSQL(FKNoFolder)+", "+file+", "+Utilities.formatSQL(action)+", now())";
	}
	/**
	 * Retourne la requ�te de s�lection des informations journalis�es pour un contact pr�cis�
	 * @param pKNoContact Cl� primaire du contact
	 * @return Requ�te SQL
	 */
	public static String getSelectAllForContact(int pKNoContact) {
		return "SELECT * FROM "+TABLE_NAME+" " +
				"LEFT JOIN trans_files ON PKNoFile=FKNoFile " +
				"WHERE FKNoContact="+Utilities.formatSQL(pKNoContact);
	}
	/**
	 * Retourne la requ�te de s�lection des informations journalis�es sur un dossier, pour un contact pr�cis�
	 * @param pKNoContact Cl� primaire du contact
	 * @param PKNoFolder Cl� primaire du dossier
	 * @return Requ�te SQL
	 */
	public static String getSelectAllForContactAndFolder(int pKNoContact, int PKNoFolder) {
		return "SELECT * FROM "+TABLE_NAME+" " +
				"LEFT JOIN trans_files ON PKNoFile=FKNoFile " +
				"WHERE FKNoContact="+Utilities.formatSQL(pKNoContact)+" AND "+TABLE_NAME+".FKNoFolder="+Utilities.formatSQL(PKNoFolder);
	}
	/**
	 * Requ�te de s�lection de toutes les informations journalis�es sur les dossiers
	 * @param where Clause WHERE de la requ�te
	 * @return Requ�te SQL
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
