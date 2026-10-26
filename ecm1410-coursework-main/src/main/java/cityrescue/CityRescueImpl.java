package cityrescue;

import java.util.Arrays;

import cityrescue.enums.IncidentStatus;
import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitStatus;
import cityrescue.enums.UnitType;
import cityrescue.exceptions.CapacityExceededException;
import cityrescue.exceptions.IDNotRecognisedException;
import cityrescue.exceptions.InvalidCapacityException;
import cityrescue.exceptions.InvalidGridException;
import cityrescue.exceptions.InvalidLocationException;
import cityrescue.exceptions.InvalidNameException;
import cityrescue.exceptions.InvalidSeverityException;
import cityrescue.exceptions.InvalidUnitException;

/**
 * Fully spec-compliant CityRescue implementation.
 *
 * Responsibilities:
 * - Manage a grid-based city with obstacles.
 * - Manage Stations and Units, their assignments and movements.
 * - Track and handle Incidents, including reporting, dispatch, escalation, and resolution.
 * - Simulate the passage of time via ticks.
 */
public class CityRescueImpl implements CityRescue {

    // FIELDS

    private CityMap cityMap;      // The city grid (width x height) and obstacle information
    private int tick;             // Simulation tick counter

    // Maximum allowed quantities
    private static final int MAX_STATIONS = 20;
    private static final int MAX_UNITS = 50;
    private static final int MAX_INCIDENTS = 200;

    // Station storage
    private Station[] stations = new Station[MAX_STATIONS];
    private int stationCount = 0;    // Current number of stations
    private int nextStationId = 1;   // Next auto-incrementing station ID

    // Unit storage
    private Unit[] units = new Unit[MAX_UNITS];
    private int unitCount = 0;       // Current number of units
    private int nextUnitId = 1;      // Next auto-incrementing unit ID

    // Incident storage
    private Incident[] incidents = new Incident[MAX_INCIDENTS];
    private int incidentCount = 0;   // Current number of incidents
    private int nextIncidentId = 1;  // Next auto-incrementing incident ID

    // INITIALISATION

    /**
     * Initialize the city grid and reset all data structures.
     * @param width grid width
     * @param height grid height
     * @throws InvalidGridException if width or height <= 0
     */
    @Override
    public void initialise(int width, int height) throws InvalidGridException {
        if (width <= 0 || height <= 0)
            throw new InvalidGridException("Grid size must be more than 0");

        this.cityMap = new CityMap(width, height); // Create city grid
        this.tick = 0;                             // Reset simulation tick

        // Reset all data arrays and counters
        stations = new Station[MAX_STATIONS];
        stationCount = 0;
        nextStationId = 1;

        units = new Unit[MAX_UNITS];
        unitCount = 0;
        nextUnitId = 1;

        incidents = new Incident[MAX_INCIDENTS];
        incidentCount = 0;
        nextIncidentId = 1;
    }

    /**
     * Asks the city how big it is.
     * @return gridSize
     */

    @Override
    public int[] getGridSize() { return cityMap.getSize(); } // Return [width, height]

    // OBSTACLES

    /**
     * Add a blocking obstacle at a grid coordinate.
     * @param x x coordinate of obstacle
     * @param y y coordinate of obstacle
     * @throws InvalidLocationException if the location is out of bounds
     */
    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException {
        if (!cityMap.isInBounds(x, y))
            throw new InvalidLocationException("Out of bounds");
        cityMap.setBlocked(x, y, true);
    }

    /**
     * Remove an obstacle at a grid coordinate.
     * @param x x coordinate of obstacle
     * @param y y coordinate of obstacle
     * @throws InvalidLocationException if the location is out of bounds
     */
    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException {
        if (!cityMap.isInBounds(x, y))
            throw new InvalidLocationException("Out of bounds");
        cityMap.setBlocked(x, y, false);
    }

    // STATIONS

