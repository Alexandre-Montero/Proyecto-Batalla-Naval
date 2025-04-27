package Game.Classes;

import java.util.ArrayList;
import java.util.List;

public class ShipComputerPlacementManager {
    
    private static ShipComputerPlacementManager instance;

    private List<ShipPlacement> computerShips = new ArrayList<>();

    // Constructor privado (buena práctica para Singleton)
    private ShipComputerPlacementManager() {}

    public static ShipComputerPlacementManager getInstance() {
        if (instance == null) {
            instance = new ShipComputerPlacementManager();
        }
        return instance;
    }

    public void addShipsComputer(ShipPlacement ship) {
        computerShips.add(ship);
    }

    public List<ShipPlacement> getShips() {
        return new ArrayList<>(computerShips);
    }

    public void clearShips() {
        computerShips.clear();
    }

    public static class ShipPlacement {
        public int row;
        public int column;
        public String type;
        public int size;
        public boolean horizontal;

        public ShipPlacement(int row, int column, String type, int size, boolean horizontal) {
            this.row = row;
            this.column = column;
            this.type = type;
            this.size = size;
            this.horizontal = horizontal;
        }
    }
}