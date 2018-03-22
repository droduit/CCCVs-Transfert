package common.component.upload;


import global.Global;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.easyuploads.DirectoryFileFactory;
import org.vaadin.easyuploads.FileBuffer;
import org.vaadin.easyuploads.FileFactory;
import org.vaadin.easyuploads.MultiUpload;
import org.vaadin.easyuploads.MultiUpload.FileDetail;
import org.vaadin.easyuploads.MultiUploadHandler;
import org.vaadin.easyuploads.UploadField.FieldType;


import com.google.gwt.thirdparty.guava.common.io.Files;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.StreamVariable.StreamingEndEvent;
import com.vaadin.terminal.StreamVariable.StreamingErrorEvent;
import com.vaadin.terminal.StreamVariable.StreamingProgressEvent;
import com.vaadin.terminal.StreamVariable.StreamingStartEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.AbstractWebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator; 
import org.vaadin.easyuploads.UploadField;

import toolbox.Utilities;

import common.component.upload.UploadActionListener;
import static java.nio.file.StandardCopyOption.*;

/**
 * MultipleFileUpload est la class principale qui permet le téléchargement de fichiers multiple.
 * Elle est basée sur la class {@link MultiFileUpload} de Vaadin qui permet le téléchargement immédiat
 * de plusieurs fichiers en parallèles. Elle affiche également les indicateurs de progression
 * de l'envoi des fichiers ainsi qu’une zone de drag&drop pour les navigateurs qui acceptent cette technologie.
 * <br><br>
 * Cette class enregistre les flux directement dans des fichiers pour limiter la consommation
 * de mémoire.
 * <br>
 * Créé des fichiers temporaires par défaut, mais cela peut être modifié avec 
 * {@link #setFileFactory(FileFactory)} (par ex. pour cibler directement le répertoire serveur)
 * TODO Temps restant estimé et taux de transfert
 * 
 */
@SuppressWarnings("serial")
public abstract class MultipleFileUpload extends CssLayout implements DropHandler {

    private CssLayout progressBars = new CssLayout();
    /** Contient les composants du plugin **/
    private CssLayout uploads = new CssLayout();
    /** Texte par défaut du bouton "Parcourir..." **/
    private String uploadButtonCaption = "Parcourir...";
    /** Texte de la zone drag&drop */
    private String areaText = "<small>"+Global.i("CAPTION_DRAG_DROP")+"</small>";
    /** Liste contenant les fichiers envoyés et leurs infos */
    private ArrayList<File> fileList = new ArrayList<File>();
    /** Liste des fichiers juste après la sélection **/
    private ArrayList<FileInfo> initFileList = new ArrayList<FileInfo>();
    /** ID du fichier en cours d'envoi **/
    private int currentFileId = 0;
    /** ID du fichier envoyé **/
    private int idFile = 0;
    /** nombre d'upload effectués */
    private long transfertNo = 0;
    /** Taile total de tous les fichiers sélectionnés */
    private long allFilesSize = 0;
    /** Nombre de fichiers en attente à l'initialisation */
    private int initPendingFilesNo = 0;
    /** Nombre de fichiers en attente */
    private int pendingFilesNo = 0;
    /** Indique si l'upload d'un fichier est en cours */
    private boolean isInProcess = false;
    /** Nombre de fichiers autorisés qui ont étés envoyés **/
    private int authorizedFilesUploadedNo = 0;
    
    /** Taille maximal de la liste d'attente (tous les fichiers), défault : 1 Go */
    private int maxQueueSize;
    
    /** Contient la progression du fichier en cours */
    private float progression = 0;
    /** Contient la liste des extensions autorisées */
    private ArrayList<String> allowedExtensions = new ArrayList<String>();
 	/** Stock le nom des fichiers non autorisés */
	ArrayList<String> filesNonAutorized = new ArrayList<String>();
	
    
    
    /** Tous les {@link UploadActionListener} enregistrés */
    private List<UploadActionListener> uploadListeners = new Vector<UploadActionListener>();

