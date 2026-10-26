package cityrescue;

import cityrescue.exceptions.InvalidCapacityException;


/**
 * Represents a rescue station in the City Rescue system.
 *
 * <p>A Station has:
 *  - A unique identifier
 *  - A name
 *  - A fixed location (x, y) on the city map {@link CityMap}
 *  - A capacity limiting the number of units it can hold
 * </p>
 * <p>Each station maintains an internal array of {@link Unit} objects.
 * The maximum possible number of units per station is limited by
 * {@code MAX_UNITS_PER_STATION}.
 * </p>
 *
 * <p>The current capacity cannot be set below the number of units
 * already assigned to the station.
 * </p>
 */

public class Station {
    private final int stationId;
    private final String name;
    private final int x, y;
    private int capacity;
    private Unit[] units;
    private int unitCount;

    private static final int MAX_UNITS_PER_STATION = 50;

    /**
     * Constructs a new Station.
     *
     * <p>The station is initialised with maximum capacity and
     * no assigned units.
     * </p>
     *
     * @param stationId unique identifier of the station
     * @param name the name of the station
     * @param x the x-coordinate of the station
     * @param y the y-coordinate of the station
     */
    public Station(int stationId, String name, int x, int y){
        this.stationId = stationId;
        this.name = name;
        this.x = x;
        this.y = y;
        this.capacity = MAX_UNITS_PER_STATION;
        units = new Unit[MAX_UNITS_PER_STATION];
        unitCount = 0;
    }

    /** @return the unique station ID */
    public int getStationId(){ return stationId; }

    /** @return the station name */
    public String getName(){ return name; }

    /** @return the x-coordinate of the station */
    public int getX(){ return x; }

    /** @return the y-coordinate of the station */
    public int getY(){ return y; }

    /** @return the current capacity of the station */
    public int getCapacity(){ return capacity; }

    /**
     * Sets a new capacity for the station.
     *
     * @param capacity the new maximum number of units allowed
     * @throws InvalidCapacityException if the capacity is less than or equal to zero,
     *                                  or less than the current number of units
     */
    public void setCapacity(int capacity) throws InvalidCapacityException {
        if(capacity<=0 || capacity<unitCount) throw new InvalidCapacityException("Invalid capacity");
        this.capacity = capacity;
    }

    /** @return the number of units currently assigned to the station */
    public int getUnitCount(){ return unitCount; }

    /**
     * Adds a unit to the station.
     *
     * @param unit the unit to add
     * @throws IllegalStateException if the station is already at full capacity
     */
    public void addUnit(Unit unit){
        if(unitCount>=capacity) throw new IllegalStateException("Station full");
        units[unitCount++] = unit;
    }

    /**
     * Removes a unit from the station.
     *
     * <p>If the unit is not found, no action is taken.
     * </p>
     *
     * @param unit the unit to remove
     */
    public void removeUnit(Unit unit){
        int index=-1;
        for(int i=0;i<unitCount;i++){ if(units[i].getUnitId()==unit.getUnitId()){ index=i; break; } }
        if(index==-1) return;
        for(int i=index;i<unitCount-1;i++) units[i]=units[i+1];
        units[--unitCount]=null;
    }

    /**
     * Returns a copy of the units currently assigned to the station.
     *
     * <p>This method returns a defensive copy to preserve encapsulation
     * and prevent external modification of the internal array.
     * </p>
     *
     * @return an array containing the assigned units
     */
    public Unit[] getUnits(){
        Unit[] arr = new Unit[unitCount];
        System.arraycopy(units,0,arr,0,unitCount);
        return arr;
    }
}
