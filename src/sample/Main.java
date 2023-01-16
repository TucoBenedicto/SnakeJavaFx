package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    //Best practice , toute les fonctions et variables/constantes de la class doit etre private

    //Taille de la fenetre
    private static final int WIDTH = 800; //largeur de la fenetre
    private static final int HEIGHT = WIDTH;

    //Taille & cases du jeu
    private static final int ROWS = 20; //Nb de ligne du quadrillage background (servira pour la couleur du background)
    private static final int COLUMNS = ROWS; //Nb de colonne du quadrillage background (servira pour la couleur du background)
    private static final int SQUARE_SIZE = WIDTH / ROWS; //SQUARE_SIZE : Taille des elements du jeux:  snake , food, case de l'aire de jeux.

    //Images
    private static final String[] FOODS_IMAGE = new String[]{ //Array de String
            "file:src/img/ic_orange.png",
            "file:src/img/ic_apple.png",
            "file:src/img/ic_cherry.png",
            "file:src/img/ic_berry.png",
            "file:src/img/ic_coconut_.png",
            "file:src/img/ic_peach.png",
            "file:src/img/ic_watermelon.png",
            "file:src/img/ic_orange.png",
            "file:src/img/ic_pomegranate.png"};

    //Gestion clavier (KeyEvent)
    private static final int RIGHT = 0; // Le KeyCode.Right retournera 0 (KeyEvent)
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private GraphicsContext gc; // Class permettant de dessiner sur la canvas .
    private List <Point> snakeBody = new ArrayList(); // ArrayListe (list infinie contrairement a Array), snakeBody recupere la liste des coordonées x,y du toutes les parties du corps du snake
    private Point snakeHead; // snakeHead recupere les coordonées x,y de la tete
    private Image foodImage; // Recupere le lien vers l'image (sous forme de string)
    private int foodX; // Recupere la coordonnée x de la variable "foodX"
    private int foodY; // Recupere la coordonnée y de la variable "foodY"
    private boolean gameOver; // Arretera le deplacement du snake et affichera le message GameOver
    private int currentDirection; // recuperea 0: right , 1 : left , 2 : up , 3 : down. pour le deplacement
    private int score = 0; // Score

    @Override
    public void start(Stage primaryStage) throws Exception { //Lancement du jeu
        //Rappel sur les couches javaFX :
        // Stage (toute la fenetre + barre de la fenetre ,
        // Scene : que la fenetre,
        //Group (root): Group est un conteneur et on peux en creer plusieur (un pour le menu , un autre pour les dessin , ect...)
        // Canvas :(on dessine a l'interieur du canvas)

        //Affichage
        primaryStage.setTitle("Snake");
        Group root = new Group();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        gc = canvas.getGraphicsContext2D();

        //Gestion des evenemenent du clavier
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    if (currentDirection != LEFT) {
                        currentDirection = RIGHT;
                    }
                } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                    if (currentDirection != RIGHT) {
                        currentDirection = LEFT;
                    }
                } else if (code == KeyCode.UP || code == KeyCode.W) {
                    if (currentDirection != DOWN) {
                        currentDirection = UP;
                    }
                } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                    if (currentDirection != UP) {
                        currentDirection = DOWN;
                    }
                }
            }
        });

        for (int i = 0; i < 3; i++) { //on demarre le jeu avec 3 carré pour le corps du snake
            snakeBody.add(new Point(5, ROWS / 2)); // on place le snake au milieu gauche de la fenetre.
        }
        snakeHead = snakeBody.get(0); // snakeBody est une list comprenant le corp + la tete, la 1ere corrrodnnée "snakeBody.get(0)" represente la tete, et la 1 , 2 , 3 le corp
        generateFood(); //On genere un fruit des le debut de la partie

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(130), e -> run(gc))); //rafraichi chaque methode contenue dans la methode "run(gc)"
        timeline.setCycleCount(Animation.INDEFINITE); //Pour spécifier le nombre de répétitions d'une animation, il faut modifier la valeur de la propriété cycleCount de l'animation en spécifiant le nombre de boucles désirées par un nombre entier. Si la valeur est égale à 1, l'animation s'exécutera une seule et unique fois. Pour répéter l'animation indéfiniment, il faut utiliser la valeur Timeline.INDEFINITE.
        timeline.play(); //on lance l'animation
    }

    //Methode "run" va contenir toutes les autres methodes a afficher
    private void run(GraphicsContext gc) {
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Digital-7", 70));
            gc.fillText("Game Over", WIDTH / 3.5, HEIGHT / 2);
            return;
        }
        drawBackground(gc);
        drawFood(gc);
        drawSnake(gc);
        drawScore();
        //On recupere les corrdonée x,y sous forme de liste de chaque carré du corps du snake
        //La recuperation se fait a chaque nouvelle frame
        for (int i = snakeBody.size() - 1; i >= 1; i--) {
            //System.out.println(i);
            /*
            ! la list "snakeBody" est le corp + la tete du snake , etant donné que
            nous ne voulons que les coordonnées du corps et pas de la tete on soustrait -1.
            Concernant  "i--" cela permet de recupere les coordonée du bas vers le haut
            de la list 'snakeBody' ce qui est normal etant donné que la derniere coordonée est la tete
            on demarre de snakeBody.size = 3 pour descendre 2 , 1
             */
            //System.out.println(i);
            //System.out.println("----------------");
            snakeBody.get(i).x = snakeBody.get(i - 1).x;
            snakeBody.get(i).y = snakeBody.get(i - 1).y;
        }

        switch (currentDirection) {
            case RIGHT:
                moveRight();
                break;
            case LEFT:
                moveLeft();
                break;
            case UP:
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
        }

        gameOver();
        eatFood();
    }

    //Dessin du cadrillage de l'aire de jeu.
    private void drawBackground(GraphicsContext gc) {
        for (int i = 0; i < ROWS; i++) {
            //System.out.println("i :" +i );
            for (int j = 0; j < COLUMNS; j++) {
                //System.out.println("j :" +j);
                if ((i + j) % 2 == 0) {//Ce modulo ne renvera que 0 ou 1
                    gc.setFill(Color.web("AAD751")); //Couleur du quadrillage
                } else {
                    //System.out.println((i + j) % 2);
                    gc.setFill(Color.web("A2D149")); //Couleur du quadrillage
                }
                // dessin du background (taille et position de chaque bloc)
                gc.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE); //(position x , position y ,width, height )
            }
        }
    }

    //Generation de la nourriture dans l'aire de jeu de maniere aleatoire.
    private void generateFood() {
        start: //Labeled "continue" Statement ("continue" avec une étiquette)
        while (true) {
            //placement de la nourriture de maniere aleatoire //(int) permet le cast, car d'origine on a un double
            foodX = (int) (Math.random() * ROWS);
            foodY = (int) (Math.random() * COLUMNS);

            // for-each loop (boucle for améliorée) - for(String s : collection) -
            //a utiliser dans le cas d'une collection (list, array , map ...) "snakebody" etant une list
            for (Point snake : snakeBody) {
                /*
                la boucle for-each est utiliser afin de prendre une par une les valeurs de la
                liste "snakeBody" et le passe chacun à son tour dans la variable "snake",
                 "snake" sera utilisé pour valider la collection dans le "if" ci dessous.
                 */
                if (snake.getX() == foodX && snake.getY() == foodY) {
                    /*L'instruction "continue" permet de sauter/annuler la boucle et de la reprendre au debut
                    grace a "continue start" si le serpent et à la meme position que que la nourriture : (snake.getX() == foodX && snake.getY() == foodY)*/
                    continue start; //Labeled "continue" Statement ("continue" avec une étiquette)
                }
            }
            //on met le lien de l'image dans la variable "foodImage" qui servira à l'affichage
            foodImage = new Image(FOODS_IMAGE[(int) (Math.random() * FOODS_IMAGE.length)]);
            //foodImage = new Image("file:src/img/ic_orange.png"); //Test OK
            break; //On arrete la boucle while , des l'affichage de l'image et on passe à l'instruction suivante
        }
    }

    //On affiche l'image de la nouriture à l'aide de la variable foodImage
    private void drawFood(GraphicsContext gc) {
        gc.drawImage(foodImage, foodX * SQUARE_SIZE, foodY * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
    }

    //On dessine la tete + le corps du snake
    private void drawSnake(GraphicsContext gc) {
        //Dessin de la tete
        gc.setFill(Color.web("4674E9"));
        gc.fillRoundRect(snakeHead.getX() * SQUARE_SIZE, snakeHead.getY() * SQUARE_SIZE, SQUARE_SIZE - 1, SQUARE_SIZE - 1, 35, 35);
        //fillRoundRect(coord X , coord Y , Width , Height , arrondie des angles , arrondie des angles )
        //Rappel -1 permet de creer  un espace entre les blocs
        //Dessin de la tete
        for (int i = 1; i < snakeBody.size(); i++) { //La boucle permet l'iteration de la liste "snakeBody" qui contient les corrdonées x,y de chaque carré du corps
            gc.fillRoundRect(snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE - 1, SQUARE_SIZE - 1, 0, 0);
        }
    }

    private void moveRight() {
        snakeHead.x++; //à l'appui sur la touche "right" on incremente les coordonnées x (piur aller a droite)
    }
    private void moveLeft() {
        snakeHead.x--; //touche left ,on decremente les coordonées de l'axe x pour aller a gauche
    }
    private void moveUp() {
        snakeHead.y--; //on decremente les coordonées de l'axe y pour aller en haut
    }
    private void moveDown() { //on decremente les coordonées de l'axe y pour aller en bas
        snakeHead.y++;
    }

    //Fin de partie
    private void gameOver() {
        //Si le snake touche les bordures = game over
        if (snakeHead.x < 0 || snakeHead.y < 0 || snakeHead.x * SQUARE_SIZE >= WIDTH || snakeHead.y * SQUARE_SIZE >= HEIGHT) {
            gameOver = true;
        }

        //destroy itself
        //Si le snake se touche lui meme = game over
        for (int i = 1; i < snakeBody.size(); i++) { //iteration pour recuperer les coordonées de chaque carré du corp
        //ou for (int i = snakeBody.size() - 1; i >= 1; i--) { //
            if (snakeHead.x == snakeBody.get(i).getX() && snakeHead.getY() == snakeBody.get(i).getY()) {
                gameOver = true;
                break; //pas utile
            }
        }
    }
    //
    private void eatFood() {
        if (snakeHead.getX() == foodX && snakeHead.getY() == foodY) { //Si la tete du snake a les meme coordonée que la nourriture alors...
            snakeBody.add(new Point(-1, -1)); //on ajoute un nouveau carré , representant les coordonées du nouveau carré
            // on place le nouveau point en dehore de la fenetre (-1,-1) lors de sa 1ere apparition il ne suivra pas la tete
            generateFood(); //On genere un nouveau fruit, des que le precedent disparait
            score += 5; //On ajoute +5 au score
        }
    }
    //Affichage du score
    private void drawScore() {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Digital-7", 35));
        gc.fillText("Score: " + score, 10, 35);
    }

    //Obligatoire methode de demarrage de la class
    public static void main(String[] args) {
        launch(args);
    }
}