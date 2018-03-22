package modules.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.CccvsTransfert;
import main.MainLayout;
import model.ActionJournalFold;
import model.Contact;
import model.Files;
import model.Folder;
import model.JournalCon;
import model.User;
import sql.query.tbl_journal_con;
import sql.query.tbl_journal_fold;
import sql.query.tbl_users;
import toolbox.Sql;
import toolbox.Utilities;

import com.vaadin.addon.timeline.Timeline;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;

import common.component.AccordionMenu;
import common.component.ContactTable;
import common.component.PanelLight;

import form.WizFoldNew;
import form.WizFoldUpload;
import global.Global;
import global.GlobalObjects;
import global.Module;

/**
 * Module disponible uniquement par les administrateurs.
 * Il permet la visualisation rapide de toutes les actions qui ont été journalisées.
 * @author Dominique Roduit
 *
 */
public class AdminJournModule {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private static GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fenêtre principale **/
	private static Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private static MainLayout mainLayout = global.getMainLayout();
	/** Table contenant la liste des contacts **/
	private static Table tblActions;
	/** Conteneur de la liste des actions journalisées pour les connexions et validations **/
	private static BeanItemContainer<JournalCon> cntActions = new BeanItemContainer<JournalCon>(JournalCon.class);
	/** Boutons du menu dans le ruban **/
	private static ArrayList<Button> btMenu;
	/** Layout horizontal qui contient les boutons de filtre **/
	private static HorizontalLayout hlFilter;
	
	/**
	 * Chargement du bouton de retour et du titre dans le ruban du corps de la page
	 */
	public static VerticalLayout getBodyRibbonContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Ruban");
		
		VerticalLayout vlMain = new VerticalLayout();
		vlMain.setSizeFull();
		vlMain.setSpacing(true);
		
		HorizontalLayout hlRibbon = new HorizontalLayout();
		hlRibbon.setSizeFull();
		hlRibbon.setSpacing(true);
		vlMain.addComponent(hlRibbon);
		
		hlFilter = new HorizontalLayout();
		hlFilter.setSizeFull();
		hlFilter.setSpacing(true);
		vlMain.addComponent(hlFilter);
		
		
		btMenu = new ArrayList<Button>();

