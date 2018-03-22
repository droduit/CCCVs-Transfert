package main;

import global.Global;
import global.UserSession;

import java.sql.ResultSet;
import java.sql.SQLException;


import sql.query.tbl_journal_con;
import sql.query.tbl_users;
import toolbox.Mailer;
import toolbox.SHA256;
import toolbox.Sql;
import toolbox.Utilities;

import com.google.gwt.user.client.Timer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

/**
 * <b>IMPORTANT !<b><br><br>Construction du composant de connexion à l'application.<br><br>
 * Ce composant personnalisé est un layout qui contient le composant de connexion à l'interface utilisateur.
 * La class est instanciée depuis {@link CccvsTransfert}.
 * @author Dominique Roduit
 * @version 1.0.0
 * @since 2013-03-01
 *
 */
public class ConnexionLayout extends CustomComponent {
	/** Hauteur du header **/
	private final String HEADER_HEIGHT = Global.getParm("LAYOUT_CONNEXION_HEADER_HEIGHT");
	/** Layout principal pleine page qui contient les trois niveaux de bases (top, middle, bottom) **/
	private AbsoluteLayout mainLayout;
	
	// Zone du haut -------------------------------------------------------------------
	/** Layout général du haut **/
	private HorizontalLayout topLayout;
	
	// Zone du milieu qui contient le corps de page -------------------------
	/** Layout général du milieu **/
	private HorizontalLayout middleLayout;
		/** Logo de la fenêtre flottante **/
		private VerticalLayout logo;
		/** Layout contenant le formulaire de connexion **/
		private VerticalLayout velConnexion;
			/** Bouton de connexion/inscription.<br>Selon le statut de l'utilisateur (enregistré ou non) le bouton change **/
			private Button btnConn;
			/** Champs pour le mot de passe **/
			private PasswordField txtPass;
			/** Champs pour l'identifiant (e-mail) **/
			private TextField txtIdentifiant;
		/** Layout conteneur du logo **/
		private VerticalLayout containerLogo;
	
	/** Fenêtre flottante de connexion **/
	private Window winConnexion;
	
	/** Stocke si l'utilisateur existe déjà dans la base de données ou non **/
	private boolean existingUser = false;
	/** Stocke l'état de connexion (Connexion ok ou non) **/
	private Boolean connexionState = false;
	
