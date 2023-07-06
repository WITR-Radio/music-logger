## Music Logger

Yet another "new" music logger for WITR. This is a Spring application using PostgreSQL.

## Setting Up

Before using, the environment variable file must be created from its template. The reason for this is so the ones being used aren't tracked by git. To do this, run the following in a bash shell at the root of the project:

```bash
cp .env.template .env
```

Add values to any necessary empty keys.

<details>
<summary>Expand for a description of each environment variable, and how to get the missing values.</summary></br>

```
OUTBOUND_PORT - The port to serve the API from
ELASTICSEARCH_URL - The URL to hosted elasticsearch (Default setup should give you http://localhost:9200)
POSTGRES_URL - The jdbc URL (e.g. jdbc:postgresql://localhost:5432/logger)
POSTGRES_USER - The database username
POSTGRES_PASS - The database password
SPOTIFY_CLIENT_ID - The client ID of the logger's Spotify application
SPOTIFY_CLIENT_SECRET - The client secret of the logger's Spotify application
INDEX_URL - The URL of the frontend logger's index, with no trailing slash. e.g. http://localhost:3003
STREAMING_API_TOKEN - A string to be used as a token to check for in the Authentication header of /api/streaming/* requests
BROADCAST_ENABLE - If broadcasting services should be enabled (boolean). If false, no values below are required
ICECAST_USER - The Icecast username 
ICECAST_PASS - The Icecast password
RDS_IP - The IP of the RDS
RDS_PORT - The port that RDS uses
TUNEIN_PARTNER_ID - The TuneIn partner ID
TUNEIN_PARTNER_KEY - The TuneIn partner API key
TUNEIN_STATION_ID - The TuneIn station ID
WIDEORBIT_HOST - The hostname of the WideOrbit server
WIDEORBIT_PORT - The port WideOrbit is broadcasting events to
WIDEORBIT_ENABLE - If data should be received from wideorbit (boolean, defaults to false)
RIVENDELL_ENABLE - If data should be received from rivendell (boolean, defaults to false)
```

For local development, it is **highly** recommended to leave `BROADCAST_ENABLE` as `false`. All broadcasting to WITR services will be disabled, which ensures no disruption to anything in production. If `false`, the variables below it in the template may be omitted or left blank.

#### SPOTIFY_CLIENT_ID / SPOTIFY_CLIENT_SECRET

These values are so the logger can use the Spotify API to look up album art and other necessary information. To get these, create a [Spotify developer application](https://developer.spotify.com/dashboard/applications)  and generate a new client ID and secret.

#### ICECAST_USER / ICECAST_PASS

The credentials for Icecast can be acquired by asking the Chief Engineer.

#### RDS_IP / RDS_PORT

These are just the IP and port for the RDS server.

#### TUNEIN_PARTNER_ID / TUNEIN_PARTNER_KEY / TUNEIN_STATION_ID

These will have to be acquired by contacting the Chief Engineer or someone who has access to the station's TuneIn account.

#### WIDEORBIT_HOST / WIDEORBIT_PORT

The IP/hostname and port for WideOrbit.

---
</details>

## Running

The next step will depend on if you are running this yourself independently, or internally at WITR. We have containerized development environments to keep application state version controlled across all projects, which is not open source quite yet (stay tuned!).

<details>
<summary><b>Independently</b></summary></br>

Set up and start a Postgres (or similar) database, setting its credentials/URL to the relevant environment variables.
</details>

<details>
<summary><b>Internally</b></summary></br>

Ensure you have set up the [dev-environments](https://github.com/WITR-Radio/dev-environments/tree/master/primary) for `primary`, and start the environment.
</details>


When the relevant step has been completed, populate the environment variables and continue.

In order to run the backend, your IDE must be able to read the `.env` file. IntelliJ does not have such a feature out of the box, so a plugin like [EnvFile](https://plugins.jetbrains.com/plugin/7861-envfile) may be used. If using the plugin, make sure it is enabled for the run configuration. Run the program from the file `edu.rit.witr.musiclogger.MusicLoggerApplication`.

## Building

Building may be done from the command line, through the following command:

```bash
./gradle bootJar
```