    /**
     * Add a new station to the city.
     * @param name station name
     * @param x grid x-coordinate
     * @param y grid y-coordinate
     * <p>
     * @return station ID
     * </p>
     * @throws InvalidNameException if name is null or empty
     * @throws InvalidLocationException if location is invalid or blocked
     */
    @Override
    public int addStation(String name, int x, int y)
            throws InvalidNameException, InvalidLocationException {
        // This checks if it meets certain condictions and throws the appropriate exception
        if (name == null || name.trim().isEmpty())
            throw new InvalidNameException("Invalid station name");
        if (!cityMap.isInBounds(x, y) || cityMap.isBlocked(x, y))
            throw new InvalidLocationException("Invalid location");
        if (stationCount >= MAX_STATIONS)
            throw new CapacityExceededException("Max stations reached");

        // Make a new station, increment the station ID, give it a name, and location
        Station s = new Station(nextStationId++, name, x, y);
        // Set the index of the next station ID within the stations array to be the newly created station
        stations[stationCount++] = s;
        return s.getStationId();    // Returns the stationID
    }

    /**
     * Remove a station if it has no units.
     * @param stationId ID of the station
     * @throws IllegalStateException if station has units assigned
     */
    @Override
    public void removeStation(int stationId)
            throws IDNotRecognisedException, IllegalStateException {
        Station s = findStation(stationId);
        // Checks if the station has units
        if (s.getUnitCount() > 0)
            throw new IllegalStateException("Station still has units");

        // Find station index and shift array to remove it
        int idx = -1;
        for (int i = 0; i < stationCount; i++)
            if (stations[i].getStationId() == stationId) idx = i;

        for (int i = idx; i < stationCount - 1; i++)
            stations[i] = stations[i + 1];

        stations[--stationCount] = null;
    }

    /**
     * Set a station's maximum capacity.
     * @param stationId ID of the station
     * @param maxUnits the maximum units the station can have
     * @throws InvalidCapacityException if new capacity < current unit count or <= 0
     */
    @Override
    public void setStationCapacity(int stationId, int maxUnits)
            throws IDNotRecognisedException, InvalidCapacityException {
        Station s = findStation(stationId);
        if (maxUnits <= 0 || maxUnits < s.getUnitCount())
            throw new InvalidCapacityException("Invalid capacity");
        s.setCapacity(maxUnits);
    }

    /**
     * Return sorted array of station IDs.
     * @return stationIDs
     */
    @Override
    public int[] getStationIds() {
        int[] ids = new int[stationCount];
        for (int i = 0; i < stationCount; i++)
            ids[i] = stations[i].getStationId();
        Arrays.sort(ids);
        return ids;
    }

    // UNITS

    /**
     * Add a unit to a station.
     *
     * @param stationId The ID of the station
     * @param type the type of the unit
     * <p>
     * @return unitID
     * </p>
     * @throws InvalidUnitException if type is null or unknown
     * @throws IllegalStateException if station is full
     * @throws IDNotRecognisedException if the ID is not recognised
     */
    @Override
    public int addUnit(int stationId, UnitType type)
            throws IDNotRecognisedException, InvalidUnitException, IllegalStateException {
        if (type == null) throw new InvalidUnitException("Unit type cannot be null");

        Station s = findStation(stationId);
        if (s.getUnitCount() >= s.getCapacity()) throw new IllegalStateException("Station full");
        if (unitCount >= MAX_UNITS) throw new CapacityExceededException("Max units reached");

        Unit u;
        int id = nextUnitId++;
        switch (type) {
            case AMBULANCE -> u = new Ambulance(id, s);
            case FIRE_ENGINE -> u = new FireEngine(id, s);
            case POLICE_CAR -> u = new PoliceCar(id, s);
            default -> throw new InvalidUnitException("Unknown unit type");
        }

        units[unitCount++] = u;
        s.addUnit(u);
        return id;
    }

    /**
     * Remove a unit if idle.
     * @param unitId the ID of the unit
     * @throws IDNotRecognisedException if the ID is not recognised
     * @throws IllegalStateException if the unit is not IDLE
     */
    @Override
    public void decommissionUnit(int unitId)
            throws IDNotRecognisedException, IllegalStateException {
        Unit u = findUnit(unitId);
        if (u.getStatus() != UnitStatus.IDLE)
            throw new IllegalStateException("Only IDLE units can be decommissioned");

        Station s = u.getStation();
        s.removeUnit(u);

        // Remove from global units array
        int idx = -1;
        for (int i = 0; i < unitCount; i++)
            if (units[i].getUnitId() == unitId) idx = i;

        for (int i = idx; i < unitCount - 1; i++)
            units[i] = units[i + 1];

        units[--unitCount] = null;
    }

