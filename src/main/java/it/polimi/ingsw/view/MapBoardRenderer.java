package it.polimi.ingsw.view;

import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Class with methods used by render for rendering the map board
 *
 * @author  davidealde
 */
public class MapBoardRenderer {

    private double scale;
    private ClientModel clientModel;

    public MapBoardRenderer(double sc, ClientModel cm) {
        this.scale = sc;
        this.clientModel = cm;
    }

    public HBox mapRenderer() {
        InputStream mapLeft;
        InputStream mapRight;
        if (clientModel.getMapID() == 1 || clientModel.getMapID() == 3) {
            mapLeft = this.getClass().getResourceAsStream("/images/miscellaneous/mapLeft1.png");
        } else {
            mapLeft = this.getClass().getResourceAsStream("/images/miscellaneous/mapLeft2.png");
        }
        if (clientModel.getMapID() == 3 || clientModel.getMapID() == 4) {
            mapRight = this.getClass().getResourceAsStream("/images/miscellaneous/mapRight2.png");
        } else {
            mapRight = this.getClass().getResourceAsStream("/images/miscellaneous/mapRight1.png");
        }
        Image imageMapLeft = new Image(mapLeft);
        Image imageMapRight = new Image(mapRight);
        ImageView mapLeftView = new ImageView(imageMapLeft);
        ImageView mapRightView = new ImageView(imageMapRight);
        mapLeftView.setFitHeight(800 * scale);
        mapRightView.setFitHeight(800 * scale);


        mapLeftView.setPreserveRatio(true);
        mapRightView.setPreserveRatio(true);
        HBox map = new HBox();
        map.getChildren().addAll(mapLeftView, mapRightView);

        return map;
    }

    public GridPane roomRenderer(){
        int mapId = clientModel.getMapID();
        List<ClientModel.SimpleSquare> squares = clientModel.getSquares();
        GridPane roomsGrid = new GridPane();
        Pane emptyRoom1 = new Pane();
        Pane  emptyRoom2 = new Pane();
        Pane  emptyRoom3 = new Pane();
        Pane  emptyRoom4 = new Pane();
        Pane  emptyRoom5 = new Pane();
        List<Button> squareButton = new ArrayList<>();
        emptyRoom1.setMinSize(175*scale, 175*scale);
        emptyRoom2.setMinSize(175*scale, 175*scale);
        emptyRoom3.setMinSize(175*scale, 175*scale);
        emptyRoom4.setMinSize(175*scale, 175*scale);
        emptyRoom5.setMinSize(175*scale, 175*scale);
        List<ImageView> ammoView = new ArrayList<>();
        int column=0;
        int row=0;
        int spawningPoint=1;

        for(ClientModel.SimpleSquare s : squares) {
            if ((mapId == 1 || mapId == 2) && column == 3 && row == 0) {
                roomsGrid.add(emptyRoom1, column, row);
                row = 1;
                column = 0;
                roomsGrid.add(emptyRoom5,column,row);
                squareButton.add(new Button());
                roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                squareButton.get(squareButton.size()-1).setPrefSize(150,150);
                squareButton.get(squareButton.size()-1).setTranslateY(-20);
                squareButton.get(squareButton.size()-1).setStyle("-fx-background-color: transparent;");
                column++;
            } else if((mapId == 1 || mapId == 3) && column == 0 && row == 2){
                roomsGrid.add(emptyRoom2,column,row);
                column++;
                ammoView.add(getImageOfSquare(s));
                roomsGrid.add(ammoView.get(ammoView.size()-1), column, row);
                ammoView.get(ammoView.size()-1).setFitHeight(65*scale);
                ammoView.get(ammoView.size()-1).setPreserveRatio(true);
                roomsGrid.setMargin(ammoView.get(ammoView.size()-1), new javafx.geometry.Insets(55*scale, 55*scale, 55*scale, 55*scale));
                squareButton.add(new Button());
                roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                squareButton.get(squareButton.size()-1).setPrefSize(150,150);
                squareButton.get(squareButton.size()-1).setTranslateY(-20);
                squareButton.get(squareButton.size()-1).setStyle("-fx-background-color: transparent;");
                column++;
            }else{
                if (!s.isSpawnPoint()) {
                    ammoView.add(getImageOfSquare(s));
                    roomsGrid.add(ammoView.get(ammoView.size()-1), column, row);
                    ammoView.get(ammoView.size()-1).setFitHeight(65*scale);
                    ammoView.get(ammoView.size()-1).setPreserveRatio(true);
                    roomsGrid.setMargin(ammoView.get(ammoView.size()-1), new javafx.geometry.Insets(55*scale, 55*scale, 55*scale, 55*scale));
                    squareButton.add(new Button());
                    roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                    squareButton.get(squareButton.size()-1).setPrefSize(150,150);
                    squareButton.get(squareButton.size()-1).setTranslateY(-20);
                    squareButton.get(squareButton.size()-1).setStyle("-fx-background-color: transparent;");
                }
                else if (spawningPoint==1){
                    roomsGrid.add(emptyRoom3,column,row);
                    squareButton.add(new Button());
                    roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                    squareButton.get(squareButton.size()-1).setPrefSize(150,150);
                    squareButton.get(squareButton.size()-1).setTranslateY(-20);
                    squareButton.get(squareButton.size()-1).setStyle("-fx-background-color: transparent;");
                    spawningPoint++;
                }
                else if (spawningPoint==2){
                    roomsGrid.add(emptyRoom4,column,row);
                    squareButton.add(new Button());
                    roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                    squareButton.get(squareButton.size()-1).setPrefSize(150,150);
                    squareButton.get(squareButton.size()-1).setTranslateY(-20);
                    squareButton.get(squareButton.size()-1).setStyle("-fx-background-color: transparent;");
                    spawningPoint++;
                }
                if(column==3) {
                    column = 0;
                    row++;
                }else{
                    column++;}
            }
        }
        roomsGrid.setTranslateX(180*scale);
        roomsGrid.setTranslateY((200*scale));
        return roomsGrid;
    }

