package common.component;

import java.util.ArrayList;

import com.vaadin.data.Item;
import com.vaadin.ui.Table;

/**
 * Extension du composant Table de Vaadin.<br>
 * Cette table contient des m�thodes utiles pour le chargement de donn�es depuis une base de donn�es,<br>
 * il faut donc l'utiliser lorsqu'on souhaite afficher les donn�es d'une table de la base de donn�es dans un tableau de l'interface utilisateur.
 * Cette class est une alternative � l'add-on Vaadin "SQLContainer" qui est complexe et propose beaucoup de m�thodes dont je n'ai pas l'utilit�.<br>
 * Contrairement au SQLContainer, les requ�tes SQL ex�cut�es pour le chargement de ce type de table contiennent directement
 * les clauses ORDER BY, LIMIT, etc...
 * @author Dominique Roduit
 *
 */
public class SQLTable extends Table {
	protected ArrayList<Integer> PKField = new ArrayList<Integer>();
	
	/**
	 * Cr�ation d'une table destin�e � l'affichage d'un r�sultat d'une requ�te SQL
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
	 * @param items (Object[]) R�sultats de la requ�te SQL sous forme d'items pr�ts � l'utilisation.
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
	 * Mise � jour des donn�es du tableau
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
	 * Utilis� pour convertir l'objet "value" en tableau de chaine de caract�res
	 * @param value Objet � passer en param�tre
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
