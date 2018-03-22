package main;


import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Contact;
import model.Folder;

import toolbox.Mailer;
import toolbox.Sql;
import toolbox.Utilities;

/**
 * Projet de suppression des fichiers et d'archivage des dossiers expirés de l'application CCCVs-Transfert.<br>
 * Cette classe contient la méthode exécutable qui est lancée lors du lancement de l'archive JAR.<br>
 * Vous trouvez ce projet sur le serveur de production au format JAR, sur /opt/tomcat7/work/CCCVsTransfert-cron/.<br>
 * Lorsqu'une nouvelle version doit être exportée, il faut choisir Jar exécutable !
 * @author Dominique Roduit
 *
 */
public class RemoveArchive {
	/**
	 * Méthode appelée lors de l'exécution du projet
	 */
	public static void main(String[] args) {
		// local : Global.UPLOAD_DIR =  "/wamp/www/files/";
		
		// Date d'expiration arrive bientôt -> Notification pour avertissement
		RemoveArchive.warningBeforeExpiration();
		// Date d'expiration arrivée, suppression
		RemoveArchive.deleteFilesFolderAndMail();
	}
	/**
	 * Avertissement des destinataires et de l'auteur du transfert.<br>
	 * On leur envoie un mail qui leur dit que leur dossier va bientôt expirer.
	 */
	public static void warningBeforeExpiration() {
		// Sélection des dossiers qui vont être supprimés dans moins de 24h et qui ont une durée de vie > 1 jour
		ResultSet folder = Sql.query(
			"SELECT * FROM trans_folders "+
			"LEFT JOIN trans_users ON PKNoUser=FKNoUser "+
			"WHERE folder_expiration<ADDDATE(now(),"+Global.getParm("DAY_BEFORE_FOLDER_DELETION")+") "+ // Qui sera supprimé dans < 24h
			"AND DATEDIFF(folder_expiration,folder_creation_date)>1 "+ // Qui a une durée de vie > 1 jour
			"AND DATEDIFF(folder_expiration,now())>0 "+ // Qui n'expire pas aujourd'hui même
			"AND folder_archive=0 "+ // Qui ne sont pas encore archivés
			"AND HOUR(folder_expiration)=HOUR(now()) " // Qui vont expirés pile un jour après maintenant (A l'heure d'exécution du script)
		);
		
		try {
			if(folder.first()) {
				folder.beforeFirst();
				
				while(folder.next()) {
					Folder modelFolder = new Folder(
							folder.getInt("PKNoFolder"),
							folder.getInt("FKNoUser"),
							folder.getString("folder_name"),
							folder.getString("folder_description"),
							folder.getString("folder_creation_date"),
							folder.getString("folder_expiration"),
							folder.getBoolean("folder_archive"),
							folder.getString("user_mail")
					);
					
					// Envoi d'un mail à l'AUTEUR
					Mailer.sendMailAuthorBeforeRemovingFolder(
							folder.getString("user_mail"),
							modelFolder,
							folder.getString("user_langue")
					);
					
					// Envoi d'un mail aux CONTACTS AYANT ACCES AU DOSSIER
					ResultSet contact = Sql.query(
						"SELECT * FROM trans_recipients " +
						"LEFT JOIN trans_contacts ON PKNoContact=FKNoContact " +
						"WHERE FKNoFolder="+folder.getString("PKNoFolder")
					);
					while(contact.next()) {
						Contact modelContact = new Contact(
							contact.getInt("PKNoContact"),
							contact.getString("contact_name"),
							contact.getString("contact_mail"),
							contact.getBoolean("contact_internal")
						);
						
						Mailer.sendMailRecipientBeforeRemovingFolder(
								modelContact,
								folder.getString("user_mail"),
								modelFolder
						);
					}
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Suppression des fichiers et archivage des dossiers expirés.<br>
	 * Envoi des mails aux destinataires et à l'auteur pour les prévenir de l'expiration.
	 */
	public static void deleteFilesFolderAndMail() {		
		// Sélection des dossiers dont la date d'expiration est inférieur à la date actuelle
		ResultSet folder = Sql.query(
			"SELECT * FROM trans_folders " +
			"LEFT JOIN trans_users ON PKNoUser=FKNoUser "+
			"WHERE folder_expiration<=now() AND folder_archive=0"
		);
		
		int nbDossier = Sql.rowCount();
		
		try {
			String sqlFolder = "";
			
			if(folder.first()) {
				folder.beforeFirst();
				
				while(folder.next()) {
					Folder modelFolder = new Folder(
							folder.getInt("PKNoFolder"),
							folder.getInt("FKNoUser"),
							folder.getString("folder_name"),
							folder.getString("folder_description"),
							folder.getString("folder_creation_date"),
							folder.getString("folder_expiration"),
							folder.getBoolean("folder_archive"),
							folder.getString("user_mail")
					);
					
					// Archivage du dossier
					System.out.println("\n");
					sqlFolder = "UPDATE trans_folders SET folder_archive=1 WHERE PKNoFolder="+folder.getString("PKNoFolder");
					Sql.exec(sqlFolder);
					
					// Suppression des fichiers
					ResultSet file = Sql.query("SELECT * FROM trans_files WHERE FKNoFolder="+folder.getString("PKNoFolder"));
					System.out.println("\n\nParcours : "+Global.UPLOAD_DIR+"\n\n");
					while(file.next()) {
						File f = new File(Global.UPLOAD_DIR+file.getString("file_name"));
						modelFolder.addFile_list(file.getString("file_rename"));
						
						if(f.exists()) {
							f.delete();
							System.out.println("Supprime : "+file.getString("file_name"));
						} else {
							System.out.println("Existe pas : "+file.getString("file_name"));
						}
					}
					
					// Suppression de l'archive du dossier
					File archive = new File(Global.UPLOAD_DIR+Utilities.getFolderArchiveName(folder.getString("folder_name"), folder.getString("folder_creation_date")));
					if(archive.exists()) {
						archive.delete();
						System.out.println("Archive supprimée : "+archive.getName());
					} else {
						System.out.println("Archive non existante : "+archive.getName());
					}
					
					// Envoi du mail à l'auteur du dossier
					Mailer.sendMailAuthorExpirationFolder(
							folder.getString("user_mail"),
							modelFolder,
							folder.getString("user_langue")
					);
					
					
					// Envoi du mail au destinataires qui ont accès au dossier
					ResultSet recipient = Sql.query("SELECT * FROM trans_recipients " +
							"LEFT JOIN trans_contacts ON PKNoContact=FKNoContact " +
							"WHERE FKNoFolder="+folder.getString("PKNoFolder"));
					
					while(recipient.next()) {
						Contact contact = new Contact(
								recipient.getInt("PKNoContact"),
								recipient.getString("contact_name"),
								recipient.getString("contact_mail"),
								recipient.getBoolean("contact_internal")
						);
						
						modelFolder.addRecipients(contact);
						Mailer.sendMailRecipientExpirationFolder(contact, folder.getString("user_mail"), modelFolder);
					}
					
					
				}
				
				System.out.println(nbDossier+" dossiers affectés");
				
			} else {
				System.out.println("Aucun dossier a archiver");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
