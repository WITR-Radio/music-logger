## Music Logger

Yet another "new" music logger for WITR. This is intended to use the current logger's database, and provide the same functionality, just better.

This is a Spring application using PostgreSQL.

Required environment variables:
```
BROADCAST_ENABLE - true or false, if broadcasting services should be enabled
ELASTICSEARCH_URL - The URL to hosted elasticsearch (Default setup should give you http://localhost:9200)
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
SPOTIFY_ACCESS_TOKEN - A spotify developer application access token
INDEX_URL - The URL of the index, with no trailing slash. e.g. http://localhost:8080
STREAMING_API_TOKEN - A string to be used as a token to check for in the Authentication header of /api/streaming/* requests
WIDEORBIT_HOST - The hostname of the WideOrbit server
WIDEORBIT_PORT - The port WideOrbit is broadcasting events to
WIDEORBIT_ENABLE - If wideorbit should be receiving data (a boolean value)
RIVENDELL_ENABLE - If rivendell should be receiving data (a boolean value)
```
