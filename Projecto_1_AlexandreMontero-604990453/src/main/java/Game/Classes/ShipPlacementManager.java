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

    public void addShipPlacement(String playerId, int row, int col, String type, int size, boolean isVertical) {
        List<ShipPlacement> ships = playerShips.getOrDefault(playerId, new ArrayList<>());
        ships.add(new ShipPlacement(row, col, type, size, isVertical));
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
        public boolean isVertical;

        public ShipPlacement(int row, int column, String type, int size, boolean isVertical) {
            this.row = row;
            this.column = column;
            this.type = type;
            this.size = size;
            this.isVertical = isVertical;
        }

        // Opcional: equals y hashCode para eliminar correctamente los objetos en remove
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ShipPlacement that = (ShipPlacement) obj;
            return row == that.row && column == that.column && size == that.size &&
                    isVertical == that.isVertical && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column, type, size, isVertical);
        }
    }
}