	/**
	 * Construction des 3 niveaux de bases (top, middle, bottom)
	 */
	public ConnexionLayout() {
		super.setSizeFull();
		
		// Absolute Layout qui va contenir tout les autres layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setSizeFull();
		mainLayout.setStyleName("connexion");
		
		// Grid pleine page qui permet de placer les 3 premiers niveau de Layout Verticalement
		GridLayout vMain = new GridLayout(1, 3);
		vMain.setSizeFull();
		vMain.setRowExpandRatio(1, 100);
		mainLayout.addComponent(vMain);
				
		// Header
		topLayout = new HorizontalLayout();
		topLayout.setHeight(HEADER_HEIGHT);
		topLayout.setWidth("100%");
		topLayout.setStyleName("v-header");
		vMain.addComponent(topLayout, 0, 0);

	
		// Body
		middleLayout = new HorizontalLayout();
		middleLayout.setSizeFull();
		middleLayout.setStyleName("v-body");
		vMain.addComponent(middleLayout, 0, 1);
		
		setCompositionRoot(mainLayout);
	}
	/**
	 * Retourne la fenêtre flottante contenant le contenu
	 * @return Fenêtre flottante
	 */
	public Window getWindowConnexion() {
		// Création de la fenetre flottante
    	winConnexion = new Window(Global.getParm("APP_NAME")+" "+Global.getParm("APP_VERSION"));
    	
		//connexionWindow.setModal(true);
    	winConnexion.setStyleName(Reindeer.LAYOUT_BLACK);
		
		// Taille de la fenêtre de connexion
    	winConnexion.setHeight("415px");
    	winConnexion.setWidth("420px");
		
		// Positionnement de la fenetre flottante
    	winConnexion.center();
		
		// Défense de fermer et de redimensionner la fenêtre flottante
    	winConnexion.setClosable(false);
    	winConnexion.setResizable(false);
    	
    	containerLogo = new VerticalLayout();
		containerLogo.setSizeFull();
		containerLogo.setStyleName("containerLogo");
		winConnexion.addComponent(containerLogo);
		
		logo = new VerticalLayout();
		logo.setStyleName("v-logo");
		logo.setWidth("201px");
		logo.setHeight("184px");
		containerLogo.addComponent(logo);
		containerLogo.setComponentAlignment(logo, Alignment.TOP_CENTER);
    	
    	VerticalLayout loginForm = getLoginForm();
    	winConnexion.addComponent(loginForm);
    	
    	return winConnexion;
	}
	/**
	 * Construction du formulaire de connexion à l'interface. Ce layout est intégré à la fenêtre flottante
	 * @return Layout contenant le formulaire de connexion
	 */
	private VerticalLayout getLoginForm() {
		// VerticalLayout qui va contenir le TextField et le bouton
		velConnexion = new VerticalLayout();
		velConnexion.setSizeFull();
		velConnexion.setMargin(true);
		velConnexion.setSpacing(true);
		velConnexion.setStyleName("velConnexion");
		
		// TextField pour l'adresse mail
		txtIdentifiant = new TextField();
		txtIdentifiant.setCaption(Global.i("CAPTION_ADRESSE_EMAIL"));
		txtIdentifiant.setImmediate(true);
		txtIdentifiant.setWidth("230px");
		txtIdentifiant.setRequired(true);
		txtIdentifiant.setInputPrompt(Global.i("CAPTION_TYPE_YOUR_MAIL"));
		txtIdentifiant.setStyleName("align-center");
		txtIdentifiant.addValidator(new EmailValidator(Global.i("CAPTION_EMAIL_VALIDATOR")));
		txtIdentifiant.setValue("dominique.roduit@avs.vs.ch");
		txtIdentifiant.addListener(new FieldEvents.BlurListener() {
			@Override
			public void blur(BlurEvent event) {
				if(txtIdentifiant!=null && txtIdentifiant.isValid()) {
					String email = txtIdentifiant.getValue().toString();
	
					// On vérifie si l'utilisateur existe déjà
					if(email!=null) {
						ResultSet user = Sql.query(tbl_users.getUserByEmail(email));
						if(user!=null) {
							existingUser = false;
							try {
								// L'utilisateur existe dans la base de données
								if(user.first()) {
									existingUser = true;
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						
							// Si l'utilisateur n'existe pas, on change le bouton par "Inscription"
							if(!existingUser) {
								btnConn.setCaption(Global.i("CAPTION_INSCRIPTION"));
							} else {
								btnConn.setCaption(Global.i("CAPTION_CONNEXION"));
							}
							btnConn.setVisible(true);
						}
					}
				} else {
					btnConn.setVisible(false);
				}
			}
		});
		
		velConnexion.addComponent(txtIdentifiant);
		velConnexion.setComponentAlignment(txtIdentifiant, Alignment.BOTTOM_CENTER);
		
		txtPass = new PasswordField();
		txtPass.setCaption(Global.i("CAPTION_PASSWORD"));
		txtPass.setWidth("230px");
		txtPass.setRequired(true);
		txtPass.setStyleName("align-center");
		txtPass.addValidator(new StringLengthValidator(Global.i("CAPTION_PASS_VALIDATOR"), Integer.parseInt(Global.getParm("PASS_MIN_LENGTH")), Integer.parseInt(Global.getParm("PASS_MAX_LENGTH")), false));
		txtPass.setRequiredError(Global.i("CAPTION_REQUIRE_ERROR_PASS"));
		velConnexion.addComponent(txtPass);
		velConnexion.setComponentAlignment(txtPass, Alignment.BOTTOM_CENTER);
		
		// Bouton de connexion pour la validation de l'adresse mail
		btnConn = new Button();
		btnConn.setCaption(Global.i("CAPTION_CONNEXION"));
		btnConn.setImmediate(true);
		btnConn.setVisible(false);
		velConnexion.addComponent(btnConn);
		velConnexion.setComponentAlignment(btnConn, Alignment.BOTTOM_CENTER);
		
		txtIdentifiant.focus();
		// Appel de l'évenement onclick sur le clique de la touche [ENTER]
		btnConn.setClickShortcut(KeyCode.ENTER);
		btnConn.addListener(connexionListener);
		
		return velConnexion;
	}
	/**
	 * Retourne le layout racine du composant
	 * @return Layout racine (CompositionRoot)
	 */
	public HorizontalLayout getMainLayout() {
		return middleLayout;
	}
	/**
	 * Retourne le bouton de connexion
	 * @return Bouton de connexion du formulaire
	 */
	public Button getButtonConnexion() {
		return btnConn;
	}
	/**
	 * Retourne le textField d'identification
	 * @return TextField d'identification
	 */
	public TextField getTextFieldID() {
		return txtIdentifiant;
	}
	/**
	 * Action exécuté sur le clique du bouton de connexion/inscription ou à la validation du formulaire par le clavier
	 */
	private ClickListener connexionListener = new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			System.out.println(Utilities.getCurrentDate() + " -- [Event] "+event.getButton().getCaption());

			String email = txtIdentifiant.getValue().toString();
			String pass = txtPass.getValue().toString();
			String passCrypted = SHA256.getHashValue(pass);
			boolean isInternal = Utilities.isInternal(email);
			String action = "";
			String message = email;
			String messDisplay = "";
			String notifText = "";
			Notification notif = null;
			
			message += (isInternal) ? " (interne) " : " (externe) ";
			
			// Si l'adresse et le mot de passe sont valides
			if(txtIdentifiant.isValid() && txtPass.isValid()) {
				Mailer.EmailValidator emailValidator = new Mailer.EmailValidator();
				
				// Si l'adresse e-mail entrée est une adresse avec un format valide
				if(txtIdentifiant.isValid()) { 
					// On vérifie si l'utilisateur existe déjà
					ResultSet user = Sql.query(tbl_users.getUserByEmail(email));
					existingUser = false;
					
					try {
						if(user.first()) {
							// L'utilisateur existe dans la base de données
							existingUser = user.first();
							
							// Si le mot de passe correspond à cet adresse e-mail
							if(passCrypted.equals(user.getString("user_pass"))) {
								if(user.getBoolean("user_validation")) { // Si l'adresse est déjà validée
									action = "con_success";
									message += " a accès, il a entré un bon mot de passe et son adresse est validée, on le connecte";
									
									// Création de la session
									UserSession.setID(user.getInt("PKNoUser"));
									UserSession.setInternal(user.getBoolean("user_internal"));
									UserSession.setMail(user.getString("user_mail"));
									UserSession.setLangue(user.getString("user_langue"));
									UserSession.setAdmin(user.getBoolean("user_admin"));
									
									System.out.println("ID utilisateur = "+UserSession.getID()+", "+UserSession.getMail());
									
									setConnexionState(true);
								} else { // L'adresse n'est pas encore validée

									action = "con_no_validate";
									message += " a entré un bon mot de passe mais n'a pas encore validé son adresse, on lui renvoie un mail avec le lien sécurisé";
									// Renvoi du mail contenant le lien sécurisé
									Mailer.sendAccountValidationMail(email);
									
									messDisplay += (!Utilities.getInternalName(email).equals("")) ? "<b>"+Global.i("CAPTION_HELLO")+", "+Utilities.getInternalName(email)+"</b><br>" : "";
									messDisplay += Global.i("CAPTION_EMAIL_REVALIDATION_SENT").replace("%email%", email);
									
									// Affichage comme quoi le mail a été ré-envoyé
									changeLogoArea(messDisplay, Global.PATH_THEME_RESSOURCES+"signUpMailSend.png");
										
									int nbEssai = tbl_journal_con.getNumberOfConnexionsWhithoutValidation(email);
									if(nbEssai>5)
										notifText = Global.i("CAPTION_TRY_NUMBER")+nbEssai+". "+Global.i("CAPTION_IF_ERROR_OCCURED_MAIL_ME");
									
								}
							} else {
								action = "con_wrong_pass";
								message += " existe mais a entré un mauvais mot de passe.";
								notifText = Global.i("CAPTION_PASSWORD_ERROR");
							}
							
						} else {
							// L'utilisateur n'existe pas dans la base de données, on lui envoye un mail avec un lien de validation
							action = "con_new_user";
							message += " n'existe pas encore dans la base de données, on lui envoie un mail avec un lien sécurisé";
							
							messDisplay += (!Utilities.getInternalName(email).equals("")) ? "<b>"+Global.i("CAPTION_WELCOME")+", "+Utilities.getInternalName(email)+"</b><br>" : "";
							messDisplay += Global.i("CAPTION_EMAIL_VALIDATION_SENT").replace("%email%", email);
							
							// Inscription dans la base de données
							Sql.exec(tbl_users.getInsertQuery(email, passCrypted, isInternal));
							
							// Envoi d'un mail contenant un lien de validation
							Mailer.sendAccountValidationMail(email);
							
							// Affichage comme quoi le mail a été envoyé
							changeLogoArea(messDisplay, Global.PATH_THEME_RESSOURCES+"signUpMailSend.png");
						}
					} catch (SQLException e) {
						action = "con_error_exception";
						message += "a déclenché une exception : erreur de connexion à la base de données\nPass : "+Utilities.formatSQL(pass);
						notifText = Global.i("CAPTION_UNKNOW_ERROR");
						e.printStackTrace();
					}
					
				} else {
					action = "con_invalid_mail";
					message += " a tenté de se connecter en entrant une adresse e-mail invalide";
				}
				
			} else {
				action = "con_invalid_infos";
				message += " a tenté de se connecter en entrant des informations invalides.";
			}
			
			if(email!=null && action!=null && message!=null) {
				Sql.exec(tbl_journal_con.getInsertQuery(email, action, message));
			}
			
			// Affichage d'un notification s'il en existe une
			if(!notifText.equals("")) {
				notif = new Notification(null, notifText, Notification.TYPE_ERROR_MESSAGE);
				notif.setPosition(Notification.POSITION_CENTERED_BOTTOM);
				notif.setDelayMsec(2000);
				getWindow().showNotification(notif);
			}
			
			// Déconnexion de la base de données
			Sql.Disconnect();
		}
		
	};
	
