package Game.Classes;

import java.util.ArrayList;
import java.util.List;

public class ShipPlacementManager {
    private static ShipPlacementManager instance;
    private List<ShipPlacement> placedShips;

    private ShipPlacementManager() { // Hacer el constructor privado
        placedShips = new ArrayList<>();
    }

    public static ShipPlacementManager getInstance() {
        if (instance == null) {
            instance = new ShipPlacementManager();
        }
        return instance;
    }

    public void initialize() { // Método agregado
        placedShips = new ArrayList<>();
    }

    public void addShipPlacement(int row, int col, String type, int size) {
        placedShips.add(new ShipPlacement(row, col, type, size));
    }

    public List<ShipPlacement> getPlacedShips() {
        return new ArrayList<>(placedShips); // Devolver copia para evitar modificaciones accidentales
    }

    public void clearPlacements() {
        placedShips.clear();
    }

    public static class ShipPlacement {
        public int row, column, size;
        public String type;

        public ShipPlacement(int row, int column, String type, int size) {
            this.row = row;
            this.column = column;
            this.type = type;
            this.size = size;
        }
    }
}