    public ImageView getImageOfSquare(ClientModel.SimpleSquare square){
        int r,b,y;
        boolean pU;
        String k1="",k2="",k3="",k4="",k5="",k6="",k7="";
        int o=square.getId();

        r = square.getRedAmmo();
        b = square.getBlueAmmo();
        y = square.getYellowAmmo();
        pU = square.isPowerup();
        if(pU)
            k1="P";
        if(r>=1)
            k2="R";
        if(r==2)
            k3="R";
        if(b>=1)
            k4="B";
        if(b==2)
            k5="B";
        if(y>=1)
            k6="Y";
        if(y==2)
            k7="Y";
        //try{
        InputStream ammoFile = this.getClass().getResourceAsStream("/images/ammo/ammo"+k1+k2+k3+k4+k5+k6+k7+".png");
        Image ammoImage = new Image(ammoFile);
        ImageView ammoView = new ImageView(ammoImage);
        return ammoView;
        //}catch (FileNotFoundException e){
        //    e.printStackTrace();
        //}
        //return null;
    }


    public GridPane killShotTrackRender(int skullNumber){
        List<ImageView> skulls = new ArrayList<>();
        InputStream skullFile = this.getClass().getResourceAsStream("/images/miscellaneous/skull.png");

        Image skullImage = new Image(skullFile);
        for(int i=0; i<skullNumber;i++)
            skulls.add(new ImageView(skullImage));

        for(ImageView s : skulls){
            s.setFitWidth(44.5*scale);
            s.setPreserveRatio(true);
        }
        GridPane skullsGrid = new GridPane();
        List<ColumnConstraints> columnConstraints = new ArrayList<>();
        for(int i=0; i<8-skullNumber;i++){
            columnConstraints.add(new ColumnConstraints(44.5*scale));
            skullsGrid.getColumnConstraints().add(columnConstraints.get(i));
        }
        for(int i=8-skullNumber; i<8;i++)
            skullsGrid.add(skulls.get(i-(8-skullNumber)),i,0,1,1);

        skullsGrid.setTranslateX(70*scale);
        skullsGrid.setTranslateY(50*scale);
        return skullsGrid;

    }


    public List<ImageView> iconsRenderer(){
        List<ImageView> iconView = new ArrayList<>();
        List<ClientModel.SimplePlayer> players = clientModel.getPlayers();
        Image iconImage;
        String color;
        for(ClientModel.SimplePlayer p : players){
            color = p.getColor();
            iconImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/" + color + "Hero.png"));
            iconView.add(new ImageView(iconImage));
            iconView.get(iconView.size()-1).setFitHeight(80*scale);
            iconView.get(iconView.size()-1).setPreserveRatio(true);

        }

        return iconView;
    }

    public int columnFinder(ClientModel.SimpleSquare square) {
        if (clientModel.getMapID() == 4) {
            return square.getId() % 4;
        }
        if (clientModel.getMapID() == 3) {
            if (square.getId() <= 7)
                return square.getId() % 4;
            else
                return (square.getId() + 1) % 4;
        }
        if (clientModel.getMapID() == 2) {
            if (square.getId() <= 2)
                return square.getId() % 4;
            else
                return (square.getId() + 1) % 4;
        } else {
            if (square.getId() <= 2)
                return square.getId() % 4;
            else if (square.getId() <= 6)
                return (square.getId() + 1) % 4;
            else
                return (square.getId() + 2) % 4;
        }
    }

        public int rowFinder(ClientModel.SimpleSquare square){
            if (clientModel.getMapID() == 4 || clientModel.getMapID() == 3) {
                return square.getId() / 4;
            } else if (square.getId() <2)
                return 0;
            else
                return (square.getId() + 1) / 4;
        }



    }