    /**
     * Transfer a unit to a new station.
     * Only IDLE units can be transferred.
     * @param unitId the ID of the unit
     * @param newStationId the ID of the new station the unit will go to
     * @throws IDNotRecognisedException if the ID is not recognised
     * @throws IllegalStateException if the unit is not IDLE
     */
    @Override
    public void transferUnit(int unitId, int newStationId)
            throws IDNotRecognisedException, IllegalStateException {
        Unit u = findUnit(unitId);
        if (u.getStatus() != UnitStatus.IDLE)
            throw new IllegalStateException("Only IDLE units can be transferred");

        Station ns = findStation(newStationId);
        if (ns.getUnitCount() >= ns.getCapacity())
            throw new IllegalStateException("Station full");

        u.getStation().removeUnit(u);
        ns.addUnit(u);
        u.setStation(ns);
        u.setX(ns.getX());
        u.setY(ns.getY());
    }

    /**
     * Set unit out of service (or back in service).
     * Only IDLE units can be set out of service.
     * @param unitId the ID of the unit
     * @param oos out of service TRUE or FALSE
     * @throws IDNotRecognisedException if the ID is not recognised
     * @throws IllegalStateException if the unit is not IDLE
     */
    @Override
    public void setUnitOutOfService(int unitId, boolean oos)
            throws IDNotRecognisedException, IllegalStateException {
        Unit u = findUnit(unitId);
        if (oos && u.getStatus() != UnitStatus.IDLE)
            throw new IllegalStateException("Only IDLE units can be set out of service");

        u.setOutOfService(oos);
        u.setStatus(oos ? UnitStatus.OUT_OF_SERVICE : UnitStatus.IDLE);
    }

    /**
     * Return sorted array of all unit IDs.
     * @return unitIds
     */
    @Override
    public int[] getUnitIds() {
        int[] ids = new int[unitCount];
        for (int i = 0; i < unitCount; i++) ids[i] = units[i].getUnitId();
        Arrays.sort(ids);
        return ids;
    }

    /**
     * Return a string representation of a unit.
     * Includes type, home station, location, status, and assigned incident.
     * @param unitId the ID for the unit
     * <p>
     * @return string representation of the unit
     * </p>
     * @throws IDNotRecognisedException if the ID is not recognised
     */
    @Override
    public String viewUnit(int unitId) throws IDNotRecognisedException {
        Unit u = findUnit(unitId);
        StringBuilder sb = new StringBuilder();
        sb.append("U#").append(u.getUnitId())
          .append(" TYPE=").append(u.getType())
          .append(" HOME=").append(u.getStation().getStationId())
          .append(" LOC=(").append(u.getX()).append(",").append(u.getY()).append(")")
          .append(" STATUS=").append(u.getStatus());

        if (u.getIncidentId() == -1) {
            sb.append(" INCIDENT=-");
        } else {
            sb.append(" INCIDENT=").append(u.getIncidentId());
            if (u.getStatus() == UnitStatus.AT_SCENE) {
                sb.append(" WORK=").append(u.getWorkRemaining());
            }
        }
        return sb.toString();
    }

    // INCIDENTS

    /**
     * Report a new incident in the city.
     * @param type the type of the incident
     * @param severity an integer representing the severity
     * @param x the x coordinate of the incident
     * @param y the y coordinate of the incident
     * <p>
     * @return a call to the {@link getIncidentId} method
     * </p>
     * @throws InvalidLocationException if location is invalid
     * @throws InvalidSeverityException if severity <1 or >5 or type is null
     */
    @Override
    public int reportIncident(IncidentType type, int severity, int x, int y)
            throws InvalidLocationException, InvalidSeverityException {
        if (type == null) throw new InvalidSeverityException("Incident type cannot be null");
        if (!cityMap.isInBounds(x, y) || cityMap.isBlocked(x, y))
            throw new InvalidLocationException("Invalid location");
        if (severity < 1 || severity > 5)
            throw new InvalidSeverityException("Severity 1-5");
        if (incidentCount >= MAX_INCIDENTS)
            throw new CapacityExceededException("Max incidents reached");

        Incident inc = new Incident(nextIncidentId++, type, x, y, severity);
        incidents[incidentCount++] = inc;
        return inc.getIncidentId();
    }

