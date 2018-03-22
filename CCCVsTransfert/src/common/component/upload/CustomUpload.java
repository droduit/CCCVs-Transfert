package common.component.upload;

import form.WizFoldUpload;
import global.Global;
import global.GlobalObjects;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import main.CccvsTransfert;
import model.Folder;

import org.vaadin.easyuploads.FileBuffer;

import toolbox.Utilities;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * Utilisation de la classe MultipleFileUpload, récupération et implémentation des évènements de l’interface UploadActionListener. Gestion de l’affichage de la progression et de la limitation de la taille des fichiers.
 * @author Dominique Roduit
 *
 */
public class CustomUpload extends MultipleFileUpload { 
	/** Liste des fichiers à la fin de l'envoi, tels qu'ils ont été enregistrés sur le disque **/
	private ArrayList<FileUp> listRenamedFiles = new ArrayList<FileUp>();
	/** Stockage des objets globals **/
	private GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Fenêtre principale **/
	private Window mainWindow = CccvsTransfert.mainWindow;
	/** Tableau qui réceptionne les fichiers sélectionnés **/
	private Table uploadTable = global.getUploadTable();
	/** Indicateurs de progression **/
	private LinkedList<ProgressIndicator> indicators;
	/** Bouton "Terminer" **/
	private Button btFinish;
	/** Bouton "Annuler" **/
	private Button btCancel;
	/** Statut (0=en train d'envoyer, 1=libre) **/
	private int status = 0;
	/** Détermine si le transfert est autorisé selon le type des fichiers et leur tailles **/
	private boolean transfertAllowed = false;
	/** Module d'upload **/
	private CustomUpload mFileUp;
	
	
	/**
	 * Gestion des évènements qui surviennent lorsque des fichiers sont transférés
	 */
	public CustomUpload(final Folder folder, final ArrayList<Integer> slctContacts){
		super();	
	
		setAllowedExtentions(Global.getParm("UPLOAD_ALLOWED_EXTENSIONS"));
		
		addUploadActionListener(new UploadActionListener() {
			
			@Override
			public void fileUploadStarted(int idFile, String filename, int pendingFiles) {
				uploadTable.setValue(null);
				uploadTable.setValue(idFile);
				uploadTable.select(idFile);
				uploadTable.setCurrentPageFirstItemIndex(idFile);
			}
			
			@Override
			public void fileUploadProgress(int idFile, String filename, int pendingFiles,
					float progress) {

				String value = uploadTable.getValue().toString();
				value = value.replace("[","").replace("]", "").trim();
				
				if(value.length()>0 && value!=null) {
					Item item = uploadTable.getItem(Integer.parseInt(value));
					
					String percents = "0%";
					NumberFormat progression = NumberFormat.getNumberInstance();
					progression.setMaximumFractionDigits(1);
					percents = progression.format(progress).concat(" %");					
						
					item.getItemProperty("file_etat").setValue(percents);
					indicators.get(Integer.parseInt(value)-1).setValue(progress/100);
				}
				
			}
			
			@Override
			public void fileUploadFinished(int idFile, String filename, File file, int pendingFiles) {
				String value = uploadTable.getValue().toString();
				value = value.replace("[","").replace("]", "").trim();
			
				FileUp f = new FileUp(file, filename);
				f.setId(idFile);
				
				if(value.length()>0 && value!=null) {
					Item item = uploadTable.getItem(Integer.parseInt(value));
					item.getItemProperty("file_etat").setValue(Global.i("CAPTION_FINISHED"));
					indicators.get(Integer.parseInt(value)-1).setValue(1);
					indicators.get(Integer.parseInt(value)-1).setStyleName("success");	
					listRenamedFiles.add(f);
					
					float progress = Float.parseFloat(Integer.toString(getAllowedFilesUploaded()))/Float.parseFloat(Integer.toString(getInitListFile().size()));
					WizFoldUpload.getSuperIndicator().setValue(progress);
					if(progress>=1) { WizFoldUpload.getSuperIndicator().setVisible(false);  }
				}
			}
			
			@Override
			public void fileUploadError(String filename, int pendingFiles) {
				getWindow().showNotification("Erreur lors de l'envoi", filename);
			}

			@Override
			public void fileUploadComplete(ArrayList<File> fileList) {
				if(transfertAllowed) {
					// On active le bouton Terminer
					btFinish.setEnabled(true);
					WizFoldUpload.getBtRename().setEnabled(true);
					WizFoldUpload.getSuperIndicator().setVisible(false);
					WizFoldUpload.getSuperIndicator().setValue(0);
				}
				// On rend les item de la table sélectionnables
				uploadTable.setSelectable(true);
				// Indique que l'upload est terminé
				status = 1;	
			}

			@Override
			public void onSelectedFiles(int error) {
				System.out.println("Code erreur : "+error);
				System.out.println("Fichiers autorisés : "+getInitListFile().size());
				btFinish = WizFoldUpload.getBtFinish();
				btCancel = WizFoldUpload.getBtCancel();
				
				
				transfertAllowed = ( (error==0 && getQueueSize()<=Integer.parseInt(Global.getParm("UPLOAD_MAX_SIZE"))) || (error==2 && getInitListFile().size()>0)) ? true : false;
				
				// On commence à uploader (0 = indisponible)
				if(transfertAllowed) {
					if(getInitListFile().size()>1) {
						WizFoldUpload.getSuperIndicator().setVisible(true);
					}
					WizFoldUpload.getLblSize().setValue(Utilities.formatSize(getQueueSize())+" / "+Utilities.formatSize(Long.parseLong(Global.getParm("UPLOAD_MAX_SIZE").replace(" ",""))));
					status = 0;
					
					// On cache le label indiquant de sélectionner les fichiers, s'il existe
					if(WizFoldUpload.getSlctFileLbl()!=null) {
						WizFoldUpload.getSlctFileLbl().setVisible(false);
					}
					
					// On supprime le bouton Annuler
					btCancel.setEnabled(false);
					btCancel.setVisible(false);
					
					// On affiche le bouton Terminer mais on ne l'active pas encore
					btFinish.setVisible(true);
					btFinish.setEnabled(false);
					WizFoldUpload.getBtRename().setVisible(true);
					WizFoldUpload.getBtRename().setEnabled(false);
					
					// On cache la zone HTML5 pour l'envoi par glissé-déposé
					setDropZoneVisible(false);
					
					// On affiche la table qui affiche les upload en cours
					uploadTable.setVisible(true);
					uploadTable.setSelectable(false);
					
					
					if(indicators == null) {
						indicators = new LinkedList<ProgressIndicator>();
					}
	
					ArrayList<FileInfo> FileList = getInitListFile();
					for (FileInfo file : FileList) { 
						System.out.println(file.getId()+" : "+file.getName());

						ProgressIndicator pi = createProgressIndicator();
					    indicators.add(pi);
					    
					    int indexNo = (indicators.get(file.getId()-1)==null) ? file.getId() : file.getId()-1;
					    
					    
						Object[] item = new Object[]{
								//file.getId(),
								Utilities.getImgFromExtension(Utilities.getExtension(file.getName())),
								file.getName(), 
								Utilities.formatSize(file.getSize()),
								indicators.get(indexNo),
								Global.i("CAPTION_QUEUED")
							};
						int itemId = file.getId();
						uploadTable.addItem(item, itemId);
					}
				}
				
				// S'il y a une erreur
				switch(error) {
					case 1 : // Si on a essayé d'envoyer la corbeille ou le bureau (Uniquement par le drag&drop)
						mainWindow.showNotification(Global.i("CAPTION_ERROR").toUpperCase(), Global.i("CAPTION_UPLOAD_ERR1"), Notification.TYPE_ERROR_MESSAGE);
					break;
					case 2 : // S'il y a des fichiers dont l'extension n'est pas autorisée
						ArrayList<String> filesNonAutorised = getNonAutorizedFiles();
						if(filesNonAutorized!=null) {
							if(filesNonAutorized.size()>0) {								
								// Construction du message d'erreur
								String message = "";
								for (String file : filesNonAutorized) {
									message += "- "+file+"<br>";
								}
								message += "<br><span style=\"color:#dff;\">"+Global.i("CAPTION_EXTENSION_AUTHORISE")+" : "+getAllowedExtentions().toString()+"</span>";
								mainWindow.showNotification(Global.i("CAPTION_FILE_NON_AUTHORIZED")+" ("+filesNonAutorised.size()+")", message, Notification.TYPE_TRAY_NOTIFICATION, true);
							}
						}
					break;
					case 3 : // Si la taille totale des fichiers sélectionnés dépasse 1 Go (Depuis le bouton Parcourir uniquement)
						mainWindow.showNotification(Global.i("CAPTION_LIMITED_SIZE"),Global.i("CAPTION_UPLOAD_ERR3").replace("%size%", Utilities.formatSize(Long.parseLong(Global.getParm("UPLOAD_MAX_SIZE")))), Notification.TYPE_ERROR_MESSAGE);
						if(uploadTable.size()==0) { // Si on a encore aucun fichier dans le tableau
							global.getWindowFolder().removeAllComponents();
							global.getWindowFolder().addComponent(new WizFoldUpload(folder, slctContacts));
						} else {
							mFileUp = global.getUploadModule();
							global.getUploadModuleLayout().removeComponent(mFileUp);
							uploadTable.setHeight("245px");
						}
					break;
					case 4 : // Si la taille totale des fichiers sélectionnés dépasse 1 Go (DEPUIS LA DROPZONE !)
						mainWindow.showNotification(Global.i("CAPTION_LIMITED_SIZE"),Global.i("CAPTION_UPLOAD_ERR3").replace("%size%", Utilities.formatSize(Long.parseLong(Global.getParm("UPLOAD_MAX_SIZE")))), Notification.TYPE_ERROR_MESSAGE);
					break;
				}
				
				System.out.println(getQueueSize()+" Octets -- "+Utilities.formatSize(getQueueSize()));
				
				// Si la taille des fichiers sélectionnés dépasse la taille totale autorisée
				if( (getQueueSize()>=Integer.parseInt(Global.getParm("UPLOAD_MAX_SIZE"))) && error==0 )  {
					mFileUp = global.getUploadModule();
					global.getUploadModuleLayout().removeComponent(mFileUp);
					WizFoldUpload.getBtRename().click();
					WizFoldUpload.getLblSize().setValue(Global.i("CAPTION_MAXSIZE_AFFECTED"));
				}
				
				
			}
		});
		
	}
	/**
	 * Fonction appelée lorsque l'upload d'un fichier est terminé (equivalent du Listener "fileUploadFinished")
	 * @param file	Informations sur le fichier uploadé
	 * @param fileName Nom du fichier uploadé  
	 * @param mimeType Type MIME du fichier uploadé
	 */
	@Override
	protected void handleFile(File file, String fileName, String mimeType, long length) {
		
	}
	/**
	 * Création du buffer qui stock les données sur le disque
	 */
	@Override
    protected FileBuffer createReceiver() {
        FileBuffer receiver = super.createReceiver();
        // S'assure que le récepteur ne supprime pas les fichiers après qu'ils aient été manipulés par #handleFile()
        receiver.setDeleteFiles(false);
        return receiver;
    }
	/**
	 * @return Obtention de la liste des fichiers tels qu'ils sont enregistrés à la fin de l'upload
	 */
	public ArrayList<FileUp> getRenamedFileList() {
		return listRenamedFiles;
	}
	/**
	 * @return Retourne le statut (0=Occupé, en envoi, 1=Libre, en attente de réception de fichiers)
	 */
	public int getStatus() {
		return status;
	}	

}
