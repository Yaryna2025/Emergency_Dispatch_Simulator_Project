package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitType;
/**
 * Represents a Police Car unit in the City Rescue system.
 *
 * <p>A PoliceCar is a specialised {@link Unit} that responds exclusively
 * to crime-related incidents.
 * </p>
 *
 * <p>According to the system specification, a PoliceCar requires
 * a fixed number of ticks to resolve an incident, regardless of severity.
 * </p>
 */
public class PoliceCar extends Unit {

    /**
     * Constructs a new PoliceCar assigned to a station.
     *
     * @param unitId the unique identifier of the police car
     * @param station the station the police car is based at
     */
    public PoliceCar(int unitId, Station station) {
        super(unitId, station);
    }

    /**
     * Determines whether this unit can handle a given incident type.
     *
     * @param type the type of incident
     * @return true if the incident type is CRIME {@link IncidentType#CRIME}, false otherwise
     */
    @Override
    public boolean canHandle(IncidentType type) {
        return type == IncidentType.CRIME;
    }

    /**
     * Returns the number of ticks required to resolve a crime incident.
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
        return 3; // fixed per spec
    }

    /**
     * Returns the unit type.
     *
     * @return UnitType.POLICE_CAR
     */
    @Override
    public UnitType getType() {
        return UnitType.POLICE_CAR;
    }
}
