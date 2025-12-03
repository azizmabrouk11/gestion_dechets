# Test Data for MongoDB Collections

This directory contains sample JSON data for testing the waste management system.

## Collections

1. **users.json** - 3 sample users (User, Employee, Admin)
2. **vehicules.json** - 3 sample vehicles (2 functional trucks, 1 under maintenance)
3. **collectpoints.json** - 3 pickup points with embedded containers
4. **containers.json** - 4 containers (standalone collection)
5. **routes.json** - 3 collection routes with vehicles and pickup points
6. **incidents.json** - 3 incidents (Reported, In Progress, Resolved)
7. **notifications.json** - 3 notifications (Capacity alert, Incident notifications)

## How to Import Data into MongoDB

### Option 1: Using mongoimport (Command Line)

```bash
# Make sure MongoDB is running
docker compose up -d mongo

# Import each collection
mongoimport --host localhost:27017 --db dechet_db --collection users --file test-data/users.json --jsonArray --username root --password admin --authenticationDatabase admin

mongoimport --host localhost:27017 --db dechet_db --collection vehicules --file test-data/vehicules.json --jsonArray --username root --password admin --authenticationDatabase admin

mongoimport --host localhost:27017 --db dechet_db --collection collectpoints --file test-data/collectpoints.json --jsonArray --username root --password admin --authenticationDatabase admin

mongoimport --host localhost:27017 --db dechet_db --collection containers --file test-data/containers.json --jsonArray --username root --password admin --authenticationDatabase admin

mongoimport --host localhost:27017 --db dechet_db --collection routes --file test-data/routes.json --jsonArray --username root --password admin --authenticationDatabase admin

mongoimport --host localhost:27017 --db dechet_db --collection incidents --file test-data/incidents.json --jsonArray --username root --password admin --authenticationDatabase admin

mongoimport --host localhost:27017 --db dechet_db --collection notifications --file test-data/notifications.json --jsonArray --username root --password admin --authenticationDatabase admin
```

### Option 2: Using MongoDB Compass (GUI)

1. Open MongoDB Compass
2. Connect to: `mongodb://root:admin@localhost:27017/?authSource=admin`
3. Select database: `dechet_db`
4. For each collection:
   - Create collection if it doesn't exist
   - Click "ADD DATA" → "Import JSON or CSV file"
   - Select the corresponding JSON file
   - Click "Import"

### Option 3: Using Mongo Express (Web UI)

1. Open Mongo Express: http://localhost:8081
2. Navigate to `dechet_db` database
3. For each collection:
   - Click on collection name or create new collection
   - Click "New Document"
   - Copy-paste JSON array content
   - Click "Save"

### Option 4: Using Docker exec

```bash
# Copy files into MongoDB container
docker cp test-data/users.json mongo-container:/tmp/
docker cp test-data/vehicules.json mongo-container:/tmp/
docker cp test-data/collectpoints.json mongo-container:/tmp/
docker cp test-data/containers.json mongo-container:/tmp/
docker cp test-data/routes.json mongo-container:/tmp/
docker cp test-data/incidents.json mongo-container:/tmp/
docker cp test-data/notifications.json mongo-container:/tmp/

# Import from inside container
docker exec -it mongo-container mongoimport --db dechet_db --collection users --file /tmp/users.json --jsonArray --username root --password admin --authenticationDatabase admin
docker exec -it mongo-container mongoimport --db dechet_db --collection vehicules --file /tmp/vehicules.json --jsonArray --username root --password admin --authenticationDatabase admin
docker exec -it mongo-container mongoimport --db dechet_db --collection collectpoints --file /tmp/collectpoints.json --jsonArray --username root --password admin --authenticationDatabase admin
docker exec -it mongo-container mongoimport --db dechet_db --collection containers --file /tmp/containers.json --jsonArray --username root --password admin --authenticationDatabase admin
docker exec -it mongo-container mongoimport --db dechet_db --collection routes --file /tmp/routes.json --jsonArray --username root --password admin --authenticationDatabase admin
docker exec -it mongo-container mongoimport --db dechet_db --collection incidents --file /tmp/incidents.json --jsonArray --username root --password admin --authenticationDatabase admin
docker exec -it mongo-container mongoimport --db dechet_db --collection notifications --file /tmp/notifications.json --jsonArray --username root --password admin --authenticationDatabase admin
```

## Data Overview

### Users
- **john_doe**: Regular user (User role)
- **jane_smith**: Employee (Employe role)
- **admin_user**: Administrator (Admin role)

### Vehicles
- **TN-1234-AB**: Functional truck (5000L capacity) - Assigned to Jane
- **TN-5678-CD**: Functional car (2000L capacity) - Unassigned
- **TN-9012-EF**: Truck under maintenance (7000L capacity)

### Pickup Points
- **Point 1**: Tunis center (36.8065, 10.1815) - 2 containers (plastic + cardboard)
- **Point 2**: North area (36.8500, 10.2000) - 1 plastic container (90% full)
- **Point 3**: South area (36.7800, 10.1500) - 1 non-functional cardboard container

### Incidents
- **Incident 1**: HIGH priority - Overflowing container (REPORTED)
- **Incident 2**: MEDIUM priority - Broken container (IN_PROGRESS, assigned to Jane)
- **Incident 3**: LOW priority - Missed collection (RESOLVED)

### Routes
- **Route 1**: Dec 3, 08:00 - Truck TN-1234-AB covering 2 pickup points
- **Route 2**: Dec 4, 09:30 - Car TN-5678-CD covering 1 pickup point
- **Route 3**: Dec 5, 07:00 - Truck TN-1234-AB covering 1 pickup point

## Notes

- All IDs are MongoDB ObjectId format strings
- Dates are in ISO 8601 format
- Enums match backend Java enums exactly
- User passwords are managed by Keycloak, not stored in MongoDB
- Container fillLevel values indicate percentage of capacity used
