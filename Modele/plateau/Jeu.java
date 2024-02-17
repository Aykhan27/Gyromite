package Modele.plateau;

import Modele.deplacements.*;
import Modele.plateau.enums.ColonneType;
import Modele.plateau.enums.SupportColonneType;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Jeu {
    public static final int NB_MAX_OF_LVL = 3;
    public static final int SIZE_X = 20;
    public static final int SIZE_Y = 15;

    String niveauReader;

    private final Ordonnanceur ordonnanceur = new Ordonnanceur(this);

    private Heros heros;
    private Point orgPos;

    private HashMap<Entite, Point> carte = new HashMap<>();
    private Entite[][] grilleEntites = new Entite[SIZE_X][SIZE_Y];

    private boolean isGameOver = false;
    private boolean isGameWin = false;
    private boolean isUpdate = false; //gère la fin de l'ordonnanceur

    private int score;
    private int niveau;

    private ArrayList<ColonneEntiere> lstColEntiere = new ArrayList<>();

    public Ordonnanceur getOrdonnanceur() {
        return ordonnanceur;
    }

    public Heros getHeros() {
        return heros;
    }

    private int compteurBombe = 0;

    public Entite[][] getGrille() {
        return grilleEntites;
    }

    public Jeu(int _niveau) {
        niveau = _niveau;
        chargerNiveau();
    }


    public Entite getEntite(int x, int y) {
        if (x < 0 || x >= SIZE_X || y < 0 || y >= SIZE_Y) {
            // L'entité demandée est en-dehors de la grille
            return null;
        }
        return grilleEntites[x][y];
    }

    /**
     * Check si l'entite est bien dans la grille du jeu
     * @param p Position de l'entite
     * @return true ou false
     */
    private boolean estDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }

    /**
     * Ouvre et lit les fichiers NiveauX.csv qui contiennent les données pour charger le niveau
     * Stocke le contenu dans un String
     * Appelle la fonction initialisationDesEntites()
     */
    public void chargerNiveau() {
        niveauReader = "";
        try {
        	String path = "Niveaux\\Niveau"+niveau+".csv";
            //String path = new File(".").getCanonicalPath();
            File carte = new File(path);


           // File carte = new File(path.replace("\\", "\\\\") + "\\Niveaux\\Niveau"+niveau+".csv");
            FileReader fr = new FileReader(carte);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            niveauReader = sb.toString();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initialisationDesEntites();
    }

    /**
     * Parcours le string du niveau et ajoute les entites correspondants dans le jeu
     */
    private void initialisationDesEntites() {

        for (int y = 0; y < Jeu.SIZE_Y; y++) {
            for (int x = 0; x < Jeu.SIZE_X; x++) {
                int n = niveauReader.charAt(y * Jeu.SIZE_X + x);
                switch (n) {
                    case 'A': //Personnage joueur
                        heros = new Heros(this, x, y, new CaseVide(this));
                        addEntite(heros, x, y);
                        orgPos = new Point(x, y);
                        Controle4Directions.getInstance().addEntiteDynamique(heros);
                        Gravite.getInstance().addEntiteDynamique(heros);
                        break;
                    case 'S':
                        Smick smick = new Smick(this, Direction.Droite, new CaseVide(this));
                        addEntite(smick, x, y);
                        Gravite.getInstance().addEntiteDynamique(smick);
                        IA.getInstance().addEntiteDynamique(smick);
                        break;
                    case 'W': //Mur
                        addEntite(new Mur(this), x, y);
                        break;
                    case 'E': //Bombe
                        addEntite(new Bombe(this), x, y);
                        compteurBombe++;
                        break;
                    case 'V': //CaseVide
                        addEntite(new CaseVide(this), x, y);
                        break;
                    case 'X': //Plafond
                        addEntite(new Plafond(this), x, y);
                        break;
                    case 'C': //Corde
                        addEntite(new Corde(this), x, y);
                        break;
                    case 'N': //Navet
                        addEntite(new Navet(this), x, y);
                        break;
                    case 'P': //Plateforme Horizontal
                        addEntite(new PoutreHorizontale(this), x, y);
                        break;
                    case 'O': //Plateforme Vertical
                        addEntite(new PoutreVerticale(this), x, y);
                        break;
                    case 'G': // Support colonne gauche
                        addEntite(new SupportColonne(this, SupportColonneType.Gauche), x, y);
                        break;
                    case 'D': // Support colonne droit
                        addEntite(new SupportColonne(this, SupportColonneType.Droite), x, y);
                        break;
                    case 'H': //bloc de colonne extremité haute
                        Colonne colH = new Colonne(this, ColonneType.Haut);
                        addEntite(colH, x, y);
                        ColonneEntiere colEntiere = new ColonneEntiere(this);
                        colEntiere.addCol(colH);
                        lstColEntiere.add(colEntiere);
                        break;
                    case 'M': //bloc de colonne milieu
                        Colonne colM = new Colonne(this, ColonneType.Milieu);
                        addEntite(colM, x, y);

                        for (ColonneEntiere colonneEntiere : lstColEntiere) {
                            if (colonneEntiere.colonnes.size() == 1)
                                colonneEntiere.addCol(colM);
                        }

                        break;
                    case 'B': //bloc de colonne extremité basse
                        Colonne colB = new Colonne(this, ColonneType.Bas);
                        addEntite(colB, x, y);

                        for (ColonneEntiere colonneEntiere : lstColEntiere) {
                            if (colonneEntiere.colonnes.size() == 2) {
                                colonneEntiere.addCol(colB);
                                ColonneControle.getInstance().addEntiteDynamique(colonneEntiere);
                            }
                        }
                        break;
                }

            }
        }
        getOrdonnanceur().addDep(Controle4Directions.getInstance());
        getOrdonnanceur().addDep(ColonneControle.getInstance());
        getOrdonnanceur().addDep(Gravite.getInstance());
        getOrdonnanceur().addDep(IA.getInstance());
    }

    /**
     * @param entite l'entite qui regarde
     * @param direction la direction dans laquelle l'entite regarde
     * @return l'entite observee
     */
    public Entite regarderDansLaDirection(Entite entite, Direction direction) {
        Point positionEntite = carte.get(entite);

        Entite entiteRegardee = null;
        Point coordEntiteRegardee = new Point(0, 0);
        if (positionEntite != null) {
            switch (direction) {
                case Droite:
                    coordEntiteRegardee.x = positionEntite.x + 1;
                    coordEntiteRegardee.y = positionEntite.y;
                    break;
                case Gauche:
                    coordEntiteRegardee.x = positionEntite.x - 1;
                    coordEntiteRegardee.y = positionEntite.y;
                    break;
                case Haut:
                    coordEntiteRegardee.x = positionEntite.x;
                    coordEntiteRegardee.y = positionEntite.y - 1;
                    break;
                case Bas:
                    coordEntiteRegardee.x = positionEntite.x;
                    coordEntiteRegardee.y = positionEntite.y + 1;
                    break;
            }

            if (estDansGrille(coordEntiteRegardee)) {
                entiteRegardee = grilleEntites[coordEntiteRegardee.x][coordEntiteRegardee.y];
            }
        }

        return entiteRegardee;
    }

    /**
     *
     * @param entite L'entite que l'on regarde (récupérée depuis regarderDansLaDirection())
     * @return l'entite en dessous de l'entite que l'on regarde (gestion des deplacements du smick)
     */
    public boolean checkSiEntiteDessousPeutServirDeSupport(Entite entite) {
        boolean isEntitePlateforme = false;

        Point coordEntiteRegardee = new Point(carte.get(entite).x, carte.get(entite).y + 1);
        if (estDansGrille(coordEntiteRegardee)) {
            isEntitePlateforme = grilleEntites[coordEntiteRegardee.x][coordEntiteRegardee.y].peutServirDeSupport() ||
                    grilleEntites[coordEntiteRegardee.x][coordEntiteRegardee.y].peutPermettreDeMonterDescendre();
        }
        return isEntitePlateforme;
    }

    /**
     * Deplace l'entite d'une case dans la direction choisie si les contraintes sont satisfaites
     * @param entite L'entite que l'on déplace
     * @param direction La direction dans laquelle on la deplace
     * @return true si le deplacement a pu se faire, false sinon
     */
    public boolean deplacerEntite(Entite entite, Direction direction) {
        int px = carte.get(entite).x;
        int py = carte.get(entite).y;

        boolean deplacementOK = false;
        if (entite instanceof EntiteDynamique) {
            EntiteDynamique eD = (EntiteDynamique) entite; //Pour eviter de cast à chaque appel et épurer le code
            switch (direction) {
                case Droite:
                    remetCasePrecedente(eD, px, py);
                    replaceEntite(eD, px + 1, py);
                    deplacementOK = true;
                    break;
                case Gauche:
                    remetCasePrecedente(eD, px, py);
                    replaceEntite(eD, px - 1, py);
                    deplacementOK = true;
                    break;
                case Haut:
                    remetCasePrecedente(eD, px, py);
                    replaceEntite(eD, px, py - 1);
                    deplacementOK = true;
                    break;
                case Bas:
                    remetCasePrecedente(eD, px, py);
                    replaceEntite(eD, px, py + 1);
                    deplacementOK = true;
                    break;
            }
        }
        return deplacementOK;
    }

    /**
     * Lorsqu'une colonne ecrase une entite
     * @param colonne l'entite bloc de colonne qui effectue l'ecrasement
     * @param entiteEcrasee l'entite ecrasee
     */
    public void ecraseEntite(EntiteDynamique colonne, EntiteDynamique entiteEcrasee) {
        if (entiteEcrasee instanceof Heros) {
            colonne.setCasePrecedente(heros.getCasePrecedente());
            grilleEntites[heros.getX()][heros.getY()] = heros.getCasePrecedente();
            playerLooseLife();
        } else if (entiteEcrasee instanceof Smick) {
            remetCasePrecedente(entiteEcrasee, carte.get(entiteEcrasee).x, carte.get(entiteEcrasee).y);
            IA.getInstance().removeEntiteDynamique(entiteEcrasee);
            Gravite.getInstance().removeEntiteDynamique(entiteEcrasee);
        }
    }

    /**
     * @param e L'entite à ajouter
     * @param x Sa position x
     * @param y Sa position y
     */
    private void addEntite(Entite e, int x, int y) {
        grilleEntites[x][y] = e;
        carte.put(e, new Point(x, y));
    }

    /**
     * On remet l'entite qui occupait precedemment la case lors du déplacement d'une entite
     * @param e L'entite qui se déplace
     * @param x Sa position x
     * @param y Sa position y
     */
    private void remetCasePrecedente(EntiteDynamique e, int x, int y) {
        carte.remove(e);
        grilleEntites[x][y] = e.getCasePrecedente();
        carte.put(e.getCasePrecedente(), new Point(x, y));
    }

    /**
     * Exemple :
     * Lors d'un déplacement en x=0, y=1, on remplace l'entite qui occupait cette position par l'entite
     * en parametre, on stock l'entitePrecedente et on met à jours notre grille de case coté vue et coté jeu
     * @param e L'entite qui effectue le déplacement
     * @param x La position x du déplacement
     * @param y La position y du déplacement
     */
    private void replaceEntite(EntiteDynamique e, int x, int y) {
        if (e instanceof Smick) {
            if (grilleEntites[x][y] instanceof Heros) {
                e.setCasePrecedente(((Heros) grilleEntites[x][y]).getCasePrecedente());
                playerLooseLife();
                grilleEntites[x][y] = e;
                carte.put(e, new Point(x, y));
            } else {
                e.setCasePrecedente(grilleEntites[x][y]);
                carte.remove(grilleEntites[x][y]);
                grilleEntites[x][y] = e;
                carte.put(e, new Point(x, y));
            }
        }
        else if (e instanceof Heros) {
            if (grilleEntites[x][y] instanceof Smick) {
                playerLooseLife();
            } else {
                if (grilleEntites[x][y] instanceof Bombe) {
                    e.setCasePrecedente(new CaseVide(this));
                    this.setScore(this.getScore() + 1);
                    this.compteurBombe--;
                } else if(grilleEntites[x][y] instanceof Navet){
                    e.setCasePrecedente(new CaseVide(this));
                    this.setScore(this.getScore() + 1);
                }
                else {
                    e.setCasePrecedente(grilleEntites[x][y]);
                }
                carte.remove(grilleEntites[x][y]);
                grilleEntites[x][y] = e;
                carte.put(e, new Point(x, y));
                this.heros.setPosXY(x,y);
            }
        }
        else if (e instanceof Colonne) {
            e.setCasePrecedente(grilleEntites[x][y]);
            this.carte.remove(grilleEntites[x][y]);
            this.grilleEntites[x][y] = e;
            this.carte.put(e, new Point(x, y));
        }
    }

    /**
     * Fais perdre une vie au joueur, reset sa position à sa position de spawn du début du niveau
     * et remet la case précédente stockée au sein de l'entite heros
     */
    public void playerLooseLife() {
        Controle4Directions.getInstance().resetControle4Directions();
        this.heros.setHerosLife(this.heros.getHerosLife() - 1);
        if (this.heros.getHerosLife() <= 0) {
            isGameOver = true;
        }
        remetCasePrecedente( this.heros,  this.heros.getX(),  this.heros.getY());
        this.carte.put( this.heros, orgPos);
        this.grilleEntites[orgPos.x][orgPos.y] =  this.heros;
        this.heros.setPosXY(orgPos.x,orgPos.y); //on reset la position du joueur
        this.heros.setCasePrecedente(new CaseVide(this)); //On stock une case vide comme case précedente
        this.heros.setDirectionCourante(Direction.Droite); // Pour l'affichage
    }

    /**
     * Check si la partie est terminée
     */
    public void checkIsWin() {
        if (this.compteurBombe <= 0) {
            isGameWin = true;
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isGameWin() {
        return isGameWin;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean getIsUpdate(){
        return this.isUpdate;
    }

    public void setIsUpdate(boolean bool){
        this.isUpdate = bool;
    }
  
    public int getNiveau(){
        return niveau;
    }
}
