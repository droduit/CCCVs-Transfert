package sql.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import global.Global;
import toolbox.Sql;
import toolbox.Utilities;

/**
 * JOURNALISATION<br>
 * Contient toutes les requ�tes effectu�es sur la table <b>trans_journal_connections</b>
 * @author Dominique Roduit
 *
 */
public class tbl_journal_con {
	/** Nom de la table sur laquelle on effectue des op�rations dans cette class **/
	private static final String TABLE_NAME = "trans_journal_connections";
	/**
	 * Insertion d'une entr�e dans le journal des connexions
	 * @param email E-mail de l'utilisateur
	 * @param action Action effectu�e / Evenement
	 * @param comment Commentaire sur l'action effectu�e
	 * @return Requ�te SQL format�e
	 */
	public static String getInsertQuery(String email, String action, String comment) {
		String ip = (Global.browser.getAddress()!=null) ? Global.browser.getAddress() : "";
		String os = (Utilities.getOS()!=null) ? Utilities.getOS() : "";
		String browser = (Utilities.getBrowserAndVersion()!=null) ? Utilities.getBrowserAndVersion() : "";
		
		return "INSERT INTO "+TABLE_NAME+" VALUES(NULL, "+Utilities.formatSQL(email)+", now(), "+Utilities.formatSQL(ip)+", "+Utilities.formatSQL(os)+", "+Utilities.formatSQL(browser)+", "+Utilities.formatSQL(action)+", "+Utilities.formatSQL(comment)+")";
	}
	/**
	 * Retourne le nombres de fois qu'un utilisateur a tent� de se connecter sans valider son compte
	 * @param email Adresse e-mail de l'utilisateur
	 * @return Nombre de connection sans avoir valid� le compte
	 */
	public static int getNumberOfConnexionsWhithoutValidation(String email) {
		ResultSet data = Sql.query("SELECT COUNT(*) AS NUMBER FROM "+TABLE_NAME+" WHERE joco_mail="+Utilities.formatSQL(email)+" AND joco_action='con_no_validate'");
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
	 * Requ�te pour la s�lection des journalisations sur les connexions
	 * @return Requ�te SQL
	 */
	public static String getSelectConnexions(String where) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE joco_action LIKE 'con_%' "+where+" ORDER BY joco_date DESC LIMIT 100";
	}
	/**
	 * Requ�te pour la s�lection des journalisations sur les validations des comptes
	 * @return Requ�te SQL
	 */
	public static String getSelectValidations(String where) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE joco_action LIKE 'validation_%' "+where+" ORDER BY joco_date DESC LIMIT 100";
	}
	
}
