# CityRescue Lite+

CityRescue Lite+ is a **grid-based city emergency simulation**.
The project simulates a city with stations, emergency units, obstacles, and incidents.
It allows you to:

* Manage the city grid and obstacles.
* Add and remove stations and units.
* Report, dispatch, escalate, cancel, and resolve incidents.
* Advance the simulation over discrete time ticks.
* Track unit movements and incident progress deterministically.

---

## What the code does

The code manages:

* Grid initialization and obstacle management
* Station and unit management (creation, transfer, decommissioning)
* Incident handling (reporting, escalation, cancellation, resolution)
* Unit dispatching according to availability, type, and distance
* Simulation progression via `tick()`
* Full status reporting for units, incidents, and the city

---

## Quick start (local)

Requirements:

* Java 25+
* Maven 3.8+

Download the zip file and save it to your prefered location on disk.

Unzip if the .zip file if you have downloaded it.

Open your prefered IDE (VS Code works best) and open the folder project folder.

---
## To run the code

Create a new terminal within VS Code.

First compile the main code file using:

```bash
./mvnw clean compile
```

To run the code using the various tests use:

```bash
./mvnw clean test
```

---

## Notes

* This project was done by Yaryna Matselyukh and James Given
