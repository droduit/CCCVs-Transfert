package modules;

import global.Global;
import global.GlobalObjects;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.CccvsTransfert;
import main.MainLayout;
import model.ActionJournalFold;
import model.Contact;
import model.Files;
import model.Folder;
import model.User;
import sql.query.tbl_files;
import sql.query.tbl_folders;
import sql.query.tbl_journal_con;
import sql.query.tbl_journal_fold;
import sql.query.tbl_recipients;
import sql.query.tbl_users;
import toolbox.Sql;
import toolbox.Utilities;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;

/**
 * Page de téléchargement des fichiers d'un dossier dans lequel un ou plusieurs contacts ont été invités.
 * @author Dominique Roduit
 * @since 2013-03-08
 *
 */
public class DownloadModule {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private  GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fenêtre principale **/
	private Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private MainLayout mainLayout = global.getMainLayout();
	/** Bouton d'ajout d'un contact **/
	private  Button btDownloadAll;
	/** Table contenant la liste des contacts **/
	private  Table tblFiles;
	/** Clé primaire du dossier **/
	private int PKNoFolder = 0;
	/** Clé primaire du contact qui accède au dossier **/
	private int PKNoContact = 0;
	/** Adresse e-mail du contact qui accède au dossier **/
	private String mail = "";
	/** Label contenant le nom du dossier **/
	private Label lblFolderName;
	/** Modèle contenant les informations du dossier **/
	private Folder folder;
	/** Modèle contenant les informations sur l'auteur du dossier **/
	private User auteur;
	/** Container pour les actions journalisées du tableau **/
	private BeanItemContainer<ActionJournalFold> containerActions = new BeanItemContainer<ActionJournalFold>(ActionJournalFold.class);
	/** Table du footer contenant les actions journalisées **/
	private Table tblActions;
	/** Container contenant la liste des fichiers du dossier **/
	private BeanItemContainer<Files> container = new BeanItemContainer<Files>(Files.class);
	/** Action "Télécharger" du menu contextuel **/
	private Action ACTION;
	/** Nombre de consultation du dossier par le contact **/
	private int nbConsultation = 0;

