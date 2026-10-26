package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitType;
/**
 * Represents an Ambulance unit in the City Rescue System.
 *
 * <p>An Ambulance is a specialised {@link Unit} that responds exclusively
 * to medical incidents.
 * </p>
 *
 * <p>According to the system specification, an Ambulance requires
 * a fixed number of ticks to resolve an incident, regardless of severity.
 * </p>
 */
public class Ambulance extends Unit {

    /**
     * Constructs a new Ambulance assigned to a station.
     *
     * @param unitId the unique identifier of the ambulance
     * @param station the station the ambulance is based at
     */
    public Ambulance(int unitId, Station station) {
        super(unitId, station);
    }

    /**
     * Determines whether this unit can handle a given incident type.
     *
     * @param type the type of incident
     * @return true if the incident type is MEDICAL {@link IncidentType#MEDICAL}, false otherwise
     */
    @Override
    public boolean canHandle(IncidentType type) {
        return type == IncidentType.MEDICAL;
    }

    /**
     * Returns the number of ticks required to resolve a medical incident.
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
        return 2; // fixed per spec
    }

    /**
     * Returns the unit type.
     *
     * @return UnitType.AMBULANCE
     */
    @Override
    public UnitType getType() {
        return UnitType.AMBULANCE;
    }
}