    public MultipleFileUpload() {
    	maxQueueSize = Integer.parseInt(Global.getParm("UPLOAD_MAX_SIZE").replace(" ", ""));
    	System.out.println(maxQueueSize);
    	allowedExtensions.add("*");
    	
        addComponent(progressBars);
        uploads.setStyleName("v-multifileupload-uploads");
        addComponent(uploads);
        prepareUpload();
    }

    /** Ajoute l'action spécifiée {@link UploadActionListener}.
     *  Tous les {@link UploadActionListener} enregistrés seront informés des actions de téléchargement des fichiers.
     *  Il faut supprimer le Listener à la fin de l'utilisation pour prévenir les fuites de mémoire<p />
     *  Si le Listener est déjà enregistré, rien ne se passera.
     * @param l Le Listener à ajouter
     */
    public void addUploadActionListener(UploadActionListener l) {
      if (!uploadListeners.contains(l)) uploadListeners.add(l);
    }
    
    /** Supprime l'action {@link UploadActionListener} spécifiée.
     *  L' {@link UploadActionListener} ne sera plus informé sur les actions de téléchargement des fichiers
     *  If the listener is not registered nothing will be do. 
     * @param l the listener to remove
     */
    public void removeUploadActionListener(UploadActionListener l) {
      uploadListeners.remove(l);
    }
    
    /** Evenement déclenché lorsque les fichiers sont sélectionnés 
     * @param error **/
    private void notifyOnSelectedFiles(int error) {
    	 for (UploadActionListener l : uploadListeners) {
    		 l.onSelectedFiles(error);
    	 }
    }
    
    /** Notifie tous les {@link UploadActionListener} du démarrage du processus d'envoi d'un fichier **/
    private void notifyUploadStart(int idFile, String filename, int pendingFiles) {
      for (UploadActionListener l : uploadListeners) {
        l.fileUploadStarted(idFile, filename, pendingFiles);
        
        if(initPendingFilesNo-pendingFiles==1) {
        	System.out.println("\n#========================================================#\n" +
        						"-------- Démarrage d'une session de transfère ("+transfertNo+") -------\n" +
        						"-------- Taille total du transfert : "+Utilities.formatSize(allFilesSize)+"      -------\n" +
        						"-------- Extensions autorisées : "+allowedExtensions.toString()+" -------\n");
        }
        
        System.out.println("#----------------- Upload ("+(initPendingFilesNo-pendingFiles)+"/"+initPendingFilesNo+") ------------------#\n- Fichier : "+filename);
      }
    }
    
    /** Notifie tous les {@link UploadActionListener} de la fin du processus d'envoi d'un fichier **/
    private void notifyUploadFinished(int idFile, String filename, File file, int pendingFiles) {
      for (UploadActionListener l : uploadListeners) {
        l.fileUploadFinished(idFile, filename, file, pendingFiles);
      }
      
      System.out.println("- Taille : "+Utilities.formatSize(file.length())+"\n" +
			   "- Fichiers restants : "+pendingFiles+"/"+initPendingFilesNo+"\n" +
			   "#-------------------------------------------------#\n");
      if(pendingFiles==0) { System.out.println("#========================================================#\n"); }
    }
    
    /** Notifie tous les {@link UploadActionListener} d'une erreur d'envoi */
    private void notifyUploadError(String filename, int pendingFiles) {
      for (UploadActionListener l : uploadListeners) {
        l.fileUploadError(filename, pendingFiles);
        System.out.println("#----------------- ERREUR ------------------#\n- "+filename+"\n- Fichiers restants : "+pendingFiles+"\n");
      }
    }
    
    /** Evenement déclenché chaque fois que la progression de l'envoi d'un fichier augmente **/
    private void notifyUploadProgess(int idFile, String filename, int pendingFiles, float progress) {
    	  for (UploadActionListener l : uploadListeners) {
    	        l.fileUploadProgress(idFile, filename, pendingFiles, progress);
    	  }
    }
    
