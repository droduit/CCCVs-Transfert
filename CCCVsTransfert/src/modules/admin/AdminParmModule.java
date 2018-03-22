package modules.admin;



import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;


import main.CccvsTransfert;
import main.MainLayout;

import model.User;

import toolbox.Sql;
import toolbox.Utilities;


import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;

import com.vaadin.terminal.ThemeResource;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Runo;
import common.component.PanelLight;

import global.Global;
import global.GlobalObjects;


/**
 * Module disponible uniquement par les administrateurs
 * Il permet la visualisation et la gestion des utilisateurs de l'interface.
 * @author Dominique Roduit
 *
 */
public class AdminParmModule {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private static GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fenêtre principale **/
	private static Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private static MainLayout mainLayout = global.getMainLayout();
	/** Table contenant la liste des contacts **/
	private static Table tblUsers;
	/** Contient les entrées du tableau **/
	private static BeanItemContainer<User> cntUsers = new BeanItemContainer<User>(User.class);
	/** Action Utilisateur Simple **/
	private static Action ACTION_NO_ADMIN;
	/** Action Administrateur **/
	private static Action ACTION_ADMIN;
	/** Action Editer **/
	private static Action ACTION_EDIT;
	/** Action Supprimer **/
	private static Action ACTION_DELETE; 
	/** Action valider **/
	private static Action ACTION_VALID; 
	/** Action invalider **/
	private static Action ACTION_INVALID;
	

	/**
	 * Chargement du bouton de retour et du titre dans le ruban du corps de la page
	 */
	public static HorizontalLayout getBodyRibbonContent() {
		
		ACTION_NO_ADMIN = new Action(Global.i("CAPTION_SIMPLE_USER"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"lock--minus.png"));
		ACTION_ADMIN = new Action(Global.i("CAPTION_ADMINISTRATOR"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"unlock.png"));
		ACTION_EDIT = new Action(Global.i("CAPTION_EDIT"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"edit.png"));
		ACTION_DELETE = new Action(Global.i("CAPTION_DELETE"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"false.png"));
		ACTION_VALID = new Action(Global.i("CAPTION_VALID"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"tick-shield.png")); 
		ACTION_INVALID = new Action(Global.i("CAPTION_INVALID"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"expanded.png")); 
		
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Ruban");
	
		HorizontalLayout vlRibbon = new HorizontalLayout();
		vlRibbon.setSizeFull();
		vlRibbon.setSpacing(true);
		
		Label lblTitle = new Label("Paramétrage de l'application (BETA)");
		lblTitle.setStyleName(Runo.LABEL_H1);
		vlRibbon.addComponent(lblTitle);
		vlRibbon.setComponentAlignment(lblTitle, Alignment.MIDDLE_LEFT);

	    return vlRibbon;
	} 
	/**
	 * Insertion du contenu dans le corps de la page.<br>Le contenu comprend le texte, extrait de la base de données ainsi qu'une image illustrative.
	 */
	
	private static final HashMap<String, String> parmValue = new HashMap<String, String>();
	private static final HashMap<String, String> parmDesc = new HashMap<String, String>();
	
	private static Slider sldUploadMaxSize;
	private static Slider sldMaxFolderAvail;
	private static Slider sldMinFolderAvail;
	private static TextField txtAllowedExt;
	private static RichTextArea editHeaderMail;
	private static RichTextArea editFooterMail;
	private static TextField txtMailFrom;
	private static TextField txtMailFromName;
	private static TextField txtPrefixeArchive;
	private static TextField txtAppName;
	private static TextField txtAppVersion;
	private static TextField txtSuffixeEmailInterne;
	
	public static PanelLight getBodyContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
	    
		PanelLight vlBody = new PanelLight();
		vlBody.setSpacing(true);
		vlBody.setMargin(true);
		vlBody.getLayout().setSizeUndefined();
		vlBody.getLayout().setWidth("100%");
		vlBody.getLayout().removeStyleName("v-menu-buttons");
		
