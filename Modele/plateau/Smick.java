package Modele.plateau;

import Modele.deplacements.Direction;

/**
 * Entite Smick, ennemi du joueur controlé par la classe IA
 */
public class Smick extends EntiteDynamique{


    public Smick(Jeu _jeu, Direction direction, Entite casePrecedente) {
        super(_jeu);
        this.setDirectionCourante(direction);
        this.setCasePrecedente(casePrecedente);
    }

    @Override
    public boolean traversable() {
        return true;
    }

    @Override
    public boolean peutEtreEcrase() {
        return true;
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
