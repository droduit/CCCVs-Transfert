package common.component;

import java.util.ArrayList;

import com.vaadin.data.Item;
import com.vaadin.ui.Table;

/**
 * Extension du composant Table de Vaadin.<br>
 * Cette table contient des méthodes utiles pour le chargement de données depuis une base de données,<br>
 * il faut donc l'utiliser lorsqu'on souhaite afficher les données d'une table de la base de données dans un tableau de l'interface utilisateur.
 * Cette class est une alternative à l'add-on Vaadin "SQLContainer" qui est complexe et propose beaucoup de méthodes dont je n'ai pas l'utilité.<br>
 * Contrairement au SQLContainer, les requêtes SQL exécutées pour le chargement de ce type de table contiennent directement
 * les clauses ORDER BY, LIMIT, etc...
 * @author Dominique Roduit
 *
 */
public class SQLTable extends Table {
	protected ArrayList<Integer> PKField = new ArrayList<Integer>();
	
	/**
	 * Création d'une table destinée à l'affichage d'un résultat d'une requête SQL
	 */
	public SQLTable() {
		super();
		setSizeFull();
		setSelectable(true);
		setImmediate(false);
		setMultiSelect(true);
	}
	/**
	 * Ajoute tout le contenu d'un tableau d'objet (Items) dans le container
	 * @param items (Object[]) Résultats de la requête SQL sous forme d'items prêts à l'utilisation.
	 */
	public void addAllItems(Object[] items) { 
		if(items!=null) {
			Item item = null;
	    	for(int i=0; i<items.length; i++) {
		    	if(items[i]!=null) {
		    		addItem((Object[]) items[i], PKField.get(i));
		    		setData(PKField.get(i));
		    	}
		     }
		}
	}
	/**
	 * Mise à jour des données du tableau
	 * @param items Lignes de la table
	 */
	public void reload(Object[] items) {
    	if(removeAllItems()) {
    		addAllItems(items);
    	}
    	// S'il n'y a encore aucune ligne on affiche pas le tableau
    	if(size()<1) {
    		setVisible(false);
    	} else {
    		setVisible(true);
    	}
    }
	/**
	 * Converti un objet en tableau de String.<br>
	 * Utilisé pour convertir l'objet "value" en tableau de chaine de caractères
	 * @param value Objet à passer en paramètre
	 * @return (String[]) Tableau de string avec les valeurs
	 */
	protected String[] getValuesFromObject(Object value) {
		String rowId = "";
		String[] ids = null;
		
		if(value!=null) {
			rowId = value.toString();
			rowId = rowId.replace("[", "").replace("]","").replaceAll(" ","");
			ids = rowId.split(",");
		}
		
		return ids;
	}
}
