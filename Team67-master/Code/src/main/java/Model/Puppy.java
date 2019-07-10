package Model;

import Viewer.Location;
import Viewer.SquareState;

import java.util.*;

public class Puppy {
    private Location puppyLocation;

    double percentToStay;// range from 0 to 1; 1 means always stay; 0 means always move
    Random randGenForAction;

    // Constructor
    public Puppy(double percentToStay) {
        this.percentToStay = percentToStay;
        randGenForAction = new Random();
    }

    public String nextAction(){
        double randDub = randGenForAction.nextDouble();
        if (randDub < percentToStay) return "stay";
        else return "move";
    }

    public Location getPuppyLocation() {
        return puppyLocation;
    }

    public void setPuppyLocation(Location puppyLocation) {
        this.puppyLocation = puppyLocation;
    }
}