		ResultSet parm = Sql.query("SELECT * FROM trans_app_param ORDER BY parm_key ASC");
		try {
			while(parm.next()) {
				parmValue.put(parm.getString("parm_key"), parm.getString("parm_value"));
				parmDesc.put(parm.getString("parm_key"), parm.getString("parm_description"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

		
		Label lblGeneral = new Label("Application");
		lblGeneral.setStyleName(Runo.LABEL_H2);
		vlBody.addComponent(lblGeneral);
		
		txtAppName = new TextField(parmDesc.get("APP_NAME").toString());
		txtAppName.setValue(parmValue.get("APP_NAME"));
		txtAppName.setWidth("340px");
		txtAppName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = event.getProperty().getValue().toString();
				if(!value.equals("")) {
					Sql.exec("UPDATE trans_app_param SET parm_value="+Utilities.formatSQL(value)+" WHERE parm_key='APP_NAME'");
				}
			}
		});
		vlBody.addComponent(txtAppName);
		
		
		txtAppVersion = new TextField(parmDesc.get("APP_VERSION").toString());
		txtAppVersion.setValue(parmValue.get("APP_VERSION"));
		txtAppVersion.setWidth("340px");
		txtAppVersion.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = event.getProperty().getValue().toString();
				if(!value.equals("")) {
					Sql.exec("UPDATE trans_app_param SET parm_value="+Utilities.formatSQL(value)+" WHERE parm_key='APP_VERSION'");
				}
			}
		});
		vlBody.addComponent(txtAppVersion);
		
		
		txtSuffixeEmailInterne = new TextField(parmDesc.get("EMAIL_INTERNE_SUFFIXE").toString());
		txtSuffixeEmailInterne.setValue(parmValue.get("EMAIL_INTERNE_SUFFIXE"));
		txtSuffixeEmailInterne.setWidth("340px");
		txtSuffixeEmailInterne.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = event.getProperty().getValue().toString();
				if(!value.equals("")) {
					Sql.exec("UPDATE trans_app_param SET parm_value="+Utilities.formatSQL(value)+" WHERE parm_key='EMAIL_INTERNE_SUFFIXE'");
				}
			}
		});
		vlBody.addComponent(txtSuffixeEmailInterne);
		
		
		
		
		
		
		
		
		Label lblUpload = new Label("Transfert & Dossiers");
		lblUpload.setStyleName(Runo.LABEL_H2);
		vlBody.addComponent(lblUpload);
		
		
		sldUploadMaxSize = new Slider(parmDesc.get("UPLOAD_MAX_SIZE")+" ("+Utilities.formatSize(Long.parseLong(parmValue.get("UPLOAD_MAX_SIZE")))+")");
		sldUploadMaxSize.setWidth("100%");
		sldUploadMaxSize.setMin(1024);
		sldUploadMaxSize.setMax(1717986920);
		sldUploadMaxSize.setImmediate(true);
		try {
			sldUploadMaxSize.setValue(Double.parseDouble(parmValue.get("UPLOAD_MAX_SIZE")));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ValueOutOfBoundsException e) {
			e.printStackTrace();
		}
		sldUploadMaxSize.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = event.getProperty().getValue().toString();
				
				Double doubleVal = Double.parseDouble(value);
				sldUploadMaxSize.setCaption(parmDesc.get("UPLOAD_MAX_SIZE")+" ("+Utilities.formatSize(doubleVal.longValue())+")");
				Sql.exec("UPDATE trans_app_param SET parm_value="+Utilities.formatSQL(Long.toString(doubleVal.longValue()))+" WHERE parm_key='UPLOAD_MAX_SIZE'");
			}
		});
		vlBody.addComponent(sldUploadMaxSize);
		
		
		
		txtAllowedExt = new TextField(parmDesc.get("UPLOAD_ALLOWED_EXTENSIONS").toString());
		txtAllowedExt.setValue(parmValue.get("UPLOAD_ALLOWED_EXTENSIONS"));
		txtAllowedExt.setWidth("340px");
		txtAllowedExt.setInputPrompt("exe, csv, jpg, xls, csv, png, tiff, mp3, pdf");
		txtAllowedExt.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = event.getProperty().getValue().toString();
				if(!value.equals("")) {
					Sql.exec("UPDATE trans_app_param SET parm_value="+Utilities.formatSQL(value)+" WHERE parm_key='UPLOAD_ALLOWED_EXTENSIONS'");
				}
			}
		});
		vlBody.addComponent(txtAllowedExt);
		
		
		
		
		
		sldMinFolderAvail = new Slider(parmDesc.get("FOLDER_MIN_AVAILABILITY")+" ("+parmValue.get("FOLDER_MIN_AVAILABILITY")+")");
		sldMinFolderAvail.setWidth("100%");
		sldMinFolderAvail.setMin(1);
		sldMinFolderAvail.setMax(120);
		sldMinFolderAvail.setImmediate(true);
		try {
			sldMinFolderAvail.setValue(Double.parseDouble(parmValue.get("FOLDER_MIN_AVAILABILITY")));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ValueOutOfBoundsException e) {
			e.printStackTrace();
		}
		sldMinFolderAvail.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = event.getProperty().getValue().toString();
				Double doubleValue = Double.parseDouble(value);
				
				if(doubleValue<Double.parseDouble(sldMaxFolderAvail.getValue().toString())) {
					sldMinFolderAvail.setCaption(parmDesc.get("FOLDER_MIN_AVAILABILITY")+" ("+doubleValue.intValue()+" jours)");
					Sql.exec("UPDATE trans_app_param SET parm_value="+doubleValue.intValue()+" WHERE parm_key='FOLDER_MIN_AVAILABILITY'");
				} else {
					try {
						sldMinFolderAvail.setValue(doubleValue-1);
					} catch (ValueOutOfBoundsException e) {
						e.printStackTrace();
					}
				}
			}
		});
		vlBody.addComponent(sldMinFolderAvail);
		
		
		
		
		sldMaxFolderAvail = new Slider(parmDesc.get("FOLDER_MAX_AVAILABILITY")+" ("+parmValue.get("FOLDER_MAX_AVAILABILITY")+")");
		sldMaxFolderAvail.setWidth("100%");
		sldMaxFolderAvail.setMin(1);
		sldMaxFolderAvail.setMax(120);
		sldMaxFolderAvail.setImmediate(true);
		try {
			sldMaxFolderAvail.setValue(Double.parseDouble(parmValue.get("FOLDER_MAX_AVAILABILITY")));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ValueOutOfBoundsException e) {
			e.printStackTrace();
		}
		sldMaxFolderAvail.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = event.getProperty().getValue().toString();
				Double doubleValue = Double.parseDouble(value);
				
				if(doubleValue>Double.parseDouble(sldMinFolderAvail.getValue().toString())) {
					sldMaxFolderAvail.setCaption(parmDesc.get("FOLDER_MAX_AVAILABILITY")+" ("+doubleValue.intValue()+" jours)");
					Sql.exec("UPDATE trans_app_param SET parm_value="+doubleValue.intValue()+" WHERE parm_key='FOLDER_MAX_AVAILABILITY'");
				} else {
					try {
						sldMaxFolderAvail.setValue(doubleValue+1);
					} catch (ValueOutOfBoundsException e) {
						e.printStackTrace();
					}
				}
			}
		});
		vlBody.addComponent(sldMaxFolderAvail);
		
		
		
		txtPrefixeArchive = new TextField(parmDesc.get("PREFIXE_ARCHIVE_FOLDER").toString());
		txtPrefixeArchive.setWidth("340px");
		txtPrefixeArchive.setValue(parmValue.get("PREFIXE_ARCHIVE_FOLDER"));
		txtPrefixeArchive.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = event.getProperty().getValue().toString();
				if(!value.equals("")) {
					Sql.exec("UPDATE trans_app_param SET parm_value="+Utilities.formatSQL(value)+" WHERE parm_key='PREFIXE_ARCHIVE_FOLDER'");
				}
			}
		});
		vlBody.addComponent(txtPrefixeArchive);
		
		
		
		
		
		Label lblMails = new Label("Messages électroniques");
		lblMails.setStyleName(Runo.LABEL_H2);
		vlBody.addComponent(lblMails);
		
		txtMailFrom = new TextField(parmDesc.get("APP_MAIL_FROM").toString());
		txtMailFrom.setWidth("340px");
		txtMailFrom.setValue(parmValue.get("APP_MAIL_FROM"));
		txtMailFrom.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = event.getProperty().getValue().toString();
				if(!value.equals("")) {
					Sql.exec("UPDATE trans_app_param SET parm_value="+Utilities.formatSQL(value)+" WHERE parm_key='APP_MAIL_FROM'");
				}
			}
		});
		vlBody.addComponent(txtMailFrom);
		
		
		
		txtMailFromName = new TextField(parmDesc.get("APP_MAIL_FROM_NAME").toString());
		txtMailFromName.setWidth("340px");
		txtMailFromName.setValue(parmValue.get("APP_MAIL_FROM_NAME"));
		txtMailFromName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = event.getProperty().getValue().toString();
				if(!value.equals("")) {
					Sql.exec("UPDATE trans_app_param SET parm_value="+Utilities.formatSQL(value)+" WHERE parm_key='APP_MAIL_FROM_NAME'");
				}
			}
		});
		vlBody.addComponent(txtMailFromName);
		
		
		
		editHeaderMail = new RichTextArea(parmDesc.get("MAIL_HEADER_CONTENT"));
		editHeaderMail.setWidth("100%");
		editHeaderMail.setValue(parmValue.get("MAIL_HEADER_CONTENT"));
		editHeaderMail.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Sql.exec("UPDATE trans_app_param SET parm_value="+Utilities.formatSQL(editHeaderMail.getValue().toString())+" WHERE parm_key='MAIL_HEADER_CONTENT'");
			}
		});
		vlBody.addComponent(editHeaderMail);
		
		
		editFooterMail = new RichTextArea(parmDesc.get("MAIL_FOOTER_CONTENT"));
		editFooterMail.setWidth("100%");
		editFooterMail.setValue(parmValue.get("MAIL_FOOTER_CONTENT"));
		editFooterMail.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Sql.exec("UPDATE trans_app_param SET parm_value="+Utilities.formatSQL(editFooterMail.getValue().toString())+" WHERE parm_key='MAIL_FOOTER_CONTENT'");
			}
		});
		vlBody.addComponent(editFooterMail);
		
		
		
		
		Button btReinitializeMaxSize = new Button("Réinitialiser toutes les valeurs", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					sldUploadMaxSize.setValue(1073741824);
					sldMinFolderAvail.setValue((double)1);
					txtAllowedExt.setValue("*");
					sldMaxFolderAvail.setValue((double)10);
					txtMailFrom.setValue("support@avs.vs.ch");
					txtMailFromName.setValue("CCCVs-Transfert");
					txtPrefixeArchive.setValue("folder_");
					txtAppName.setValue("CCCVs-Transfert");
					txtAppVersion.setValue("1.0.0");
					txtSuffixeEmailInterne.setValue("avs.vs.ch");
					editHeaderMail.setValue("<HTML><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\"><BODY><FONT COLOR=\"#CC0610\" FACE=\"Arial, sans-serif\" style=\"font-size:14pt\">CCCVs-Transfert<br><FONT STYLE=\"font-size: 10pt; font-style:italic\" COLOR=\"#91060D\">______________________________________________________________________________________________</FONT><br></FONT><br><FONT COLOR=\"#000080\" FACE=\"Verdana, sans-serif\" style=\"font-size:9pt\">");
					editFooterMail.setValue("<br></FONT><br><FONT COLOR=\"#000080\" FACE=\"Verdana, sans-serif\" style=\"font-size:8pt\">______________________________________________________________________________________________<br><br>Ce message vous a été envoyé automatiquement, merci de ne pas y répondre.<br><br><FONT STYLE=\"font-size: 10pt\" COLOR=\"#91060D\">Caisse de compensation du Valais</FONT><br><FONT STYLE=\"font-size: 10pt; font-style:italic\" COLOR=\"#B0B0B0\">Ausgleichskasse Wallis</FONT><br>______________________________________________________________________________________________<br></FONT></BODY></HTML>");
				} catch (ValueOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		});
		vlBody.addComponent(btReinitializeMaxSize);
		
		
		return vlBody;
	}
	
	private static ClickListener prepareArchivage = new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			
		}
	};
	

}
