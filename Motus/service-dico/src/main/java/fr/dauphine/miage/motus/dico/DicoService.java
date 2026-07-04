package fr.dauphine.miage.motus.dico;

import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

/**
 * Couche "service" : contient la logique metier du dictionnaire.
 *
 * On separe volontairement la logique metier du controleur REST.
 * Le controleur s'occupe du HTTP, le service s'occupe des regles
 * du jeu. C'est plus propre et plus facile a tester.
 *
 * L'annotation @Service indique a Spring de creer et gerer une
 * instance de cette classe (un "bean").
 */
@Service
public class DicoService {

    private final MotRepository motRepository;
    private final DictionnaireFrancaisValidateur validateurFrancais;
    private final Random random = new Random();

    /**
     * Injection de dependance par constructeur : Spring fournit
     * automatiquement le MotRepository. C'est la maniere recommandee
     * (plutot que @Autowired sur un champ).
     */
    public DicoService(MotRepository motRepository,
                       DictionnaireFrancaisValidateur validateurFrancais) {
        this.motRepository = motRepository;
        this.validateurFrancais = validateurFrancais;
    }

    /**
     * Tire un mot mystere aleatoire d'une longueur donnee.
     *
     * @param longueur nombre de lettres souhaite
     * @return un Mot choisi au hasard parmi ceux de cette longueur
     * @throws MotIntrouvableException si aucun mot de cette longueur
     */
    public Mot tirerMotAleatoire(int longueur) {
        List<Mot> candidats = motRepository.findByLongueur(longueur);
        if (candidats.isEmpty()) {
            throw new MotIntrouvableException(
                    "Aucun mot de " + longueur + " lettres dans le dictionnaire.");
        }
        int indice = random.nextInt(candidats.size());
        return candidats.get(indice);
    }

    /**
     * Verifie si un mot est une proposition valide pour le jeu.
     *
     * Attention a ne pas confondre deux dictionnaires distincts :
     *  - le dictionnaire DU JEU (table "mot", ~258 mots) : le pool
     *    restreint parmi lequel le mot MYSTERE est tire (methode
     *    tirerMotAleatoire ci-dessus) ;
     *  - le dictionnaire FRANCAIS DE REFERENCE (~319 000 mots,
     *    DictionnaireFrancaisValidateur) : tous les mots que
     *    l'on accepte comme PROPOSITION du joueur.
     *
     * Comme au vrai Motus/Wordle, le joueur peut essayer n'importe
     * quel mot reel de la bonne longueur, meme s'il ne fait pas
     * partie du pool restreint des mots mysteres possibles. On
     * verifie donc contre le dictionnaire de reference, pas contre
     * le pool du jeu.
     *
     * @param valeur le mot a verifier
     * @return true si le mot est un mot francais valide, false sinon
     */
    public boolean motExiste(String valeur) {
        return validateurFrancais.estMotFrancaisValide(valeur);
    }

    /**
     * Renvoie la liste complete des mots du dictionnaire.
     */
    public List<Mot> listerTousLesMots() {
        return motRepository.findAll();
    }

    /**
     * Ajoute un nouveau mot au dictionnaire.
     *
     * Deux controles avant d'accepter le mot :
     *  1. c'est un mot francais reellement existant (voir
     *     DictionnaireFrancaisValidateur), pas une suite de lettres
     *     quelconque ;
     *  2. il n'est pas deja present dans le dictionnaire du jeu.
     *
     * @throws MotInvalideException si l'un de ces controles echoue
     */
    public Mot ajouterMot(Mot mot) {
        if (mot == null || mot.getValeur() == null) {
            throw new MotInvalideException("Le mot ne peut pas etre vide.");
        }
        String valeur = mot.getValeur().trim().toUpperCase();

        if (!validateurFrancais.estMotFrancaisValide(valeur)) {
            throw new MotInvalideException(
                    "\"" + valeur + "\" n'est pas reconnu comme un mot francais valide.");
        }
        if (motRepository.existsByValeur(valeur)) {
            throw new MotInvalideException(
                    "Le mot \"" + valeur + "\" est deja present dans le dictionnaire.");
        }

        mot.setValeur(valeur);
        return motRepository.save(mot);
    }
}
