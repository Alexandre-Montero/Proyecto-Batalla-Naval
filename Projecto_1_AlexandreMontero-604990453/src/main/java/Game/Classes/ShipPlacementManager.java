package Game.Classes;

import java.util.*;

public class ShipPlacementManager {
    private static ShipPlacementManager instance;
    private Map<String, List<ShipPlacement>> playerShips;

    private ShipPlacementManager() {
        playerShips = new HashMap<>();
    }

    public static ShipPlacementManager getInstance() {
        if (instance == null) {
            instance = new ShipPlacementManager();
        }
        return instance;
    }

    public void initializePlayer(String playerId) {
        playerShips.put(playerId, new ArrayList<>());
    }

    public void addShipPlacement(String playerId, int row, int col, String type, int size, boolean horizontal) {
        List<ShipPlacement> ships = playerShips.getOrDefault(playerId, new ArrayList<>());
        ships.add(new ShipPlacement(row, col, type, size, horizontal));
        playerShips.put(playerId, ships);
    }

    public List<ShipPlacement> getPlacedShips(String playerId) {
        return new ArrayList<>(playerShips.getOrDefault(playerId, new ArrayList<>()));
    }

    public void clearPlacements(String playerId) {
        playerShips.remove(playerId);
    }

    // NUEVOS MÉTODOS NECESARIOS

    public List<ShipPlacement> getPlacedShipsForPlayer(String playerId) {
        return getPlacedShips(playerId);
    }

    public void addShipPlacementForPlayer(String playerId, ShipPlacement sp) {
        List<ShipPlacement> ships = playerShips.getOrDefault(playerId, new ArrayList<>());
        ships.add(sp);
        playerShips.put(playerId, ships);
    }

    public void removeShipPlacementForPlayer(String playerId, ShipPlacement sp) {
        List<ShipPlacement> ships = playerShips.getOrDefault(playerId, new ArrayList<>());
        ships.remove(sp);
        playerShips.put(playerId, ships);
    }

    // Clase interna para representar un barco colocado
    public static class ShipPlacement {
        public int row, column, size;
        public String type;
        public boolean horizontal;  // Cambiado de isVertical a horizontal

        public ShipPlacement(int row, int column, String type, int size, boolean horizontal) {
            this.row = row;
            this.column = column;
            this.type = type;
            this.size = size;
            this.horizontal = horizontal;
        }

        // Modificación de equals y hashCode para tener en cuenta el campo horizontal
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ShipPlacement that = (ShipPlacement) obj;
            return row == that.row && column == that.column && size == that.size &&
                   horizontal == that.horizontal && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column, type, size, horizontal);
        }
    }
}