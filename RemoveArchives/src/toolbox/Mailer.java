package toolbox;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import main.Global;
import model.Contact;
import model.Folder;

/**
 * Class qui répertorie différents outils relatifs à l'utilisation des adresses e-mail<br>
 * <b>Outils principaux :</b><br>
 * <ul>
 * <li>Envoi de mails en java via le protocole SMPT (requis : librairie javamail).</li>
 * <li>Validation et contrôles de formats d'adresses</li>
 * </ul>
 * @author Dominique Roduit
 *
 */
public class Mailer {
	/**
	 * Nom ou adresse ip du serveur SMTP
	 */
	private final static String SERVER = "localhost";
	/**
	 * Suffixe d'une addresse e-mail standard de la CCCVs
	 */
	private final static String SUFFIXE_FORMAT = Global.getParm("EMAIL_INTERNE_SUFFIXE");
	/**
	 * Adresse mail utilisée pour envoyer des messages automatiques aux utilisateurs
	 */
	public final static String APP_MAIL_FROM = Global.getParm("APP_MAIL_FROM");
	/**
	 * Nom d'affichage de l'adresse e-mail définie par APP_MAIL_FROM
	 */
	public final static String APP_MAIL_FROM_NAME = Global.getParm("APP_MAIL_FROM_NAME");
	/**
	 * Envoi d'un mail au(x) destinataire(s) spécifié(s).
	 * @param from (String) Auteur du mail
	 * @param from_name (String) Nom de l'auteur du mail
	 * @param to (String) Destinataires (séparés par des virgules)
	 * @param subject (String) Sujet du message
	 * @param content (String) Contenu du mail au format HTML
	 */
	public static void send(String from, String from_name, String to, String subject, String content) {
		try {
            Properties prop = System.getProperties();
            prop.put("mail.smtp.host", SERVER);
            prop.put("mail.mime.charset", "UTF-8");
            
            Session session = Session.getInstance(prop);
            Message message = new MimeMessage(session);
            
            // Auteur 
            message.setFrom(new InternetAddress(from,from_name));
           
            // Destinataire(s)
            InternetAddress[] internetAddresses;
            if(to.indexOf(",")>-1) {
            	String[] to_array = to.split(",");
            	internetAddresses = new InternetAddress[to_array.length+1];
            	for (int i=0; i<to_array.length; i++) {
					internetAddresses[i] = new InternetAddress(to_array[i]);
				}
            } else {
            	internetAddresses = new InternetAddress[1];
            	internetAddresses[0] = new InternetAddress(to);
            }
            message.setRecipients(Message.RecipientType.TO,internetAddresses);
           
            // Sujet
            message.setSubject(new String(subject.getBytes(), "UTF-8"));
           
            // Contenu du mail
            message.setContent(new String(content.getBytes(), "UTF-8"), "text/html; charset=UTF-8");
          
            message.setSentDate(new Date());
            session.setDebug(true);
            
            Transport.send(message);
		} catch(NoSuchProviderException e) {
            System.err.println("Pas de transport disponible pour ce protocole");
            System.err.println(e);
        }
        catch(AddressException e) {
            System.err.println("Adresse invalide");
            System.err.println(e);
        }
        catch(MessagingException e) {
            System.err.println("Erreur dans le message");
            System.err.println(e);
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Envoi d'un mail automatique, l'adresse de l'auteur reste fixe et le nom aussi
	 * @param to Destinataires séparés par des virgules
	 * @param subject Sujet du message
	 * @param content Contenu du mail
	 */
	public static void send(String to, String subject, String content) {
		send(APP_MAIL_FROM, APP_MAIL_FROM_NAME, to, subject, content);
	}

	/**
	 * Mail envoyé à l'auteur d'un dossier avant la suppression de celui-ci.
	 * @param to Adresse e-mail de l'auteur du dossier
	 * @param folder Informations sur le dossier
	 * @param langue Langue de l'utilisateur
	 */
	public static void sendMailAuthorBeforeRemovingFolder(String to, Folder folder, String langue) {	
		String history = getHistoryForContactsOfFolder(folder, langue);
		
		// Contenu du mail
		String mailContent =
		Global.getParm("MAIL_HEADER_CONTENT")+
		Global.getParm("MAIL_AUTHOR_BEFORE_REMOVING_CONTENT")
		.replace("%nom%", folder.getName())
		.replace("%date%", Utilities.formatSQLDate(folder.getExpiration_date(),"dd.MM.YY"))
		.replace("%heure%", Utilities.zeroFill(Integer.parseInt(Utilities.formatSQLDate(folder.getExpiration_date(),"HH"))+1)+":00")
		.replace("%historique%",history)+
		Global.getParm("MAIL_FOOTER_CONTENT"); 
		
		Mailer.send(to, Global.i("SUBJECT_MAIL_AUTHOR_BEFORE_REMOVING_FOLDER", langue), mailContent);
	}
	/**
	 * Mail envoyé à chacun des contacts qui avaient accès au dossier expiré
	 * @param to Adresse e-mail du contact
	 * @param folder Informations sur le dossier
	 * @param langue Langue de l'utilisateur
	 */
	public static void sendMailRecipientBeforeRemovingFolder(Contact contact, String mail_author, Folder folder) {
		String langue = "fr";
		
		// Actions journalisées pour ce contact
		String history = getHistoryOfContactFromFolder(contact, folder, langue);
		
		// Contenu du mail
		String mailContent =
		Global.getParm("MAIL_HEADER_CONTENT")+
		Global.getParm("MAIL_RECIPIENT_BEFORE_REMOVING_CONTENT")
		.replace("%date%", Utilities.formatSQLDate(folder.getExpiration_date(),"dd.MM.YY"))
		.replace("%author%", mail_author)
		.replace("%nom%", folder.getName())
		.replace("%heure%", Utilities.zeroFill(Integer.parseInt(Utilities.formatSQLDate(folder.getExpiration_date(),"HH"))+1)+":00")
		.replace("%historique%",history)+
		Global.getParm("MAIL_FOOTER_CONTENT"); 
		
		Mailer.send(contact.getMail(), Global.i("SUBJECT_MAIL_RECIPIENT_BEFORE_REMOVING_FOLDER", langue), mailContent);
	}
	/**
	 * Mail envoyé à l'auteur d'un dossier lors de la suppression des fichiers et l'expiration de celui-ci.
	 * @param to Adresse e-mail de l'auteur du dossier
	 * @param modelFolder Informations sur le dossier
	 * @param langue Langue de l'utilisateur
	 */
	public static void sendMailAuthorExpirationFolder(String to, Folder folder, String langue) {
		// Actions journalisées pour ce contact
		String history = getHistoryForContactsOfFolder(folder, langue);
		
		// Contenu du mail
		String mailContent =
		Global.getParm("MAIL_HEADER_CONTENT")+
		Global.getParm("MAIL_AUTHOR_EXPIRATION_FOLDER_CONTENT")
		.replace("%nom%", folder.getName())
		.replace("%date%", Utilities.formatSQLDate(folder.getExpiration_date(),"dd.MM.YY"))
		.replace("%heure%", Utilities.getDate("HH")+":00")
		.replace("%historique%",history)+
		Global.getParm("MAIL_FOOTER_CONTENT"); 
		

		Mailer.send(to, Global.i("SUBJECT_MAIL_AUTHOR_EXPIRATION_FOLDER", langue), mailContent);
	}
	/**
	 * Mail envoyé à chacun des contacts qui avaient accès au dossier expiré
	 * @param to Adresse e-mail du contact
	 * @param folder Informations sur le dossier
	 * @param langue Langue de l'utilisateur
	 */
	public static void sendMailRecipientExpirationFolder(Contact contact, String mail_author, Folder folder) {
		String langue = "fr";
		
		// Actions journalisées pour ce contact
		String history = getHistoryOfContactFromFolder(contact, folder, langue);
		
		// Contenu du mail
		String mailContent =
		Global.getParm("MAIL_HEADER_CONTENT")+
		Global.getParm("MAIL_RECIPIENT_EXPIRATION_FOLDER_CONTENT")
		.replace("%nom%", contact.getName())
		.replace("%date%", Utilities.formatSQLDate(folder.getExpiration_date(),"dd.MM.YY"))
		.replace("%author%", mail_author)
		.replace("%dossier%", folder.getName())
		.replace("%heure%", Utilities.getDate("HH")+":00")
		.replace("%historique%",history)+
		Global.getParm("MAIL_FOOTER_CONTENT"); 
		
		Mailer.send(contact.getMail(), Global.i("SUBJECT_MAIL_RECIPIENT_EXPIRATION_FOLDER", langue), mailContent);
	}
	/**
	 * Retourne l'historiques de tous les contacts d'un dossier. L'historique Résumé et détaillé.
	 * @param folder Modèle du dossier de référence
	 * @param langue Langue de l'utilisateur
	 * @return Historique complet de tous les contacts d'un dossier
	 */
	private static String getHistoryForContactsOfFolder(Folder folder, String langue) {
		String history = "";
		
		// Sélection des contacts qui ont accès au dossier
		ResultSet contact = Sql.query(
			"SELECT * FROM trans_folders "+
			"LEFT JOIN trans_recipients ON FKNoFolder=PKNoFolder "+
			"LEFT JOIN trans_contacts ON FKNoContact=PKNoContact "+
			"WHERE PKNoFolder="+folder.getPKNoFolder()
		);
		
		// Historique résumé
		try {
			history = "<br><h3>"+Global.i("CAPTION_RESUME_HISTORY", langue)+"</h3><table border=\"0\">";
			while(contact.next()) {
				ResultSet action = Sql.query(
					"SELECT *, COUNT(*) AS NB FROM trans_journal_folders " +
					"LEFT JOIN trans_files ON FKNoFile=PKNoFile " +
					"WHERE FKNoContact="+contact.getString("PKNoContact")+" AND trans_journal_folders.FKNoFolder="+folder.getPKNoFolder()+" "+
					"GROUP BY FKNoFile, joufo_action "+
					"ORDER BY FKNoFile, joufo_date DESC"
				);
				
				history +=
				"<tr><td colspan=\"5\" style=\"background:#000; color:#fff; font-weight: bold;\">"+contact.getString("contact_name")+"</td></tr>";
				
				// S'il existe des actions pour ce contact
				if(action.first()) {
					int i = 0;
					action.beforeFirst();
					
					while(action.next()) {
						i++;
						
						String color = "ddf";
						String name = action.getString("file_rename");
						if(action.getString("FKNoFile")==null) {
							if(action.getString("joufo_action").equals("download")) {
								name = Global.i("CAPTION_ARCHIVE_OF_FOLDER", langue);
								color = "fdd";
							} else {
								name = Global.i("CAPTION_CONSULTATION", langue);
								color = "dfd";
							}
						}
						
						history +=
						"<tr style=\"background:#"+color+"\">"+
							"<td>"+i+" </td>"+
							"<td>&nbsp;  </td>"+
							"<td>"+name+"</td>"+
							"<td>&nbsp;  </td>"+
							"<td>"+action.getString("NB")+"x</td>"+
						"</tr>";
					}
				} else {
					history +=
					"<tr>"+
						"<td colspan=\"5\">"+Global.i("CAPTION_NO_ACCES_AND_DOWNLOAD", langue)+"</td>"+
					"</tr>";
				}
				
			}
			history += "</table>";
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Historique détaillé
		try {
			history += "<br>_______________________________________________________<br><br>" +
					"<h3>"+Global.i("CAPTION_EXPAND_HISTORY", langue)+"</h3><table border=\"0\">";
			contact.beforeFirst();
			while(contact.next()) {
				// Sélection des actions pour chaques contacts
				ResultSet action = Sql.query(
						"SELECT * FROM trans_journal_folders " +
						"LEFT JOIN trans_files ON FKNoFile=PKNoFile " +
						"WHERE FKNoContact="+contact.getString("PKNoContact")+" AND trans_journal_folders.FKNoFolder="+folder.getPKNoFolder()+" "+
						"ORDER BY joufo_date DESC"
				);
				
				history +=
				"<tr><td colspan=\"7\" style=\"background:#000; color:#fff; font-weight: bold;\">"+contact.getString("contact_name")+"</td></tr>";
				
				// S'il existe des actions pour ce contact
				if(action.first()) {
					int i = 0;
					action.beforeFirst();
					
					while(action.next()) {
						i++;
						String actionName = "";
						String actionDescription = "";
						String actionDate = Utilities.formatSQLDate(action.getString("joufo_date"), "dd.MM.YY HH:mm");
						
						if(action.getString("joufo_action").equals("download")) {
							actionName = Global.i("CAPTION_DOWNLOADING", langue);
							actionDescription = (action.getString("FKNoFile")!=null) ? "<span style=\"color:blue\">"+action.getString("file_rename")+"</span>" :  "<span style=\"color:red\">"+Global.i("CAPTION_ARCHIVE_OF_FOLDER", langue)+"</span>";
						} else {
							actionName = Global.i("CAPTION_CONSULTATION", langue);
							actionDescription = "<span style=\"color:green\">"+Global.i("CAPTION_FOLDER_CONSULTATION", langue)+"</span>";
						}
						
						history +=
						"<tr>"+
							"<td>"+i+" </td>"+
							"<td>&nbsp;  </td>"+
							"<td>"+actionName+"</td>"+
							"<td>&nbsp;  </td>"+
							"<td>"+actionDescription+"</td>"+
							"<td>&nbsp;  </td>"+
							"<td>"+actionDate+"</td>"+
						"</tr>";
						
						
					}
				} else {
					history += "<tr><td colspan=\"5\">"+Global.i("CAPTION_NO_ACCES_AND_DOWNLOAD", langue)+"</td></tr>";
				}
			}
			history+="</table>";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(history.equals("")) {
			history = "- "+Global.i("CAPTION_EMPTY_HISTORY", langue)+" -";
		}
		return history;
	}
	
	/**
	 * Retourne l'historique d'un contact sur un dossier
	 * @param contact Modèle du contact de référence
	 * @param folder Modèle du dossier de référence
	 * @param langue Langue de l'utilisateur
	 * @return Historique du contact mentionné
	 */
	private static String getHistoryOfContactFromFolder(Contact contact, Folder folder, String langue) {
		String history = "";
		ResultSet action = Sql.query(
				"SELECT * FROM trans_journal_folders " +
				"LEFT JOIN trans_files ON FKNoFile=PKNoFile " +
				"WHERE FKNoContact="+contact.getPK()+" AND trans_journal_folders.FKNoFolder="+folder.getPKNoFolder()+" "+
				"ORDER BY joufo_date DESC"
		);
		
		try {
			if(action.first()) {
				int consultation = 0;
				int i = 0;
				action.beforeFirst();
				history = "<br><table border=\"0\"><tr><th> <th></th> </th><th><b>"+Global.i("CAPTION_FILE_DOWNLOADED", langue)+"</b></th> <th></th> <th><b>"+Global.i("CAPTION_DATE", langue)+"</b></th></tr>";
				while(action.next()) {
					String actionDescription = "";
					String actionDate = Utilities.formatSQLDate(action.getString("joufo_date"), "dd.MM.YY HH:mm");
					
					if(action.getString("joufo_action").equals("download")) {
						i++;
						actionDescription = (action.getString("FKNoFile")!=null) ? "<span style=\"color:blue\">"+action.getString("file_rename")+"</span>" :  "<span style=\"color:red\">"+Global.i("CAPTION_ARCHIVE_OF_FOLDER", langue)+"</span>";
					
						history +=
						"<tr>"+
							"<td>"+i+" </td>"+
							"<td>&nbsp;  </td>"+
							"<td>"+actionDescription+"</td>"+
							"<td>&nbsp;  </td>"+
							"<td>"+actionDate+"</td>"+
						"</tr>";
					} else {
						consultation++;
					}
					
					
				}
				history+="</table>";
				
				
				String resume = "<strong>"+Global.i("CAPTION_CONSULTATION", langue)+" : "+consultation+"x</strong>";
	
				history = resume+"<br>"+history;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(history.equals("")) {
			history = "- "+Global.i("CAPTION_EMPTY_HISTORY", langue)+" -";
		}
		return history;
	}
}