	/**
	 * Set l'état de la connexion
	 * @param connexionState Etat de la connexion
	 */
	public void setConnexionState(Boolean connexionState) {
		this.connexionState = connexionState;
	}
	/**
	 * Retourne l'état de la connexion
	 * @return true si la connexion est ok
	 */
	public Boolean getConnexionState() {
		return connexionState;
	}
	/**
	 * Modifie la partie qui contient initialement le logo de la caisse en la chargeant par d'autres contenus
	 * @param content Texte affiché
	 * @param icon Icone d'illustration du texte
	 */
	private void changeLogoArea(String content, String icon) {
		//On redimensionne la fenêtre
		winConnexion.setHeight("310px");
		winConnexion.setWidth("440px");
		
		// On cache le formulaire de connexion
		velConnexion.setVisible(false);
		
		// On supprime le logo
		logo.removeStyleName("v-logo");
		logo.setWidth("290px");
		logo.setHeight("118px");
		
		// On ajoute une image d'illustration
		Embedded imgIllustration = new Embedded(null, new ThemeResource(icon));
		logo.addComponent(imgIllustration);
		logo.setComponentAlignment(imgIllustration, Alignment.TOP_CENTER);
		
		// On ajoute le texte en dessous de l'illustration
		Label label = new Label(content);
		label.setContentMode(Label.CONTENT_XHTML);
		label.setStyleName("align-center");
		label.setWidth("400px");
		label.setHeight("122px");
		winConnexion.addComponent(label);
	}
	
}
