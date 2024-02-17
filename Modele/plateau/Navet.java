package Modele.plateau;

/**
 * Entite Navet, bonus
 */
public class Navet extends EntiteStatique {

    public Navet(Jeu _jeu){super(_jeu);}

    @Override
    public boolean traversable() {
        return true;
    }

    @Override
    public boolean peutEtreEcrase() {
        return false;
    }

    @Override
    public boolean peutServirDeSupport() {
        return false;
    }

    @Override
    public boolean peutPermettreDeMonterDescendre() {
        return false;
    }


}
