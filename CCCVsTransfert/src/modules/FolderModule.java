package modules;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import main.CccvsTransfert;
import main.MainLayout;
import model.Contact;
import model.Files;
import model.Folder;
import sql.query.tbl_contacts;
import sql.query.tbl_files;
import sql.query.tbl_folders;
import sql.query.tbl_journal_fold;
import sql.query.tbl_recipients;
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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

import common.component.ContactTable;

import form.WizFoldNew;
import form.WizFoldUpload;
import global.Global;
import global.GlobalObjects;

/**
 * Page d'affichage des fichiers et informations d'un dossier.<br>
 * Le ruban n'est pas rechargé par cette class, le bouton d'ajout d'un dossier est donc conservé.<br>
 * Un panneau affiche les informations du dossier<br>
 * <ul><li>Création</li><li>Expiration</li><li>Disponibilité</li><li>Destinataires</li><li>Statut (Archivé ou non)</li><li>...</li></ul><br><br>
 * Les types de fichiers sont illustrés par une icône d'extension.
 * @author Dominique Roduit
 *
 */
public class FolderModule {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private static GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fenêtre principale **/
	private static Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private static MainLayout mainLayout = global.getMainLayout();
	/** Bouton d'ajout d'un contact **/
	private static Button btAddFolder;
	/** Table contenant la liste des contacts **/
	private static ContactTable tblFolder;
	/** Contient la fenêtre de création d'un dossier **/
	private static Window winFolder;
	/** Contient la clé primaire du dossier à afficher **/
	private static int FKNoFolder = 0;
	/** Container contenant la liste des fichiers du dossier **/
	private static BeanItemContainer<Files> container = new BeanItemContainer<Files>(Files.class);
	/** Table contenant les fichier contenus dans le dossier **/
	private static Table tblFiles;
	/** Action "Télécharger" du menu contextuel **/
	private static Action ACTION_DOWNLOAD;
	/** Action pour la récupération du lien de téléchargement **/
	protected static Action ACTION_GET_LINK;
	/** Informations sur le dossier **/
	private static Folder folder = null;

	/**
	 * Chargement des boutons dans le ruban du corps de page
	 */
	public static Button getBodyRibbonContent() {
	    return null;
	} 
	/**
	 * Insertion du contenu dans le corps de page.<br>Le contenu comprend le tableau répertoriant les fichiers ainsi que les informations relatives au dossier.
	 */
	public static HorizontalLayout getBodyContent(int PKNoFolder) {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
		
		ACTION_DOWNLOAD = new Action(Global.i("CAPTION_DOWNLOAD"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"down.png"));
		ACTION_GET_LINK = new Action(Global.i("CAPTION_GET_DOWNLOAD_LINK"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"link.png"));
		
		FKNoFolder = PKNoFolder;
		
		// Récupération des informations sur le dossier
		ResultSet info = Sql.query(tbl_folders.getSelectFolderInfos(PKNoFolder));
		
		try {
			if(info.first()) {
				folder = new Folder(
						info.getInt("PKNoFolder"), info.getInt("FKNoUser"),
						info.getString("folder_name"), info.getString("folder_description"),
						info.getString("folder_creation_date"), info.getString("folder_expiration"),
						info.getBoolean("folder_archive"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		HorizontalLayout body = new HorizontalLayout();
		body.setSizeFull();
		
		GridLayout grdLayout = new GridLayout(2,1);
		grdLayout.setColumnExpandRatio(0, 100);
		grdLayout.setSizeFull();
		body.addComponent(grdLayout);
	
		ArrayList<Files> fileList = getFileList();
		container.removeAllItems();
		container.addAll(fileList);
		
		tblFiles = new Table();
		tblFiles.setSizeFull();
		tblFiles.setSelectable(true);
		tblFiles.setContainerDataSource(container);
		tblFiles.setVisibleColumns(new String[]{"file_rename", "file_size_formatted", "file_extension","file_description"});
		tblFiles.setColumnHeaders(new String[]{Global.i("CAPTION_FILENAME"),Global.i("CAPTION_SIZE"),Global.i("CAPTION_TYPE"),Global.i("CAPTION_DESCRIPTION")});
		tblFiles.setColumnCollapsingAllowed(true);
		tblFiles.setColumnReorderingAllowed(true);
		tblFiles.setNullSelectionAllowed(false);
		tblFiles.setColumnCollapsed("file_description", true);
		// Clique d'une ligne du tableau
		tblFiles.addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if(event.getButton()==ItemClickEvent.BUTTON_LEFT) {
					Files slctFile = ((Files) event.getItemId());
					System.out.println(slctFile.getPKNoFile()+" : "+slctFile.getFile_name()+" - "+slctFile.getFile_size_formatted());
				}
			}
		});
		tblFiles.setColumnWidth("file_size_formatted", 90);
		tblFiles.setColumnWidth("file_extension", 70);
		
		// Ajout de la colonne des icones
		tblFiles.addGeneratedColumn("icon", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Files file = (Files)itemId;
				return Utilities.getImgFromExtension(file.getFile_extension());
			}
		});
		tblFiles.setColumnHeader("icon", "");
		tblFiles.setColumnWidth("icon", 28);
		tblFiles.setColumnAlignment("icon", Table.ALIGN_CENTER);
		tblFiles.setColumnCollapsible("icon", false);
		
