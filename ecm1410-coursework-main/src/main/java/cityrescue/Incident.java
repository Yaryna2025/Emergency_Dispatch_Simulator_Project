package cityrescue;

import cityrescue.enums.IncidentStatus;
import cityrescue.enums.IncidentType;
/**
 * Represents an emergency incident in the City Rescue system.
 *
 * <p>An Incident has:
 *  - A unique incident ID
 *  - A type (e.g. FIRE, MEDICAL, CRIME)
 *  - A location defined by x and y coordinates
 *  - A severity level
 *  - A current status
 *  - An assigned emergency unit (if any)
 *  </p>
 * <p>Incidents are initially created with status {@link IncidentStatus#REPORTED}
 * and no assigned unit (unitId = -1).</p>
 */
public class Incident {
    private final int incidentId;
    private final IncidentType type;
    private final int x, y;
    private int severity;
    private IncidentStatus status;
    private int unitId; // -1 if none

    /**
     * Constructs a new Incident.
     *
     * @param incidentId unique identifier for the incident
     * @param type the type of incident
     * @param x the x-coordinate of the incident location
     * @param y the y-coordinate of the incident location
     * @param severity the severity level of the incident
     */

    public Incident(int incidentId, IncidentType type, int x, int y, int severity){
        this.incidentId = incidentId;
        this.type = type;
        this.x = x;
        this.y = y;
        this.severity = severity;
        status = IncidentStatus.REPORTED;
        unitId = -1;
    }
    /** @return the unique incident ID */
    public int getIncidentId(){ return incidentId; }

    /** @return the type of the incident */
    public IncidentType getType(){ return type; }

    /** @return the x-coordinate of the incident */
    public int getX(){ return x; }

    /** @return the y-coordinate of the incident */
    public int getY(){ return y; }

    /** @return the current severity level */
    public int getSeverity(){ return severity; }

    /**
     * Sets the severity of the incident.
     *
     * @param severity the new severity level
     */
    public void setSeverity(int severity){ this.severity=severity; }

    /** @return the current status of the incident */
    public IncidentStatus getStatus(){ return status; }

    /**
     * Sets the current status of the incident.
     *
     * @param status the new incident status
     */
    public void setStatus(IncidentStatus status){ this.status=status; }

    /**
     * @return the ID of the assigned unit, or -1 if no unit is assigned
     */
    public int getUnitId(){ return unitId; }

    /**
     * Assigns a unit to this incident.
     *
     * @param unitId the ID of the unit to assign,
     *               or -1 to unassign
     */
    public void setUnitId(int unitId){ this.unitId=unitId; }

    /**
     * Escalates the incident by updating its severity.
     *
     * @param severity the new severity level
     */
    public void escalate(int severity){ this.severity=severity; }

    /**
     * Determines whether the incident is still active.
     *
     * @return true if the incident is not RESOLVED or CANCELLED, false otherwise
     */
    public boolean isActive(){ return status!=IncidentStatus.RESOLVED && status!=IncidentStatus.CANCELLED; }
}
