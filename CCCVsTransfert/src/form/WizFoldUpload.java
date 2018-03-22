package form;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.easyuploads.MultiFileUpload;

import sql.query.tbl_contacts;
import sql.query.tbl_folders;
import sql.query.tbl_recipients;
import toolbox.Mailer;
import toolbox.Sql;
import toolbox.Utilities;
import toolbox.ZipFileWritter;

import main.CccvsTransfert;
import model.Contact;
import model.Folder;
import modules.FolderModule;

import common.component.upload.CustomUpload;
import common.component.upload.FileUp;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;

import common.component.ContactTable;
import common.component.MenuContactTable;

import global.Global;
import global.GlobalObjects;
import global.Module;
import global.UserSession;

/**
 * 3e vue de l'assistant de création d'un dossier.<br>
 * Ce Wizard permet l'upload des fichiers, le stockage dans un tableau et le renommage des fichiers envoyés.<br>
 * Sur le clic du bouton "Terminer", toutes les informations sont enregistrées dans la base de données,
 *  les mails envoyés aux destinataires et l'archive contenant l'ensemble des fichiers du dossier est générée.
 * @author Dominique Roduit
 *
 */
public class WizFoldUpload extends CustomComponent {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la sous-fenêtre qui va contenir ce formulaire **/
	private Window winFolder = global.getWindowFolder();
	/** Layout vertical permettant d'afficher le formulaire en ligne **/
	private VerticalLayout mainLayout;
	/** Bouton Ajouter **/
	private static Button btFinish;
	/** Formulaire permettant l'ajout et l'édition **/
	private Form form;
	/** Bas du formulaire **/
	private HorizontalLayout footerLayout;
	/** Zone contenant les boutons du formulaire **/
	private HorizontalLayout buttons;
	/** Bouton Annuler **/
	private static Button cancel;
	/** Informations sur le dossier **/
	private Folder folder = null;
	/** Liste des contacts sélectionnés comme destinataires **/
	private ArrayList<Integer> slctContacts = new ArrayList<Integer>();
	/** Table qui va contenir les fichiers uploadés **/
	private Table tblUpload;
	/** Action supprimé **/
	private Action ACTION_DEL;
	/** Label qui indique de sélectionner les fichiers (sur les navigateur ou le drag&drop n'est pas implémenté) **/
	private static Label lblSlctFiles = null;
	/** Uploader **/
	private CustomUpload mFileUp;
	/** Dossier dans lequel nous enverrons nos fichiers **/
	private String ROOT_DIRECTORY;
	/** Bouton "Renommer" **/
	private static Button btRename;
	/** Indique si nous sommes passé en vue "Renommage" **/
	private boolean isRenamedView;
	/** Label qui contient la taille envoyée/restante **/
	private static Label lblSize;
	/** Indicateur de progression général **/
	private static ProgressIndicator superIndicator;
	