		// Bouton Connexions
		final ArrayList<Button> filterConnexions = new ArrayList<Button>();
		filterConnexions.add(new Button(Global.i("CAPTION_PASSWORD_FAILED"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithConnexions("con_wrong_pass");
			}
		}));
		filterConnexions.add(new Button(Global.i("CAPTION_CONNECTION_SUCCESSFUL"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithConnexions("con_success");
			}
		}));
		filterConnexions.add(new Button(Global.i("CAPTION_INVALID_INFOS"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithConnexions("con_invalid_infos");
			}
		}));
		filterConnexions.add(new Button(Global.i("CAPTION_EMAIL_INVALID_FORMAT"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithConnexions("con_invalid_mail");
			}
		}));
		filterConnexions.add(new Button(Global.i("CAPTION_NEW_USER"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithConnexions("con_new_user");
			}
		}));
		filterConnexions.add(new Button(Global.i("CAPTION_INVALIDATE_ACCOUNT"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithConnexions("con_no_validate");
			}
		}));
		
		btMenu.add(new Button(Global.i("CAPTION_CONNECTIONS"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				addFilters(filterConnexions);
				loadTableWithConnexions("");
			}
		}));
		btMenu.get(0).setStyleName(Runo.BUTTON_DEFAULT);
		btMenu.get(0).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"unlock.png"));
		
		
		// Bouton Validation des comptes
		final ArrayList<Button> filderValidation = new ArrayList<Button>();
		filderValidation.add(new Button(Global.i("CAPTION_ERRORS"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithValidations("validation_error");
			}
		}));
		filderValidation.add(new Button(Global.i("CAPTION_RECCURENCES"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithValidations("validation_recurrent");
			}
		}));
		filderValidation.add(new Button(Global.i("CAPTION_VALIDES"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithValidations("validation_success");
			}
		}));
		
		btMenu.add(new Button(Global.i("CAPTION_ACCOUNTS_VALIDATION"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				addFilters(filderValidation);
				loadTableWithValidations("");
			}
		}));
		btMenu.get(1).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"tick-shield.png"));
		
		
		// Bouton Téléchargements
		final ArrayList<Button> filterDownload = new ArrayList<Button>();
		filterDownload.add(new Button(Global.i("CAPTION_FOLDERS_ARCHIVE"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithDownloads("trans_journal_folders.FKNoFile IS NULL AND joufo_action='download'");
			}
		}));
		filterDownload.add(new Button(Global.i("CAPTION_CONSULTATIONS"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				loadTableWithDownloads("joufo_action='consult' AND trans_journal_folders.FKNoFile IS NULL");
			}
		}));
		filterDownload.add(new Button(Global.i("CAPTION_FILES_DOWNLOADS"), new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				loadTableWithDownloads("joufo_action='download' AND trans_journal_folders.FKNoFile IS NOT NULL");
			}
		}));
		
		btMenu.add(new Button(Global.i("CAPTION_DOWNLOADS"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				addFilters(filterDownload);
				loadTableWithDownloads("");
			}
		})); 
		btMenu.get(2).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"down.png"));
				
		// Insertion des boutons
		for(Button button : btMenu) {
			button.setWidth("100%");
			button.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					deselectAllButtons();
					event.getButton().setStyleName(Runo.BUTTON_DEFAULT);
				}
			});
			hlRibbon.addComponent(button);
			hlRibbon.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		}
		
		// Ajout des composants de filtrage de l'information
		addFilters(filterConnexions);

	    return vlMain;
	} 
	/**
	 * Ajout des boutons de filtrage
	 * @param btList Liste des boutons de filtrage
	 */
	private static void addFilters(ArrayList<Button> btList) {
		hlFilter.removeAllComponents();
		for(Button bt : btList) {
			bt.setStyleName(Runo.BUTTON_SMALL);
			hlFilter.addComponent(bt);
		}
	}
	/**
	 * Insertion du contenu dans le corps de la page.<br>Le contenu comprend le texte, extrait de la base de données ainsi qu'une image illustrative.
	 */
	public static VerticalLayout getBodyContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
	    
		VerticalLayout vlBody = new VerticalLayout();
		vlBody.setSizeFull();
		
		loadTableWithConnexions("");

		tblActions = new Table();
		vlBody.addComponent(tblActions);
		tblActions.setSizeFull();
		tblActions.setSelectable(true);
		tblActions.setContainerDataSource(cntActions);
		tblActions.setVisibleColumns(new String[]{"joco_action","joco_mail","joco_browser","joco_date","joco_os","joco_ip",});

		
		// Génération de la colonne "Admin"
		tblActions.addGeneratedColumn("action", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				JournalCon action = (JournalCon)itemId;
				String imgAction = "";
				if(action.getJoco_action().equals("con_invalid_infos")) {
					imgAction = "ui-text-field-password-red.png";
				} else if(action.getJoco_action().equals("con_invalid_mail")) {
					imgAction = "mail--exclamation.png";				
				} else if(action.getJoco_action().equals("con_new_user")) {
					imgAction = "plus.png";
				} else if(action.getJoco_action().equals("con_no_validate")) {
					imgAction = "mail-forward-all.png";
				} else if(action.getJoco_action().equals("con_wrong_pass") || action.getJoco_action().equals("validation_error")) {
					imgAction = "exclamation-red.png";
				} else if(action.getJoco_action().equals("con_success") || action.getJoco_action().equals("validation_success")) {
					imgAction = "tick.png";
				} else if(action.getJoco_action().equals("validation_recurrent")) {
					imgAction = "reload.png";
				} else if(action.getJoco_action().equals("consult")) {
					imgAction = "search.png";
				} else if(action.getJoco_action().equals("download")) {
					imgAction = "down.png";
				} else if(action.getJoco_action().equals("archive")) {
					imgAction = "archive_down.png";
				}
				return new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+imgAction));
			}
		});
		tblActions.setColumnWidth("action", 25);
		tblActions.setColumnAlignment("action", Table.ALIGN_CENTER);
		
		// Génération de la colonne "Navigateur"
		tblActions.addGeneratedColumn("browser", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				JournalCon action = (JournalCon)itemId;
				String imgBrowser = "browser.png";
				if(action.getJoco_browser().indexOf("Internet Explorer")>-1) {
					imgBrowser = "ie.png";
				} else if(action.getJoco_browser().indexOf("Firefox")>-1) {
					imgBrowser = "firefox.png";				
				} else if(action.getJoco_browser().indexOf("Chrome")>-1) {
					imgBrowser = "chrome.png";
				} else if(action.getJoco_browser().indexOf("Opera")>-1) {
					imgBrowser = "opera.png";
				} else if(action.getJoco_browser().indexOf("Safari")>-1) {
					imgBrowser = "safari.png";
				}
				return new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+imgBrowser));
			}
		});
		tblActions.setColumnWidth("browser", 25);
		tblActions.setColumnAlignment("browser", Table.ALIGN_CENTER);
		
		// Génération de la colonne "OS"
		tblActions.addGeneratedColumn("os", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				JournalCon action = (JournalCon)itemId;
				String imgOS = "other.png";
				if(action.getJoco_os().indexOf("Windows")>-1) {
					imgOS = "windows.png";
				} else if(action.getJoco_os().indexOf("Mac OSX")>-1) {
					imgOS = "mac.png";
				} else if(action.getJoco_os().indexOf("Linux")>-1) {
					imgOS = "linux.png";
				}
				return new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+imgOS));
			}
		});
		tblActions.setColumnWidth("os", 25);
		tblActions.setColumnAlignment("os", Table.ALIGN_CENTER);
		
		// Génération de la colonne "Action"
		tblActions.addGeneratedColumn("action_text", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				JournalCon action = (JournalCon)itemId;
				String txtAction = "-";
				if(action.getJoco_action().equals("con_invalid_infos")) {
					txtAction = Global.i("CAPTION_INVALID_INFOS");
				} else if(action.getJoco_action().equals("con_invalid_mail")) {
					txtAction = Global.i("CAPTION_EMAIL_INVALID");				
				} else if(action.getJoco_action().equals("con_new_user")) {
					txtAction = Global.i("CAPTION_NEW_USER");
				} else if(action.getJoco_action().equals("con_no_validate")) {
					txtAction = Global.i("CAPTION_INVALIDATE_ACCOUNT");
				} else if(action.getJoco_action().equals("con_wrong_pass")) {
					txtAction = Global.i("CAPTION_PASSWORD_FAILED");
				} else if(action.getJoco_action().equals("con_success")) {
					txtAction = Global.i("CAPTION_CONNECTION_SUCCESSFUL");
				} else if(action.getJoco_action().equals("validation_success")) {
					txtAction = Global.i("CAPTION_VALIDATION_SUCCESS");
				} else if(action.getJoco_action().equals("validation_error")) {
					txtAction = Global.i("CAPTION_VALIDATION_ERROR");
				} else if(action.getJoco_action().equals("validation_recurrent")) {
					txtAction = Global.i("CAPTION_REPEATED_VALIDATION");
				} else if(action.getJoco_action().equals("consult")) {
					txtAction = Global.i("CAPTION_CONSULTATION");
				} else if(action.getJoco_action().equals("download") || action.getJoco_action().equals("archive")) {
					txtAction = Global.i("CAPTION_DOWNLOADING");
				} 
				return txtAction;
			}
		});
		tblActions.setColumnWidth("action_text", 130);
		tblActions.setColumnAlignment("action_text", Table.ALIGN_CENTER);

		tblActions.setVisibleColumns(new String[]{"action","browser","os","action_text","joco_mail","joco_date","joco_ip","joco_browser"});
		tblActions.setColumnHeaders(new String[]{"","","",Global.i("CAPTION_ACTION"),Global.i("CAPTION_ADRESSE_EMAIL"),Global.i("CAPTION_DATE"),"IP",Global.i("CAPTION_BROWSER")});
	
		return vlBody;
	}
	/**
	 * Remplissage de la table avec les connexions journalisées 
	 * @param where Valeur de la clause WHERE de la requête SQL
	 */
	private static void loadTableWithConnexions(String where) {
		cntActions.removeAllItems();
		cntActions.addAll(getConnexionsList(0, where));
		
		if(tblActions!=null) {
			tblActions.setVisibleColumns(new String[]{"action","browser","os","action_text","joco_mail","joco_date","joco_ip","joco_browser"});
			tblActions.setColumnHeaders(new String[]{"","","",Global.i("CAPTION_ACTION"),Global.i("CAPTION_ADRESSE_EMAIL"),Global.i("CAPTION_DATE"),"IP",Global.i("CAPTION_BROWSER")});
		}
	}
	/**
	 * Remplissage de la table avec les validations de comptes journalisées
	 * @param where Valeur de la clause WHERE de la requête SQL
	 */
	private static void loadTableWithValidations(String where) {
		cntActions.removeAllItems();
		cntActions.addAll(getConnexionsList(1, where));
		tblActions.setVisibleColumns(new String[]{"action","browser","os","action_text","joco_mail","joco_date","joco_ip","joco_browser"});
		tblActions.setColumnHeaders(new String[]{"","","",Global.i("CAPTION_ACTION"),Global.i("CAPTION_ADRESSE_EMAIL"),Global.i("CAPTION_DATE"),"IP",Global.i("CAPTION_BROWSER")});
	}
	/**
	 * Remplissage de la table avec les journalisations de téléchargement
	 * @param where Valeur de la clause WHERE de la requête SQL
	 */
	private static void loadTableWithDownloads(String where) {
		cntActions.removeAllItems();
		cntActions.addAll(getDownloadsList(where));
		tblActions.setVisibleColumns(new String[]{"action","action_text","joco_os","joco_browser","joco_ip","joco_mail","joco_date"});
		tblActions.setColumnHeaders(new String[]{"",Global.i("CAPTION_ACTION"),Global.i("CAPTION_AUTHOR_FOLDER"),Global.i("CAPTION_FOLDER"),Global.i("CAPTION_FILENAME"),Global.i("CAPTION_CONTACT_ACTION_AFFECTED"),Global.i("CAPTION_DATE")});
	}
	/**
	 * Récupération de la liste des utilisateurs
	 * @param type 0 = connexion, 1 = validations
	 * @param condition Valeur de la clause WHERE de la requête SQL
	 * @return Liste des utilisateurs
	 */
	private static ArrayList<JournalCon> getConnexionsList(int type, String condition) {
		ArrayList<JournalCon> connectList = new ArrayList<JournalCon>();
		String where = (!condition.isEmpty()) ? " AND joco_action="+Utilities.formatSQL(condition) : "";

		String query = (type==0) ? tbl_journal_con.getSelectConnexions(where) : tbl_journal_con.getSelectValidations(where);
		ResultSet action = Sql.query(query);
		try {
			while(action.next()) {
				connectList.add(
					new JournalCon(
						action.getInt("PKNoJourConnection"),
						action.getString("joco_mail"),
						Utilities.formatSQLDate(action.getString("joco_date"), "dd MMMM YY - HH:mm:ss"),
						action.getString("joco_ip"),
						action.getString("joco_os"),
						action.getString("joco_browser"),
						action.getString("joco_action"),
						action.getString("joco_comment")
					)
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connectList;
	}
	/**
	 * Récupération de la liste des actions journalisées pour les téléchargements
	 * @param condition Valeur de la clause WHERE de la requête SQL
	 * @return Liste des téléchargements
	 */
	private static ArrayList<JournalCon> getDownloadsList(String condition) {
		ArrayList<JournalCon> downloadList = new ArrayList<JournalCon>();
		String where = (!condition.isEmpty()) ? condition : "";

		String query = tbl_journal_fold.getSelectAll(where);
		ResultSet action = Sql.query(query);
		
		try {
			while(action.next()) {
				downloadList.add(new JournalCon(
					action.getInt("PKNoJourFolder"),
					action.getString("contact_name")+" <"+action.getString("contact_mail")+">",
					Utilities.formatSQLDate(action.getString("joufo_date"), "dd MMMM YY - HH:mm:ss"),
					(action.getString("FKNoFile")==null) ? (action.getString("joufo_action").equals("download")) ? Global.i("CAPTION_ARCHIVE_OF_FOLDER") : "-" : action.getString("file_rename")+" ("+action.getString("file_name")+")",
					action.getString("user_mail"), // Nom de l'auteur
					action.getString("folder_name"),
					(action.getString("FKNoFile")==null && action.getString("joufo_action").equals("download")) ? "archive" : action.getString("joufo_action"),
					action.getString("FKNoFile")
				));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return downloadList;
	}
	/**
	 * Déselectionne tous les boutons du menu (par changement de style)
	 */
	private static void deselectAllButtons() {
		for(Button button : btMenu) {
			button.removeStyleName(Runo.BUTTON_DEFAULT);
		}
	}
}