		// Réorganisation des colonnes
		tblFiles.setVisibleColumns(new String[]{"icon", "file_rename", "file_size_formatted", "file_extension","file_description"});
		grdLayout.addComponent(tblFiles,0,0);
		
		// Menu contextuel lors du clique droit sur un fichier
		tblFiles.addActionHandler(new Action.Handler() {
			public void handleAction(Action action, Object sender, Object target) {
				if(action==ACTION_DOWNLOAD) {
					Files file = (Files)target;
					File fileToDownload = new File(Global.UPLOAD_DIR+file.getFile_name());
					
					if(fileToDownload.exists()) {
						CccvsTransfert.mainWindow.open(new ExternalResource(Utilities.getURLForFile(file.getFile_name())),"_self");
					} else {
						CccvsTransfert.mainWindow.showNotification(Global.i("CAPTION_INEXISTING_FILE"), Notification.TYPE_WARNING_MESSAGE);
					}
				}
				
				if(action==ACTION_GET_LINK) {
					Files file = (Files)target;
					global.openWindowGetLink(file.getFile_name());
				}
			}
			public Action[] getActions(Object target, Object sender) {
				if(target!=null && !folder.getArchive()) {
					return new Action[] { ACTION_DOWNLOAD, ACTION_GET_LINK };
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
		//vlInfo.setMargin(true);
		grdLayout.addComponent(vlInfo,1,0);
		
		GridLayout grdInfos = new GridLayout(1,3);
		grdInfos.setSizeFull();
		vlInfo.addComponent(grdInfos);
		
		
		String contentLabel = "<div class=\"infos\">" +
		"<h2>"+Utilities.htmlentities(folder.getName())+"</h2>" +
		"<div class=\"filesize\">"+
		"<div style=\"float:left; width:50%; border-right: 1px solid #ddd\">"+tbl_files.getFileNumberFromFolder(PKNoFolder)+" "+Global.i("CAPTION_FILES").toLowerCase()+"</div>"+
		"<div style=\"float:left; width:49%;\">"+Utilities.formatSize(tbl_files.getFileSizeFromFolder(PKNoFolder))+"</div>"+
		"<div style=\"clear:both\"></div>"+
		"</div>"+
		"<div class=\"dates\">";
		
		if(folder.getArchive()) { // Dossier archivé
			contentLabel +=
			"<table>"+
				"<tr><th>"+Global.i("CAPTION_CREATION")+"</th> <th>"+Global.i("CAPTION_EXPIRED")+"</th></tr>"+
				"<tr><td>"+Utilities.formatSQLDateWtText(folder.getCreation_date(), "dd MMMM YY")+"</td> <td>"+Utilities.formatSQLDateWtText(folder.getExpiration_date(), "dd MMMM YY")+"</td></tr>"+
			"</table>";
		} else { // Dossier disponible
			String restant = "";
			int jRestant = tbl_folders.getRemainingDaysFolderIsAvailable(PKNoFolder);
			int hRestant = tbl_folders.getRemainingHoursFolderIsAvailable(PKNoFolder);
			restant = (jRestant>0) ? "<span style=\"color:green\">"+jRestant+" "+Global.i("CAPTION_DAYS").toLowerCase()+"</span>" : "<span style=\"color:orange\">"+  (hRestant+1) +" "+Global.i("CAPTION_HOURS").toLowerCase()+"</span>"; 
			
			
			if(jRestant==0 && hRestant<=0) {
				restant = "<span style=\"color:red\">"+Global.i("CAPTION_ARCHIVAGE_AT")+" "+Utilities.zeroFill(Integer.parseInt(Utilities.formatSQLDate(folder.getCreation_date(),"HH"))+1)+":00</span>";
			}
			
			contentLabel +=
			"<table>"+
				"<tr><th>"+Global.i("CAPTION_CREATION")+"</th> <th>"+Global.i("CAPTION_EXPIRATION")+"</th> <th>"+Global.i("CAPTION_RESTANT")+"</th></tr>"+
				"<tr><td>"+Utilities.formatSQLDateWtText(folder.getCreation_date(), "dd MMMM YY")+"</td> <td>"+Utilities.formatSQLDateWtText(folder.getExpiration_date(), "dd MMMM YY")+"</td> <td>"+restant+"</td></tr>"+
			"</table>";
		}
		
		contentLabel +=
		"</div>";
		
		if(folder.getDescription()!=null && folder.getDescription().length()>0) {
			contentLabel += "<div class=\"description\">"+Utilities.htmlentities(folder.getDescription())+"</div>";
		}
		
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
			
		
		if(recipient.size()>0) {
			contentLabel+= "<p class=\"title\">"+Global.i("CAPTION_RECIPIENTS")+"</p>";
		} else {
			contentLabel+= "<br><div align=\"center\"><b>"+Global.i("CAPTION_NO_RECIPIENT")+"</b></div>";
		}
		
		contentLabel+="</div>";
		
		// Affichage du contenu imbriqué jusqu'ici (informations sur le dossier)
		Label lblInfos = new Label(contentLabel,Label.CONTENT_XHTML);
		grdInfos.addComponent(lblInfos, 0,0);
		
		// Affichage des boutons des destinataires
		VerticalLayout vlRecipients = new VerticalLayout();
		vlRecipients.setWidth("100%");
		vlRecipients.setStyleName("vl-recipients");
		grdInfos.addComponent(vlRecipients, 0, 1);
		grdInfos.setRowExpandRatio(1, 100);
		
		if(recipient.size()>0) {
			ArrayList<Button> btRecipient = new ArrayList<Button>();
			int i = 0;
			// On parcours tout les destinataires
			for (final Contact contact : recipient) {
				btRecipient.add(new Button(contact.getName()));
				btRecipient.get(i).setStyleName(Runo.BUTTON_SMALL);
				
				final ResultSet data = Sql.query(
					"SELECT * FROM trans_journal_folders " +
					"LEFT JOIN trans_files ON FKNoFile=PKNoFile " +
					"WHERE FKNoContact="+contact.getPK()+" AND trans_journal_folders.FKNoFolder="+folder.getPKNoFolder()+" "+
					"ORDER BY joufo_date DESC"		
				);
				
				
				try {
					if(data.first()) { // Le contact a des actions.
						int numberActions = 0;
						data.last();
						numberActions = data.getRow();
						data.beforeFirst();
						
						btRecipient.get(i).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"play.png"));
						btRecipient.get(i).addStyleName("bt-active");
						btRecipient.get(i).setDescription(numberActions+" "+Global.i("CAPTION_LOG_ACTIONS"));
						btRecipient.get(i).addListener(new ClickListener() {
							public void buttonClick(ClickEvent event) {
								Window winStat = new Window(Global.i("CAPTION_LOG_ACTIONS_FOR")+" \""+contact.getName()+"\"");
								CccvsTransfert.mainWindow.addWindow(winStat);
								winStat.center();
								winStat.setWidth("740px");
								winStat.setHeight("510px");
								winStat.setResizable(false);
								winStat.setModal(true);
								
								GridLayout grdStat = new GridLayout(2, 1);
								grdStat.setStyleName("winStat");
								grdStat.setSizeFull();
								grdStat.setColumnExpandRatio(1, 100);
								winStat.addComponent(grdStat);
								
								VerticalLayout vlLeft = new VerticalLayout();
								vlLeft.setSizeFull();
								vlLeft.setWidth("220px");
								grdStat.addComponent(vlLeft,0,0);
								
								VerticalLayout vlRight = new VerticalLayout();
								vlRight.setSizeFull();
								grdStat.addComponent(vlRight,1,0);
								
								Label lblResume = new Label(Global.i("CAPTION_RESUME"));
								lblResume.setStyleName("title");
								vlLeft.addComponent(lblResume);
								
								
								Label lblDetailled = new Label(Global.i("CAPTION_DETAILLED"));
								lblDetailled.setStyleName("title");
								vlRight.addComponent(lblDetailled);
								
								Table tblStatExpanded = new Table();
								tblStatExpanded.setSizeFull();
								tblStatExpanded.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
								tblStatExpanded.addContainerProperty("action_img", Embedded.class, null, "", null, Table.ALIGN_CENTER);
								tblStatExpanded.addContainerProperty("action_type", String.class, null, Global.i("CAPTION_OBJECT"), null, null);
								tblStatExpanded.addContainerProperty("action_date", String.class, null, Global.i("CAPTION_DATE"), null, null);
								tblStatExpanded.setColumnWidth("action_img", 23);
								tblStatExpanded.setColumnWidth("action_date", 140);
								tblStatExpanded.setColumnWidth("action_type", 260);
								vlRight.addComponent(tblStatExpanded);
								
								int consultations = 0;
								int archive = 0;
								int download = 0;
								try {
									data.beforeFirst(); 
									while(data.next()) {
										if(data.getString("joufo_action").equals("download")) {
											String filename = (data.getString("FKNoFile")!=null) ? data.getString("file_rename") : Global.i("CAPTION_ARCHIVE_OF_FOLDER");
											if(data.getString("FKNoFile")==null) { archive++; } else { download++; } 
											boolean isFile = (data.getString("FKNoFile")!=null) ? true : false;
											
											Embedded imgIllustr = new Embedded(null, new ThemeResource((isFile) ? Global.IMG_DOWNLOAD : Global.IMG_ARCHIVE_DOWNLOAD));
											tblStatExpanded.addItem(new Object[]{ imgIllustr, filename, Utilities.formatSQLDate(data.getString("joufo_date"), "dd MMMM YY HH:mm:ss") }, tblStatExpanded.size());
										} else {
											consultations++;
											Embedded imgIllustr = new Embedded(null, new ThemeResource(Global.IMG_SEARCH));
											tblStatExpanded.addItem(new Object[]{ imgIllustr, Global.i("CAPTION_CONSULTATION"), Utilities.formatSQLDate(data.getString("joufo_date"), "dd MMMM YY HH:mm:ss")  }, tblStatExpanded.size());
										}
									}
								} catch (UnsupportedOperationException e) {
									e.printStackTrace();
								} catch (SQLException e) {
									e.printStackTrace();
								}
								
								Table tblStatResume = new Table();
								tblStatExpanded.setSizeFull();
								tblStatResume.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
								tblStatResume.addContainerProperty("action_img", Embedded.class, null, "", null, Table.ALIGN_CENTER);
								tblStatResume.addContainerProperty("action_type", String.class, null, Global.i("CAPTION_ACTION"), null, null);
								tblStatResume.addContainerProperty("action_num", Integer.class, null, "", null, null);
								tblStatResume.setColumnWidth("action_img", 25);
								tblStatResume.setColumnWidth("action_num", 25);
								tblStatResume.addItem(new Object[]{new Embedded(null, new ThemeResource(Global.IMG_SEARCH)), Global.i("CAPTION_CONSULTATION"), consultations}, 1);
								tblStatResume.addItem(new Object[]{new Embedded(null, new ThemeResource(Global.IMG_ARCHIVE_DOWNLOAD)), Global.i("CAPTION_ARCHIVE_OF_FOLDER"), archive}, 2);
								tblStatResume.addItem(new Object[]{new Embedded(null, new ThemeResource(Global.IMG_DOWNLOAD)), Global.i("CAPTION_DOWNLOADS"), download}, 3);
								vlLeft.addComponent(tblStatResume);
							}
						});

					} else { // Si le contact n'a aucune action
						btRecipient.get(i).addStyleName("bt-non-active");
						btRecipient.get(i).addListener(new ClickListener() {
							public void buttonClick(ClickEvent event) {
								CccvsTransfert.mainWindow.showNotification(contact.getName(), Global.i("CAPTION_FOLDER_NOT_WATCHED"), Notification.TYPE_WARNING_MESSAGE);
							}
						});
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				vlRecipients.addComponent(btRecipient.get(i));
				i++;
			}
		}
		
		Label lblStatus = new Label(Global.i("CAPTION_FREE"));
		lblStatus.setStyleName("folder-status");
		if(folder.getArchive()) {
			lblStatus.addStyleName("archive");
			lblStatus.setValue(Global.i("CAPTION_ARCHIVED"));
		}
		grdInfos.addComponent(lblStatus, 0,2);
		grdInfos.setComponentAlignment(lblStatus, Alignment.BOTTOM_CENTER);

		
		return body;
	}
	/**
	 * Retourne la liste des fichiers contenus dans le dossier sélectionné
	 * @return Liste des fichiers du dossier
	 */
	private static ArrayList<Files> getFileList() {
		ArrayList<Files> files = new ArrayList<Files>();
		ResultSet data = Sql.query(tbl_files.getSelectAllFilesFromFolder(FKNoFolder));
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
					//Utilities.getImgFromExtension(data.getString("file_extension"))
				));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return files;
	}
}
