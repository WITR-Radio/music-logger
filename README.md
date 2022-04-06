## Music Logger

Yet another "new" music logger for WITR. This is intended to use the current logger's database, and provide the same functionality, just better.

This is a Spring application using PostgreSQL.

Required environment variables:
```
POSTGRES_URL - The jdbc URL (e.g. jdbc:postgresql://localhost:5432/logger)
POSTGRES_USER - The database username
POSTGRES_PASS - The database password
ICECAST_USER - The Icecast username 
ICECAST_PASS - The Icecast password
RDS_IP - The IP of the RDS
RDS_PORT - The port that RDS uses
TUNEIN_PARTNER_ID - The TuneIn partner ID
TUNEIN_PARTNER_KEY - The TuneIn partner API key
TUNEIN_STATION_ID - The TuneIn station ID
```