    /**
     * Cancel an incident if it is reported or dispatched.
     * Resets any assigned units.
     * @param incidentId the ID of the incident
     * <p>
     * @throws IDNotRecognisedException if the ID is not recognised
     * @throws IllegalStateException if the incident status isn't REPORTED or DISPATCHED
     * </p>
     */
    @Override
    public void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException {
        Incident inc = findIncident(incidentId);
        if (inc.getStatus() != IncidentStatus.REPORTED && inc.getStatus() != IncidentStatus.DISPATCHED)
            throw new IllegalStateException("Cannot cancel incident");

        int uid = inc.getUnitId();
        if (uid != -1) {
            Unit u = findUnit(uid);
            u.setStatus(UnitStatus.IDLE);
            u.setIncidentId(-1);
            u.setWorkRemaining(0);
        }

        inc.setStatus(IncidentStatus.CANCELLED);
        inc.setUnitId(-1);
    }

    /**
     * Escalate an incident to a new severity level.
     * If a unit is already on scene, update its work remaining.
     * @param incidentId the ID of the incident
     * @param newSeverity the new severity of the incident
     * <p>
     * @throws IDNotRecognisedException if the ID is not recognised
     * @throws InvalidSeverityException if severity <1 or >5 or type is null
     * @throws IllegalStateException if the incident status is RESOLVED or CANCELLED
     * </p>
     */
    @Override
    public void escalateIncident(int incidentId, int newSeverity)
            throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException {
        if (newSeverity < 1 || newSeverity > 5) throw new InvalidSeverityException("Severity 1-5");
        Incident inc = findIncident(incidentId);
        if (inc.getStatus() == IncidentStatus.RESOLVED || inc.getStatus() == IncidentStatus.CANCELLED)
            throw new IllegalStateException("Cannot escalate resolved/cancelled incident");

        inc.setSeverity(newSeverity);

        int uid = inc.getUnitId();
        if (uid != -1) {
            Unit u = findUnit(uid);
            if (u.getStatus() == UnitStatus.AT_SCENE)
                u.setWorkRemaining(u.getTicksToResolve(newSeverity));
        }
    }

    /**
     * Return sorted array of all incident IDs.
     * @return sorted array of all incidetn IDs
     */
    @Override
    public int[] getIncidentIds() {
        int[] ids = new int[incidentCount];
        for (int i = 0; i < incidentCount; i++) ids[i] = incidents[i].getIncidentId();
        Arrays.sort(ids);
        return ids;
    }

    /**
     * Return a string representation of an incident.
     * Includes type, severity, location, status, and assigned unit.
     * @param incidentId the ID of the incident
     * <p>
     * @return a string representation of the incident
     * </p>
     * @throws IDNotRecognisedException if the ID is not recognised
     */
    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException {
        Incident inc = findIncident(incidentId);
        StringBuilder sb = new StringBuilder();
        sb.append("I#").append(inc.getIncidentId())
          .append(" TYPE=").append(inc.getType())
          .append(" SEV=").append(inc.getSeverity())
          .append(" LOC=(").append(inc.getX()).append(",").append(inc.getY()).append(")")
          .append(" STATUS=").append(inc.getStatus())
          .append(" UNIT=").append(inc.getUnitId() == -1 ? "-" : inc.getUnitId());
        return sb.toString();
    }

    // DISPATCH