	/**
	 * Création d'une page de téléchargement des fichiers d'un dossier spécifique
	 * @param PKNoFolder Clé primaire du dossier consulté
	 * @param PKNoContact Clé primaire du contact invité au dossier
	 * @param mail Adresse e-mail du contact invité au dossier
	 */
	public DownloadModule(int PKNoFolder, int PKNoContact, String mail) {
		this.PKNoFolder = PKNoFolder;
		this.PKNoContact = PKNoContact;
		this.mail = mail;
		
		ACTION = new Action(Global.i("CAPTION_DOWNLOAD"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"down.png"));
		
		// Récupération et stockage des informations sur le dossier
		ResultSet info = Sql.query(tbl_folders.getSelectFolderInfos(PKNoFolder));
		folder = new Folder();
		folder.setPKNoFolder(PKNoFolder);
		try {
			if(info.first()) {
				folder.setName(info.getString("folder_name"));
				folder.setCreation_date(info.getString("folder_creation_date"));
				folder.setExpiration_date(info.getString("folder_expiration"));
				folder.setArchive(info.getBoolean("folder_archive"));
				folder.setFKNoUser(info.getInt("FKNoUser"));
				folder.setDescription(info.getString("folder_description"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Récupération et stockage des informations sur l'auteur du dossier
		ResultSet iAut = Sql.query(tbl_users.getSelectUserInfos(folder.getFKNoUser()));
		auteur = new User(); 
		auteur.setPKNoUser(folder.getFKNoUser());
		try {
			if(iAut.first()) {
				auteur.setUser_mail(iAut.getString("user_mail"));
				auteur.setInternal(iAut.getBoolean("user_internal"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Journalisation de la consultation du dossier
		Sql.exec(tbl_journal_fold.getInsertQuery("consult", PKNoContact, PKNoFolder, 0));
		
		// Chargement du contenu du footer
		tblActions = new Table();
		tblActions.setSizeFull();
		tblActions.addContainerProperty("action_img", Embedded.class, null, "", null, Table.ALIGN_CENTER);
		tblActions.addContainerProperty("action_type", String.class, null, Global.i("CAPTION_ACTION"), null, null);
		tblActions.addContainerProperty("action_description", String.class, null, Global.i("CAPTION_DESCRIPTION"), null, null);
		tblActions.addContainerProperty("action_date", String.class, null, Global.i("CAPTION_DATE"), null, null);
		tblActions.setColumnWidth("action_img", 30);
		tblActions.setColumnWidth("action_type", 150);
		mainLayout.getFooter().addComponent(tblActions);
		
		ResultSet actionsJournal = Sql.query(tbl_journal_fold.getSelectAllForContactAndFolder(PKNoContact, PKNoFolder));
		try {
			while(actionsJournal.next()) {
				if(actionsJournal.getString("joufo_action").equals("download")) {
					addActionDownloadItem((actionsJournal.getString("FKNoFile")!=null) ? actionsJournal.getString("file_rename") : Global.i("CAPTION_ARCHIVE_OF_FOLDER"),
							actionsJournal.getString("joufo_date"),
							(actionsJournal.getString("FKNoFile")!=null) ? true : false);
				} else {
					addActionConsultItem(actionsJournal.getString("joufo_date"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Chargement des boutons dans le ruban du corps de la page
	 */
	public HorizontalLayout getBodyRibbonContent() {
		HorizontalLayout vlRibbon = new HorizontalLayout();
		vlRibbon.setSizeFull();
		vlRibbon.setWidth("96%");
		mainLayout.getBodyRibbon().addComponent(vlRibbon);
		
		lblFolderName = new Label(folder.getName());
		lblFolderName.setStyleName(Runo.LABEL_H1);
		vlRibbon.addComponent(lblFolderName);
		vlRibbon.setComponentAlignment(lblFolderName, Alignment.MIDDLE_LEFT);
		
		Button btDownload = new Button(Global.i("CAPTION_DOWNLOAD_ALL"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String archiveName = Utilities.getFolderArchiveName(folder.getName(), folder.getCreation_date());

				System.out.println(archiveName);
				
				File internFile = new File(Global.UPLOAD_DIR+archiveName);
				if(internFile.exists()) {
					CccvsTransfert.mainWindow.open(new ExternalResource(Utilities.getURLForFile(archiveName)),"_self");
					addActionDownloadItem(Global.i("CAPTION_ARCHIVE_OF_FOLDER"), false);
					Sql.exec(tbl_journal_fold.getInsertQuery("download", PKNoContact, PKNoFolder, 0));
				} else {
					mainWindow.showNotification(Global.i("CAPTION_INEXISTING_FILE"), Notification.TYPE_WARNING_MESSAGE);
				}

			}
		});
		btDownload.setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"download.png"));
		btDownload.setStyleName("ribbon-right-alignment");
		vlRibbon.addComponent(btDownload);
		vlRibbon.setComponentAlignment(btDownload, Alignment.MIDDLE_RIGHT);
		
	    return vlRibbon;
	} 
	/**
	 * Insertion du contenu dans le corps de la page.<br>Le contenu comprend un tableau listant les fichiers du dossier ainsi que les informations y relatives.
	 */
	public Table getBodyContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
		
		ArrayList<Files> fileList = getFileList();
		container.removeAllItems();
		container.addAll(fileList);
		
		// Table contenant les fichiers du dossier
		tblFiles = new Table();
		tblFiles.setSizeFull();
		tblFiles.setSelectable(true);
		tblFiles.setContainerDataSource(container);
		tblFiles.setVisibleColumns(new String[]{"file_rename", "file_size_formatted", "file_extension","file_description"});
		tblFiles.setColumnHeaders(new String[]{Global.i("CAPTION_FILENAME"),Global.i("CAPTION_SIZE"),Global.i("CAPTION_TYPE"), Global.i("CAPTION_DESCRIPTION")});
		tblFiles.setColumnCollapsingAllowed(true);
		tblFiles.setColumnReorderingAllowed(true);
		tblFiles.setNullSelectionAllowed(false);
		tblFiles.addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if(event.getButton()==ItemClickEvent.BUTTON_LEFT) {
					Files slctFile = ((Files) event.getItemId());
					System.out.println(slctFile.getFile_name()+" - "+slctFile.getFile_size_formatted());
				}
			}
		});
		tblFiles.setColumnWidth("file_size_formatted", 100);
		tblFiles.setColumnWidth("file_extension", 60);
		
		// Ajout de la colonne des boutons de téléchargement
		tblFiles.addGeneratedColumn("download", new Table.ColumnGenerator() {
			public Component generateCell(Table source, Object itemId, Object columnId) {
				final Files file = (Files)itemId;
				
				Button btDown = new Button(Global.i("CAPTION_DOWNLOAD"), new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						downloadFile(file);
					}
				});

				btDown.setStyleName(Runo.BUTTON_SMALL);
				return btDown;
			}
		});
		tblFiles.setColumnHeader("download", Global.i("CAPTION_ACTION"));
		tblFiles.setColumnWidth("download", 90);
		tblFiles.setColumnAlignment("download", Table.ALIGN_CENTER);
		
		// Ajout de la colonne des icones
		tblFiles.addGeneratedColumn("icone", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Files file = (Files)itemId;
				return Utilities.getImgFromExtension(file.getFile_extension());
			}
		});
		tblFiles.setColumnHeader("icone", "");
		tblFiles.setColumnWidth("icone", 28);
		tblFiles.setColumnAlignment("icone", Table.ALIGN_CENTER);
		tblFiles.setColumnCollapsible("icone", false);
		
		// Réorganisation des colonnes
		tblFiles.setVisibleColumns(new String[]{"icone", "file_rename", "file_size_formatted", "file_extension","file_description", "download"});

		tblFiles.addActionHandler(new Action.Handler() {
			public void handleAction(Action action, Object sender, Object target) {
				if(action==ACTION) {
					Files file = (Files)target;
					downloadFile(file);
				}
			}
			public Action[] getActions(Object target, Object sender) {
				if(target!=null) {
					return new Action[] { ACTION };
				} else {
					return null;
				}
			}
		});
		tblFiles.addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if(event.getButton()==ItemClickEvent.BUTTON_RIGHT) {
					tblFiles.setValue(null);   // Dé-sélectionne tout
					tblFiles.select(event.getItemId()); // Sélectionne la ligne en cours
				}
			}
		});
		
		VerticalLayout vlInfo = new VerticalLayout();
		vlInfo.setWidth(Global.getParm("LAYOUT_FOLDER_INFO_WIDTH"));
		vlInfo.setHeight("100%");
		vlInfo.setStyleName("info-folder");
		
		
		// Récupération des destinataires qui ont été ajoutés au dossier
		ArrayList<Contact> recipient = new ArrayList<Contact>();
		ResultSet dest = Sql.query(tbl_recipients.getSelectRecipientsFromFolder(PKNoFolder));
		try {
			while(dest.next()) {
				recipient.add(new Contact(dest.getInt("PKNoContact"), dest.getString("contact_name"), dest.getString("contact_mail"), dest.getBoolean("contact_internal")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String restant = "";
		int jRestant = tbl_folders.getRemainingDaysFolderIsAvailable(PKNoFolder);
		int hRestant = tbl_folders.getRemainingHoursFolderIsAvailable(PKNoFolder);
		restant = (jRestant>0) ? "<span style=\"color:green\">"+jRestant+" "+Global.i("CAPTION_DAYS").toLowerCase()+"</span>" : "<span style=\"color:orange\">"+( (Integer.parseInt(Utilities.formatSQLDate(folder.getExpiration_date(),"mm"))>30) ? (hRestant+1) : hRestant)+" "+Global.i("CAPTION_HOURS").toLowerCase()+"</span>"; 
		
		if(jRestant==0 && hRestant<=0) {
			restant = "<span style=\"color:red\">"+Global.i("CAPTION_ARCHIVAGE_AT")+" "+Utilities.zeroFill(Integer.parseInt(Utilities.formatSQLDate(folder.getCreation_date(),"HH"))+1)+":00</span>";
		}
		
		String contentLabel = "<div class=\"infos\">" +
		"<h2>"+Utilities.htmlentities(folder.getName())+"</h2>" +
		"<div class=\"filesize\">"+
		"<div style=\"float:left; width:50%; border-right: 1px solid #ddd\">"+tbl_files.getFileNumberFromFolder(PKNoFolder)+" "+Global.i("CAPTION_FILES").toLowerCase()+"</div>"+
		"<div style=\"float:left; width:49%;\">"+Utilities.formatSize(tbl_files.getFileSizeFromFolder(PKNoFolder))+"</div>"+
		"<div style=\"clear:both\"></div>"+
		"</div>"+
		"<div class=\"dates\">" +
		"<table>"+
			"<tr><th>"+Global.i("CAPTION_CREATION")+"</th> <th>"+Global.i("CAPTION_EXPIRATION")+"</th> <th>"+Global.i("CAPTION_AVAILABILITY")+"</th></tr>"+
			"<tr><td>"+Utilities.formatSQLDateWtText(folder.getCreation_date(), "dd MMMM YY")+"</td> <td>"+Utilities.formatSQLDateWtText(folder.getExpiration_date(), "dd MMMM YY")+"</td> <td>"+restant+"</td>"+
		"</table>"+
		"</div>";
		
		if(folder.getDescription()!=null) {
			if(folder.getDescription().length()>0) {
				contentLabel += "<div class=\"description\">"+Utilities.htmlentities(folder.getDescription())+"</div>";
			}
		}
		
		
		contentLabel+= "<p class=\"title\">"+Global.i("CAPTION_AUTHOR")+"</p>"+
		"<ul><li>"+auteur.getUser_mail()+"</li></ul>";
		
		
		if(recipient.size()>0) {
			contentLabel+= "<p class=\"title\">"+Global.i("CAPTION_RECIPIENTS")+" ("+recipient.size()+")</p>"+
			"<ul>";
			for (Contact contact : recipient) {
				String liDest = "<li>"+contact.getName()+"</li>";
				if(contact.getPK()==PKNoContact) {
					liDest = "<li style=\"font-weight:bold\">"+contact.getName()+" (Moi)</li>";
				}
				contentLabel += liDest;
			}
			contentLabel+= "</ul>";
		} else {
			contentLabel+= "<br><div align=\"center\"><b>"+Global.i("CAPTION_NO_RECIPIENT")+"</b></div>";
		}
		

		
		contentLabel+="</div>";

		
		Label lblInfos = new Label(contentLabel,Label.CONTENT_XHTML);
		vlInfo.addComponent(lblInfos);
		
		mainLayout.getMenu().addComponent(vlInfo);
		
		return tblFiles;
	}
	/**
	 * Lance le téléchargement d'un fichier et journalise l'opération
	 * @param file Informations sur le fichier
	 */
	private void downloadFile(Files file) {
		File fileToDownload = new File(Global.UPLOAD_DIR+file.getFile_name());
		
		if(fileToDownload.exists()) {
			CccvsTransfert.mainWindow.open(new ExternalResource(Utilities.getURLForFile(file.getFile_name())),"_self");
			// Journalisation
			Sql.exec(tbl_journal_fold.getInsertQuery("download", PKNoContact, PKNoFolder, file.getPKNoFile()));
			addActionDownloadItem(file.getFile_rename(), true);

		} else {
			mainWindow.showNotification(Global.i("CAPTION_INEXISTING_FILE"), Notification.TYPE_WARNING_MESSAGE);
		}

	}
	/**
	 * Ajoute une action de journalisation pour un téléchargement dans la table
	 * @param filename Nom du fichier téléchargé
	 */
	private void addActionDownloadItem(String filename, String file_date, boolean isFile) {
		Embedded imgIllustr = new Embedded(null, new ThemeResource((isFile) ? Global.IMG_DOWNLOAD : Global.IMG_ARCHIVE_DOWNLOAD));
		tblActions.addItem(new Object[]{ imgIllustr, Global.i("CAPTION_DOWNLOADING"), filename, Utilities.formatSQLDate(file_date, "dd MMMM YY HH:mm:ss") }, tblActions.size());
		tblActions.setCurrentPageFirstItemIndex(tblActions.size());
	}
	/**
	 * Ajoute une action de journalisation pour un téléchargement dans la table
	 * @param filename Nom du fichier téléchargé
	 */
	private void addActionDownloadItem(String filename, boolean isFile) {
		addActionDownloadItem(filename, Utilities.getCurrentDate(), isFile);
	}
	/**
	 * Ajoute une action de journalisation pour une consultation dans la table
	 */
	private void addActionConsultItem(String date) {
		nbConsultation++;
		Embedded imgIllustr = new Embedded(null, new ThemeResource(Global.IMG_SEARCH));
		tblActions.addItem(new Object[]{ imgIllustr, Global.i("CAPTION_CONSULTATION"), Global.i("CAPTION_CONSULTATION")+" "+nbConsultation, Utilities.formatSQLDate(date, "dd MMMM YY HH:mm:ss")  }, tblActions.size());
		tblActions.setCurrentPageFirstItemIndex(tblActions.size());
	}
	/**
	 * Ajoute une action de journalisation pour une consultation dans la table
	 */
	private void addActionConsultItem() {
		addActionConsultItem(Utilities.getCurrentDate());
	}
	/**
	 * Retourne la liste des fichiers contenus dans le dossier
	 * @return Liste des fichiers du dossier
	 */
	private ArrayList<Files> getFileList() {
		ArrayList<Files> files = new ArrayList<Files>();
		ResultSet data = Sql.query(tbl_files.getSelectAllFilesFromFolder(PKNoFolder));
		try {
			while(data.next()) {
				files.add(new Files(
					data.getInt("PKNoFile"),
					data.getInt("FKNoFolder"),
					data.getString("file_name"),
					data.getString("file_rename"),
					data.getLong("file_size"),
					data.getString("file_extension"),
					data.getString("file_description"),
					Utilities.formatSize(data.getLong("file_size"))
				));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return files;
	}
}