    /** Evenement déclenché lorsque le transfert de tous les fichiers de la file d'attente sont envoyés **/
    private void notifyUploadComplete(ArrayList<File> fileList) {
        for (UploadActionListener l : uploadListeners) {
          l.fileUploadComplete(fileList);
        }
		initPendingFilesNo = 0;
      }
    
    private MultiUpload upload;
    /**
     * Initialisation du plugin pour l'upload des fichiers (Sélection par bouton Parcourir...)
     */
    private void prepareUpload() {
        final FileBuffer receiver = createReceiver();

        upload = new MultiUpload();
        MultiUploadHandler handler = new MultiUploadHandler() {
            private LinkedList<ProgressIndicator> indicators;

            /**
             * Exécuté losrque l'envoi d'un fichier démarre
             */
            public void streamingStarted(StreamingStartEvent event) {
	            isInProcess = true;
	            pendingFilesNo--;
	            
	            if(!filesNonAutorized.contains(event.getFileName())) {
		            currentFileId++; 
		            notifyUploadStart(currentFileId, event.getFileName(), pendingFilesNo);
	            }
            }
            /**
             * Exécuté lorsque l'envoi d'un fichier se termine
             */
            public void streamingFinished(StreamingEndEvent event) {
               File file = receiver.getFile();
                
               String originalFileName = file.getName();
               File newFile = null;
                // Renommage du fichier
                if(file.exists()) {
                	file.setWritable(true);
                	file.setReadable(true);
                	file.setExecutable(true);

                	String newName = Global.UPLOAD_DIR+Utilities.getNewFileName(originalFileName);
                	//getWindow().showNotification("Enregistré sous", newName);
                	newFile = new File(newName);
                	
                	file.renameTo(newFile);
                }
                fileList.add(newFile);
                
                handleFile(newFile, newFile.getName(), event.getMimeType(),
                        event.getBytesReceived());
                receiver.setValue(null);
                
                isInProcess = false; 
                
                // Suppression des fichiers non-autorisés
                if(filesNonAutorized.contains(originalFileName)) {
        		   newFile.delete(); 
                } else {
                	// Incrémentation du nombre de fichier autorisé envoyé
                	authorizedFilesUploadedNo++; 
                }
            	   
                notifyUploadFinished(currentFileId, event.getFileName(), newFile, pendingFilesNo);
                
                
                if(authorizedFilesUploadedNo==(initPendingFilesNo-filesNonAutorized.size()) || pendingFilesNo==0) {
                	notifyUploadComplete(fileList);
                	setEnabledUploadDropZone(true);
                	setEnableUploadButton(true);
                }
            }
            /**
             * Exécuté lorsque l'envoi d'un fichier échoue
             */
            public void streamingFailed(StreamingErrorEvent event) {
                Logger.getLogger(getClass().getName()).log(Level.FINE,
                        "L'envoi à échoué", event.getException());

                
                isInProcess = false;
                
                notifyUploadError(event.getFileName(), pendingFilesNo);
            }
            /**
             * Exécuté lors de la progression de l'envoi du fichier
             */
            public void onProgress(StreamingProgressEvent event) {
            	long readBytes = event.getBytesReceived();
                long contentLength = event.getContentLength();
                float f = (float) readBytes / (float) contentLength;
                
                progression = (f*100);
                notifyUploadProgess(currentFileId, event.getFileName(), pendingFilesNo, progression);
                
                isInProcess = true;
            }

            public OutputStream getOutputStream() {
                FileDetail next = upload.getPendingFileNames().iterator().next();
                return receiver.receiveUpload(next.getFileName(), next.getMimeType());
            }
            /**
             * Exécuté lorsque les fichiers à envoyer sont sélectionnés
             */
            public void filesQueued(Collection<FileDetail> pendingFileNames) {
            	filesNonAutorized.clear();
            	pendingFilesNo = (pendingFileNames == null) ? 0 : pendingFileNames.size();
            	
            	authorizedFilesUploadedNo = 0;
				transfertNo++;
				initPendingFilesNo = pendingFilesNo;
				//allFilesSize = 0;
				fileList.clear();
				initFileList.clear();
				  

				int error = 0;
				int allowedFileNo = 0;
                
                // Passe en revue un fichier après l'autre
                for (FileDetail f : pendingFileNames) {
                	// Si l'extension n'est pas autorisée
                    if(!allowedExtensions.contains(Utilities.getExtension(f.getFileName()).toLowerCase()) && !allowedExtensions.contains("*")) {
                    	filesNonAutorized.add(f.getFileName());
                    	error = 2;

                    } else {
                    	// Crée une barre de progression pour chaque fichier
	                    idFile++;
	                    allowedFileNo++;
	                    
	                    initFileList.add(new FileInfo(currentFileId+allowedFileNo, f.getFileName(), f.getContentLength(), f.getMimeType()));
	                    allFilesSize += f.getContentLength();
                    }
                    System.out.println(
                    		"filename : "+f.getFileName()+";" +
                    		"idFile : "+idFile+";" +
                    		"allowedFileNo : "+allowedFileNo+";" +
                    		"currentFileId : "+currentFileId+";" +
                    		"assigné : "+(currentFileId+allowedFileNo)
                    );
                    
	               
                }
                
               
                
                // Si la taille max est plus grande que la taille max autorisée, on envoie une erreur
                if(allFilesSize>maxQueueSize) {
                	 error = 3;
                	 idFile = 0;
                     allowedFileNo = 0;
                	 for (FileDetail f : pendingFileNames) {
                		 filesNonAutorized.add(f.getFileName());
                	 }
                } else {
                	 // Désactivation de la zone Drag&Drop
    				if(initFileList.size()>0) {
    					setEnabledUploadDropZone(false);
    					setEnableUploadButton(false);
    				}
                }
                
                System.out.println("---- initFileList -------");
                for(FileInfo initFl : initFileList) {
                	System.out.println(initFl.getName());
                }
                
                notifyOnSelectedFiles(error);
            }
        };
        upload.setHandler(handler);
        upload.setButtonCaption(getUploadButtonCaption());
        uploads.addComponent(upload);

    }
    /**
     * Création d'une barre de progression
     * @return (ProgressIndicator) Barre de progression
     */
    public ProgressIndicator createProgressIndicator() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPollingInterval(300);
        progressIndicator.setValue(0);
        return progressIndicator;
    }
    /**
     * Active/Désactive la zone de Drag/Drop
     * @param enable (true) Activé (false) Désactivé
     */
    public void setEnabledUploadDropZone(boolean enable) {
    	if(supportsFileDrops()) dropZone.setEnabled(enable);
    }
    /**
     * Active/Désactive le bouton pour choisir les fichiers
     * @param enable(true) Activé (false) Désactivé
     */
    public void setEnableUploadButton(boolean enable) {
    	upload.setEnabled(enable);
    }
    /**
     * Fixe les extensions de fichiers qui peuvent être envoyés
     * @param extentions Extensions de fichiers séparés par des virgules (exe,jpg,txt) ou * pour tous les fichiers
     */
    public void setAllowedExtentions(String extentions) {
		String[] ext = extentions.split(",");
		allowedExtensions.clear();
		for(String in : ext) {
			allowedExtensions.add(in.trim());
		}
    }
    /**
     * Retourne la liste des fichiers sélectionnés au départ
     * @return Liste des fichiers sélectionnés
     */
    public ArrayList<FileInfo> getInitListFile() {
    	return initFileList;
    }
    /**
     * Retourne la liste des extensions autorisées
     * @return Extensions des fichiers autorisés
     */
    public ArrayList<String> getAllowedExtentions() {
    	return allowedExtensions;
    }
    /**
     * Retourne la liste des fichiers non-autorisés
     * @return Liste des fichiers non-autorisés
     */
    public ArrayList<String> getNonAutorizedFiles() {
    	return filesNonAutorized;
    }
    /**
     * @return Nombre de fichié autorisés envoyés
     */
    public int getAllowedFilesUploaded() {
    	return authorizedFilesUploadedNo;
    }
   /**
    * Fixe la taille limite pour le total des fichiers
    * @param SizeLimit Taille maximum en octet
    */
    public void setMaxQueueSize(int SizeLimit) {
    	maxQueueSize = SizeLimit;
    }
    /**
     * Retourne la taille des fichiers envoyés
     * @return Taille des fichiers déjà sélectionnés
     */
    public long getQueueSize() {
    	return allFilesSize;
    }
    /**
     * Soustrait une valeur à la taille de la liste des fichiers envoyés
     * @param sizeToDecrease La valeur à soustraire
     */
    public void decreaseQueueSize(long sizeToDecrease) {
    	this.allFilesSize = allFilesSize-sizeToDecrease;
    	System.out.println("Réduction de "+Utilities.formatSize(allFilesSize));
    	System.out.println("Taille de la liste : "+allFilesSize+" ("+Utilities.formatSize(allFilesSize)+")");
    }
    /**
     * Retourne le texte du bouton qui permet de sélectionner les fichiers
     * @return Texte du bouton Parcourir...
     */
    public String getUploadButtonCaption() {
        return uploadButtonCaption;
    }
    /**
     * Fixe le texte du bouton Parcourir...
     * @param uploadButtonCaption Texte du bouton
     */
    public void setUploadButtonCaption(String uploadButtonCaption) {
        this.uploadButtonCaption = uploadButtonCaption;
        Iterator<Component> componentIterator = uploads.getComponentIterator();
        while (componentIterator.hasNext()) {
            Component next = componentIterator.next();
            if (next instanceof MultiUpload) {
                MultiUpload upload = (MultiUpload) next;
                if (upload.isVisible()) {
                    upload.setButtonCaption(getUploadButtonCaption());
                }
            }
        }
    }
    

    private FileFactory fileFactory;
    
    public FileFactory getFileFactory() {
        if (fileFactory == null) {
            fileFactory = new TempFileFactory2();
        }
        return fileFactory;
    }
    
    public void setFileFactory(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
    }
    
    protected FileBuffer createReceiver() {
        FileBuffer receiver = new FileBuffer(FieldType.FILE) {
            @Override
            public FileFactory getFileFactory() {
                return MultipleFileUpload.this.getFileFactory();
            }
        };
        return receiver;
    }

    /**
     * Renvoi l'interval de mise à jour de la barre de progression
     * @return Interval de mise à jour de la ProgressBar
     */
    protected int getPollinInterval() {
        return 500;
    }
    /**
     * Retourne le nombre de fichiers en attente
     * @return Nombre de fichiers en attente de téléchargement
     */
    protected int getPendingFiles() {
    	return pendingFilesNo;
    }

    @Override
    public void attach() {
        super.attach();
        if (supportsFileDrops()) {
            prepareDropZone();
        }
    }
    
    //------------------------------------------------------------------------------------
	//------------------------------- HTML5 Drag&Drop ------------------------------------
    //------------------------------------------------------------------------------------
    
    private DragAndDropWrapper dropZone;
    
    /** Indique si la dropZone doit être visible ou non */
    private boolean dropZoneVisible = true;

    /** Retourne la visibilité de la dropZone.
     * @return true : dropZone visible, false : dropZone masquée.
     */
    public boolean isDropZoneVisible() {
      return dropZoneVisible;
    }

    /** Définit la visibilité de la dropZone
     * @param Définit l'attribut dropZoneVisible à la valeur spécifiée
     */
    public void setDropZoneVisible(boolean dropZoneVisible) {
    	if(supportsFileDrops()) {
	      this.dropZoneVisible = dropZoneVisible;
	      
	      if(!dropZoneVisible)
	    	  dropZone.setStyleName("v-multifileupload-dropzone-hide");
	      else 
	    	  dropZone.removeStyleName("v-multifileupload-dropzone-hide");
    	}
    }

    /** Indique si un envoi est en cours ou s'il y a encore des fichiers en attentes
     * @return true si un fichier est encours de téléchargement ou s'il y a encore des fichiers en attente
     */
    public boolean isInProcess() {
      return (isInProcess || pendingFilesNo > 0);
    }

    /**
     * Règle le DragAndDropWrapper pour accepter les dépôts de fichiers multiples dans la fenêtre.
     */
    private void prepareDropZone() {
        if (dropZone == null && isDropZoneVisible()) {
        	Component label = new Label(getAreaText(), Label.CONTENT_XHTML);
            label.setSizeUndefined();
            
            dropZone = new DragAndDropWrapper(label);
            dropZone.setStyleName("v-multifileupload-dropzone");
            dropZone.setSizeFull();
            addComponent(dropZone, 2);
            dropZone.setDropHandler(this);
            addStyleName("no-horizontal-drag-hints");
            addStyleName("no-vertical-drag-hints");

        }
    }

    /** Retourne le texte de la zone de drag&drop
     * @return Texte de la zone de drag&drop
     */
    public String getAreaText() {
        return areaText;
    }

    /** Définit le texte de la zone de drag&drop
     * @param areaText Texte de la zone de drag&drop. Peut contenir du code HTML
     */
    public void setAreaText(String areaText) {
        this.areaText = areaText;
    }
    
    /**
     * Indique si le drag&drop est supporté par le navigateur ou non.<br><br>
     * Les navigateurs qui prennent en charge cette fonctionnalité sont :<br><ul>
     * <li>Chrome</li><li>Firefox</li><li>Safari</li></ul>
     * @return true : le drag&drop est activé.
     */
    protected boolean supportsFileDrops() {
        AbstractWebApplicationContext context = (AbstractWebApplicationContext) getApplication()
                .getContext();
        WebBrowser browser = context.getBrowser();
        if (browser.isChrome()) {
            return true;
        } else if (browser.isFirefox()) {
            return true;
        } else if (browser.isSafari()) {
            return true;
        }
        return false;
    }
    /**
     * Méthode appelée obligatoirement à la fin de chaque envoi de fichier
     * @param file Informations sur le fichier envoyé
     * @param fileName Nom du fichier envoyé
     * @param mimeType Type MIME du fichier envoyé
     * @param length Taille du fichier envoyé
     */
    abstract protected void handleFile(File file, String fileName,
            String mimeType, long length);

    /**
     * Une méthode d'assistance pour définir DirectoryFileFactory avec le chemin du répertoire spécifié
     * @param directoryWhereToUpload Répertoire dans lequel envoyer les fichiers
     */
    public void setRootDirectory(String directoryWhereToUpload) {
        setFileFactory(new DirectoryFileFactory(
                new File(directoryWhereToUpload)));
    }

    /**
     * Retourne les critères d'acceptation d'un fichier pour l'envoi
     * Ne pas utiliser cette méthode pour le moment, elle n'est pas encore implémentée
     */
    public AcceptCriterion getAcceptCriterion() {
        return AcceptAll.get();
    }
    /**
     * Méthode exécutée lorsqu'un fichier est déposé dans la zone prévue à cet effet
     */
    public void drop(DragAndDropEvent event) {
        DragAndDropWrapper.WrapperTransferable transferable = (WrapperTransferable) event
                .getTransferable();
        Html5File[] files = transferable.getFiles();
        
        filesNonAutorized.clear();
        pendingFilesNo = (files==null) ? 0 : files.length;
        
        int error = 0;
        
        if(files!=null) {
        	authorizedFilesUploadedNo = 0;
	        transfertNo++;
	        initPendingFilesNo = pendingFilesNo;
	        allFilesSize = 0;
	        fileList.clear();
	        initFileList.clear();

	        // Calcul de la taille total des fichiers
	        for(final Html5File html5File : files) {
	        	allFilesSize += html5File.getFileSize();
	        }
	        
	        // Si la taille total des fichiers dépasse la taille limite, on ne laisse pas envoyer
	        if(allFilesSize<=maxQueueSize) {
	        	setEnableUploadButton(false);
	        	
		        int allowedFileNo=0;
		        for(final Html5File html5File : files) {
		        	// Si l'extension n'est pas autorisée
		        	if(!allowedExtensions.contains(Utilities.getExtension(html5File.getFileName()).toLowerCase()) && !allowedExtensions.contains("*")) {
		        		// On décrémente le nombre de fichiers dans la liste d'attente pour chaque fichier non autorisé
		        		initPendingFilesNo--; pendingFilesNo--;
		        		// On crée un tableau contenant les fichiers non autorisés
		                filesNonAutorized.add(html5File.getFileName()); 
		                
		                error = 2;
		        	} else {
		        		idFile++;
		        		allowedFileNo++;
			        
			            initFileList.add(new FileInfo(currentFileId+allowedFileNo, html5File.getFileName(), html5File.getFileSize(), html5File.getType()));
			            final FileBuffer receiver = createReceiver();
			            html5File.setStreamVariable(new StreamVariable() {
			
			                private String name;
			                private String mime;
			
			                public OutputStream getOutputStream() {
			                    return receiver.receiveUpload(name, mime);
			                }
			
			                public boolean listenProgress() {
			                    return true;
			                }
			
			                public void onProgress(StreamingProgressEvent event) {
			                    float p = (float) event.getBytesReceived()
			                            / (float) event.getContentLength();
			                  
			                    progression = p*100;
			                    notifyUploadProgess(currentFileId, event.getFileName(), pendingFilesNo, progression);
			                }
			
			                public void streamingStarted(StreamingStartEvent event) {
			                    name = event.getFileName();
			                    mime = event.getMimeType();
			                    
			                    pendingFilesNo--;
			                    currentFileId++;
			                    
			                    notifyUploadStart(currentFileId, event.getFileName(), pendingFilesNo);
			                }
			
			                public void streamingFinished(StreamingEndEvent event) {
			                	authorizedFilesUploadedNo++;
			                	
			                	String originalFileName = event.getFileName();
			                	File originalFile = new File(Global.UPLOAD_DIR+originalFileName);
			                	
			                	 File newFile = null;
			                     // Renommage du fichier
			                     if(originalFile.exists()) {
			                    	originalFile.setWritable(true);
			                    	originalFile.setReadable(true);
			                    	originalFile.setExecutable(true);

			                     	String newName = Global.UPLOAD_DIR+Utilities.getNewFileName(originalFileName);
			                     	//getWindow().showNotification("Enregistré sous", newName);
			                     	newFile = new File(newName);
			                     	
			                     	originalFile.renameTo(newFile);
			                     }
			                	
			                    fileList.add(newFile);
			                    notifyUploadFinished(currentFileId, event.getFileName(), newFile, pendingFilesNo);
			                    
			                    if(pendingFilesNo==0) {
			                    	notifyUploadComplete(fileList);
			                    	setEnableUploadButton(true);
			                    }
			                    
			                    handleFile(newFile, newFile.getName(),
			                            html5File.getType(), html5File.getFileSize());
			                   
			                    receiver.setValue(null);
			
			                }
			
			                public void streamingFailed(StreamingErrorEvent event) {
			                   // progressBars.removeComponent(pi);
			                    notifyUploadError(event.getFileName(), pendingFilesNo);
			                }
			
			                public boolean isInterrupted() {
			                    return false;
			                }
			            });
		        	}
		        	
		        }
		        
		        // Si l'utilisateur n'a ajouté que des fichiers qui ne sont pas autorisés, on réactive l'upload
		        if(filesNonAutorized.size()>0 && initPendingFilesNo==0) {
		        	setEnableUploadButton(true);
		        }
	        } else {
	        	error = 4;
	        }
        } else {
        	error = 1;
        }
        
        System.out.println(allFilesSize+" Octets -- "+Utilities.formatSize(allFilesSize));
        
		// Exécuté lorsque les fichiers sont ajoutés dans la zone
		notifyOnSelectedFiles(error);
    }
    
}