    /**
     * Dispatch available IDLE units to REPORTED incidents.
     * Sets incident DISPATCHED and unit EN_ROUTE.
     * <p>Units are chosen based on shortest Manhattan distance, then lowest unit ID.
     * </p>
     */
    @Override
    public void dispatch() {
        int[] sortedIncidentIds = getIncidentIds();
        for (int iid : sortedIncidentIds) {
            Incident inc;
            try { inc = findIncident(iid); } catch(Exception e){ continue; }
            if (inc.getStatus() != IncidentStatus.REPORTED) continue;

            Unit best = null;
            int bestDist = Integer.MAX_VALUE;
            int[] sortedUnitIds = getUnitIds();

            for (int uid : sortedUnitIds) {
                Unit u;
                try { u = findUnit(uid); } catch (IDNotRecognisedException e) { continue; }

                if (u.getStatus() != UnitStatus.IDLE) continue;
                if (u.isOutOfService()) continue;
                if (!u.canHandle(inc.getType())) continue;

                int dist = Math.abs(u.getX()-inc.getX()) + Math.abs(u.getY()-inc.getY());
                if(best==null || dist < bestDist) {
                    best=u; bestDist=dist;
                } else if(dist==bestDist){
                    if(u.getUnitId() < best.getUnitId()) best=u;
                    else if(u.getUnitId() == best.getUnitId() && u.getStation().getStationId() < best.getStation().getStationId())
                        best=u;
                }
            }

            if (best != null) {
                best.setStatus(UnitStatus.EN_ROUTE);
                best.setIncidentId(inc.getIncidentId());
                inc.setStatus(IncidentStatus.DISPATCHED);
                inc.setUnitId(best.getUnitId());
            }
        }
    }

    //  TICK

    /**
     * Advance simulation by one tick:
     * - Move EN_ROUTE units towards their incident.
     * - Mark arrivals
     * - Reduce work remaining for AT_SCENE units.
     * - Resolve incidents when work is done.
     */
    @Override
    public void tick() {

        // 0. Tick increases FIRST
        tick++;

        // Snapshot of unit IDs in ascending order for determinism
        int[] sortedUnitIds = getUnitIds();

        // STEP 1: Move all EN_ROUTE units
        for (int uid : sortedUnitIds) {
            Unit u;
            try { u = findUnit(uid); } catch (IDNotRecognisedException e) { continue; }

            if (u.getStatus() == UnitStatus.EN_ROUTE) {
                int incidentId = u.getIncidentId();
                if (incidentId == -1) continue;

                Incident inc;
                try { inc = findIncident(incidentId); } catch (IDNotRecognisedException e) { continue; }

                int[] newPos = moveUnitTowards(u, inc.getX(), inc.getY());
                u.setX(newPos[0]);
                u.setY(newPos[1]);
            }
        }

        // STEP 2: Mark arrivals (units that reached their incident this tick)
        for (int uid : sortedUnitIds) {
            Unit u;
            try { u = findUnit(uid); } catch (IDNotRecognisedException e) { continue; }

            if (u.getStatus() == UnitStatus.EN_ROUTE) {
                int incidentId = u.getIncidentId();
                if (incidentId == -1) continue;

                Incident inc;
                try { inc = findIncident(incidentId); } catch (IDNotRecognisedException e) { continue; }

                if (u.getX() == inc.getX() && u.getY() == inc.getY()) {
                    u.setStatus(UnitStatus.AT_SCENE);
                    // Set work remaining according to severity & unit type
                    u.setWorkRemaining(u.getTicksToResolve(inc.getSeverity()));
                    inc.setStatus(IncidentStatus.IN_PROGRESS);
                }
            }
        }

        // STEP 3: Process work for units that were already AT_SCENE at the start of the tick
        for (int uid : sortedUnitIds) {
            Unit u;
            try { u = findUnit(uid); } catch (IDNotRecognisedException e) { continue; }

            if (u.getStatus() == UnitStatus.AT_SCENE) {
                // Decrement work remaining
                int remaining = Math.max(0, u.getWorkRemaining() - 1);
                u.setWorkRemaining(remaining);
            }
        }

        // STEP 4: Resolve incidents where workRemaining reached 0
        int[] sortedIncidentIds = getIncidentIds();
        for (int iid : sortedIncidentIds) {
            Incident inc;
            try { inc = findIncident(iid); } catch (IDNotRecognisedException e) { continue; }

            if (inc.getStatus() == IncidentStatus.IN_PROGRESS) {
                int unitId = inc.getUnitId();
                if (unitId == -1) continue;

                Unit u;
                try { u = findUnit(unitId); } catch (IDNotRecognisedException e) { continue; }

                if (u.getStatus() == UnitStatus.AT_SCENE && u.getWorkRemaining() == 0) {
                    u.setStatus(UnitStatus.IDLE);
                    u.setIncidentId(-1);
                    inc.setStatus(IncidentStatus.RESOLVED);
                }
            }
        }
    }

