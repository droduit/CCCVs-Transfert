package main;

import global.Global;
import global.GlobalObjects;
import global.Module;
import global.UserSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sql.query.tbl_contacts;
import sql.query.tbl_folders;
import sql.query.tbl_journal_con;
import sql.query.tbl_users;
import toolbox.SHA256;
import toolbox.Sql;
import toolbox.Utilities;

import modules.AboutModule;
import modules.ConditionsModule;
import modules.ContactModule;
import modules.DownloadModule;
import modules.TransfertModule;


import com.vaadin.Application;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;

import common.component.AccordionMenu;

/**
 * <b>Application CCCVs-Transfert</b><br>
 * Class principale de l'application qui instancie les composants racines (Fen�tres et Layouts de base).<br>
 * C'est cette classe qui est appel�e au lancement de l'application.<br>
 * Elle impl�mente un ThreadLocal pour conserver l'�tat de l'application lors d'un rechargement de la page.<br>
 * Si l'application est red�marr�e (?restartApplication), le ThreadLocal est r�initialis�.
 * @author Roduit Dominique
 * @version 1.0
 * @since 2013-02-28
 */
public class CccvsTransfert extends Application implements HttpServletRequestListener  {
	/** ThreadLocal qui stocke l'�tat de l'application pour le restituer lors du rechargement de la page **/
	private static ThreadLocal<CccvsTransfert> threadLocal = new ThreadLocal<CccvsTransfert>();
	/** Contient tous les objets enregistr�s en global **/
	private static GlobalObjects global = new GlobalObjects();
	/** Fen�tre principale qui doit �tre accessible depuis n'importe ou. Pas de getter/setter parce qu'on ne peut pas le faire pour la fen�tre racine **/
	public static Window mainWindow;
	/** Analyse l'URL et r�agit en fonction des ancres re�ues **/
	private final UriFragmentUtility uri = new UriFragmentUtility();
	/** Composant personnalis� contenant le layout de l'interface utilisateur **/
	private static MainLayout mainLayout;
	/** Composant personnalis� contenant le layout de connexion � l'application **/
	private ConnexionLayout connexionLayout;
	/** Fen�tre flottante int�gr�e dans le layout de connexion **/
    private Window winConnexion;
    
