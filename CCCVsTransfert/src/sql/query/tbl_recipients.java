package sql.query;

import toolbox.Utilities;

/**
 * Contient toutes les requ�tes effectu�es sur la table <b>trans_recipients</b>
 * @author Dominique Roduit
 *
 */
public class tbl_recipients {
	/** Nom de la table sur laquelle on effectue des op�rations dans cette class **/
	private static final String TABLE_NAME = "trans_recipients";
	
	/** Requ�te d'insertion pour les destinataires **/
	public static String getInsertRecipient(int FKNoFolder, int FKNoContact) {
		return "INSERT INTO "+TABLE_NAME+" VALUES (NULL, "+Utilities.formatSQL(FKNoFolder)+", "+Utilities.formatSQL(FKNoContact)+")";
	}
	/**
	 * Requ�te de s�lection des destinataire pour un dossier
	 * @param pKNoFolder PK du dossier
	 * @return Requ�te SQL
	 */
	public static String getSelectRecipientsFromFolder(int pKNoFolder) {
		return "SELECT * FROM "+TABLE_NAME+" " +
				"LEFT JOIN trans_contacts ON FKNoContact=PKNoContact "+
				"WHERE FKNoFolder="+Utilities.formatSQL(pKNoFolder);
	}
}