    // STATUS

    /**
     * Return a full status summary of the simulation.
     * Includes tick, counts of stations/units/incidents/obstacles, and details of all units and incidents.
     * @return a full status summary of the simulation
     */
    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("TICK=").append(tick).append("\n");
        sb.append("STATIONS=").append(stationCount)
          .append(" UNITS=").append(unitCount)
          .append(" INCIDENTS=").append(incidentCount)
          .append(" OBSTACLES=").append(cityMap.countObstacles()).append("\n");

        sb.append("INCIDENTS\n");
        int[] incIds = getIncidentIds();
        for (int iid : incIds) {
            try { sb.append(viewIncident(iid)).append("\n"); }
            catch (IDNotRecognisedException e) { e.printStackTrace(); }
        }

        sb.append("UNITS\n");
        int[] unitIds = getUnitIds();
        for (int uid : unitIds) {
            try { sb.append(viewUnit(uid)).append("\n"); }
            catch (IDNotRecognisedException e) { e.printStackTrace(); }
        }

        return sb.toString();
    }

    //  HELPERS

    /**
     * Find a station by ID.
     * @param id the ID of the station
     * <p>
     * @return the station with that ID
     * </p>
     * @throws IDNotRecognisedException if the ID is not recognised
     */
    private Station findStation(int id) throws IDNotRecognisedException {
        for (int i = 0; i < stationCount; i++)
            if (stations[i].getStationId() == id) return stations[i];
        throw new IDNotRecognisedException("Station ID not recognised");
    }

    /**
     * Find a unit by ID.
     * @param id the ID of the unit
     * <p>
     * @return the unit with that ID
     * </p>
     * @throws IDNotRecognisedException if the ID is not recognised
     */
    private Unit findUnit(int id) throws IDNotRecognisedException {
        for (int i = 0; i < unitCount; i++)
            if (units[i].getUnitId() == id) return units[i];
        throw new IDNotRecognisedException("Unit ID not recognised");
    }

    /**
     * Find an incident by ID.
     * @param id the ID of the incident
     * <p>
     * @return the incident with that ID
     * </p>
     * @throws IDNotRecognisedException if the ID is not recognised
     */
    private Incident findIncident(int id) throws IDNotRecognisedException {
        for (int i = 0; i < incidentCount; i++)
            if (incidents[i].getIncidentId() == id) return incidents[i];
        throw new IDNotRecognisedException("Incident ID not recognised");
    }

    /**
     * Move a unit one step towards target, avoiding obstacles.
     * Follows Section 5 spec:
     * 1. Prefer moves that reduce Manhattan distance.
     * 2. If none reduce distance, take the first legal move in N, E, S, W order.
     * @param u the unit
     * @param targetX the x coordinate of target location
     * @param targetY the y coordinate of target location
     * <p>
     * @return new [x, y] position
     * </p>
     */
    private int[] moveUnitTowards(Unit u, int targetX, int targetY) {
        int cx = u.getX();
        int cy = u.getY();
        int currentDist = Math.abs(cx - targetX) + Math.abs(cy - targetY);

        // Directions in N, E, S, W order
        int[][] dirs = {
            {0, -1},  // N (x, y-1)
            {1, 0},   // E (x+1, y)
            {0, 1},   // S (x, y+1)
            {-1, 0}   // W (x-1, y)
        };

        // 1. First, try to reduce Manhattan distance
        for (int[] d : dirs) {
            int nx = cx + d[0];
            int ny = cy + d[1];
            if (!cityMap.isInBounds(nx, ny) || cityMap.isBlocked(nx, ny)) continue;

            int newDist = Math.abs(nx - targetX) + Math.abs(ny - targetY);
            if (newDist < currentDist) {
                return new int[]{nx, ny};
            }
        }

        // 2. If no distance-reducing move found, take the first legal move
        for (int[] d : dirs) {
            int nx = cx + d[0];
            int ny = cy + d[1];
            if (!cityMap.isInBounds(nx, ny) || cityMap.isBlocked(nx, ny)) continue;
            return new int[]{nx, ny};
        }

        // 3. No moves possible, stay put
        return new int[]{cx, cy};
    }
}
