package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitType;
/**
 * Represents a Fire Engine unit in the City Rescue System.
 *
 * <p>A FireEngine is a specialised {@link Unit} that responds exclusively
 * to fire-related incidents.
 * </p>
 *
 * <p>According to the system specification, a FireEngine requires
 * a fixed number of ticks to resolve an incident, regardless of severity.
 * </p>
 */
public class FireEngine extends Unit {

    /**
     * Constructs a new FireEngine assigned to a station.
     *
     * @param unitId the unique identifier of the fire engine
     * @param station the station the fire engine is based at
     */
    public FireEngine(int unitId, Station station) {
        super(unitId, station);
    }

    /**
     * Determines whether this unit can handle a given incident type.
     *
     * @param type the type of incident
     * @return true if the incident type is FIRE {@link IncidentType#FIRE}, false otherwise
     */
    @Override
    public boolean canHandle(IncidentType type) {
        return type == IncidentType.FIRE;
    }

    /**
     * Returns the number of ticks required to resolve a fire incident.
     *
     * <p>This implementation returns a fixed value as defined
     * in the system specification.
     * </p>
     *
     * @param severity the severity level of the incident
     * @return the number of ticks required to resolve the incident
     */
    @Override
    public int getTicksToResolve(int severity) {
        return 4; // fixed per spec
    }

    /**
     * Returns the unit type.
     *
     * @return UnitType.FIRE_ENGINE
     */
    @Override
    public UnitType getType() {
        return UnitType.FIRE_ENGINE;
    }
}