	/**
	 * Enregistrement des valeurs récoltées par les Wizard précédent et création du formulaire
	 * @param folder Informations sur le dossier, définies dans le wizard 1
	 * @param slctContacts Destinataires sélectionnés dans le wizard 2
	 */
	public WizFoldUpload(Folder folder, ArrayList<Integer> slctContacts) {
		this.folder = folder;
		this.slctContacts = slctContacts;
		
		ACTION_DEL = new Action(Global.i("CAPTION_DELETE"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"false.png"));
		
		winFolder.setClosable(false);
		winFolder.setWidth("720px");
		winFolder.setHeight("360px");
		winFolder.setCaption(Global.i("CAPTION_TRANSFERT")+" - "+Global.i("TITLE_WINDOW_ADD_FILES"));
		winFolder.setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"reload.png"));
		winFolder.center();
		
		doForm();
	}
	/**
	 * Construction du formulaire
	 */
	private void doForm() {
		Global.reloadParm();
		
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);
		
		// TABLE
		tblUpload = new Table();
		global.setUploadTable(tblUpload);
		tblUpload.setSelectable(true);
		tblUpload.setImmediate(false);
		tblUpload.setMultiSelect(true);
		tblUpload.setColumnReorderingAllowed(true);
        tblUpload.setColumnCollapsingAllowed(true);
		tblUpload.setSizeFull();
		tblUpload.setHeight("215px");
		tblUpload.setVisible(false);
		
		//tblUpload.addContainerProperty("file_id", String.class, null, "ID", null, null);
		tblUpload.addContainerProperty("file_icon", Embedded.class, null, "", null, Table.ALIGN_CENTER);
		tblUpload.addContainerProperty("file_name", String.class, null, Global.i("CAPTION_NAME"), null, null);
		tblUpload.addContainerProperty("file_size", String.class, null, Global.i("CAPTION_SIZE"), null, null);
		tblUpload.addContainerProperty("file_progress", ProgressIndicator.class, null, Global.i("CAPTION_PROGRESS"), null, Table.ALIGN_CENTER);
		tblUpload.addContainerProperty("file_etat", String.class, null, Global.i("CAPTION_STATUS"), null, Table.ALIGN_CENTER);
		
		tblUpload.setColumnWidth("file_icon", 23);
		tblUpload.setColumnWidth("file_name", 300);
		tblUpload.setColumnWidth("file_size", 66);
		tblUpload.setColumnWidth("file_progress", 150);
		tblUpload.setColumnWidth("file_etat", 70);
		
		mFileUp = new CustomUpload(folder, slctContacts);
		
		// Menu contextuel sur les éléments du tableau
		tblUpload.addActionHandler(new Action.Handler() {
			public void handleAction(Action action, Object sender, Object target) {
				if(action==ACTION_DEL) {
					// Obtention de la liste des fichiers renommés
					ArrayList<FileUp> fi = mFileUp.getRenamedFileList();
					
					
					Item item = tblUpload.getItem(target);
					String name = item.getItemProperty("file_name").toString();
					
					// Sélection de l'index du tableau ou réside le bon nom de fichier à supprimer.
					int indexToDelete = 0;
					for(int i=0; i<fi.size(); i++) {
						if(fi.get(i).getOriginalName().equals(name)) {
							indexToDelete = i;
						}
					}
					
					System.out.println(
						"name : "+name+"\n" +
						"index : "+indexToDelete+"\n" +
						"nom sur le disque : "+fi.get(indexToDelete).getFileDiskInfo().getName()+"\n"
					);
					
					// Suppression physique du fichier renommé
					File file = new File(Global.UPLOAD_DIR+fi.get(indexToDelete).getFileDiskInfo().getName());
					if(file.exists()) {
						mFileUp.decreaseQueueSize(file.length());
						lblSize.setValue(Utilities.formatSize(mFileUp.getQueueSize())+" / "+Utilities.formatSize(Long.parseLong(Global.getParm("UPLOAD_MAX_SIZE").replace(" ",""))));
						
						if(file.delete()) {
							getWindow().showNotification(null, Global.i("CAPTION_FILE_DELETED"), Notification.TYPE_TRAY_NOTIFICATION);
						}
					}

					// Suppression de la ligne du tableau
					tblUpload.removeItem(target);
					
					
					// Si on a tout supprimé les éléments du tableau
					if(tblUpload.size()<1) {
						btFinish.setEnabled(false);
						btFinish.setVisible(false);
						btRename.setVisible(false);
						cancel.setVisible(true);
						cancel.setEnabled(true);
					}
				}
			}
			public Action[] getActions(Object target, Object sender) {
				if(target!=null) {
					if(mFileUp.getStatus()==1) {
						return new Action[] { ACTION_DEL };
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		});
		// Clique droit sur les éléments du tableau
		tblUpload.addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if(event.getButton()==ItemClickEvent.BUTTON_RIGHT && mFileUp.getStatus()==1) {
					tblUpload.setValue(null);   // Dé-sélectionne tout
					tblUpload.select(event.getItemId()); // Sélectionne la ligne en cours
				}
			}
		});
		
		
		// UPLOAD
		// -----------------------------------------------------------
		mFileUp.setCaption(null);
		
		ROOT_DIRECTORY = Global.UPLOAD_DIR;
		mFileUp.setRootDirectory(ROOT_DIRECTORY);
		mFileUp.setUploadButtonCaption(Global.i("CAPTION_UPLOAD_BUTTON"));
		mFileUp.setAllowedExtentions(Global.getParm("UPLOAD_ALLOWED_EXTENSIONS")); 
		global.setUploadModule(mFileUp);
		
		System.out.println(Global.getParm("UPLOAD_ALLOWED_EXTENSIONS"));
		
		boolean DragAndDropSupported = false;
		if(Global.browser.isChrome()) {
			DragAndDropSupported = true;
        } else if (Global.browser.isFirefox()) {
        	DragAndDropSupported = true;
        } else if (Global.browser.isSafari()) {
        	DragAndDropSupported = true;
        }
		
		// Si le navigateur ne supporte pas le drag&drop
		if(!DragAndDropSupported) {
			lblSlctFiles = new Label("<br><br><br><br><br>"+Global.i("CAPTION_SELECT_FILES"), Label.CONTENT_XHTML);
			lblSlctFiles.setSizeFull();
			lblSlctFiles.setHeight("210px");
			lblSlctFiles.setStyleName("v-label-slct-files");
			lblSlctFiles.addStyleName("align-center");
		}
		// ---------------------------------------------------------------
		
		mainLayout.addComponent(mFileUp);
		mainLayout.addComponent(tblUpload);
		if(lblSlctFiles!=null && !DragAndDropSupported) {
			mainLayout.addComponent(lblSlctFiles);
		}
		global.setUploadModuleLayout(mainLayout);
	
	
		buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		mainLayout.addComponent(buttons);
		mainLayout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		
		
		lblSize = new Label(Utilities.formatSize(0)+" / "+Utilities.formatSize(Long.parseLong(Global.getParm("UPLOAD_MAX_SIZE").replace(" ",""))));
		lblSize.setStyleName("caption-uploadsize");
		buttons.addComponent(lblSize);
		buttons.setComponentAlignment(lblSize, Alignment.MIDDLE_LEFT);
		
		superIndicator = new ProgressIndicator((float)0);
		superIndicator.setStyleName("super");
		superIndicator.setVisible(false);
		buttons.addComponent(superIndicator);
		buttons.setComponentAlignment(superIndicator, Alignment.MIDDLE_LEFT);
		
		
		cancel = new Button(Global.i("CAPTION_CANCEL"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				(winFolder.getParent()).removeWindow(winFolder);
			}
		});
		cancel.setTabIndex(4);
		buttons.addComponent(cancel);
		buttons.setComponentAlignment(cancel, Alignment.BOTTOM_RIGHT);
		
		
		// Bouton "Renommer"
		btRename = new Button(Global.i("CAPTION_RENAME_FILES"));
		btRename.addListener(EditTable);
		btRename.setVisible(false);
		buttons.addComponent(btRename);
		buttons.setComponentAlignment(btRename, Alignment.MIDDLE_RIGHT);
		
		// Création du bouton "Ajouter"
		btFinish = new Button(Global.i("CAPTION_FINISH"));
		btFinish.setStyleName(Runo.BUTTON_DEFAULT);
		btFinish.setVisible(false);
		btFinish.setTabIndex(3);
		btFinish.setClickShortcut(KeyCode.ENTER);
		btFinish.addListener(SubmitListener);
		buttons.addComponent(btFinish);
		buttons.setComponentAlignment(btFinish, Alignment.MIDDLE_RIGHT);
		
		setCompositionRoot(mainLayout);
	}
	/**
	 * Retourne le bouton "Terminer"
	 * @return Bouton "Terminer"
	 */
	public static Button getBtFinish() {
		return btFinish;
	}
	/**
	 * @return Bouton "Renommer"
	 */
	public static Button getBtRename() {
		return btRename;
	}
	/**
	 * Retourne le bouton "Annuler"
	 * @return Bouton "Annuler"
	 */
	public static Button getBtCancel() {
		return cancel;
	}
	/**
	 * Retourne le label indiquant de sélectionner les fichier
	 * @return Label indiquant de sélectionner les fichiers
	 */
	public static Label getSlctFileLbl() {
		return lblSlctFiles;
	}
	/**
	 * @return Label qui contient la taille envoyée/restante
	 */
	public static Label getLblSize() {
		return lblSize;
	}
	/**
	 * @return Indicateur de progression de l'envoi des fichiers
	 */
	public static ProgressIndicator getSuperIndicator() {
		return superIndicator;
	}
	/**
	 * Action sur le clic du bouton "Renommer"
	 */
	private ClickListener EditTable = new ClickListener() {
		public void buttonClick(ClickEvent event) {
			mFileUp.setVisible(false);
			tblUpload.setStyleName("no-resizable");
			tblUpload.setHeight("245px");
			tblUpload.select(null);
			tblUpload.setValue(null);
			tblUpload.setSelectable(false);
			tblUpload.setColumnCollapsingAllowed(false);
			tblUpload.setStyleName("table-editable");
			tblUpload.setVisibleColumns(new String[]{"file_icon", "file_name"});
			tblUpload.addContainerProperty("file_description", String.class, "", Global.i("CAPTION_DESCRIPTION"), null, null);
			tblUpload.setColumnWidth("file_name", 240);
			tblUpload.setEditable(true);
			tblUpload.setCurrentPageFirstItemIndex(tblUpload.getCurrentPageFirstItemIndex());
			tblUpload.setColumnWidth("file_description", 240);
			tblUpload.removeAllActionHandlers();
			winFolder.setCaption(Global.i("CAPTION_TRANSFERT")+" - "+Global.i("CAPTION_RENAME_FILES"));
			btRename.setVisible(false);
			isRenamedView = true;
		}
	};
	/**
	 * Action sur le clic du bouton "Terminer"
	 */
	private ClickListener SubmitListener = new ClickListener() {
		public void buttonClick(ClickEvent event) {
			
			int PKNoFolder = 0;
			
			// Création du dossier
			Sql.exec(tbl_folders.getInsertFolder(folder.getName(), folder.getExpiration(), folder.getDescription()));
			ResultSet dataF = Sql.query(tbl_folders.getSelectMaxPK());
			try {
				if(dataF.first()) {
					PKNoFolder = dataF.getInt("PKNoFolder");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			// Enregistrement des destinataires
			for(int contact : slctContacts) {
				Sql.exec(tbl_recipients.getInsertRecipient(PKNoFolder, contact));
			}
			
			// Obtention de la liste des fichiers a enregistrer ------------------------------
			// Contient tous les fichiers envoyés, même ceux non autorisés
			ArrayList<FileUp> fi = mFileUp.getRenamedFileList();
			// Contient tous les fichiers autorisés
			ArrayList<FileUp> fiWithouthNonAutExt = new ArrayList<FileUp>();
			
			System.out.println("Liste des fichiers renommés : ");
			
			// On ajoute à la liste uniquement les fichiers autorisés
			String extAutorise = Global.getParm("UPLOAD_ALLOWED_EXTENSIONS");
			for(FileUp f : fi) {
				System.out.println(f.getId()+" - "+f.getOriginalName()+" - "+f.getFileDiskInfo().getName());
				if(extAutorise.indexOf(Utilities.getExtension(f.getFileDiskInfo().getName()))>-1 || extAutorise.equals("*")) {
					System.out.println("ajout : "+f.getId()+" - "+f.getOriginalName()+" - "+f.getFileDiskInfo().getName());
					fiWithouthNonAutExt.add(f);
				}
			}
			
			// Contient les fichiers autorisés et qui sont affichés dans le tableau (expulsion des fichiers supprimés du tableau)
			ArrayList<FileUp> listFileOk = new ArrayList<FileUp>(); // Contient les fichiers a enregistrer
			System.out.println("Fichiers sélectionnés : ");
			for (Object item : tblUpload.getItemIds()) {
				int row = Integer.parseInt(item.toString());
				listFileOk.add(fiWithouthNonAutExt.get(row-1));
				
				System.out.println( (row-1)+" - "+fi.get(row-1).getId()+" - "+fiWithouthNonAutExt.get(row-1).getOriginalName()+" - "+fiWithouthNonAutExt.get(row-1).getFileDiskInfo().getName() );
			}
			

			// Si on est en vue "Renommage", on assigne le nouveau nom donné aux fichiers
			if(isRenamedView) {
				for (FileUp fileOk : listFileOk) {
					Item it = tblUpload.getItem(fileOk.getId());
					if(it!=null) {
						String nomDonneParUtilisateur = it.getItemProperty("file_name").toString();
						fileOk.setOriginalName((nomDonneParUtilisateur.isEmpty()) ? fileOk.getOriginalName() : nomDonneParUtilisateur);
						fileOk.setRename(fileOk.getOriginalName());
						fileOk.setDescription(it.getItemProperty("file_description").toString());
					}
				}
			}
			
			
			// Enregistrement du nom des fichiers envoyés
			String queryInsert = "INSERT INTO trans_files VALUES ";
			for (FileUp file : listFileOk) {
				File fileOnDisk = file.getFileDiskInfo();
				//System.out.println(fileOnDisk.getName()+" ("+file.getOriginalName()+") -- "+fileOnDisk.length()+" --- "+Utilities.getExtension(fileOnDisk.getName()));
				
				String nameOnDisk = fileOnDisk.getName();
				String originalName = file.getOriginalName().replace(Utilities.getExtension(file.getOriginalName()), "");
				if(originalName.length()>=50) originalName = originalName.substring(0,50);
				originalName += Utilities.getExtension(file.getOriginalName());
				
				long size = fileOnDisk.length();
				
				queryInsert += "(NULL, "+PKNoFolder+", "+Utilities.formatSQL(nameOnDisk)+", "+Utilities.formatSQL(originalName)+"," +
								size+", "+Utilities.formatSQL(Utilities.getExtension(nameOnDisk))+", "+Utilities.formatSQL(file.getDescription())+"),";
			}
			queryInsert = queryInsert.substring(0, queryInsert.length()-1);
			Sql.exec(queryInsert);
			
			
			// On récupère les contact sélectionnés comme destinataires dans le wizard précédent pour leur envoyer un mail
			ResultSet recipient = Sql.query(tbl_recipients.getSelectRecipientsFromFolder(PKNoFolder));
			ResultSet fold = Sql.query(tbl_folders.getSelectFolderInfos(PKNoFolder));
			
			try {
				int expiration = tbl_folders.getNumberOfDayFolderIsAvailable(PKNoFolder);
				
				String fold_expiration = "", fold_description="", fold_name="";
				if(fold.first()) {
					fold_expiration = fold.getString("folder_expiration");
					fold_description = folder.getDescription();
					fold_name = fold.getString("folder_name");
				}
				
				while(recipient.next()) {
					String to = recipient.getString("contact_mail");
					int PK = recipient.getInt("PKNoContact");
					Mailer.sendDownloadLink(to, PKNoFolder, PK, fold_expiration, expiration, fold_description, fold_name);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			
			// Génération de l'archive ZIP -----------------------------------
			try {
				ZipFileWritter zip = new ZipFileWritter(ROOT_DIRECTORY+Utilities.getFolderArchiveName(folder.getName()));
				
				// Remplissage de l'archive
				for (FileUp file : listFileOk) {
					File fileOnDisk = file.getFileDiskInfo();
					
					String name = fileOnDisk.getName();
					zip.addFile(ROOT_DIRECTORY+name);
				}
				
				zip.close();
			} catch (IOException ex) {
				Logger.getLogger(ZipFileWritter.class.getName()).log(Level.SEVERE, null, ex);
			}
			
			
			// Mise à jour du menu
			global.getMenuFolderTable().reload();
			
			// Sélection du dernier dossier de la liste
			ResultSet data = Sql.query(tbl_folders.getSelectMaxPK());
			int lastFolder = 0;
			try {
				if(data.first()) {
					lastFolder = data.getInt("PKNoFolder");
					CccvsTransfert.loadModule(new Module(FolderModule.getBodyRibbonContent(), FolderModule.getBodyContent(lastFolder)));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			Sql.Disconnect();
			
			// Fermeture du wizard
			(winFolder.getParent()).removeWindow(winFolder);
			
			CccvsTransfert.mainWindow.showNotification(Global.i("CAPTION_FOLDER_SENT"), Global.i("CAPTION_FOLDER_SEND_DESCRIPTION") ,Notification.TYPE_TRAY_NOTIFICATION);
		}
	};
	
}
