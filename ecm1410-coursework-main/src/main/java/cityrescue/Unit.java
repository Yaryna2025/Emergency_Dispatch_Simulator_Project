package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitStatus;
import cityrescue.enums.UnitType;
/**
 * Represents an emergency response unit in the City Rescue System.
 *
 * <p>A Unit is assigned to a {@link Station}, has a location on the grid,
 * and may be dispatched to handle incidetns.
 * </p>
 *
 * <p>Each concrete subclass {@link FireEngine}, {@link PoliceCar}, {@link Ambulance}
 * must define:
 *  - What types of incidents it can handle
 *  - How long it takes to resolve an incident
 *  - Its specific unit type
 * </p>
 *
 * <p>Units are initially created at their station location with:
 *  - Status {@link UnitStatus#IDLE}
 *  - No assigned incident (incidentId = -1)
 *  - Not out of service
 * </p>
 */
public abstract class Unit {
    private final int unitId;
    private Station station;
    private int x, y;
    private UnitStatus status;
    private boolean outOfService;
    private int incidentId;
    private int workRemaining;

    /**
     * Constructs a new Unit assigned to a station.
     *
     * @param unitId unique identifier for the unit
     * @param station the station this unit belongs to
     */
    public Unit(int unitId, Station station){
        this.unitId=unitId;
        this.station=station;
        this.x=station.getX();
        this.y=station.getY();
        status=UnitStatus.IDLE;
        outOfService=false;
        incidentId=-1;
        workRemaining=0;
    }

    /**
     * Determines whether this unit can handle a given type of incident.
     *
     * @param type the incident type
     * @return true if this unit can respond to the given type, false otherwise
     */
    public abstract boolean canHandle(IncidentType type);

    /**
     * Calculates the number of ticks required to resolve
     * an incident of a given severity.
     *
     * @param severity the severity level of the incident
     * @return the number of simulation ticks required to resolve it
     */
    public abstract int getTicksToResolve(int severity);

    /**
     * Returns the specific type of this unit.
     *
     * @return the unit type
     */
    public abstract UnitType getType();

    /** @return the unique unit ID */
    public int getUnitId(){ return unitId; }

    /** @return the station this unit belongs to */
    public Station getStation(){ return station; }

    /**
     * Sets the station this unit belongs to.
     *
     * @param s the new station
     */
    public void setStation(Station s){ this.station=s; }

    /** @return the current x-coordinate of the unit */
    public int getX(){ return x; }

    /** @return the current y-coordinate of the unit */
    public int getY(){ return y; }

    /**
     * Sets the x-coordinate of the unit.
     *
     * @param x the new x-coordinate
     */
    public void setX(int x){ this.x=x; }

    /**
     * Sets the y-coordinate of the unit.
     *
     * @param y the new y-coordinate
     */
    public void setY(int y){ this.y=y; }

    /** @return the current status of the unit */
    public UnitStatus getStatus(){ return status; }

    /**
     * Sets the unit's current status.
     *
     * @param s the new status
     */
    public void setStatus(UnitStatus s){ status=s; }

    /**
     * Indicates whether the unit is out of service.
     *
     * @return true if the unit is unavailable for dispatch
     */
    public boolean isOutOfService(){ return outOfService; }

    /**
     * Sets whether the unit is out of service.
     *
     * @param oos true if the unit should be marked unavailable
     */
    public void setOutOfService(boolean oos){ outOfService=oos; }

    /**
     * @return the ID of the currently assigned incident,
     * or -1 if none is assigned
     */
    public int getIncidentId(){ return incidentId; }

    /**
     * Assigns an incident to this unit.
     *
     * @param id the incident ID, or -1 to clear assignment
     */
    public void setIncidentId(int id){ incidentId=id; }

    /**
     * @return the remaining work (in ticks) to complete the current incident
     */
    public int getWorkRemaining(){ return workRemaining; }

    /**
     * Sets the remaining work time for the current incident.
     *
     * @param w the number of ticks remaining
     */
    public void setWorkRemaining(int w){ workRemaining=w; }
}