    /**
     * M�thode appel�e � l'initialisation de l'application.<br>
     * Cette m�thode est rappel�e chaque fois que le param�tre URL ?restartApplication existe.
     */
	@Override
    public void init() {
		Global.URL = getURL();
		Global.isDev = (Global.URL.toString().indexOf("localhost")>-1 || Global.URL.toString().indexOf("127.0.0.1")>-1) ? true : false;
		Global.UPLOAD_DIR = (Global.isDev) ? "/wamp/www/files/" : Global.UPLOAD_DIR;
		
		// Application d'un th�me personnalis� bas� sur un th�me natif de vaadin
    	setTheme("CccvsDesign");
    	
    	Global.reloadParm();
		
		// D�finition de la fen�tre principal
		mainWindow = new Window(Global.getParm("APP_NAME"));
		mainWindow.setSizeFull();
        mainWindow.getContent().setSizeFull();
        setMainWindow(mainWindow);
        //global.setMainWindow(mainWindow);
        
		// Enregistre dans browser des informations sur le navigateur de l'utilisateur
		WebApplicationContext context = ((WebApplicationContext) mainWindow.getApplication().getContext());
		Global.browser = context.getBrowser();
        
        // Suppression des marges de 18px du body g�n�r�es automatiquement par vaadin
 		AbstractLayout panelLayout = (AbstractLayout) mainWindow.getContent();
 		panelLayout.setMargin(false);
 		
 		// Cr�ation du Layout de connexion
 		createConnexionLayout();
        
        // Action execut�es lors d'interception de param�tres dans l'URL
        setNavigationByURLParameters();
        //setNavigationByURLAnchors();  
    }
    // -------------------------------------------------------------------------------
 	/**
 	*  Commence l'impl�mentation du ThreadLocal
 	*  @return Application enregistr�e dans le threadLocal
 	*/
 	public static CccvsTransfert getInstance() {
 	   return threadLocal.get();
 	}
    /**
 	* Remplace l'�tat en cours de l'application par celui enregistr�e dans le Thread
 	* @param application L'application � remplacer
 	*/
 	public static void setInstance(CccvsTransfert application) {
 		threadLocal.set(application);
 	}
 	/**
 	 * M�thode appel�e avant le chargement de l'application,
 	 * elle permet de r�tablir l'�tat de l'application avant le rechargement de la page.
 	 */
 	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
 		CccvsTransfert.setInstance(this);
 	}
 	/**
 	 * M�thode appel�e � la fin de chaque requ�te
 	 */
 	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
 		threadLocal.remove();
 	}
 	// -------------------------------------------------------------------------------
	/**
     * M�thode tr�s importante qui renvoie les objets stock�s en global
     * @return (GlobalMethod) Instance stock�es dans la class GlobalMethod
     */
    public static GlobalObjects getGlobalMethod() {
		return global;
	}
    /**
     * Configuration des diff�rents messages du syst�me VAADIN, par exemple, lorsque les cookies sont d�sactiv�s ou que 
     * la session a expir�e.
     * @return Messages personnalis�s
     */
    public static SystemMessages getSystemMessages() {
    	CustomizedSystemMessages messages = new CustomizedSystemMessages();
		messages.setSessionExpiredCaption(Global.i("MESS_SYS_SESSION_EXPIRED_CAPTION"));
		messages.setSessionExpiredMessage(Global.i("MESS_SYS_SESSION_EXPIRED_MESSAGE"));
		messages.setCookiesDisabledCaption(Global.i("MESS_SYS_COOKIES_DISABLED_CAPTION"));
		messages.setCookiesDisabledMessage(Global.i("MESS_SYS_COOKIES_DISABLED_MESSAGE"));
		messages.setAuthenticationErrorCaption(Global.i("MESS_SYS_AUTH_ERROR_CAPTION"));
		messages.setAuthenticationErrorMessage(messages.getSessionExpiredMessage());
		messages.setCommunicationErrorCaption(Global.i("MESS_SYS_COMM_ERROR_CAPTION"));
		messages.setCommunicationErrorMessage(messages.getSessionExpiredMessage());
		messages.setInternalErrorCaption(Global.i("MESS_SYS_INTERNAL_ERROR_CAPTION"));
		messages.setInternalErrorMessage(messages.getSessionExpiredMessage());
		return messages;
    } 
    /**
     * Reste � l'�coute des param�tres de l'URL pour d�finir les pages selon les param�tres
     */
    private void setNavigationByURLParameters() {
		// On reste � l'�coute des �ventuels param�tres de l'URL qui pourraient survenir
		ParameterHandler URLParameter = new ParameterHandler() {
			@Override
			public void handleParameters(Map<String, String[]> parameters) {
				// R�cup�ration des param�tres et placement dans un tableau
				ArrayList<String> keys = new ArrayList<String>();
				HashMap<String, String> parm = new HashMap<String, String>();
				int index = 0;
				for(Iterator it = parameters.keySet().iterator(); it.hasNext();) {
					keys.add((String) it.next());
					parm.put(keys.get(index), ((String[]) parameters.get(keys.get(index)))[0]);

					Global.URLParameters += keys.get(index)+"="+parm.get(keys.get(index))+"&";
					index++;
				}
				
				// On parcours les param�tres
				for(Iterator it = parameters.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					String value = ((String[]) parameters.get(key))[0];
					
					// Si on tente d'acc�der � la page de t�l�chargement
					if(key.equals(Global.getParm("URL_PARM_DOWNLOAD"))) {
						
						// Obtention des informations sur le contact qui acc�de � la page
						ResultSet contact = Sql.query(tbl_contacts.getContactByCryptedURL(parm.get("id")));
						int PKNoContact = 0;
						String mail = "";
						try {
							if(contact.first()) {
								mail = contact.getString("contact_mail");
								if(SHA256.getHashValue(Global.getParm("SECRET_KEY")+mail).equals(parm.get("idm"))) {
									PKNoContact = contact.getInt("PKNoContact");
									
									UserSession.setMail(mail);
									UserSession.setLangue(Global.browser.getLocale().toString().substring(0, 2));
								}
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						
						// Obtention des informations sur le dossier demand�
						ResultSet folder = Sql.query(tbl_folders.getFolderByCryptedURL(value));
						int PKNoFolder = 0;
						try {
							if(folder.first()) {
								if(!folder.getBoolean("folder_archive")) {
									if(PKNoContact>0) {
										createMainLayout(false);
										mainLayout.setMenuWidth(Global.getParm("LAYOUT_FOLDER_INFO_WIDTH")); 
										mainLayout.getBodyRibbon().setSizeFull();
										mainLayout.setFooterEnabled(true);
										mainLayout.getMenuRibbon().addStyleName("expand");
										
										PKNoFolder = folder.getInt("PKNoFolder");
										DownloadModule downMod = new DownloadModule(PKNoFolder, PKNoContact, mail);
										loadModule(new Module(downMod.getBodyRibbonContent(), downMod.getBodyContent()));
									} else {
										mainWindow.showNotification(Global.i("CAPTION_ERROR"), Global.i("CAPTION_USER_NOT_ALLOWED"), Notification.TYPE_ERROR_MESSAGE);
									}
								} else {
									mainWindow.showNotification(Global.i("CAPTION_EXPIRATION"), Global.i("CAPTION_FOLDER_NOT_AVAILABLE"), Notification.TYPE_ERROR_MESSAGE);
								}
							} else {
								mainWindow.showNotification(Global.i("CAPTION_ERROR"), Global.i("CAPTION_FOLDER_NOT_EXISTS"), Notification.TYPE_ERROR_MESSAGE);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						//getMainWindow().showNotification(value);
					}
					
					// On re�oit une demande de validation
					if(key.equals(Global.getParm("URL_PARM_VALIDATION"))) {
						// S�lection de l'utilisateur en se basant sur la valeur du param�tre crypt� pass� dans l'URL
						ResultSet user = Sql.query(tbl_users.getUserByCryptedURL(value));
						try {
							if(user.first()) {
								if(!user.getBoolean("user_validation")) {
									getMainWindow().showNotification(Global.i("CAPTION_MAIL_VALIDATION_OK"), user.getString("user_mail"), Notification.TYPE_TRAY_NOTIFICATION);
									
									// Validation de l'utilisateur
									Sql.exec(tbl_users.getUpdByMailValidation(user.getString("user_mail")));
									// Journalisation de la validation
									Sql.exec(tbl_journal_con.getInsertQuery(user.getString("user_mail"), "validation_success", "Validation r�ussie"));
									
									// Enregistrement des informations sur l'utilisateur
									UserSession.setID(user.getInt("PKNoUser"));
									UserSession.setMail(user.getString("user_mail"));
									UserSession.setInternal(user.getBoolean("user_internal"));
									UserSession.setLangue(user.getString("user_langue"));
									UserSession.setAdmin(user.getBoolean("user_admin"));
									
									// Connexion automatique
									createMainLayout(true);
								} else {
									getMainWindow().showNotification(Global.i("CAPTION_MAIL_ALREADY_VALIDATE"), user.getString("user_mail"), Notification.TYPE_WARNING_MESSAGE);
									
									// Journalisation de la validation d�j� effectu�e
									Sql.exec(tbl_journal_con.getInsertQuery(user.getString("user_mail"), "validation_recurrent", "Relancement du lien alors que l'adresse est d�j� valid�e"));
								}
							} else {
								// Journalisation de l'erreur
								Sql.exec(tbl_journal_con.getInsertQuery(value, "validation_error", "Essai de validation d'une adresse qui n'existe pas !"));
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		mainWindow.addParameterHandler(URLParameter);
    }
    /**
     * Reste � l'�coute des ancres de l'URL pour d�finir les pages selon l'ancre re�ue
     */
    private void setNavigationByURLAnchors() {
		uri.addListener(new UriFragmentUtility.FragmentChangedListener() {
			private static final long serialVersionUID = -3992570785776598885L;
			@Override
			public void fragmentChanged(FragmentChangedEvent source) {
				getMainWindow().showNotification(source.getUriFragmentUtility().getFragment());
			}
		
		});
		mainWindow.addComponent(uri);
    }
    /**
     * Cr�ation du layout principal
     * @param loadMainComponents true : Charger les composants, false : ne charger que le layout de base
     */
    private void createMainLayout(boolean loadMainComponents) {
    	System.out.println("Chargement du layout principal");
    	
    	// D�sactive le layout de connexion
    	if(connexionLayout!=null) {
		    mainWindow.removeComponent(connexionLayout);
		    mainWindow.removeWindow(winConnexion);
    	}
    	
    	// D�sactive le layout principal s'il existe d�j�
    	if(mainLayout!=null) {
	    	mainWindow.removeComponent(mainLayout);
    	}

    	// Ajoute le design personnalis� � la page
        mainLayout = new MainLayout();
        mainLayout.setFooterEnabled(false);
        mainWindow.addComponent(mainLayout);
        global.setMainLayout(mainLayout);
        
        // Ajout du logo --------------------------------------------------------
        Embedded logo = new Embedded();
        logo.setSource(new ThemeResource(Global.IMG_MAIN_LOGO));
        mainLayout.getHeaderLogo().addComponent(logo);
        
        
        // Ajout des composants
        if(loadMainComponents) createMainComponents(); else mainLayout.getBtChangeLanguage().setVisible(false);
    }
    /**
     * Chargement des composants du layout principal
     */
    private void createMainComponents() {
    	// Ajout de bouton dans le menu du header -------------------------------
        Button btAbout = new Button(Global.i("CAPTION_ABOUT"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				 loadModule(new Module(AboutModule.getBodyRibbonContent(), AboutModule.getBodyContent())); 
			}
		});
    	Button btFileManager = new Button(Global.i("CAPTION_USAGE_CONDITIONS"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadModule(new Module(ConditionsModule.getBodyRibbonContent(), ConditionsModule.getBodyContent())); 
			}
		});
        mainLayout.getHeaderMenu().addComponent(btFileManager);
        mainLayout.getHeaderMenu().addComponent(btAbout);
        
        // Bouton de changement de langue
        mainLayout.getBtChangeLanguage().addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// Suppression du layout affich�
				mainWindow.removeComponent(mainLayout);
				// Rechargement complet du layout
				createMainLayout(true);
				// Affichage d'une notification
				String langue = (UserSession.getLangue().equals("fr")) ? Global.i("CAPTION_FRENCH") : Global.i("CAPTION_GERMAN");
				mainWindow.showNotification(Global.i("CAPTION_LANGUE_CHANGE"), langue, Notification.TYPE_TRAY_NOTIFICATION);
			}
		});
        
        // Ajout du contenu du menu ---------------------------------------
         System.out.println("Cr�ation du menu accord�on");
	     AccordionMenu menu = new AccordionMenu();
	     menu.setSizeFull(); 
	     mainLayout.getMenu().addComponent(menu);
	     // ----------------------------------------------------------------
	        
	    // Chargement des modules (= pages) ------------------------------------
	     if(tbl_contacts.getNumberOfContact()<1) {
	    	 loadModule(new Module(ContactModule.getBodyRibbonContent(), TransfertModule.getBodyContent())); 
	     } else {
	    	 loadModule(new Module(TransfertModule.getBodyRibbonContent(), TransfertModule.getBodyContent())); 
	     }
	    //----------------------------------------------------------------------
    }
    /**
     * Charge un module dans l'application
     * @param module (Module) Le module � charger
     */
    public static void loadModule(Module module) {
    	if(mainLayout!=null) {
    		mainLayout.restoreRibbonHeight();
    		
	    	if(module.getRibbonContent()!=null) {
	    		mainLayout.getBodyRibbon().removeAllComponents();
	    		mainLayout.getBodyRibbon().addComponent(module.getRibbonContent());
	    	}
	    	if(module.getBodyContent()!=null) {
	    		mainLayout.getBody().removeAllComponents();
	    		mainLayout.getBody().addComponent(module.getBodyContent());
	    	}
    	}
    }
    /**
	 * Supprime le contenu de la page en cours
	 */
	public static void resetContent() {
		if(mainLayout!=null) {
			mainLayout.getBody().removeAllComponents();
			mainLayout.getBodyRibbon().removeAllComponents();
		}
	}
    /**
     * Cr�ation du layout de connexion
     */
    private void createConnexionLayout() {
    	System.out.println("Chargement du layout de connexion");
    	
    	// D�sactive le layout principal
    	if(mainLayout!=null) {
	    	mainWindow.removeComponent(mainLayout);
    	}
    	
    	connexionLayout = new ConnexionLayout();
    	mainWindow.addComponent(connexionLayout);
    	
    	winConnexion = connexionLayout.getWindowConnexion();
    	winConnexion.center();
    	mainWindow.addWindow(winConnexion);
    	
    	connexionLayout.getTextFieldID().focus();
    	
    	// On �coute quand le formulaire est valid�
    	connexionLayout.getButtonConnexion().addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if(connexionLayout.getConnexionState()) {
					createMainLayout(true);
				}
			}
		});
    }
   

}

